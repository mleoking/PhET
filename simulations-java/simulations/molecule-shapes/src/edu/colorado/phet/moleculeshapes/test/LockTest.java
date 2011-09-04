// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.test;

import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

public class LockTest {
    public static void main( String[] args ) {
        new Thread() {
            @Override public void run() {
                try {
                    Thread.sleep( 1000 );
                    SwingUtilities.invokeAndWait( new Runnable() {
                        public void run() {
                            System.out.println( "Hello World!" );
                        }
                    } );
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
                catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        }.start();

        System.exit( 0 );
    }
}
