// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;
import java.util.LinkedList;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.Kit;
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

        Kit kit = new Kit( new LinkedList<Bucket>() {{
            add( new Bucket( new Atom.H(), new PDimension( 300, 60 ), "Hydrogen" ) {{
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", 0, 0, MakeMoleculeModule.this.getClock() ), false );
                addAtom( new AtomModel( new Atom.H(), "Hydrogen", 0, 0, MakeMoleculeModule.this.getClock() ), false );
            }} );
            add( new Bucket( new Atom.O(), new PDimension( 140, 60 ), "Oxygen" ) {{
                addAtom( new AtomModel( new Atom.O(), "Oxygen", 0, 0, MakeMoleculeModule.this.getClock() ), false );
            }} );
            add( new Bucket( new Atom.C(), new PDimension( 140, 60 ), "Carbon" ) {{
                addAtom( new AtomModel( new Atom.C(), "Carbon", 0, 0, MakeMoleculeModule.this.getClock() ), false );
            }} );
        }} );

        // Canvas
        canvas = new BuildAMoleculeCanvas( kit );
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
