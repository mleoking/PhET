/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.model.BaseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SaltMolecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SaltMolecule extends Molecule {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------

    private static Random random = new Random( System.currentTimeMillis() );
    private static double dissociationLikelihood;

    public static void setDissociationLikelihood( double dissociationLikelihood ) {
        SaltMolecule.dissociationLikelihood = dissociationLikelihood;
    }

    private BaseModel model;

    public SaltMolecule( BaseModel model ) {
        this.model = model;
    }

    public SaltMolecule( ArrayList atoms, BaseModel model ) {
        super( atoms );
        this.model = model;
    }

    public double getDissociationLikelihood() {
        return dissociationLikelihood;
    }

    /**
     * If the molecule choses to dissociate, it releases its bound atoms and removes
     * itself from the model
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        if( random.nextDouble() < dissociationLikelihood ) {
            List atoms = getAtoms();
//            for( int i = 0; i < atoms.size(); i++ ) {
//                Atom atom = (Atom)atoms.get( i );
//                atom.setIsBound( false);
//            }
//            atoms.clear();
//            model.removeModelElement( this );
//            if( true ) return;

            // Choose an ion to release from the molecule, and reverse its momentum so it
            // doesn't just immediately get stuck again
//            int atomToReleaseIdx = random.nextInt( atoms.size() );

            int atomToReleaseIdx = atoms.size() - 1;
            Atom releasedAtom = (Atom)atoms.remove( atomToReleaseIdx );
            releasedAtom.setVelocity( -releasedAtom.getVelocity().getX(),
                                      -releasedAtom.getVelocity().getY() );
            releasedAtom.unbindFrom( this );
            releasedAtom.stepInTime( dt );
//            releasedAtom.setIsBound( false );
            if( atoms.size() <= 1 ) {
                if( atoms.size() == 1 ) {
                    ( (Atom)atoms.get( 0 ) ).unbindFrom( this );
//                    ((Atom)atoms.get( 0 )).setIsBound( false);
                }
                atoms.clear();
                model.removeModelElement( this );
            }
        }
        else {
            super.stepInTime( dt );
        }
    }
}
