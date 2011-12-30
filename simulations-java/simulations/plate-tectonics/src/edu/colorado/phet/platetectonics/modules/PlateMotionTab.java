// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;

import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionTab extends PlateTectonicsTab {

    public PlateMotionTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.PLATE_MOTION_TAB, 0.5f );
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
        setModel( new PlateMotionModel( grid ) );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        sceneLayer.addChild( new PlateView( getModel(), this, grid ) );
    }
}
