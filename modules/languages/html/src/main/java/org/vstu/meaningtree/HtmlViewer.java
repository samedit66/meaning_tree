package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.LanguageViewer;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.NodeVisitor;

public class HtmlViewer extends LanguageViewer implements NodeVisitor<String> {

    @Override
    public String toString(Node node) {
        return "";
    }
}
