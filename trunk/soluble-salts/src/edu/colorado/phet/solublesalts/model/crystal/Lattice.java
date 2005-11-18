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

import edu.colorado.phet.solublesalts.model.Ion;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;

/**
 * Lattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface Lattice {

    /**
     *
     * @param seedIon
     */
    void setSeed( Ion seedIon );

    /**
     *
     * @param bounds
     */
    void setBounds( Rectangle2D bounds );

    /**
     * Returns the location of the open lattice point that is nearest to a specified ion
     *
     * @param ion
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    Point2D getNearestOpenSite( Ion ion, List ionsInLattice, double orientation );

    /**
     * Returns the ion with the greatest number of unoccupied neighboring lattice sites. The
     * seed ion is not eligible for consideration.
     *
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    Ion getLeastBoundIon( List ionsInLattice, double orientation );

    /**
     * Returns a list of the lattice sites that are neighboring a specified ion that are
     * not occupied.
     *
     * @param ion
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    List getOpenNeighboringSites( Ion ion, List ionsInLattice, double orientation );
}


