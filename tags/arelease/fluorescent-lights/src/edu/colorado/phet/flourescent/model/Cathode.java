/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;

/**
 * Cathode
 * <p>
 * The model element that emits electrons
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Cathode extends ElectronSource {
    private double potential;

    /**
     * Emits electrons along a line between two points
     * @param model
     * @param p1 One endpoint of the line
     * @param p2 The other endpoint of the line
     */
    public Cathode( BaseModel model, Point2D p1, Point2D p2 ) {
        super( model, p1, p2 );
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public double getPotential() {
        return potential;
    }

    public void setPotential( double potential ) {
        this.potential = potential;
    }

}
