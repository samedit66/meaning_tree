package org.vstu.meaningtree;

import org.vstu.meaningtree.languages.JavaLanguage;
import org.vstu.meaningtree.nodes.Expression;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.ProgramEntryPoint;
import org.vstu.meaningtree.nodes.Statement;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.literals.IntegerLiteral;
import org.vstu.meaningtree.nodes.expressions.math.AddOp;
import org.vstu.meaningtree.nodes.expressions.math.MulOp;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.nodes.statements.assignments.AssignmentStatement;
import org.vstu.meaningtree.nodes.statements.conditions.IfStatement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HtmlJavaViewer viewer = new HtmlJavaViewer();

        ProgramEntryPoint testNode = (ProgramEntryPoint) createTestCode();

        System.out.println(testNode.getClass().getName());

        String htmlOutput = testNode.accept(viewer);

        File outputFile = new File("viewer-test.html");
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(generateHtmlPage(htmlOutput));
        }

        System.out.println("Generated HTML output at: " + outputFile.getAbsolutePath());
    }

    private static String generateHtmlPage(String content) {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>HtmlJavaViewer Test</title>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; margin: 20px; }\n" +
               "        .container { border: 1px solid #ddd; padding: 20px; border-radius: 5px; }\n" +
               "        h1 { color: #333; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <h1>HtmlJavaViewer Test Output</h1>\n" +
               "    <div class=\"container\">\n" +
               content + "\n" +
               "    </div>\n" +
               "</body>\n" +
               "</html>";
    }

    private static Node createTestCode() {
        var javaLang = new JavaLanguage();
        var mt = javaLang.getMeaningTree("if (a < b) { a + b; }");

        return mt.getRootNode();
    }
}