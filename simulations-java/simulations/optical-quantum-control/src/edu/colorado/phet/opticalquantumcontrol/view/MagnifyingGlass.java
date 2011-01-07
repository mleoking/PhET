// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.Component;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;


/**
 * MagnifyingGlass is the graphical representation of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnifyingGlass extends PhetImageGraphic {

    /**
     * Sole constructor.
     * 
     * @param component
     */
    public MagnifyingGlass( Component component ) {
        super( component, OQCResources.MAGNIFYING_GLASS_IMAGE );
    }
}
