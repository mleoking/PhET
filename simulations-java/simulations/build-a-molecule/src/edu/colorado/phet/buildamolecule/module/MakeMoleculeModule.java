// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;
import java.util.LinkedList;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.util.PDimension;

public class MakeMoleculeModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BuildAMoleculeCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MakeMoleculeModule( Frame parentFrame ) {
        super( BuildAMoleculeStrings.TITLE_MAKE_MOLECULE, new ConstantDtClock( 30 ) );

        // TODO: add in model

        setClockControlPanel( null );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollectionModel initialModel = new KitCollectionModel();

        initialModel.addKit( new Kit( initialModel, new LinkedList<Bucket>() {{
            add( new Bucket( new Atom.H(), new PDimension( 300, 100 ), "Hydrogen" ) {{
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock(), initialModel ), false );
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock(), initialModel ), false );
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock(), initialModel ), false );
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock(), initialModel ), false );
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock(), initialModel ), false );
            }} );
            add( new Bucket( new Atom.O(), new PDimension( 300, 100 ), "Oxygen" ) {{
                addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock(), initialModel ), false );
            }} );
            add( new Bucket( new Atom.C(), new PDimension( 300, 100 ), "Carbon" ) {{
                addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock(), initialModel ), false );
            }} );
        }} ) );

        initialModel.addCollectionBox( new CollectionBox( CompleteMolecule.H2O, 1 ) );
        initialModel.addCollectionBox( new CollectionBox( CompleteMolecule.O2, 1 ) );
        initialModel.addCollectionBox( new CollectionBox( CompleteMolecule.H2, 1 ) );
        initialModel.addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 1 ) );
        initialModel.addCollectionBox( new CollectionBox( CompleteMolecule.N2, 1 ) );

        /*---------------------------------------------------------------------------*
        * canvas
        *----------------------------------------------------------------------------*/
        canvas = new BuildAMoleculeCanvas( initialModel, true ); // single collection mode
        setSimulationPanel( canvas );

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
        // TODO: global reset entry point
    }
}
