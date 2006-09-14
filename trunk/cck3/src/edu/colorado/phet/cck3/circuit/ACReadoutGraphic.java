/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.model.Branch;
import edu.colorado.phet.cck3.model.components.ACVoltageSource;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 12:42:14 PM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class ACReadoutGraphic extends ReadoutGraphic {
    private ACVoltageSource ac;

    public ACReadoutGraphic( CCKModule module, Branch branch, ModelViewTransform2D transform, ApparatusPanel panel, DecimalFormat formatter ) {
        super( module, branch, transform, panel, formatter );
        this.ac = (ACVoltageSource)branch;
        recompute();
    }

    protected String[] getText() {
        if( ac == null ) {
            return new String[0];
        }
        String amp = super.format( ac.getAmplitude() ) + " V";
        String freq = format( ac.getFrequency() ) + " Hz";
        return new String[]{amp, freq};
    }
}
