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
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * ConstantTemperatureMoleculeParamGenerator
 * <p/>
 * Generates a random position and direction of travel within specified rectangular bounds.
 * The kinetic energy of the molecule is specified which, in conjuction with the molecule's
 * mass, determines the magnitude of its velocity.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConstantTemperatureMoleculeParamGenerator implements MoleculeParamGenerator {
    private static Random random = new Random();
    private static AbstractMolecule prototypeA = new MoleculeA();
    private static AbstractMolecule prototypeB = new MoleculeB();
    private static AbstractMolecule prototypeC = new MoleculeC();

    private Rectangle2D bounds;
    private MRModel model;
    private double minTheta;
    private double maxTheta;
    private double maxAlpha;
    private double mass = 0;

    /**
     * Generates a Params object for a molecule.
     *
     * @param bounds        The bounds within which the molecule's CM lies
     * @param model         A reference to the model, from which we will get the current temperature
     * @param maxAlpha      Max angular velocity
     * @param minTheta      Min angle of the molecule's initial velocity
     * @param maxTheta      Max angle of the molecule's initial velocity
     * @param moleculeType  The type of the molecule for which params are to be generated.
     *                      This provides the mass needed to determine the velocity.
     */
    public ConstantTemperatureMoleculeParamGenerator( Rectangle2D bounds,
                                                      MRModel model,
                                                      double maxAlpha,
                                                      double minTheta,
                                                      double maxTheta,
                                                      Class moleculeType ) {
        this.bounds = bounds;
        this.model = model;
        this.minTheta = minTheta;
        this.maxTheta = maxTheta;
        this.maxAlpha = maxAlpha;

        if( moleculeType == MoleculeA.class ) {
            mass = prototypeA.getMass();
        }
        if( moleculeType == MoleculeBC.class ) {
            mass = prototypeB.getMass() + prototypeC.getMass();
        }
        if( moleculeType == MoleculeAB.class ) {
            mass = prototypeA.getMass() + prototypeB.getMass();
        }
        if( moleculeType == MoleculeC.class ) {
            mass = prototypeC.getMass();
        }
    }

    public Params generate() {
        // Generate position
        double x = bounds.getMinX() + random.nextDouble() * bounds.getWidth();
        double y = bounds.getMinY() + random.nextDouble() * bounds.getHeight();
        Point2D p = new Point2D.Double( x, y );

        // Generate velocity
        double speed = Math.sqrt( 2 * model.getTemperature() / mass );
        double phi = ( maxTheta - minTheta ) * random.nextDouble() + minTheta;
        Vector2D v = new Vector2D.Double( speed, 0 ).rotate( phi );

        // Generate angular velocity
        double a = 0;
//        double a = maxAlpha * random.nextDouble() * MathUtil.nextRandomSign();

        return new Params( p, v, a );
    }
}
