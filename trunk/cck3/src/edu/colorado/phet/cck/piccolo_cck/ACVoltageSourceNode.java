package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.ACVoltageSource;

/**
 * User: Sam Reid
 * Date: Sep 20, 2006
 * Time: 8:59:30 AM
 * Copyright (c) Sep 20, 2006 by Sam Reid
 */

public class ACVoltageSourceNode extends ComponentImageNode {
    public ACVoltageSourceNode( CCKModel model, ACVoltageSource acVoltageSource ) {
        super( model, acVoltageSource, CCKImageSuite.getInstance().getACVoltageSourceImage() );
    }
}
