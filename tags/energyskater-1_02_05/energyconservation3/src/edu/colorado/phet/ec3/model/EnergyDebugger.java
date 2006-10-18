/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 10:49:38 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class EnergyDebugger {
    private static double origEnergy;
    static JFrame frame = new JFrame();
    static PCanvas pCanvas = new PCanvas();
    static PText orig = new PText( "orig=" );
    private static boolean showEnergy = false;

    static {
        frame.setSize( 400, 400 );
        frame.setContentPane( pCanvas );
        pCanvas.getLayer().addChild( orig );
    }

    public static void stepStarted( Body body, double dt ) {
        origEnergy = new State( body ).getTotalEnergy();
        if( showEnergy ) {
            if( !frame.isVisible() ) {
                frame.setVisible( true );
            }

            orig.setText( "orig=" + origEnergy );
        }
    }

    public static void stepFinished( Body body ) {
        double dE = new State( body ).getTotalEnergy() - origEnergy;
        if( Math.abs( dE ) > 10E-6 ) {
            System.out.println( "STEP FINISHED dE = " + dE + ", totalEnergy=" + new State( body ).getTotalEnergy() );
        }
    }

    public static void postProcessed( EnergyConservationModel model, Body body, double dt, String name ) {
    }
}
