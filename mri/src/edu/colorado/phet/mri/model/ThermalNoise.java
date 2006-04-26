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

import java.util.Random;
import java.util.List;

/**
 * ThermalNoise
 * <p>
 * Injects thermal noise into the system by flipping random dipoles at random times,
 * then telling the DipoleOrientationAgent to rebalance.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThermalNoise implements ModelElement {
    private Random random = new Random();
    private double meanInjectionTime = 500;
    private double timeToInjection = 0;
    private double elapsedTime = 0;
    private DipoleOrientationAgent dipoleOrientationAgent;
    private MriModel model;

    public ThermalNoise( double meanInjectionTime, DipoleOrientationAgent dipoleOrientationAgent, MriModel model ) {
        this.meanInjectionTime = meanInjectionTime;
        this.dipoleOrientationAgent = dipoleOrientationAgent;
        this.model = model;
        setInjectionTime();
    }

    private void setInjectionTime() {
        timeToInjection = meanInjectionTime * Math.abs(random.nextGaussian());
        System.out.println( "timeToInjection = " + timeToInjection );
    }

    public void stepInTime( double dt ) {
        elapsedTime+=dt;
        if( elapsedTime >= timeToInjection ) {
            elapsedTime = 0;

            // Flip a random dipole
            Spin spinToChange = random.nextBoolean() ? Spin.UP : Spin.DOWN;
            List dipoles = model.getDipoles();
            boolean flipped = false;
            for( int i = 0; i < dipoles.size() && !flipped; i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                if( dipole.getSpin() == spinToChange ) {
                    Spin newSpin = spinToChange == Spin.UP ? Spin.DOWN : Spin.UP;
                    dipole.setSpin( newSpin );
                    flipped = true;
                }
            }
        }
    }
}
