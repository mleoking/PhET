/* Copyright 2004, Sam Reid */

package edu.colorado.phet.quantumtunneling.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 3:15:30 PM
 * Copyright (c) Jan 23, 2006 by Sam Reid
 */

public class TestAppStartup2 {

    private static boolean running = true;

    public static void main( String[] args ) throws InterruptedException {
        JFrame frame = new JFrame( "Title" );
        JMenuBar menubar = new JMenuBar();
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
        final TestPanel contentPane = new TestPanel();
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Thread t = new Thread( new Runnable() {

            public void run() {
                while ( running ) {
                    contentPane.paintImmediately( 0, 0, contentPane.getWidth(), contentPane.getHeight() );
                }
            }
        } );
        t.setPriority( Thread.MAX_PRIORITY );

        frame.setSize( 800, 800 );
        frame.setVisible( true );
        t.start();
        Thread.sleep( 3000 );

        running = false;

    }

    static class TestPanel extends JPanel {

        Random random = new Random();

        protected void paintComponent( Graphics g ) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D) g;

            for ( int i = 0; i < 500; i++ ) {
                g2.setColor( new Color( random.nextFloat(), random.nextFloat(), random.nextFloat() ) );
                g2.setStroke( new BasicStroke( random.nextFloat() * 10 ) );
                g2.drawLine( random.nextInt( getWidth() ), random.nextInt( getHeight() ), random.nextInt( getWidth() ), random.nextInt( getHeight() ) );
            }

        }

    }
}