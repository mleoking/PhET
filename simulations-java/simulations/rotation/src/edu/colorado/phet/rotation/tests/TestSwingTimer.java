package edu.colorado.phet.rotation.tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Created by: Sam
 * Nov 12, 2007 at 5:48:19 PM
 */
public class TestSwingTimer {
    static long lastEndTime;

    public static void main( String[] args ) throws InterruptedException {
        Timer timer = new Timer( 10, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                long betweenTime=System.currentTimeMillis()-lastEndTime;
                System.out.print( "betweenTime = " + betweenTime+", " );
                try {
                    Thread.sleep( 10 );
                }
                catch( InterruptedException e1 ) {
                    e1.printStackTrace();
                }
                long tickTime=System.currentTimeMillis()-lastEndTime;
                System.out.println( "tickTime = " + tickTime );
                lastEndTime = System.currentTimeMillis();
            }
        } );
        timer.start();
        Thread.sleep(5000);
        System.exit( 0);
    }
}
