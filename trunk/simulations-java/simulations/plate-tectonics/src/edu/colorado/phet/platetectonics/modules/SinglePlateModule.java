// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.PhetCamera;
import edu.colorado.phet.jmephet.PhetCamera.CenteredStageCameraStrategy;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.test.AnimatedPlateModel;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class SinglePlateModule extends PlateTectonicsModule {

    private AnimatedPlateModel model;
    private PlateView plateView;
    private JMEView mainView;

    public SinglePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.SINGLE_PLATE__TITLE );
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
        plateView.updateView();
//        terrainNode.rotate( tpf, 0, 0 );
    }

    @Override public void initialize() {
        canvasTransform = new CenteredStageCanvasTransform( getApp() );

        /*---------------------------------------------------------------------------*
        * temporary test scene
        *----------------------------------------------------------------------------*/
        mainView = createMainView( "Main", new PhetCamera( getStageSize(), new CenteredStageCameraStrategy( 45, 1, 1000 ) ) {{
            setLocation( new Vector3f( 0, 100, 400 ) );
            lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );
        }} );

        // light it
        PlateTectonicsJMEApplication.addLighting( mainView.getScene() );

        // create the model and terrain
        model = new AnimatedPlateModel();
        plateView = new PlateView( model, this );
        mainView.getScene().attachChild( plateView );

        /*---------------------------------------------------------------------------*
        * "Test" GUI
        *----------------------------------------------------------------------------*/

        JMEView guiView = createFrontGUIView( "GUI" );

        Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PText( "Toolbox" ) {{
            setFont( new PhetFont( 16, true ) );
        }} ), getInputHandler(), this, canvasTransform, position ) ); // TODO: use module input handler
    }

    @Override public Camera getDebugCamera() {
        return mainView.getCamera();
    }
}
