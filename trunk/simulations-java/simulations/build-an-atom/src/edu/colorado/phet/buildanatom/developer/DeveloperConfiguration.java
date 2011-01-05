/* Copyright 2011, University of Colorado */

package edu.colorado.phet.buildanatom.developer;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;

/**
 * This class contains properties that affect aspects of the simulation's
 * behavior and that can be configured through the developer controls.
 *
 * @author John Blanco
 */
public class DeveloperConfiguration {

    public static final BooleanProperty animateUnstableNucleusProperty = new BooleanProperty( true );
}
