/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;

import edu.colorado.phet.faraday.model.Electromagnet;


/**
 * ElectromagnetGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetGraphic extends BarMagnetGraphic {

    public ElectromagnetGraphic( Component component, Electromagnet magnetModel ) {
        super( component, magnetModel );
    }
}
