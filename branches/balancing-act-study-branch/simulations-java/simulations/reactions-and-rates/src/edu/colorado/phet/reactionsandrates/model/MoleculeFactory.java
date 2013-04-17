// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

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
     * @param moleculeParamGenerator
     * @return a molecule
     */
    public static AbstractMolecule createMolecule( Class moleculeClass,
                                                   MoleculeParamGenerator moleculeParamGenerator ) {
        AbstractMolecule molecule = null;
        MoleculeParamGenerator.Params params = moleculeParamGenerator.generate();
        Point2D position = params.getPosition();
        MutableVector2D velocity = params.getVelocity();
        double angularVelocity = params.getAngularVelocity();
        if ( moleculeClass == MoleculeAB.class ) {
            SimpleMolecule mA = new MoleculeA();
//            mA.setPosition( position.getX() - mA.getRadius(), position.getY() );
            mA.setVelocity( velocity );
            SimpleMolecule mB = new MoleculeB();
//            mB.setPosition( position.getX() + mB.getRadius(), position.getY() );
            mB.setVelocity( velocity );
            molecule = new MoleculeAB( new SimpleMolecule[] { mA, mB } );
            molecule.setOmega( angularVelocity );

            double d = getComponentMoleculesOffset( mA, mB );
            mA.setPosition( position.getX() - d / 2, position.getY() );
            mB.setPosition( position.getX() + d / 2, position.getY() );
        }
        else if ( moleculeClass == MoleculeBC.class ) {
            SimpleMolecule mC = new MoleculeC();
//            mC.setPosition( position.getX() - mC.getRadius(), position.getY() );
            mC.setVelocity( velocity );
            SimpleMolecule mB = new MoleculeB();
//            mB.setPosition( position.getX() + mB.getRadius(), position.getY() );
            mB.setVelocity( velocity );
            molecule = new MoleculeBC( new SimpleMolecule[] { mC, mB } );
            molecule.setOmega( angularVelocity );

            double d = getComponentMoleculesOffset( mC, mB );
            mC.setPosition( position.getX() - d / 2, position.getY() );
            mB.setPosition( position.getX() + d / 2, position.getY() );
        }
        else {
            try {
                molecule = (AbstractMolecule) moleculeClass.newInstance();
            }
            catch ( InstantiationException e ) {
                e.printStackTrace();
            }
            catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }

        molecule.setPosition( position );
        molecule.setVelocity( velocity );

        return molecule;
    }

    public static double getComponentMoleculesOffset( SimpleMolecule mA, SimpleMolecule mB ) {
        return Math.max( mA.getRadius(), mB.getRadius() );
    }
}
