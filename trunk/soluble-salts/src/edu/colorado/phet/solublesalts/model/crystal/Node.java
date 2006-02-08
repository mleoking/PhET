/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Node
 * <p/>
 * Represents an atom in a crystal lattice. A Node has a list of Bonds associate with it
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Node {
    private Ion ion;
    private List bonds = new ArrayList();

    public Node( Ion ion ) {
        this.ion = ion;
    }

    public Ion getIon() {
        return ion;
    }

    public int getPolarity() {
        return (int)( ion.getCharge() / Math.abs( ion.getCharge() ) );
    }

    public List getBonds() {
        return bonds;
    }

    public void addBond( Bond bond ) {
        bonds.add( bond );
    }

    public Point2D getPosition() {
        return ion.getPosition();
    }

    /**
     * Sets the theta of all the node's bond to be based off a given
     * angle.
     *
     * @param theta
     */

    public void setBaseOrientation( double theta ) {
        // Get the current base theta of the bonds
        double baseOrientation = ( (Bond)bonds.get( 0 ) ).getOrientation();

        // Rotate all the bonds' theta to be based off the new one specified
        // in the parameter
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            bond.setOrientation( bond.getOrientation() - baseOrientation + theta );
        }
    }

    public int getNumOpenBonds() {
        int cnt = 0;
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            cnt += bond.isOpen() ? 1 : 0;
        }
        return cnt;
    }

    /**
     * Returns a double that characterizes how strongly the node it bound. It is the quotient of
     * the number of open bonds whose open ends are within specified bounds divided by the
     * node's total number of bonds. A low binding metric indicates a tightly bound node.
     *
     * @param bounds
     * @return
     */
    public double getBindingMetric( Rectangle2D bounds ) {
        double numOpenBonds = 0;
        for( int i = 0; i < bonds.size(); i++ ) {
            Bond bond = (Bond)bonds.get( i );
            numOpenBonds += bond.isOpen() && bounds.contains( bond.getOpenPosition() ) ? 1 : 0;
        }
        return numOpenBonds / bonds.size();
    }
}
