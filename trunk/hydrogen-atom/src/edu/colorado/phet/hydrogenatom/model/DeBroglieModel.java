/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

/**
 * DeBroglieModel is the deBroglie model of a hydrogen atom.
 * It is identical to the Bohr model, but has a different visual representation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieModel extends BohrModel {

    public DeBroglieModel( Point2D position ) {
        super( position );
    }
}
