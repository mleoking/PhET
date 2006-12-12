/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

/**
 * CollisionParams
 * <p>
 * A data structure class that provides important parameters for a
 * collision between two molecules
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class CollisionParams {
    private AbstractMolecule am1;
    private AbstractMolecule am2;
    private SimpleMolecule freeMolecule;
    private SimpleMolecule bMolecule;
    private CompositeMolecule compositeMolecule;

    public CollisionParams( AbstractMolecule am1, AbstractMolecule am2 ) {
        this.am1 = am1;
        this.am2 = am2;

        if( am1 instanceof CompositeMolecule ) {
            compositeMolecule = (CompositeMolecule)am1;
        }
        else if( am1 instanceof SimpleMolecule ) {
            freeMolecule = (SimpleMolecule)am1;
        }
        else {
            throw new IllegalArgumentException( "internal error");
        }

        if( am2 instanceof CompositeMolecule ) {
            compositeMolecule = (CompositeMolecule)am2;
        }
        else if( am2 instanceof SimpleMolecule ) {
            freeMolecule = (SimpleMolecule)am1;
        }
        else {
            throw new IllegalArgumentException( "internal error");
        }

        if( compositeMolecule.getComponentMolecules()[0] instanceof MoleculeB ) {
            bMolecule = compositeMolecule.getComponentMolecules()[0];
        }
        else            if( compositeMolecule.getComponentMolecules()[1] instanceof MoleculeB ) {
            bMolecule = compositeMolecule.getComponentMolecules()[1];
        }
        else {
            throw new IllegalArgumentException( "internal error");
        }
    }

    public SimpleMolecule getFreeMolecule() {
        return freeMolecule;
    }

    public SimpleMolecule getbMolecule() {
        return bMolecule;
    }

    public CompositeMolecule getCompositeMolecule() {
        return compositeMolecule;
    }
}
