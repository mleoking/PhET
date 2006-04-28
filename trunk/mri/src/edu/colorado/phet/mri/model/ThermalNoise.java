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

import java.util.Random;
import java.util.List;

/**
 * ThermalNoise
 * <p>
 * Injects thermal noise into the system by flipping dipoles at random times,
 * then telling the DipoleOrientationAgent to rebalance. Dipoles are randomly
 * selected in a way that maintains the ratio of up to down dipoles as closely
 * as possible.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThermalNoise implements ModelElement {
    private Random random = new Random();
    private double meanInjectionTime;
    private double timeToInjection = 0;
    private double elapsedTime = 0;
    private MriModel model;

    public ThermalNoise( double meanInjectionTime, MriModel model ) {
        this.meanInjectionTime = meanInjectionTime;
        this.model = model;
        setInjectionTime();
    }

    private void setInjectionTime() {
//        timeToInjection = meanInjectionTime * random.nextDouble();
        timeToInjection = meanInjectionTime * ( 1 + random.nextGaussian());
    }

    public void stepInTime( double dt ) {
        elapsedTime+=dt;
        if( elapsedTime >= timeToInjection ) {
            elapsedTime = 0;
            setInjectionTime();

            // Flip a random dipole
            Spin spinToChange = random.nextBoolean() ? Spin.UP : Spin.DOWN;
            List dipoles = model.getDipoles();
            boolean flipped = false;
            int attempts = 0;
            while( !flipped && attempts++ < dipoles.size() ) {
                Dipole dipole = (Dipole)dipoles.get( random.nextInt( dipoles.size()) );
                if( dipole.getSpin() == spinToChange ) {
                    Spin newSpin = spinToChange == Spin.UP ? Spin.DOWN : Spin.UP;
                    dipole.setSpin( newSpin );
                    flipped = true;
                }
            }
        }
    }
}
