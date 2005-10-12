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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

/**
 * Ion
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Ion extends Particle {

    private double charge;

    public Ion( double charge ) {
        this.charge = charge;
    }

    public Ion( Point2D position, Vector2D velocity, Vector2D acceleration, double charge ) {
        super( position, velocity, acceleration );
        this.charge = charge;
    }

    public double getCharge() {
        return charge;
    }
}
