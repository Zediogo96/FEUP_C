package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.HashMap;
import java.util.Map;

public class JavaCalcGenerator extends AJmmVisitor<String, String> {
    private final String className;
    private Map<String, Integer> variableReads;
    private Map<String, Integer> variableWrites;

    public JavaCalcGenerator(String className) {
        this.className = className;
        this.variableReads = new HashMap<>();
        this.variableWrites = new HashMap<>();
    }

    protected void buildVisitor() {
        addVisit("Program", this::dealWithProgram);
        addVisit("Assignment", this::dealWithAssignment);
        addVisit("Integer", this::dealWithLiteral);
        addVisit("Identifier", this::dealWithLiteral);
        addVisit("ExprStmt", this::dealWithExprStmt);
        addVisit("BinaryOp", this::dealWithBinaryOp);
        addVisit("Parenthesis", this::dealWithParenthesis);
    }

    private String dealWithProgram(JmmNode jmmNode, String s) {
        s = (s != null ? s : "");
        StringBuilder ret = new StringBuilder(s + "public class " + this.className + " {\n");
        String s2 = s;
        ret.append(s2).append("\tpublic static void main(String[] args) {\n");

        for (JmmNode child : jmmNode.getChildren()) {
            ret.append("\t\t").append(visit(child, s2));
            ret.append("\n");
        }
        ret.append(s2).append("\t}\n");
        ret.append(s).append("}\n");

        for (String var : variableWrites.keySet()) {
            int reads = variableReads.get(var);
            int writes = variableWrites.getOrDefault(var, 0);
            System.out.println("\"" + var + "\": " + writes + " writes and " + reads + " reads. \n");
        }

        return ret.toString();
    }

    private String dealWithAssignment(JmmNode jmmNode, String s) {
        String varName = jmmNode.get("var");
        String exprCode = visit(jmmNode.getChildren().get(0), s);
        variableWrites.put(varName, variableWrites.getOrDefault(varName, 0) + 1);
        return s + "int " + varName + " = " + exprCode + ";";
    }

    private String dealWithLiteral(JmmNode jmmNode, String s) {
        String varName = jmmNode.get("value");
        variableReads.put(varName, variableReads.getOrDefault(varName, 0) + 1);
        return jmmNode.get("value");
    }


    private String dealWithExprStmt(JmmNode jmmNode, String s) {
        for (JmmNode child : jmmNode.getChildren()) {
            s += visit(child, s);
        }
        return "System.out.println(" + s + ");";
    }

    private String dealWithParenthesis(JmmNode jmmNode, String s) {
        String exprCode = visit(jmmNode.getChildren().get(0), s);
        return "(" + exprCode + ")";
    }

    private String dealWithBinaryOp(JmmNode jmmNode, String s) {
        return s + visit(jmmNode.getChildren().get(0), s) + " " + jmmNode.get("op").replace("'", "") + " " + visit(jmmNode.getChildren().get(1), s);
    }
}
