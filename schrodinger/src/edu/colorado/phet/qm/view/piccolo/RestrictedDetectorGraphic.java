/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 3:14:15 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class RestrictedDetectorGraphic extends DetectorGraphic {
    public RestrictedDetectorGraphic( HighIntensitySchrodingerPanel highIntensitySchrodingerPanel, Detector detector ) {
        super( highIntensitySchrodingerPanel, detector );

        setCloseButtonVisible( false );
        setPercentDisplayVisible( false );
        setResizeComponentVisible( false );
        setPickable( false );
        setChildrenPickable( false );
    }
}
