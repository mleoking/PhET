// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.RadiowaveSource;

/**
 * EnergySquiggleUpdater
 * <p/>
 * Changes the color, length, and transparency of an EnergySquiggle based on the
 * frequency and power of a specified model's radiowave source
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergySquiggleUpdater {

    //    private boolean matched;
    private EnergySquiggle energySquiggle;
    private MriModel model;
    private RadiowaveSource radiowaveSource;

    public EnergySquiggleUpdater( EnergySquiggle energySquiggle, MriModel model ) {
        this.energySquiggle = energySquiggle;
        this.model = model;
        radiowaveSource = model.getRadiowaveSource();
    }

    public void updateSquiggle( double xOffset, double yOffset,
                                double SQUIGGLE_LENGTH_CALIBRATION_FACTOR ) {
        double frequency = radiowaveSource.getFrequency();
        double wavelength = PhysicsUtil.frequencyToWavelength( frequency );

        double transparency = radiowaveSource.getPower() / MriConfig.MAX_POWER;
        energySquiggle.setTransparency( (float)transparency );

        // TODO: the "caibration" numbers here need to be understood and made more systematic
        double length = PhysicsUtil.frequencyToEnergy( frequency ) * SQUIGGLE_LENGTH_CALIBRATION_FACTOR;
        energySquiggle.update( wavelength, 0, (int)length, 10 );
        energySquiggle.setOffset( xOffset,
                                  yOffset - length );

        // Test to see if the squiggle should be flashed
        boolean match = model.isTransitionMatch();
        if( energySquiggle.isMatch() != match ) {
            energySquiggle.setMatch( match );
            energySquiggle.update( wavelength, 0, (int)length, 10 );//redraw the graphics, TODO this duplicates work above
        }
    }

}
