/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Feasibility test for improved cursor handling
 */
public class CursorHandler2 extends PBasicInputEventHandler {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Some common cursors...
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    public static final Cursor DEFAULT = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

    private static final CursorManager manager = new CursorManager();
    
    //todo: should make 1 manager per JComponent?
    private static class CursorManager {
        
        private Cursor lastEntered;
        boolean pressed = false;

        private CursorManager() {
        }

        public void mouseEntered( JComponent component, Cursor cursor ) {
            if ( !pressed ) {
                component.setCursor( cursor );
            }
            else {
                lastEntered = cursor;
            }
        }

        public void mousePressed() {
            pressed = true;
            lastEntered = null;
        }

        public void mouseReleased( JComponent component ) {
            if ( lastEntered != null ) {
                component.setCursor( lastEntered );
            }else{
                component.setCursor( Cursor.getDefaultCursor() );
            }

            pressed = false;
        }

        public void mouseExited( JComponent component ) {
            if ( !pressed ) {
                component.setCursor( Cursor.getDefaultCursor() );
            }
            lastEntered = null;
        }
    }

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Cursor cursor;  // cursor to change to
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Creates a handler that uses the HAND cursor.
     */
    public CursorHandler2() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public CursorHandler2( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public CursorHandler2( Cursor cursor ) {
        this.cursor = cursor;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        manager.mouseEntered( (JComponent) event.getComponent(), cursor );
    }

    public void mousePressed( PInputEvent event ) {
        manager.mousePressed();
    }

    public void mouseReleased( PInputEvent event ) {
        manager.mouseReleased( (JComponent) event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        manager.mouseExited( (JComponent) event.getComponent() );
    }

    //----------------------------------------------------------------------------
    // test
    //----------------------------------------------------------------------------

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();

        PPath redNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        redNode.setPaint( Color.RED );
        redNode.setOffset( 50, 50 );
        redNode.addInputEventListener( new CursorHandler2( CROSSHAIR ) );
        canvas.getLayer().addChild( redNode );

        PPath greenNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.setOffset( redNode.getFullBoundsReference().getMaxX() + 50, redNode.getFullBoundsReference().getY() );
        greenNode.addInputEventListener( new CursorHandler2( HAND ) );
        canvas.getLayer().addChild( greenNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}