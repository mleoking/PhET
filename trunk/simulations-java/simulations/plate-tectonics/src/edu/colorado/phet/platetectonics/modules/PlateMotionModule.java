// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.test.AnimatedPlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;

import com.jme3.renderer.Camera;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.PLATE_MOTION_TAB;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionModule extends PlateTectonicsModule {

    private PlateModel model;

    public PlateMotionModule( Frame parentFrame ) {
        super( parentFrame, PLATE_MOTION_TAB, 0.5f );
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
    }

    @Override public void initialize() {
        super.initialize();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -700000, 700000,
                                     -400000, 15000,
                                     -250000, 0 ),
                256, 256, 32 );

        // create the model and terrain
//        model = new AnimatedPlateModel( grid );
        model = new PlateMotionModel( grid );
        mainView.getScene().attachChild( new PlateView( model, this, grid ) );
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }
}
