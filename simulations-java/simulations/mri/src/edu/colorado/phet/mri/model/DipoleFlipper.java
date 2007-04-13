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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.quantum.model.Photon;

/**
 * DipoleFlipper
 * <p/>
 * Flips a dipole's spin after a specified timeout
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleFlipper implements ModelElement {
    private Dipole dipole;
    private Spin targetSpin;
    private double timeout;
    private MriModel model;
    private boolean emitPhoton;
    private long elapsedTime;

    /**
     * @param dipole
     * @param timeout
     * @param model
     * @param emitPhoton Flag to determine if a photon should be produced if the dipole flips to
     *                   the lower energy state
     */
    DipoleFlipper( Dipole dipole, Spin targetSpin, double timeout, MriModel model, boolean emitPhoton ) {
        this.dipole = dipole;
        this.targetSpin = targetSpin;
        this.timeout = timeout;
        this.model = model;
        this.emitPhoton = emitPhoton;
    }

    /**
     * @param dt
     */
    public void stepInTime( double dt ) {

        // If we've time out, flip the dipole
        elapsedTime += dt;
        if( elapsedTime >= timeout ) {
            // Flip the dipole's spine
            if( dipole.getSpin() != targetSpin ) {
                dipole.setSpin( targetSpin );

                // Create a photon if appropriate
                if( emitPhoton && targetSpin == Spin.DOWN ) {
                    emitPhoton();
                }
            }
            // Remove us from the model
            model.removeModelElement( this );
        }
    }

    /**
     * Emit a photon
     */
    private void emitPhoton() {
        Vector2D velocity = new Vector2D.Double( MriConfig.EMITTED_PHOTON_DIRECTION ).normalize().scale( Photon.SPEED );
        double wavelength = PhysicsUtil.frequencyToWavelength( model.getLowerMagnet().getFieldStrength() * model.getSampleMaterial().getMu() );
        MriEmittedPhoton photon = new MriEmittedPhoton();
        photon.setWavelength( wavelength );
        photon.setPosition( dipole.getPosition() );
        photon.setVelocity( velocity );
        model.addModelElement( photon );

        // Create an EM wave and a medium to carry it through the model
        PlaneWaveCycle waveCycle = new PlaneWaveCycle( dipole.getPosition(), 10,
                                                       new Vector2D.Double( 1, 0 ) );
        waveCycle.setWavelength( PhysicsUtil.frequencyToWavelength( 42E6 ) );
        waveCycle.setPower( MriConfig.MAX_POWER );
        model.addModelElement( waveCycle );
        PlaneWaveMedium planeWaveMedium = new PlaneWaveMedium( waveCycle,
                                                               waveCycle.getPosition(),
                                                               20,
                                                               model.getBounds().getMaxX() - waveCycle.getPosition().getX(),
                                                               PlaneWaveMedium.EAST,
                                                               10 );
        model.addModelElement( planeWaveMedium );

        // Add a listener to the photon that will remove the wave representation from the model when
        // the photon leaves the model
        photon.addLeftSystemListener( new ModelElementRemover( waveCycle, planeWaveMedium ) );
    }

    private class ModelElementRemover implements Photon.LeftSystemEventListener {
        private PlaneWaveCycle waveCycle;
        private PlaneWaveMedium planeWaveMedium;

        public ModelElementRemover( PlaneWaveCycle waveCycle, PlaneWaveMedium planeWaveMedium ) {
            this.waveCycle = waveCycle;
            this.planeWaveMedium = planeWaveMedium;
        }

        public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
            model.removeModelElement( planeWaveMedium );
            model.removeModelElement( waveCycle );
        }
    }
}