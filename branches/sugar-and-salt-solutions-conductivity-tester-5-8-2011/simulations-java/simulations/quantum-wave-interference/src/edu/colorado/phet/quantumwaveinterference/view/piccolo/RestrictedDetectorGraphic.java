// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.quantumwaveinterference.model.Detector;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityBeamPanel;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 3:14:15 PM
 */

public class RestrictedDetectorGraphic extends DetectorGraphic {
    public RestrictedDetectorGraphic( IntensityBeamPanel intensityBeamPanel, Detector detector ) {
        super( intensityBeamPanel, detector );

        setCloseButtonVisible( false );
        setPercentDisplayVisible( false );
        setResizeComponentVisible( false );
        setPickable( false );
        setChildrenPickable( false );
    }
}
