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

import edu.colorado.phet.mri.MriConfig;

import java.util.List;
import java.util.Random;

/**
 * DipoleOrientationAgent
 * <p/>
 * Sets the spins of all dipoles in the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleOrientationAgent implements Electromagnet.ChangeListener {
    private MriModel model;
    private SpinDeterminationPolicy spinDeterminationPolicy = MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY;

    /**
     * Constructor
     *
     * @param model
     */
    public DipoleOrientationAgent( MriModel model ) {
        this.model = model;
    }

    private void updateSpins() {
        spinDeterminationPolicy.setSpins( model );
    }

    /**
     * When the field changes, the number of dipoles with each spin changes
     *
     * @param event
     */
    public void stateChanged( Electromagnet.ChangeEvent event ) {
        updateSpins();
    }

    public void setPolicy( SpinDeterminationPolicy policy ) {
        spinDeterminationPolicy = policy;
    }

    //----------------------------------------------------------------
    // Strategies for determining the spins of dipoles in the model
    //----------------------------------------------------------------

    public static interface SpinDeterminationPolicy {
        void setSpins( MriModel model );
    }

    /**
     * Sets a fixed number of dipoles to each orientation
     */
    public static class DeterministicPolicy implements SpinDeterminationPolicy {
        Random random = new Random();
        long meanFlipTimout = 100;

        public void setSpins( MriModel model ) {

            List dipoles = model.getDipoles();
            if( dipoles.size() > 0 ) {
                // Determine how many dipoles should be spin up. There must always be at least 1 up
                double fractionUp = 1 - model.determineDesiredFractionDown();
                double minNumUp = dipoles.size() * 0.1;
                int desiredNumUp = (int)Math.round(Math.max( fractionUp * dipoles.size(), minNumUp ));

                // Flip dipoles until we get the desired number spin up
                while( model.getUpDipoles().size() > desiredNumUp ) {
                    Dipole dipole = (Dipole)model.getUpDipoles().get( random.nextInt( model.getUpDipoles().size() ) );
                    dipole.flip();
                }

                while( model.getUpDipoles().size() < desiredNumUp ) {
                    Dipole dipole = (Dipole)model.getDownDipoles().get( random.nextInt( model.getDownDipoles().size() ) );
                    dipole.flip();
                }
            }
        }
    }


    /**
     *
     */
    public static class StocasticPolicy implements SpinDeterminationPolicy {
        Random random = new Random();

        public void setSpins( MriModel model ) {
            double fractionUp = model.determineDesiredFractionDown();
            List dipoles = model.getDipoles();
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                boolean up = fractionUp > random.nextDouble();
                if( up && dipole.getSpin() != Spin.UP ) {
                    dipole.setSpin( Spin.UP );
                }
                else if( !up && dipole.getSpin() != Spin.DOWN ) {
                    dipole.setSpin( Spin.DOWN );
                }
            }
        }
    }
}
