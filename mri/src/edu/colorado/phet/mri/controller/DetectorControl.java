/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.MriConfig;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * PrecessionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DetectorControl extends ModelSlider {
    private static double maxPeriod = MriConfig.DETECTOR_DEFAULT_PERIOD * 2;

    public DetectorControl( final HeadModule module ) {
        super( "Detector period", "msec (sim)", 0, maxPeriod, MriConfig.DETECTOR_DEFAULT_PERIOD );

        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.getDetector().setDetectingPeriod( getValue() );
            }
        } );
    }
}
