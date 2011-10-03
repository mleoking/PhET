// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.control.MyCrustPanel;
import edu.colorado.phet.platetectonics.model.BlockCrustPlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.renderer.Camera;

// TODO: better name?
public class SinglePlateModule extends PlateTectonicsModule {

    private BlockCrustPlateModel model;

    public SinglePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.SINGLE_PLATE__TITLE );
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
    }

    @Override public void initialize() {
        super.initialize();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -150000, 150000,
                                     -150000, 150000,
                                     -50000, 0 ),
                512, 512, 32 );

        // create the model and terrain
        model = new BlockCrustPlateModel();
        mainView.getScene().attachChild( new PlateView( model, this, grid ) );

        /*---------------------------------------------------------------------------*
        * "Test" GUI
        *----------------------------------------------------------------------------*/

        JMEView guiView = createFrontGUIView( "GUI" );

        // toolbox
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PText( "Toolbox" ) {{
            setFont( new PhetFont( 16, true ) );
            // TODO: create toolbox
        }} ), getInputHandler(), this, canvasTransform ) {{
            position.set( new ImmutableVector2D( 10, 10 ) );
        }} );

        // "my crust" control
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new MyCrustPanel( model ) ), getInputHandler(), this, canvasTransform ) {{
            // layout the panel if its size changes (and on startup)
            onResize.addUpdateListener( new UpdateListener() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from top
                }
            }, true ); // TODO: default to this?
        }} );

        // "oceanic crust" label
        guiView.getScene().attachChild( new PiccoloJMENode( new PText( "Oceanic Crust" ) {{
            setFont( new PhetFont( 16, true ) );
        }}, getInputHandler(), this, canvasTransform ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( 30,
                                                 getStageSize().getHeight() * 0.6 ) );
        }} );

        // "continental crust" label
        guiView.getScene().attachChild( new PiccoloJMENode( new PText( "Continental Crust" ) {{
            setFont( new PhetFont( 16, true ) );
        }}, getInputHandler(), this, canvasTransform ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                                 getStageSize().getHeight() * 0.6 ) );
        }} );
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }

}
