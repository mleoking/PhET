/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.piccolo;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.GaussianWave;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 9:05:08 AM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class SchrodingerCanvas extends PSwingCanvas {
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

        final DiscreteModel discreteModel = new DiscreteModel( 100, 100 );
        discreteModel.fireParticle( new WaveSetup( new GaussianWave( new Point2D.Double( 50, 50 ),
                                                                     new Vector2D.Double( 0, -0.8 ), 7 ) ) );
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

        SchrodingerCanvas schrodingerCanvas = new SchrodingerCanvas( discreteModel );
        schrodingerCanvas.setDefaultRenderQuality( PPaintContext.LOW_QUALITY_RENDERING );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( schrodingerCanvas );

        t.start();

        frame.setSize( 800, 800 );
        frame.setVisible( true );
    }
}
