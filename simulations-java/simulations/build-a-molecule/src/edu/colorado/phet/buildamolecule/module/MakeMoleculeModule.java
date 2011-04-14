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

        // TODO: improve model construction
        final KitCollectionModel initialModel = new KitCollectionModel( availableKitBounds ) {{
            addKit( new Kit( new LinkedList<Bucket>() {{
                add( new Bucket( new Atom.H(), new PDimension( 400, 200 ), "Hydrogen" ) {{
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.O(), new PDimension( 450, 200 ), "Oxygen" ) {{
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                }} );
            }}, availableKitBounds ) );

            addKit( new Kit( new LinkedList<Bucket>() {{
                add( new Bucket( new Atom.C(), new PDimension( 350, 200 ), "Carbon" ) {{
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.O(), new PDimension( 450, 200 ), "Oxygen" ) {{
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.N(), new PDimension( 500, 200 ), "Nitrogen" ) {{
                    addAtom( new AtomModel( new Atom.N(), "Nitrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.N(), "Nitrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
            }}, availableKitBounds ) );

            /*---------------------------------------------------------------------------*
            * example kits
            *----------------------------------------------------------------------------*/
            addKit( new Kit( new LinkedList<Bucket>() {{
                add( new Bucket( new Atom.H(), new PDimension( 400, 200 ), "Hydrogen" ) {{
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.O(), new PDimension( 450, 200 ), "Oxygen" ) {{
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.O(), "Oxygen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.C(), new PDimension( 500, 200 ), "Carbon" ) {{
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.N(), new PDimension( 500, 200 ), "Nitrogen" ) {{
                    addAtom( new AtomModel( new Atom.N(), "Nitrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.N(), "Nitrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
            }}, availableKitBounds ) );
            addKit( new Kit( new LinkedList<Bucket>() {{
                add( new Bucket( new Atom.H(), new PDimension( 400, 200 ), "Hydrogen" ) {{
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.F(), new PDimension( 500, 200 ), "Fluorine" ) {{
                    addAtom( new AtomModel( new Atom.F(), "Fluorine", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.F(), "Fluorine", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.Cl(), new PDimension( 600, 200 ), "Chlorine" ) {{
                    addAtom( new AtomModel( new Atom.Cl(), "Chlorine", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.Cl(), "Chlorine", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.C(), new PDimension( 350, 200 ), "Carbon" ) {{
                    addAtom( new AtomModel( new Atom.C(), "Carbon", MakeMoleculeModule.this.getClock() ), false );
                }} );
            }}, availableKitBounds ) );
            addKit( new Kit( new LinkedList<Bucket>() {{
                add( new Bucket( new Atom.H(), new PDimension( 400, 200 ), "Hydrogen" ) {{
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                    addAtom( new AtomModel( new Atom.H(), "Hydrogen", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.B(), new PDimension( 350, 200 ), "Boron" ) {{
                    addAtom( new AtomModel( new Atom.B(), "Boron", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.S(), new PDimension( 350, 200 ), "Sulphur" ) {{
                    addAtom( new AtomModel( new Atom.S(), "Sulphur", MakeMoleculeModule.this.getClock() ), false );
                }} );
                add( new Bucket( new Atom.Si(), new PDimension( 500, 200 ), "Silicon" ) {{
                    addAtom( new AtomModel( new Atom.Si(), "Silicon", MakeMoleculeModule.this.getClock() ), false );
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
