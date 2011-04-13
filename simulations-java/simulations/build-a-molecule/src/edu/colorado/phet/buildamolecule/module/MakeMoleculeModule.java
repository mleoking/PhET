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
import edu.umd.cs.piccolo.util.PBounds;
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

        setClockControlPanel( null );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final PBounds availableKitBounds = new PBounds( -1600, -1000, 2200, 500 );

        final KitCollectionModel initialModel = new KitCollectionModel( availableKitBounds ) {{
            addKit( new Kit( new LinkedList<Bucket>() {{
                add( new Bucket( new Atom.H(), new PDimension( 625, 200 ), "Hydrogen" ) {{
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.O(), new PDimension( 625, 200 ), "Oxygen" ) {{
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.C(), new PDimension( 625, 200 ), "Carbon" ) {{
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                }} );
            }}, availableKitBounds ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2O, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.O2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.N2, 1 ) );
        }};

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
