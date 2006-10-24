/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests.ui;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.piccolo.PiccoloModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 3:15:30 PM
 * Copyright (c) Jan 23, 2006 by Sam Reid
 */

public class TestAppStartup2 {
    private static boolean running = true;
    private static boolean inited = false;

    public static void main( String[] args ) throws InterruptedException {
        final JFrame frame = new JFrame( "Title" );
        final JMenuBar menubar = new JMenuBar();
        JMenu c = new JMenu( "File" );
        JMenuItem menuItem = new JMenuItem( "Exit" );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        c.add( menuItem );
        menubar.add( c );
        frame.setJMenuBar( menubar );
        final TestPanel testPanel = new TestPanel();
        ControlPanel controlpanel = createControlPanel();
        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.add( testPanel, BorderLayout.CENTER );
        contentPane.add( controlpanel, BorderLayout.EAST );
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Thread t = new Thread( new Runnable() {
            public void run() {
                while( running ) {
//                    if( !inited ) {
                    if( false ) {
                        inited = true;
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    paintImmediately( frame );
                                }
                            } );
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        catch( InvocationTargetException e ) {
                            e.printStackTrace();
                        }

                    }
                    testPanel.paintImmediately( 0, 0, testPanel.getWidth(), testPanel.getHeight() );
//                    menubar.paintImmediately( 0,0,menubar.getWidth(),menubar.getHeight());
                }
            }
        } );
        t.setPriority( Thread.MAX_PRIORITY );

        frame.setSize( 800, 800 );
        frame.setVisible( true );
        t.start();
        Thread.sleep( 10000 );

        running = false;
        frame.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                paintImmediately( frame );
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );

    }

    public static void paintImmediately( final Component component ) {

        if( component instanceof JFrame ) {
            JFrame jf = (JFrame)component;
            for( int i = 0; i < jf.getComponentCount(); i++ ) {
                paintImmediately( jf.getComponent( i ) );
            }
        }
        if( component instanceof JComponent ) {
            JComponent jc = (JComponent)component;
            jc.paintImmediately( 0, 0, component.getWidth(), component.getHeight() );
            for( int i = 0; i < jc.getComponentCount(); i++ ) {
                Component c = jc.getComponent( i );
                paintImmediately( c );
            }
        }


    }

    private static ControlPanel createControlPanel() {
        ControlPanel controlPanel = new ControlPanel( new PiccoloModule( "", new SwingClock( 30, 1 ) ) );

        // Misc controls that do nothing
        controlPanel.addControl( new JCheckBox( "See no evil" ) );
        controlPanel.addControl( new JCheckBox( "Hear no evil" ) );
        controlPanel.addControl( new JCheckBox( "Speak no evil" ) );
        controlPanel.addControlFullWidth( new JSeparator() );
        controlPanel.addControl( new JCheckBox( "short clowns" ) );
        controlPanel.addControl( new JCheckBox( "tall clowns" ) );
        controlPanel.addControl( new JCheckBox( "fat clowns" ) );
        controlPanel.addControl( new JCheckBox( "skinny clowns" ) );
        controlPanel.addControl( new JButton( "Release the Clowns" ) );
        controlPanel.addControlFullWidth( new JSeparator() );
        controlPanel.addControl( new JCheckBox( "lights" ) );
        controlPanel.addControl( new JCheckBox( "camera" ) );
        controlPanel.addControl( new JCheckBox( "action" ) );
        controlPanel.addControlFullWidth( new JSeparator() );
        controlPanel.addControl( new JButton( "Check email..." ) );
        controlPanel.addControl( new JButton( "Take a nap" ) );
        controlPanel.addControlFullWidth( new JSeparator() );
        return controlPanel;
    }

    static class TestPanel extends JPanel {
        Random random = new Random();

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D)g;

            for( int i = 0; i < 3000; i++ ) {
                g2.setColor( new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() ) );
                g2.setStroke( new BasicStroke( random.nextFloat() * 10 ) );
                g2.drawLine( random.nextInt( getWidth() ), random.nextInt( getHeight() ), random.nextInt( getWidth() ), random.nextInt( getHeight() ) );
            }

        }

    }
}
