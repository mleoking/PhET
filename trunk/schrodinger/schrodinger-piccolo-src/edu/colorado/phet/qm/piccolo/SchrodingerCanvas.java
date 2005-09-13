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
        wavefunctionPGraphic = new WavefunctionPGraphic( this,discreteModel.getWavefunction().getWidth(), discreteModel.getWavefunction().getHeight() );
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

//    private static void enhancePriorities( Thread t ) {
//        t.setPriority( Thread.MIN_PRIORITY );
//        try {
//            SwingUtilities.invokeAndWait( new Runnable() {
//                public void run() {
//                    Thread.currentThread().setPriority( Thread.MAX_PRIORITY );
//                }
//            } );
//        }
//        catch( InterruptedException e ) {
//            e.printStackTrace();
//        }
//        catch( InvocationTargetException e ) {
//            e.printStackTrace();
//        }
//    }
}
