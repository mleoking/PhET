// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.platetectonics.util.JMEModelViewTransform;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;

import com.jme3.math.Matrix4f;
import com.jme3.renderer.Camera;

/**
 * General plate tectonics module that consolidates common behavior between the various tabs
 */
public abstract class PlateTectonicsModule extends JMEModule {
    protected PlateTectonicsJMEApplication app;
    protected CenteredStageCanvasTransform canvasTransform;

    private JMEModelViewTransform modelViewTransform;

    public PlateTectonicsModule( Frame parentFrame, String name ) {
        super( parentFrame, name, new ConstantDtClock( 30.0 ) );

        // TODO: better initialization for this model view transform (for each module)
        modelViewTransform = new JMEModelViewTransform( Matrix4f.IDENTITY.mult( 0.002f ) ); // 0.5 km => 1 distance in view
    }

    public PlateTectonicsJMEApplication getApp() {
        return app;
    }

    @Override public PhetJMEApplication createApplication( Frame parentFrame ) {
        app = new PlateTectonicsJMEApplication( parentFrame );
        return app;
    }

    public JMEModelViewTransform getModelViewTransform() {
        return modelViewTransform;
    }

    // camera to use for debugging purposes
    public Camera getDebugCamera() {
        return null;
    }
}
