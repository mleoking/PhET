package edu.colorado.phet.tests.piccolo;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 9:24:27 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestRenderingSize {
    private JFrame frame;
    private TestPPath centeredOnRight;
    private PhetPCanvas phetPCanvas;

    public TestRenderingSize() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
        phetPCanvas.addWorldChild( new TestPPath( 0, 0, 100, 100, Color.green ) );
        frame.setContentPane( phetPCanvas );

        centeredOnRight = new TestPPath( 0, 0, 100, 100, Color.blue );
        phetPCanvas.addWorldChild( centeredOnRight );
        centeredOnRight.addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                relayoutNode();
            }
        } );
        phetPCanvas.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                relayoutNode();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutNode();
            }
        } );
        relayoutNode();
    }

    private void relayoutNode() {
        Rectangle viewBounds = new Rectangle( phetPCanvas.getWidth(), phetPCanvas.getHeight() );
        Dimension2D dim = new PDimension( viewBounds.width, viewBounds.height );
        phetPCanvas.getPhetRootNode().screenToWorld( dim );
        centeredOnRight.setPathTo( new Rectangle2D.Double( dim.getWidth() - 100, dim.getHeight() / 2 - 50, 100, 100 ) );
    }

    static class TestPPath extends PPath {
        public TestPPath( int x, int y, int w, int h, Color color ) {
            super( new Rectangle( x, y, w, h ) );
            setPaint( color );
        }
    }

    public static void main( String[] args ) {
        new TestRenderingSize().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
