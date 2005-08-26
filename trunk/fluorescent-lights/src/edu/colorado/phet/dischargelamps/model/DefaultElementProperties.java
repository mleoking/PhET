/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

/**
 * HydrogenProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DefaultElementProperties extends ElementProperties {
    private static double[] energyLevels = {
        -13.6,
        -0.378
    };

    public DefaultElementProperties() {
        super( "Default", energyLevels );
    }
}

