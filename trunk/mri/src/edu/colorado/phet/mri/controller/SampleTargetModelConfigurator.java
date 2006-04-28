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

import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.SampleTarget;

/**
 * SampleTargetModelConfigurator
 * <p>
 * Sets the model parameters to resonate for the sample material at a SampleTarget point
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleTargetModelConfigurator implements SampleTarget.ChangeListener {
    MriModel model;

    public SampleTargetModelConfigurator( MriModel model ) {
        this.model = model;
    }

    private void setRadioFrequency( SampleTarget sampleTarget ) {
        double hEnergy = PhysicsUtil.frequencyToEnergy( model.getTotalFieldStrengthAt( sampleTarget.getLocation() ) * model.getSampleMaterial().getMu() );
        model.getRadiowaveSource().setFrequency( PhysicsUtil.energyToFrequency( hEnergy ));
    }

    public void stateChanged( SampleTarget.ChangeEvent event ) {
        setRadioFrequency( event.getSampleTarget() );
    }
}
