/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.introduction;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;

/**
 * The "Introduction" module.
 * <p>
 * This module is identical to the "Dielectric" module, except that:
 * <ul>
 * <li>the dielectric is not visible
 * <li>the dielectric controls are not visible
 * <li>the dielectric is fully outside the capacitor at all times
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends DielectricModule {
    
    public IntroductionModule( Frame parentFrame, boolean dev ) {
        super( CLStrings.TAB_INTRODUCTION, parentFrame, dev );
        setDielectricVisible( false );
        setDielectricPropertiesControlPanelVisible( false );
    }
    
    @Override
    public void reset() {
        super.reset();
        setDielectricOffset( CLConstants.PLATE_SIZE_RANGE.getMax() + 1 ); // move dielectric outside plates
    }
}
