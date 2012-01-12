// Copyright 2002-2011, University of Colorado
package com.pixelzoom.dbi;

import java.awt.*;

import javax.swing.*;

/**
 * Simple example that does not use double-brace initialization (DBI).
 * Compiles to 1 class file, total 913 bytes.
 * <p/>
 * % javac -version
 * javac 1.6.0_24
 * % javac WithoutDBI.java
 * % ls -l WithoutDBI*.class
 * -rw-r--r--  1 cmalley  staff  913 May 12 10:22 WithoutDBI.class
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WithoutDBI extends JPanel {

    public WithoutDBI() {

        JLabel label1 = new JLabel( "red" );
        label1.setForeground( Color.RED );

        JLabel label2 = new JLabel( "green" );
        label2.setForeground( Color.GREEN );

        JLabel label3 = new JLabel( "blue" );
        label3.setForeground( Color.BLUE );

        add( label1 );
        add( label2 );
        add( label3 );
    }

    public static void main( String[] args ) {
        long tStart = System.currentTimeMillis();
        JFrame frame = new JFrame();
        frame.setContentPane( new WithoutDBI() );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
        long tEnd = System.currentTimeMillis();
        System.out.println( "startup time = " + ( tEnd - tStart ) + " ms" );
    }
}
