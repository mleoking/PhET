/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.coreadditions.CubicUtil;
import edu.colorado.phet.nuclearphysics.Config;

import java.awt.*;
import java.awt.geom.*;

/**
 * This class represents the energy profile of a particular atom.
 * <p/>
 * It is based on the following model:
 * <pre>
 * PE = (k * q1 * q2 ) / r
 * where:
 *  q1 = charge of an alpha particle (= 2 protons * 1.6E-19 / proton)
 *  q2 = charge of the rest of the nucleus (= total number of protons -2 )
 *  r = distance from center of nucleus, and r > nominal radius of nucleus
 * <p/>
 *  when r < nominal radius of nucleus, PE = -100 for now
 * <p/>
 *  according to Kathy Perkins' notes, k = 8.99E3
 * </pre>
 * <p>
 * <h2>IMPORTANT NOTE!!!</h2>
 * <p>
 * Currently, this class is sort of screwed up between model and view units. Internally,
 * all computations are done in model units. All getters, however, report things in
 * view units. This should definitely be changed so that everything comes out in model units.
 *
 */
public class EnergyProfile implements IEnergyProfile {

    public  static final double alphaParticleKE = 7.595;

    private double width;
    private double maxEnergy;
    private double minEnergy;
    private Shape[] shape = new Shape[5];
    private GeneralPath potentilaProfilePath;
    private Shape totalEnergyPath;
    private CubicUtil cubicUtil;
    private ProfileableNucleus nucleus;
    private double k;
    private double alphaParticleCharge;

    /**
     * @param nucleus
     */
    public EnergyProfile( ProfileableNucleus nucleus ) {
        this.nucleus = nucleus;
        this.generate();
        this.width = Config.defaultProfileWidth * 2;
    }

    /**
     * I made up the function. It is constructed to make the level drop when the min energy drops
     *
     * @return
     */
    public double getTotalEnergy() {
//        double energy = alphaParticleKE * Config.modelToViewMeV;
        double energy = ( 100 + minEnergy + alphaParticleKE ) * Config.modelToViewMeV;
        return energy;
    }

    public double getWidth() {
        return width;
    }

    public double getMinEnergy() {
        return minEnergy * Config.modelToViewDist;
    }

    public double getMaxEnergy() {
        return maxEnergy * Config.modelToViewDist;
    }

    private double getPe( double x ) {
        double nucleausRadiusTemp = 7.12E-15;
        k = 8.99E3;
        alphaParticleCharge = 2 * 1.6E-19;
        double minPE = -100;

        double xAbs = Math.abs( x );
        if( xAbs < nucleausRadiusTemp ) {
            return minPE;
        }
        else {
            double r = xAbs;
            double pe = ( k * ( nucleus.getNumProtons() - 2 ) * alphaParticleCharge ) / r;
            double y = pe;
            return y;
        }
    }

    /**
     * Returns the distance from the center of the nucleus of a specified potential energy.
     *
     * @return Nan if the specified pe is out of range for the profile
     */
    private double getR( double pe ) {
        if( pe < minEnergy || pe > maxEnergy ) {
            return Double.NaN;
        }
        else if( pe == minEnergy ) {
            return 0;
        }
        else {
            return ( k * ( nucleus.getNumProtons() - 2 ) * alphaParticleCharge ) / pe;
        }
    }

    /**
     * Generates the cubic splines that define the energy profile. Also computes
     * the x coordinate of the hill slope that is at the same y coordinat as the
     * bottom of the well.
     */
    private void generate() {

        // Width of entire profile, in pixels
        double profileWidth = 800;
        double nucleausRadiusTemp = 7.12E-15;
        minEnergy = nucleus.getMinPotentialEnergy();

        // Draw the right side of the profile
        GeneralPath potentialPath = new GeneralPath();
        potentialPath.moveTo( 0, (float)( -minEnergy * Config.modelToViewMeV ) );
        potentialPath.lineTo( (float)( nucleausRadiusTemp * Config.modelToViewDist ),
                              (float)( -minEnergy * Config.modelToViewMeV ) );
        maxEnergy = minEnergy;
        for( double r = nucleausRadiusTemp;
             r < profileWidth / Config.modelToViewDist / 2;
             r += 3 / Config.modelToViewDist ) {
            double y = getPe( r );
            maxEnergy = Math.max( y, maxEnergy );
            potentialPath.lineTo( (float)( r * Config.modelToViewDist ),
                                  (float)( -y * Config.modelToViewMeV ) );
        }

        // Make a copy of the path and reflect it horizontally
        GeneralPath otherHalfOfPath = new GeneralPath( potentialPath );
        otherHalfOfPath.transform( AffineTransform.getScaleInstance( -1, 1 ) );

        potentialPath.append( otherHalfOfPath, false );
        potentilaProfilePath = potentialPath;

        // Generate the line for the total energy
        totalEnergyPath = new Line2D.Double( -(profileWidth / 2),
                                             -getTotalEnergy(),
                                             profileWidth / 2,
                                             -getTotalEnergy() );
    }

    public GeneralPath getPotentialEnergyPath() {
        return potentilaProfilePath;
    }

    public Shape getTotalEnergyPath() {
        return totalEnergyPath;
    }

    public double getAlphaDecayX() {
        return getR( getTotalEnergy() / Config.modelToViewMeV ) * Config.modelToViewDist;
    }

    /**
     *
     */
    public Shape[] getShape() {
        return shape;
    }

    /**
     * Gives the y coordinate of the hill-side of the profile that
     * corresponds to a particular x coordinate.
     * <p/>
     * See: http://www.moshplant.com/direct-or/bezier/math.html
     *
     * @param x
     * @return
     */
    public double getHillY( double x ) {
        return getPe( x / Config.modelToViewDist );
    }

    public double getDyDx( double v ) {
        double dyDx = cubicUtil.dyDx( v );
        return Double.isNaN( dyDx ) ? 0 : dyDx;
    }
}
