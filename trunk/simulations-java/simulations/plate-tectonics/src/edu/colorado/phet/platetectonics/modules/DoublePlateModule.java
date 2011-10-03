// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Frame;

import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.test.AnimatedPlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;

import com.jme3.renderer.Camera;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.TWO_PLATES;

// TODO: better name?
public class DoublePlateModule extends PlateTectonicsModule {

    private PlateModel model;

    public DoublePlateModule( Frame parentFrame ) {
        super( parentFrame, TWO_PLATES );
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
    }

    @Override public void initialize() {
        super.initialize();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -100000, 100000,
                                     -100000, 100000,
                                     -50000, 0 ),
                512, 512, 32 );

        // create the model and terrain
        model = new AnimatedPlateModel();
        mainView.getScene().attachChild( new PlateView( model, this, grid ) );
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }
}
