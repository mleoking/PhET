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
import edu.colorado.phet.molecularreactions.view.SpatialView;
import edu.colorado.phet.molecularreactions.view.EnergyView;
import edu.colorado.phet.molecularreactions.model.*;

import java.awt.*;

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
        setModel( model );

        // create the control panel
        setControlPanel( new TestControlPanel( this ) );

        // Create the basic graphics
        PhetPCanvas canvas = new PhetPCanvas( size );
        setSimulationPanel( canvas );

        // Create spatial view
        SpatialView spatialView = new SpatialView( model );
        spatialView.setOffset( 30, 30 );
        canvas.addScreenChild( spatialView );

        // Create energy view
        EnergyView energyView = new EnergyView( model );
        energyView.setOffset( 400, 20 );
        canvas.addScreenChild( energyView );

        // Test
        testDefinedMolecules( model );
    }

    private void testDefinedMolecules( MRModel model ) {
        {
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 120, 160 );
            m1.setVelocity( 5, 3 );
            model.addModelElement( m1 );
        }

        {
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 140, 60 );
            m1.setVelocity( 7, -3 );
            model.addModelElement( m1 );
        }

        {
            SimpleMolecule m1 = new MoleculeB();
            m1.setPosition( 80, 60 );
            m1.setVelocity( 7, -3 );
            model.addModelElement( m1 );
            m1.setSelectionStatus( Selectable.SELECTED );
        }


    }


    private void testSimpleToCompositeMolecules( MRModel model ) {

        // Set up two simple molecules to ram together
        SimpleMolecule sm1 = new SimpleMolecule( 7 );
        sm1.setMass( 49 );
        sm1.setPosition( 100, 110 );
        sm1.setVelocity( 5, 0 );
        model.addModelElement( sm1 );

        SimpleMolecule sm2 = new SimpleMolecule( 10 );
        sm2.setMass( 100 );
        sm2.setPosition( 200, 105 );
        sm2.setVelocity( -0, 0 );
        model.addModelElement( sm2 );

        SimpleMolecule rm3 = new SimpleMolecule( 5 );
        rm3.setMass( 25 );
        rm3.setPosition( 70, 100 );
//        rm3.setPosition( 200, 115 );
        model.addModelElement( rm3 );

    }

    private void testSimpleMoleculesA( MRModel model ) {

        SimpleMolecule rm = new SimpleMolecule( 7 );
        rm.setMass( 49 );
        rm.setPosition( 100, 105 );
        rm.setVelocity( 5, 0 );
        model.addModelElement( rm );

        SimpleMolecule rm2 = new SimpleMolecule( 10 );
        rm2.setMass( 100 );
        rm2.setPosition( 200, 100 );
        rm2.setVelocity( -5, 0 );
        model.addModelElement( rm2 );

        SimpleMolecule rm3 = new SimpleMolecule( 5 );
        rm3.setMass( 25 );
        rm3.setPosition( 200, 115 );
        model.addModelElement( rm3 );
    }


    private void testCompositeMoleculeA( MRModel model ) {

        SimpleMolecule rm2 = new SimpleMolecule( 10 );
        rm2.setMass( 100 );
        rm2.setPosition( 200, 100 );

        SimpleMolecule rm3 = new SimpleMolecule( 5 );
        rm3.setMass( 25 );
        rm3.setPosition( 200, 115 );

        CompositeMolecule cm1 = new CompositeMolecule( new Molecule[]{rm2, rm3} );
        cm1.setVelocity( 1, 0 );
//        cm1.setVelocity( 0, 4);
//        cm1.setOmega( 0 );
        cm1.setOmega( -0.3 );
        model.addModelElement( cm1 );
    }
}
