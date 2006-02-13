/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Point2D;

/**
 * Venus
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Venus extends Earth {

    public Venus( Point2D.Double center, double alpha, double beta ) {
        super( center, alpha, beta );
        setBaseTemperature( GreenhouseConfig.venusBaseTemperature );
    }
}
