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

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * RandomMoleculeParamGenerator
 * <p>
 * Generates a random position and velocity within specified rectangular bounds
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RandomMoleculeParamGenerator implements MoleculeParamGenerator {
    private static Random random = new Random();

    private Rectangle2D bounds;
    private double maxSpeed;

    public RandomMoleculeParamGenerator( Rectangle2D bounds, double maxSpeed ) {
        this.bounds = bounds;
        this.maxSpeed = maxSpeed;
    }

    public Params generate() {
        // Generate position
        double x = bounds.getMinX() + random.nextDouble() * bounds.getWidth();
        double y = bounds.getMinY() + random.nextDouble() * bounds.getHeight();
        Point2D p = new Point2D.Double( x, y );

        // Generate velocity
        double theta = Math.PI * 2 * random.nextDouble();
        double speed = maxSpeed * random.nextDouble();
        Vector2D v = new Vector2D.Double( speed, 0).rotate( theta );

        return new Params( p, v );
    }
}
