// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.moleculeshapes.jme.JMEModule;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;

import com.jme3.app.Application;

/**
 * Main module for Molecule Shapes
 */
public class MoleculeShapesModule extends JMEModule {

    private MoleculeJMEApplication app;

    public MoleculeShapesModule( Frame parentFrame, String name ) {
        super( parentFrame, name, new ConstantDtClock( 30.0 ) );

        // listen to resize events on our canvas, so that we can update our layout
        getCanvas().addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                app.onResize( getCanvas().getSize() );
            }
        } );
    }

    public MoleculeJMEApplication getApp() {
        return app;
    }

    @Override public Application createApplication( Frame parentFrame ) {
        app = new MoleculeJMEApplication( parentFrame );
        return app;
    }
}
