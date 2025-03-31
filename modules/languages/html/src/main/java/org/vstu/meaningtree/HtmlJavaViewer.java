package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.JavaViewer;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.utils.NodeVisitor;


public class HtmlJavaViewer extends JavaViewer implements NodeVisitor<String> {

    public String visit(Node node) {
        return super.visit(node);
    }

    public String visit(ProgramEntryPoint programEntryPoint) {
        return super.visit(programEntryPoint);
    }

}
