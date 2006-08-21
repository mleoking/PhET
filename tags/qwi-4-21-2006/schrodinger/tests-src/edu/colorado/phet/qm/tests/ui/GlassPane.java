package edu.colorado.phet.qm.tests.ui;
/*
* This example is from javareference.com
* for more information visit,
* http://www.javareference.com
*/

//package

//import statements

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * GlassPane.java
 * This class demonstrates the use of GlassPane
 *
 * @author Rahul Sapkal(rahul@javareference.com)
 */
public class GlassPane extends JPanel implements MouseListener {
    //RootPaneContainer interface
    private RootPaneContainer m_rootPane = null;

    //Stores the previous Glass Pane Component
    private Component m_prevGlassPane = null;
    private boolean m_handleMouseEvents = false;
    private boolean m_drawing = false;

    /**
     * Constructor
     *
     * @param title
     * @throws
     */
    public GlassPane() {
    }

    //Mouse Events
    public void mousePressed( MouseEvent e ) {
        //sound beep
        Toolkit.getDefaultToolkit().beep();
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }

    /**
     * Set the glassPane
     */
    public void setGlassPane( RootPaneContainer rootPane ) {
        m_rootPane = rootPane;

        //store the current glass pane
        m_prevGlassPane = m_rootPane.getGlassPane();

        //set this as new glass pane
        m_rootPane.setGlassPane( this );

        //set opaque to false, i.e. make transparent
        setOpaque( false );
    }

    /**
     * remove the glassPane
     */
    public void removeGlassPane() {
        //set the glass pane visible false
        setVisible( false );

        //reset the previous glass pane
        m_rootPane.setGlassPane( m_prevGlassPane );
    }

    /**
     * Set the handleMoveEvents
     * This will add the mouseListener and all the event will be
     * trapped my the glass pane
     */
    public void setHandleMouseEvents( boolean handleMouseEvents ) {
        if( m_handleMouseEvents == handleMouseEvents ) {
            //ignore if the state is same
            return;
        }

        m_handleMouseEvents = handleMouseEvents;

        if( m_handleMouseEvents ) {
            //add this as mouse event listener and trap all the
            //mouse events
            addMouseListener( this );
        }
        else {
            //remove mouse events listener
            removeMouseListener( this );
        }

        //This is important otherwise the glass pane will no catch events
        setVisible( true );
    }

    /**
     * setDrawing
     */
    public void setDrawing( boolean drawing ) {
        m_drawing = drawing;

        //This is important otherwise the glass pane will not be visible
        setVisible( true );

        //Call repaint
        repaint();
    }

    /**
     * get the drawing state
     */
    public boolean getDrawing() {
        return m_drawing;
    }

    /**
     * Handling Paint
     */
    public void paint( Graphics g ) {
        super.paint( g );

        if( m_drawing ) {
            g.setColor( Color.cyan );
            g.fillRoundRect( 100, 100, 250, 200, 40, 40 );

            g.setFont( new Font( "Arial", Font.BOLD, 12 ) );
            g.setColor( Color.black );

            g.drawString( "This example is from JavaReference.com", 110, 210 );
        }
    }
}