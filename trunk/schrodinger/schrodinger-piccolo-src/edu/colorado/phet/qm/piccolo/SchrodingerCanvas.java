/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.GaussianWave;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 9:05:08 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class SchrodingerCanvas extends PhetPCanvas {
    private DiscreteModel discreteModel;
    private WavefunctionPGraphic wavefunctionPGraphic;
    private GunPGraphic gunPGraphic;
    private ScreenPGraphic screenPGraphic;

    public SchrodingerCanvas( DiscreteModel discreteModel ) {
        this.discreteModel = discreteModel;
        wavefunctionPGraphic = new WavefunctionPGraphic( this );
        addChild( wavefunctionPGraphic );

        gunPGraphic = new GunPGraphic( this );
        addChild( gunPGraphic );

        screenPGraphic = new ScreenPGraphic();
        addChild( screenPGraphic );
    }

    private void addChild( PNode node ) {
        getLayer().addChild( node );
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public WavefunctionPGraphic getWavefunctionPGraphic() {
        return wavefunctionPGraphic;
    }

    public GunPGraphic getGunPGraphic() {
        return gunPGraphic;
    }

    public ScreenPGraphic getScreenPGraphic() {
        return screenPGraphic;
    }

    public static void main( String[] args ) {
        PhetLookAndFeel.setLookAndFeel();
        final DiscreteModel discreteModel = new DiscreteModel();
        discreteModel.fireParticle( new WaveSetup( new GaussianWave( new Point2D.Double( 50, 50 ),
                                                                     new Vector2D.Double( 0, -0.9 ), 7 ) ) );
        Thread t = new Thread( new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                    discreteModel.stepInTime( 1.0 );
                }
            }
        } );

        PActivity pActivity = new PActivity( -1, 30 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                discreteModel.stepInTime( 1.0 );
            }
        };


//        enhancePriorities( t );
        SchrodingerCanvas schrodingerCanvas = new SchrodingerCanvas( discreteModel );
//        schrodingerCanvas.setDebugRegionManagement( true );
        schrodingerCanvas.setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( schrodingerCanvas );

//        t.start();
        discreteModel.setWaveSize( 60, 60 );
        schrodingerCanvas.getRoot().addActivity( pActivity );
        frame.setSize( 800, 800 );
        frame.setVisible( true );
    }

    private static void enhancePriorities( Thread t ) {
        t.setPriority( Thread.MIN_PRIORITY );
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    Thread.currentThread().setPriority( Thread.MAX_PRIORITY );
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
}
