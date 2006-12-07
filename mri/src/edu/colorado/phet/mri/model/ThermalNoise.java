/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.mri.MriConfig;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * ThermalNoise
 * <p/>
 * Injects thermal noise into the system by flipping dipoles at random times,
 * then telling the DipoleOrientationAgent to rebalance. Dipoles are randomly
 * selected in a way that maintains the ratio of up to down dipoles as closely
 * as possible.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThermalNoise implements ModelElement {

    private static final int UP = 1, DOWN = 2;

    private Random random = new Random();
    private double minMeanInjectionTime;
    private double meanInjectionTime;
    private double timeToInjection = 0;
    private double elapsedTime = 0;
    private MriModel model;


    public ThermalNoise( double minMeanInjectionTime, MriModel model ) {
        this.minMeanInjectionTime = minMeanInjectionTime;
        meanInjectionTime = minMeanInjectionTime;
        this.model = model;
        setInjectionTime();
        setMeanInjectionTime();

        model.getLowerMagnet().addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                setMeanInjectionTime();
            }
        } );
    }

    private void setMeanInjectionTime() {
        double b = model.getTotalFieldStrengthAt( new Point2D.Double( 0, 0 ) );
        // Mean time to injection is 5x longer when magnetic field is at max
        meanInjectionTime = minMeanInjectionTime * ( 1 + 5 * b / MriConfig.MAX_FADING_COIL_FIELD );
        setInjectionTime();
    }

    /**
     * Determines the elapsed time before noice is next injected.
     */
    private void setInjectionTime() {
        double sigma = 0.5;
        timeToInjection = meanInjectionTime * ( 1 + random.nextGaussian() * sigma );
    }

    /**
     * Flips an appropriate dipole when the time is right
     */
    public void stepInTime( double dt ) {
        elapsedTime += dt;
        if( elapsedTime >= timeToInjection ) {
            elapsedTime = 0;
            setInjectionTime();

            // Flip a random dipole
            double desiredFractionDown = model.determineDesiredFractionDown();
            double fractionDown = ( (double)model.getDownDipoles().size() ) / model.getDipoles().size();
            Dipole dipole = null;
            if( fractionDown > desiredFractionDown && model.getDownDipoles().size() > 0 ) {
                dipole = (Dipole)model.getDownDipoles().get( random.nextInt( model.getDownDipoles().size() ) );
            }
            else if( model.getUpDipoles().size() > 0 ) {
                dipole = (Dipole)model.getUpDipoles().get( random.nextInt( model.getUpDipoles().size() ) );
            }
            if( dipole != null ) {
                dipole.flip();
            }
        }
    }
}
