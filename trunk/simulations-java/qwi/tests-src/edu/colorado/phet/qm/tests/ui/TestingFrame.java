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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TestingFrame.java
 * This test frame to demonstrates the use of GlassPane
 *
 * @author Rahul Sapkal(rahul@javareference.com)
 */
public class TestingFrame extends JFrame implements ActionListener {
    JButton m_draw;
    JButton m_mouse;
    GlassPane m_glassPane;

    public TestingFrame() {
        super( "GlassPane Test" );

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout( new BorderLayout( 10, 10 ) );

        mainPanel.add( BorderLayout.NORTH, new JTextField( "TextField1" ) );
        mainPanel.add( BorderLayout.CENTER, new JTextField( "TextField2" ) );

        m_draw = new JButton( "Click here to toggle drawing, ON/OFF" );
        m_draw.addActionListener( this );
        mainPanel.add( BorderLayout.EAST, m_draw );

        m_mouse = new JButton( "Click here to Handle Mouse Events" );
        m_mouse.addActionListener( this );
        mainPanel.add( BorderLayout.WEST, m_mouse );

        getContentPane().add( mainPanel );

        //setting glassPane
        m_glassPane = new GlassPane();
        m_glassPane.setGlassPane( this );
    }

    public void actionPerformed( ActionEvent ae ) {
        if( ae.getSource().equals( m_draw ) ) {
            //toggle drawing
            m_glassPane.setDrawing( !m_glassPane.getDrawing() );
        }
        else if( ae.getSource().equals( m_mouse ) ) {
            //handle mouse events
            m_glassPane.setHandleMouseEvents( true );
        }
    }

    public static void main( String[] arg ) {
        TestingFrame m = new TestingFrame();

        m.setVisible( true );
        m.setSize( new Dimension( 575, 400 ) );
        m.validate();
    }
}