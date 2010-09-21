package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class is to help ensure that drag event handling works properly in different coordinate frames.
 *
 * @author Sam Reid
 */
public class TestDraggingInDifferentFrames {
    private JFrame frame = new JFrame();

    public static class BoxNode extends PhetPPath {
        public BoxNode() {
            this( 100, 100 );
            addChild( new PText( "Origin" ) );
            final PText child = new PText( "Drag me" );
            child.setOffset( 50, 50 );
            child.addInputEventListener( new PDragEventHandler() );
            addChild( child );
        }

        public BoxNode( int width, int height ) {
            super( new Rectangle2D.Double( 0, 0, width, height ), new BasicStroke(), Color.darkGray );
        }
    }

    public TestDraggingInDifferentFrames() {
        final PhetPCanvas contentPane = new PhetPCanvas();
        contentPane.setZoomEventHandler( new PZoomEventHandler() );
        frame.setContentPane( contentPane );
        frame.setSize( 1024, 768 );

        BoxNode boxNode = new BoxNode();
        contentPane.addScreenChild( boxNode );

        PNode rotateFrame = new PNode();
        rotateFrame.rotate( Math.PI / 2 );
        rotateFrame.translate( 0, -300 );
        rotateFrame.addChild( new BoxNode() );
        contentPane.addScreenChild( rotateFrame );

        PNode offsetFrame = new PNode();
        offsetFrame.setOffset( 200, 200 );
        offsetFrame.addChild( new BoxNode() );

        PNode offsetScaleNode = new PNode();
        offsetScaleNode.setOffset( 0, 200 );
        offsetScaleNode.scale( 2.0 );
        offsetScaleNode.addChild( new BoxNode() );
        contentPane.addScreenChild( offsetScaleNode );

        PNode parentNode = new PNode();
        parentNode.translate( 400, 400 );
        parentNode.scale( 1.2 );
        parentNode.rotate( Math.PI / 12 );
        parentNode.addChild( new BoxNode() );
        contentPane.addScreenChild( parentNode );

        PNode childNode = new PNode();
        childNode.scale( 1.2 );
        childNode.rotate( Math.PI / 6 );
        childNode.translate( 50, 50 );
        childNode.addChild( boxNode );

        parentNode.addChild( childNode );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new TestDraggingInDifferentFrames().start();
            }
        } );
    }

    private void start() {
        frame.setVisible( true );
    }
}