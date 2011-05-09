// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 9:16:40 PM
 */

public class TestBoundedDragHandler2 {
    private JFrame frame;
    private PCanvas piccoloCanvas;

    public TestBoundedDragHandler2() throws IOException {
        frame = new JFrame( "Simple Piccolo Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        SwingUtils.centerWindowOnScreen( frame );

        piccoloCanvas = new PCanvas();
        piccoloCanvas.setPanEventHandler( null );

        frame.setContentPane( piccoloCanvas );

        // Node that defines the drag bounds
        Rectangle rectangleBounds = new Rectangle( 10, 10, 300, 300 );
        PPath dragBoundsNode = new PPath( rectangleBounds );
        dragBoundsNode.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, new float[] { 8, 12 }, 0 ) );
        dragBoundsNode.setStrokePaint( Color.black );
        piccoloCanvas.getLayer().addChild( dragBoundsNode );

        // Draggable text node
        final PText pText = new PText( "Hello Piccolo" );
        piccoloCanvas.getLayer().addChild( pText );
        pText.setOffset( 100, 100 );
        pText.addInputEventListener( new BoundedDragHandler( pText, dragBoundsNode ) );
        pText.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pText.setPaint( Color.green );

        // A more complicated draggable node
        ComplicatedNode cn = new ComplicatedNode( new Dimension( 25, 50 ), dragBoundsNode );
        piccoloCanvas.getLayer().addChild( cn );
        cn.setOffset( 100, 200 );

        // Red rectangle, constrained to vertical dragging
        Rectangle2D rect = new Rectangle2D.Double( 0, 0, 200, 20 );
        PPath pathNode = new PPath( rect );
        pathNode.setPaint( Color.red );
        pathNode.setOffset( 10, 350 );
        piccoloCanvas.getLayer().addChild( pathNode );
        Rectangle rectangleBounds2 = new Rectangle( 0, 0, 200, 200 );
        PPath dragBoundsNode2 = new PPath( rectangleBounds2 );
        dragBoundsNode2.setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1, new float[] { 8, 12 }, 0 ) );
        dragBoundsNode2.setStrokePaint( Color.black );
        dragBoundsNode2.setOffset( pathNode.getFullBounds().getX(), pathNode.getFullBounds().getY() );
        piccoloCanvas.getLayer().addChild( dragBoundsNode2 );
        pathNode.addInputEventListener( new BoundedDragHandler( pathNode, dragBoundsNode2 ) );
        pathNode.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
    }

    public static void main( String[] args ) throws IOException {
        new TestBoundedDragHandler2().start();
    }

    private void start() {
        frame.show();
    }

    /**
     * ComplicatedNode tests a more complicated situation.
     * This node has 3 children: 2 rectangles and 1 ellipse.
     * The rectangles can be used to drag the node; the ellipse cannot.
     */
    public static class ComplicatedNode extends PNode {

        public ComplicatedNode( Dimension size, PNode dragBoundsNode ) {
            super();

            double x, y, w, h;

            x = -size.getWidth() / 2;
            y = -size.getHeight() / 2;
            w = size.getWidth();
            h = size.getHeight() / 2;
            PPath rect1 = new PPath( new Rectangle2D.Double( x, y, w, h ) );
            rect1.setPaint( Color.RED );
            addChild( rect1 );

            x = -size.getWidth() / 2;
            y = 0;
            w = size.getWidth();
            h = size.getHeight() / 2;
            PPath rect2 = new PPath( new Rectangle2D.Double( x, y, w, h ) );
            rect2.setPaint( Color.GREEN );
            addChild( rect2 );

            w = size.getWidth() * 2;
            h = 0.15 * w;
            x = -w / 2;
            y = -h / 2;
            PPath ellipse1 = new PPath( new Ellipse2D.Double( x, y, w, h ) );
            ellipse1.setPaint( Color.BLUE );
            addChild( ellipse1 );

            BoundedDragHandler dragHandler = new BoundedDragHandler( this, dragBoundsNode );
            rect1.addInputEventListener( dragHandler );
            rect2.addInputEventListener( dragHandler );

            rect1.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            rect2.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        }
    }
}
