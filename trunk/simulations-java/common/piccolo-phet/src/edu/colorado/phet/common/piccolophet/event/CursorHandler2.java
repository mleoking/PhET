/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PComponent;
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

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Cursor cursor;  // cursor to change to
    private boolean mousePressed = false;
    private boolean mouseEntered = false;

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
        this.mouseEntered = true;
        updateComponent( event.getComponent() );
    }

    public void mousePressed( PInputEvent event ) {
        this.mousePressed = true;
        updateComponent( event.getComponent() );
    }

    public void mouseReleased( PInputEvent event ) {
        this.mousePressed = false;
        updateComponent( event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        this.mouseEntered = false;
        updateComponent( event.getComponent() );
    }

    private void updateComponent( PComponent component ) {
        if ( component instanceof JComponent ) {
            JComponent jc = (JComponent) component;
            jc.setCursor( getCursorState() );
        }
        else {
            throw new RuntimeException( "Only supported for Swing components");
        }
    }

    private Cursor getCursorState() {
        if ( !mouseEntered && !mousePressed ) {
            return DEFAULT;
        }
        else if ( mouseEntered && !mousePressed ) {
            return cursor;
        }
        else if ( !mouseEntered && mousePressed ) {
            return cursor;
        }
        else {//if ( mouseEntered && mousePressed ) {
            return cursor;
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities for examining PInputEvent
    //----------------------------------------------------------------------------
    
    private static boolean isAnyButtonDown( PInputEvent event ) {
        return isButton1Down( event ) || isButton2Down( event ) || isButton3Down( event );
    }
    
    private static boolean isButton1Down( PInputEvent event ) {
        return  hasModifier( event, InputEvent.BUTTON1_DOWN_MASK );
    }
    
    private static boolean isButton2Down( PInputEvent event ) {
        return hasModifier( event, InputEvent.BUTTON2_DOWN_MASK );
    }
    
    private static boolean isButton3Down( PInputEvent event ) {
        return hasModifier( event, InputEvent.BUTTON3_DOWN_MASK );
    }
    
    /*
     * Checks to see if event has the specified modifier as part of its extended modifier mask.
     * 
     * @param event
     * @param modifier one of the modifier constants documented in java.awt.event.InputEvent
     */
    private static boolean hasModifier( PInputEvent event, int modifier ) {
        return ( event.getModifiersEx() & modifier ) != 0;
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