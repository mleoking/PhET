/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.introduction;

import java.awt.Frame;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Air;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;

/**
 * The "Introduction" module.
 * <p>
 * This module is identical to the "Dielectric" module, except that:
 * <ul>
 * <li>the dielectric is air
 * <li>the dielectric is not visible
 * <li>the dielectric controls are not visible
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionModule extends DielectricModule {
    
    public IntroductionModule( Frame parentFrame, boolean dev ) {
        super( CLStrings.TAB_INTRODUCTION, parentFrame, false /* hasDielectricPropertiesControl */, dev );  // no dielectric properties control
        getDielectricCanvas().setDielectricVisible( false ); // dielectric and its drag handle are not visible
    }
    
    public void reset() {
        super.reset();
        getDielectricModel().getCapacitor().setDielectricMaterial( new Air() );  // dielectric is air
    }
}
