/* Generated By:JJTree: Do not edit this line. ASTnode_stmt.java */

package org.graffiti.plugins.ios.importers.dot.parser;

public class ASTnode_stmt extends SimpleNode {
    public ASTnode_stmt(int id) {
        super(id);
    }

    public ASTnode_stmt(DOTParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(DOTParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}