// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

/**
 * A molecule that behaves with a behavior that doesn't discriminate between bond or atom types (only lone pairs vs bonds)
 */
public class VSEPRMolecule extends Molecule {
    @Override public LocalShape getLocalShape( PairGroup atom ) {
        return getLocalVSEPRShape( atom );
    }

    @Override public boolean isReal() {
        return false;
    }
}
