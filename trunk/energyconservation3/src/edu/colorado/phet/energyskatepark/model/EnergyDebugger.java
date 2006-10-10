/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.model;

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

    public static void stepStarted( EnergyConservationModel model, Body body, double dt ) {
        if( showEnergy ) {
            if( !frame.isVisible() ) {
                frame.setVisible( true );
            }
            origEnergy = model.getTotalMechanicalEnergy( body );
            orig.setText( "orig=" + origEnergy );
        }
    }

    public static void stepFinished( EnergyConservationModel model, Body body ) {
        double finalEnergy = model.getTotalMechanicalEnergy( body );
        double dE = finalEnergy - origEnergy;
    }

    public static void postProcessed( EnergyConservationModel model, Body body, double dt, String name ) {
    }
}
