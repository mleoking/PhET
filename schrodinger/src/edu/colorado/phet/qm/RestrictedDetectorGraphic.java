/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.view.DetectorGraphic;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 3:14:15 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class RestrictedDetectorGraphic extends DetectorGraphic {
    public RestrictedDetectorGraphic( IntensityPanel intensityPanel, Detector detector ) {
        super( intensityPanel, detector );

        setCloseButtonVisible( false );
        setPercentDisplayVisible( false );
        setResizeComponentVisible( false );
        setIgnoreMouse( true );
    }
}
