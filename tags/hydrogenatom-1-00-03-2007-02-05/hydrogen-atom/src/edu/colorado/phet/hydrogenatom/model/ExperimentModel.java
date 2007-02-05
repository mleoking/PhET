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
 * ExperimentModel is the model when we are in "Experiment" mode,
 * and is identical to the Schrodinger model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ExperimentModel extends SchrodingerModel {
    
    public ExperimentModel( Point2D position ) {
        super( position );
    }
}
