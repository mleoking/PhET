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

import java.util.Random;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * ConstantTemperatureMoleculeParamGenerator
 * <p>
 * Generates a random position and direction of travel within specified rectangular bounds.
 * The kinetic energy of the molecule is specified which, in conjuction with the molecule's
 * mass, determines the magnitude of its velocity.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConstantTemperatureMoleculeParamGenerator implements MoleculeParamGenerator {
    private static Random random = new Random();

    private Rectangle2D bounds;
    private double speed;
    private double minTheta;
    private double maxTheta;
    private double maxAlpha;
    private AbstractMolecule prototypeMolecule;

    /**
     * Generates a Params object for a molecule.
     *
     * @param bounds    The bounds within which the molecule's CM lies
     * @param kineticEnergy  Kinetic energy of the molecule whose parameters are generated
     * @param maxAlpha  Max angular velocity
     * @param minTheta  Min angle of the molecule's initial velocity
     * @param maxTheta  Max angle of the molecule's initial velocity
     * @param prototypeMolecule  A prototype for the molecules for which parameters are generated. This
     *                          provides the mass needed to determine the velocity
     */
    public ConstantTemperatureMoleculeParamGenerator( Rectangle2D bounds,
                                         double kineticEnergy,
                                         double maxAlpha,
                                         double minTheta,
                                         double maxTheta,
                                         AbstractMolecule prototypeMolecule ) {
        this.bounds = bounds;
        this.speed = Math.sqrt( 2 * kineticEnergy / prototypeMolecule.getMass() );
        this.minTheta = minTheta;
        this.maxTheta = maxTheta;
        this.maxAlpha = maxAlpha;
        this.prototypeMolecule = prototypeMolecule;
    }

    public Params generate() {
        // Generate position
        double x = bounds.getMinX() + random.nextDouble() * bounds.getWidth();
        double y = bounds.getMinY() + random.nextDouble() * bounds.getHeight();
        Point2D p = new Point2D.Double( x, y );

        // Generate velocity
        double phi = ( maxTheta - minTheta ) * random.nextDouble() + minTheta;
        Vector2D v = new Vector2D.Double( speed, 0).rotate( phi );

        // Generate angular velocity
        double a = maxAlpha * random.nextDouble() * MathUtil.nextRandomSign();

        return new Params( p, v, a );
    }
}
