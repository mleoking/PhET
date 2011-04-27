// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.introduction;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;

/**
 * The "Introduction" module.
 * <p/>
 * This module is identical to the "Dielectric" module, except that:
 * <ul>
 * <li>the dielectric is not visible
 * <li>the dielectric controls are not visible
 * <li>the dielectric is fully outside the capacitor at all times
 * <li>the E-Field Detector is simplified (fewer controls, fewer vectors visible)
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends DielectricModule {

    public IntroductionModule( CLGlobalProperties globalProperties ) {
        super( CLStrings.INTRODUCTION, globalProperties );
        setDielectricVisible( false );
        setDielectricPropertiesControlPanelVisible( false );
        setEFieldDetectorSimplified( true );
    }

    @Override
    public void reset() {
        super.reset();
        setDielectricOffset( CLConstants.PLATE_WIDTH_RANGE.getMax() + 1 ); // move dielectric outside plates
    }
}
