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

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * MoleculeFactory
 * <p/>
 * A factory class that creates instances of Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeFactory {

    /**
     * Creates an instance of a specified class on module
     * 
     * @param moleculeClass
     * @param position
     * @param velocity
     * @return
     */
    public static Molecule createMolecule( Class moleculeClass,
                                           Point2D position,
                                           Vector2D velocity ) {
        Molecule molecule = null;
        if( moleculeClass == MoleculeAB.class ) {
            SimpleMolecule mA = new MoleculeA();
            mA.setPosition( position.getX() - mA.getRadius(), position.getY() );
            mA.setVelocity( velocity );
            SimpleMolecule mB = new MoleculeB();
            mB.setPosition( position.getX() + mB.getRadius(), position.getY() );
            mB.setVelocity( velocity );
            molecule = new MoleculeAB( new SimpleMolecule[]{mA, mB},
                                       new Bond[]{new Bond( mA, mB )} );
        }
        else if( moleculeClass == MoleculeBC.class ) {
            SimpleMolecule mC = new MoleculeC();
            mC.setPosition( position.getX() - mC.getRadius(), position.getY() );
            mC.setVelocity( velocity );
            SimpleMolecule mB = new MoleculeB();
            mB.setPosition( position.getX() + mB.getRadius(), position.getY() );
            mB.setVelocity( velocity );
            molecule = new MoleculeBC( new SimpleMolecule[]{mC, mB},
                                       new Bond[]{new Bond( mC, mB )} );
        }
        else {
            try {
                molecule = (Molecule)moleculeClass.newInstance();
            }
            catch( InstantiationException e ) {
                e.printStackTrace();
            }
            catch( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }

        molecule.setPosition( position );
        molecule.setVelocity( velocity );

        return molecule;
    }
}
