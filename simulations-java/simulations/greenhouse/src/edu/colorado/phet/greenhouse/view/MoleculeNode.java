/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import edu.colorado.phet.greenhouse.model.Atom;
import edu.colorado.phet.greenhouse.model.AtomicBond;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.umd.cs.piccolo.PNode;


/**
 *
 * @author John Blanco
 */
public class MoleculeNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private final PNode atomLayer;
    private final PNode bondLayer;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public MoleculeNode (Molecule molecule){
        bondLayer = new PNode();
        addChild(bondLayer);
        atomLayer = new PNode();
        addChild(atomLayer);
        
        for (Atom atom : molecule.getAtoms()){
            atomLayer.addChild( new AtomNode( atom ) );
        }
        
        for (AtomicBond atomicBond : molecule.getAtomicBonds()){
            bondLayer.addChild( new AtomicBondNode( atomicBond ) );
        }
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
