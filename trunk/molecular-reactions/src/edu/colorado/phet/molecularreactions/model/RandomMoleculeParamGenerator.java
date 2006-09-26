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
import edu.colorado.phet.common.math.MathUtil;

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
    private double minTheta;
    private double maxTheta;
    private double maxAlpha;

    /**
     * Generates a Params object for a molecule.
     *
     * @param bounds    The bounds within which the molecule's CM lies
     * @param maxSpeed  Max initial speed
     * @param maxAlpha  Max angular velocity
     * @param minTheta  Min angle of the molecule's initial velocity
     * @param maxTheta  Max angle of the molecule's initial velocity
     */
    public RandomMoleculeParamGenerator( Rectangle2D bounds,
                                         double maxSpeed,
                                         double maxAlpha,
                                         double minTheta,
                                         double maxTheta ) {
        this.bounds = bounds;
        this.maxSpeed = maxSpeed;
        this.minTheta = minTheta;
        this.maxTheta = maxTheta;
        this.maxAlpha = maxAlpha;
    }

    public Params generate() {
        // Generate position
        double x = bounds.getMinX() + random.nextDouble() * bounds.getWidth();
        double y = bounds.getMinY() + random.nextDouble() * bounds.getHeight();
        Point2D p = new Point2D.Double( x, y );

        // Generate velocity
        double phi = ( maxTheta - minTheta ) * random.nextDouble() + minTheta;
        double speed = maxSpeed * random.nextDouble();
        Vector2D v = new Vector2D.Double( speed, 0).rotate( phi );

        // Generate angular velocity
        double a = maxAlpha * random.nextDouble() * MathUtil.nextRandomSign();

        return new Params( p, v, a );
    }
}
