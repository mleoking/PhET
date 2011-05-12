// Copyright 2002-2011, University of Colorado
package com.pixelzoom.dbi;

import java.awt.*;

import javax.swing.*;

/**
 * Simple example that uses double-brace initialization (DBI).
 * Compiles to 6 class files, total 3517 bytes.
 * This is 385% larger than the version of this example without DBI.
 * Startup time is about 10% longer (average of 10 runs).
 * <p/>
 * % javac -version
 * javac 1.6.0_24
 * % javac WithDBI.java
 * % ls -l WithDBI*.class
 * -rw-r--r--  1 cmalley  staff  581 May 12 10:22 WithDBI$1$1$1.class
 * -rw-r--r--  1 cmalley  staff  583 May 12 10:22 WithDBI$1$1$2.class
 * -rw-r--r--  1 cmalley  staff  582 May 12 10:22 WithDBI$1$1$3.class
 * -rw-r--r--  1 cmalley  staff  784 May 12 10:22 WithDBI$1$1.class
 * -rw-r--r--  1 cmalley  staff  622 May 12 10:22 WithDBI$1.class
 * -rw-r--r--  1 cmalley  staff  365 May 12 10:22 WithDBI.class
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WithDBI extends JPanel {

    public static void main( String[] args ) {
        long tStart = System.currentTimeMillis();
        JFrame frame = new JFrame() {{
            setContentPane( new JPanel() {{
                add( new JLabel( "red" ) {{
                    setForeground( Color.RED );
                }} );
                add( new JLabel( "green" ) {{
                    setForeground( Color.GREEN );
                }} );
                add( new JLabel( "blue" ) {{
                    setForeground( Color.BLUE );
                }} );
            }} );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
            setVisible( true );
        }};
        long tEnd = System.currentTimeMillis();
        System.out.println( "startup time = " + ( tEnd - tStart ) + " ms" );
    }
}
