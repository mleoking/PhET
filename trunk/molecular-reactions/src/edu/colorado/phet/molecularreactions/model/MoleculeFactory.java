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
 * <p>
 * A factory class that creates instances of Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeFactory {

    public static Molecule createMolecule( Class moleculeClass,
                                           Point2D position,
                                           Vector2D velocity ) {
        Molecule molecule = null;
        try {
            molecule = (Molecule)moleculeClass.newInstance();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }

        molecule.setPosition( position );
        molecule.setVelocity( velocity );

        return molecule;
    }
}
