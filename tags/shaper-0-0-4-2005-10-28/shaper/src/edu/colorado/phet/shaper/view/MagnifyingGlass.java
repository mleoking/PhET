/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.Component;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.shaper.ShaperConstants;


/**
 * MagnifyingGlass
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnifyingGlass extends PhetImageGraphic {

    public MagnifyingGlass( Component component ) {
        super( component, ShaperConstants.MAGNIFYING_GLASS_IMAGE );
    }
}
