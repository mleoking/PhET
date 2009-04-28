
package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * OutlinedHTMLNode draws outlines HTML text.
 *
 * @author Sam Reid
 */
public class OutlinedHTMLNode extends PComposite {

    private String html;
    private Font font;
    private Color fill;
    private Color outline;

    public OutlinedHTMLNode( String html, Font font, Color fill, Color outline ) {
        this.html = html;
        this.font = font;
        this.fill = fill;
        this.outline = outline;
        update();
    }

    public void setHTML( String html ) {
        this.html = html;
        update();
    }

    public void setFont( Font font ) {
        this.font = font;
        update();
    }

    public void setFillColor( Color fill ) {
        this.fill = fill;
        update();
    }

    public void setOutlineColor( Color outline ) {
        this.outline = outline;
        update();
    }

    private void update() {
        double dtheta = Math.PI * 2 / 10;
        double r = 2;
        for ( double theta = 0; theta < Math.PI * 2; theta += dtheta ) {
            addChild( outline, r * Math.sin( theta ), r * Math.cos( theta ) );
        }
        addChild( fill, 0, 0 );
    }

    private void addChild( Color color, double dx, double dy ) {
        HTMLNode node = new HTMLNode( html, font, color );
        node.setOffset( dx, dy );
        addChild( node );
    }

    public static void main( String[] args ) {

        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 700, 700 ) );

        PNode node = new OutlinedHTMLNode( "<html>Testing html<br></br>H<sub>2</sub>O", new PhetFont( Font.PLAIN, 30 ), Color.yellow, Color.blue );
        canvas.getLayer().addChild( node );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
