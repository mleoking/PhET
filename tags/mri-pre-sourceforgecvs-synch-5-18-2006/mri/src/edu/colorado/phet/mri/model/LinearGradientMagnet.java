/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.clock.IClock;

import java.awt.geom.Point2D;

/**
 * LinearGradientMagnet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LinearGradientMagnet extends GradientElectromagnet {
    public LinearGradientMagnet( Point2D position, double width, double height, IClock clock, GradientElectromagnet.LinearGradient gradient, Orientation orientation ) {
        super( position, width, height, clock, gradient, orientation );
    }
}
