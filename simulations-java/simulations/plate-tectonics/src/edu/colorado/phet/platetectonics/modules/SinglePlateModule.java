// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.PhetCamera;
import edu.colorado.phet.jmephet.PhetCamera.CenteredStageCameraStrategy;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.AnimatedPlateModel;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;
import edu.colorado.phet.platetectonics.view.TerrainNode;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class SinglePlateModule extends JMEModule {

    private PlateTectonicsJMEApplication app;
    private CenteredStageCanvasTransform canvasTransform;
    private TerrainNode terrainNode;
    private AnimatedPlateModel model;

    public SinglePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.SINGLE_PLATE__TITLE, new ConstantDtClock( 30.0 ) );
    }

    public PlateTectonicsJMEApplication getApp() {
        return app;
    }

    @Override public void updateState( float tpf ) {
        super.updateState( tpf );
        model.update( tpf );
//        terrainNode.rotate( tpf, 0, 0 );
    }

    @Override public void initialize() {
        canvasTransform = new CenteredStageCanvasTransform( getApp() );

        JMEView mainView = createMainView( "Main", new PhetCamera( getStageSize(), new CenteredStageCameraStrategy( 45, 1, 1000 ) ) {{
            setLocation( new Vector3f( 0, 100, 400 ) );
            lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );
        }} );

        PlateTectonicsJMEApplication.addLighting( mainView.getScene() );
        model = new AnimatedPlateModel();
        terrainNode = new TerrainNode( model, this );
        mainView.getScene().attachChild( terrainNode );
        final float tempWaterSize = 500;
        mainView.getScene().attachChild( new Geometry( "Water", new Quad( tempWaterSize, tempWaterSize, true ) ) {{
            setMaterial( new Material( getAssetManager(), "Common/MatDefs/Light/Lighting.j3md" ) {{
                setBoolean( "UseMaterialColors", true );

                setColor( "Diffuse", new ColorRGBA( 0, 0.5f, 1f, 0.5f ) );
//            setColor( "Diffuse", MoleculeShapesConstants.LONE_PAIR_SHELL_COLOR );
                setFloat( "Shininess", 1f ); // [0,128]

                // allow transparency
                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
            setLocalTranslation( -tempWaterSize / 2, 0, 0 );
            rotate( -FastMath.PI / 2, 0, 0 );
        }} );

        JMEView guiView = createFrontGUIView( "GUI" );

        Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PText( "Toolbox" ) {{
            setFont( new PhetFont( 16, true ) );
        }} ), getInputHandler(), this, canvasTransform, position ) ); // TODO: use module input handler
    }

    @Override public PhetJMEApplication createApplication( Frame parentFrame ) {
        app = new PlateTectonicsJMEApplication( parentFrame );
        return app;
    }
}
