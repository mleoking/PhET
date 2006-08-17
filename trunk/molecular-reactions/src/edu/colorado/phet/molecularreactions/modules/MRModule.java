/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.molectularreactions.view.SpatialView;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.RoundMolecule;
import edu.colorado.phet.collision.Box2D;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModule extends Module {

    private Dimension size = new Dimension( 600, 500 );

    public MRModule() {
        super( SimStrings.get( "Module-1.title" ),
               new SwingClock( 40, 1 ) );

        // Create the model
        MRModel model = new MRModel( getClock() );

        // Create basic graphic
        PhetPCanvas canvas = new PhetPCanvas( size );
        SpatialView spatialView = new SpatialView( model );
        spatialView.setOffset( 100, 100 );
        canvas.addScreenChild( spatialView );
        setSimulationPanel( canvas );

        // Add a test molecule
        RoundMolecule rm = new RoundMolecule( 7 );
        rm.setMass( 10 );
        rm.setPosition( 100, 105);
        rm.setVelocity( 5, 0 );
        model.addModelElement( rm );

        RoundMolecule rm2 = new RoundMolecule( 7 );
        rm2.setMass( 5  );
        rm2.setPosition( 200, 100);
        rm2.setVelocity( -5, 0 );
        model.addModelElement( rm2 );
    }

}
