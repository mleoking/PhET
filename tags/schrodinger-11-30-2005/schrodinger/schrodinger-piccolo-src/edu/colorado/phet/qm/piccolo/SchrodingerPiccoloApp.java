/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.GaussianWave;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 13, 2005
 * Time: 12:14:33 AM
 * Copyright (c) Sep 13, 2005 by Sam Reid
 */

public class SchrodingerPiccoloApp {
    private JFrame frame;
    private Thread thread;
    private PActivity pActivity;

    public SchrodingerPiccoloApp( String[] args ) {

        PhetLookAndFeel.setLookAndFeel();
        final DiscreteModel discreteModel = new DiscreteModel(20,20);
        discreteModel.fireParticle( new WaveSetup( new GaussianWave( new Point2D.Double( 50, 50 ),
                                                                     new Vector2D.Double( 0, -0.9 ), 7 ) ) );
        thread = new Thread( new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        discreteModel.stepInTime( 1.0 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );

        pActivity = new PActivity( -1, 30 ) {
                    protected void activityStep( long elapsedTime ) {
                        super.activityStep( elapsedTime );
                        discreteModel.stepInTime( 1.0 );
                    }
                };

        SchrodingerCanvas schrodingerCanvas = new SchrodingerCanvas( discreteModel );
        schrodingerCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( schrodingerCanvas );
        schrodingerCanvas.getRoot().addActivity( pActivity );
        frame.setSize( 800, 800 );
    }

    public static void main( String[] args ) {
        new SchrodingerPiccoloApp( args ).start();
    }

    private void start() {
        frame.setVisible( true );
//        thread.start();
        
    }

}
