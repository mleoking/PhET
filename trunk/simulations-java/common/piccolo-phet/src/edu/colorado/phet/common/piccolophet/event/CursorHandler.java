// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.event;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo event handler that shows a different Cursor when entering a PNode
 */
public class CursorHandler extends PBasicInputEventHandler {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Some common cursors...
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    public static final Cursor DEFAULT = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

    private static final CursorManager manager = new CursorManager();

    //Keep track of which component the user has moused over in case the cursor needs to change during dragging, see #3061
    private PComponent entered;

    //todo: should make 1 manager per JComponent?
    //the current implementation assumes state is global across all JPanels, may not work properly when moving from one JComponent to another

    /**
     * This class manages global state for the cursor.  Consider the following scenario:
     * 1. The cursor enters Node A
     * >The cursor becomes a hand
     * 2. The cursor is pressed and dragged out of Node A into Node B
     * >The cursor should remain a hand
     * <p/>
     * This is difficult (or impossible) to do without maintaining global state about which
     * nodes have been entered and dragged.
     */
    private static class CursorManager {

        private Cursor lastEntered;
        boolean pressed = false;

        private CursorManager() {
        }

        public void mouseEntered( JComponent component, Cursor cursor ) {
            if ( !pressed ) {
                component.setCursor( cursor );
            }
            lastEntered = cursor;
        }

        public void mousePressed() {
            pressed = true;
        }

        public void mouseReleased( JComponent component ) {
            if ( lastEntered != null ) {
                component.setCursor( lastEntered );
            }
            else {
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
    public CursorHandler() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public CursorHandler( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public CursorHandler( Cursor cursor ) {
        this.cursor = cursor;
    }

    /**
     * Change the cursor to the newly specified cursor.  The effect will take place immediately, even if the user is dragging an object.
     * This is to support scenarios in which the cursor must change while interacting, see #3061
     *
     * @param cursor the new cursor to set
     */
    public void setCursor( Cursor cursor ) {
        this.cursor = cursor;

        //If the user is currently in a component for which the cursor is being shown, then signify to the manager the new cursor and set the cursor on the component
        if ( entered != null ) {
            final JComponent component = (JComponent) entered;
            manager.mouseEntered( component, cursor );
            component.setCursor( cursor );
        }
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        manager.mouseEntered( (JComponent) event.getComponent(), cursor );

        //Keep track of the component the mouse has entered for potentially changing the cursor while inside the component, see #3061
        entered = event.getComponent();
    }

    public void mousePressed( PInputEvent event ) {
        manager.mousePressed();
    }

    public void mouseReleased( PInputEvent event ) {
        manager.mouseReleased( (JComponent) event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        manager.mouseExited( (JComponent) event.getComponent() );

        //Set the entered component to null so it won't be updated incorrectly, see #3061
        entered = null;
    }

    //----------------------------------------------------------------------------
    // test
    //----------------------------------------------------------------------------

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();

        PPath redNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        redNode.setPaint( Color.RED );
        redNode.setOffset( 50, 50 );
        redNode.addInputEventListener( new CursorHandler( CROSSHAIR ) );
        canvas.getLayer().addChild( redNode );

        PPath greenNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.setOffset( redNode.getFullBoundsReference().getMaxX() + 50, redNode.getFullBoundsReference().getY() );
        greenNode.addInputEventListener( new CursorHandler( HAND ) );
        canvas.getLayer().addChild( greenNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}