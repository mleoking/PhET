// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;

import com.jme3.app.Application;

/**
 * Main module for Molecule Shapes
 */
public class MoleculeShapesModule extends JMEModule {

    private MoleculeJMEApplication app;

    public MoleculeShapesModule( Frame parentFrame, String name ) {
        super( parentFrame, name, new ConstantDtClock( 30.0 ) );
    }

    public MoleculeJMEApplication getApp() {
        return app;
    }

    @Override public Application createApplication( Frame parentFrame ) {
        app = new MoleculeJMEApplication( parentFrame );
        return app;
    }
}
