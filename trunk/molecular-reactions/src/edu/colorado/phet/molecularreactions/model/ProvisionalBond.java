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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.geom.Point2D;

/**
 * ProvisionalBond
 * <p/>
 * A ProvisionalBond is a bond that exists between two SimpleMolecules that are not yet completely bonded, or
 * were bonded and are now separating.
 * <p>
 * This is a ModelElement. At each time step, it checks to see if distance between the molecules it is
 * bonding is less than or equal to the maximum bond length. If it is, the bond removes itself from the
 * model.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBond extends SimpleObservable implements ModelElement {
    SimpleMolecule[] molecules;
    private double maxBondLength;
    private MRModel model;

    public ProvisionalBond( SimpleMolecule sm1, SimpleMolecule sm2, double maxBondLength, MRModel model ) {
        this.maxBondLength = maxBondLength;
        this.model = model;
        molecules = new SimpleMolecule[]{sm1, sm2};
    }

    /**
     * If the molecules in the bond get too far apart the bond should go away
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        double dist = molecules[0].getPosition().distance( molecules[1].getPosition() ) - molecules[0].getRadius() - molecules[1].getRadius();
        if( dist > maxBondLength ) {
            model.removeModelElement( this );
        }
        notifyObservers();
    }

    /**
     * Gets the molecules that participate in the bond
     *
     * @return an array with references to the molecules
     */
    public SimpleMolecule[] getMolecules() {
        return molecules;
    }

    /**
     * Gets the maximum length of the bond
     *
     * @return the max length of the bond
     */
    public double getMaxBondLength() {
        return maxBondLength;
    }
}
