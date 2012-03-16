// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * The API for this class is still under design and subject to change; do not
 * rely on it too heavily.
 * <p/>
 * Ticket #3038 was created to track improvements to this class, since it is not
 * too useful in its current state.  If you need such a node, please feel free
 * to improve this and note changes in the ticket.
 *
 * @author Sam Reid
 */
public class OutlineHTMLNode extends PNode {
    private String html;
    private Font font;

    public OutlineHTMLNode( String html, Font font, Color fill, Color outline ) {
        this( html, font, fill, outline, Math.PI * 2 / 4 );
    }

    public OutlineHTMLNode( String html, Font font, Color fill, Color outline, double dTheta ) {
        this.html = html;
        this.font = font;

        double r = 1.5;
        for ( double theta = 0; theta < Math.PI * 2; theta += dTheta ) {
            addChild( outline, r * Math.sin( theta ), r * Math.cos( theta ) );
        }
        addChild( fill, 0, 0 );
    }

    private void addChild( Color color, double dx, double dy ) {
        HTMLNode node = new HTMLNode( html, color, font );
        node.setOffset( dx, dy );
        addChild( node );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PCanvas contentPane = new PCanvas();
        contentPane.getLayer().addChild( new OutlineHTMLNode( "<html>Testing html<br></br> H<sub>2</sub>O", new PhetFont( 30, true ), Color.yellow, Color.blue ) );
        frame.setContentPane( contentPane );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 700, 700 );
        frame.setVisible( true );
    }
}
