package pt.up.fe.comp2023;

import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

public class JavaCalcGenerator extends AJmmVisitor<String, String> {
    private final String className;

    public JavaCalcGenerator(String className) {
        this.className = className;
    }

    protected void buildVisitor() {
        addVisit("Program", this::dealWithProgram);
        addVisit("Assignment", this::dealWithAssignment);
        addVisit("Integer", this::dealWithLiteral);
        addVisit("Identifier", this::dealWithLiteral);
        addVisit("ExprStmt", this::dealWithExprStmt);
        addVisit("BinaryOp", this::dealWithBinaryOp);
    }

    private String dealWithProgram(JmmNode jmmNode, String s) {
        s = (s != null ? s : "");
        String ret = s + "public class " + this.className + " {\n";
        String s2 = s;
        ret += s2 + "\tpublic static void main(String[] args) {\n";

        for (JmmNode child : jmmNode.getChildren()) {
            ret += "\t\t" + visit(child, s2);
            ret += "\n";
        }
        ret += s2 + "\t}\n";
        ret += s + "}\n";
        return ret;
    }

    private String dealWithAssignment(JmmNode jmmNode, String s) {
        String varName = jmmNode.get("var");
        String exprCode = visit(jmmNode.getChildren().get(0), s);
        return s + "int " + varName + " = " + exprCode + ";";
    }
    
    private String dealWithLiteral(JmmNode jmmNode, String s) {
        return jmmNode.get("value");
    }

    private String dealWithExprStmt(JmmNode jmmNode, String s) {
        for (JmmNode child : jmmNode.getChildren()) {
            s += visit(child, s);
        }
        return "System.out.println(" + s + ");";
    }

    private String dealWithBinaryOp(JmmNode jmmNode, String s) {
        return s + visit(jmmNode.getChildren().get(0), s) + " " + jmmNode.get("op").replace("'", "") + " " + visit(jmmNode.getChildren().get(1), s);
    }
}
