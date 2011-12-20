// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;

import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionTab extends PlateTectonicsTab {

    private PlateModel model;

    public PlateMotionTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.PLATE_MOTION_TAB, 0.5f );
    }

    @Override public void start() {
        super.start();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -700000, 700000,
                                     -400000, 15000,
                                     -250000, 0 ),
                256, 256, 32 );

        // create the model and terrain
//        model = new AnimatedPlateModel( grid );
        model = new PlateMotionModel( grid );

        final GLNode sceneLayer = new GLNode() {
            @Override protected void preRender( GLOptions options ) {
                loadCameraMatrices();
                glEnable( GL_DEPTH_TEST );
            }

            @Override protected void postRender( GLOptions options ) {
                glDisable( GL_DEPTH_TEST );
            }
        };
        final GuiNode guiLayer = new GuiNode( this );
        rootNode.addChild( sceneLayer );
        rootNode.addChild( guiLayer );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        sceneLayer.addChild( new PlateView( model, this, grid ) );
    }


    @Override public void loop() {
        super.loop();
        model.update( getTimeElapsed() );
    }
}
