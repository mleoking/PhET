// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;

public class DoublePlateModule extends PlateTectonicsModule {

    private PlateTectonicsJMEApplication app;

    public DoublePlateModule( Frame parentFrame ) {
        super( parentFrame, Strings.DOUBLE_PLATE__TITLE );
    }

    public PlateTectonicsJMEApplication getApp() {
        return app;
    }

    @Override public void initialize() {
    }

    @Override public PhetJMEApplication createApplication( Frame parentFrame ) {
        app = new PlateTectonicsJMEApplication( parentFrame );
        return app;
    }
}
