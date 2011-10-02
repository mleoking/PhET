// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.test.VerySimplePlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.renderer.Camera;

public class SinglePlateModule extends PlateTectonicsModule {

    private PlateModel model;
    private PlateView plateView;

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
        super.initialize();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D( new Bounds3D(
                -100000,
                -100000,
                -50000,
                200000,
                200000,
                50000 ), 512, 512, 32 );

        // create the model and terrain
        model = new VerySimplePlateModel();
        plateView = new PlateView( model, this, grid );
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
