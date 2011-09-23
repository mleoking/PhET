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
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;
import edu.umd.cs.piccolo.nodes.PText;

public class SinglePlateModule extends JMEModule {

    private PlateTectonicsJMEApplication app;
    private CenteredStageCanvasTransform canvasTransform;

    public SinglePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.SINGLE_PLATE__TITLE, new ConstantDtClock( 30.0 ) );
    }

    public PlateTectonicsJMEApplication getApp() {
        return app;
    }

    @Override public void initialize() {
        canvasTransform = new CenteredStageCanvasTransform( getApp() );

        JMEView guiView = createFrontGUIView( "GUI" );

        Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
        guiView.getScene().attachChild( new PiccoloJMENode( new ControlPanelNode( new PText( "Toolbox" ) {{
            setFont( new PhetFont( 16, true ) );
        }} ), getApp().getDirectInputHandler(), this, canvasTransform, position ) ); // TODO: use module input handler
    }

    @Override public PhetJMEApplication createApplication( Frame parentFrame ) {
        app = new PlateTectonicsJMEApplication( parentFrame );
        return app;
    }
}
