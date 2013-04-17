// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test;

import java.awt.*;

import javax.swing.*;

import org.jfree.ui.RefineryUtilities;

public class TestProgressBar extends JFrame {

    public static final int MAX = 100;

    public JProgressBar bar;

    public TestProgressBar() throws HeadlessException {
        super( "TestProgressBar" );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );

        bar = new JProgressBar( 0, MAX );
        panel.add( bar );

        System.out.println( bar.getPreferredSize() );

        bar.setPreferredSize( new Dimension( (int) 50, (int) bar.getPreferredSize().getHeight() ) );

        System.out.println( bar.getPreferredSize() );

        setContentPane( panel );

    }

    public static void main( String[] args ) {
        final TestProgressBar demo = new TestProgressBar();
        demo.pack();
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );

        final int[] val = new int[]{0};


        ( new Thread() {
            @Override
            public void run() {
                try {
                    while ( true ) {
                        Thread.sleep( 1000 / 25 );

                        val[0]++;

                        if ( val[0] > MAX ) { val[0] = 0; }

                        demo.bar.setValue( val[0] );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } ).start();
    }
}
