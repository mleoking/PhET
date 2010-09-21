package edu.colorado.phet.tracking.reports;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Apr 28, 2009
 * Time: 3:11:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestHTMLOutlineNode extends PNode {
    private String html;
    private Font font;
    private Color fill;
    private Color outline;

    public TestHTMLOutlineNode(String html, Font font, Color fill, Color outline) {
        this.html = html;
        this.font = font;
        this.fill = fill;
        this.outline = outline;

        double dtheta = Math.PI * 2 / 10;
        double r=2;
        for (double theta = 0; theta < Math.PI * 2; theta += dtheta) {
            addChild(outline, r*Math.sin(theta), r*Math.cos(theta));
        }


//        addChild(outline, -1, -1);
//        addChild(outline, -1, 1);
//        addChild(outline, 1, 1);
//        addChild(outline, 1, -1);

        addChild(fill, 0, 0);
    }

    private void addChild(Color color, double dx, double dy) {
        HTMLNode node = new HTMLNode(html, color,font);
        node.setOffset(dx, dy);
        addChild(node);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        PCanvas contentPane = new PCanvas();
        contentPane.getLayer().addChild(new TestHTMLOutlineNode("<html>Testing html<br></br> H<sub>2</sub>O", new PhetFont(30, true), Color.yellow, Color.blue));
        frame.setContentPane(contentPane);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setVisible(true);
    }
}
