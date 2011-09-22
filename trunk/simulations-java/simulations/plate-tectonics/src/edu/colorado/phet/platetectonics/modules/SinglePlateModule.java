// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;

import com.jme3.app.Application;

public class SinglePlateModule extends JMEModule {

    private PlateTectonicsJMEApplication app;

    public SinglePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.SINGLE_PLATE__TITLE, new ConstantDtClock( 30.0 ) );
    }

    public PlateTectonicsJMEApplication getApp() {
        return app;
    }

    @Override public Application createApplication( Frame parentFrame ) {
        app = new PlateTectonicsJMEApplication( parentFrame );
        return app;
    }
}
