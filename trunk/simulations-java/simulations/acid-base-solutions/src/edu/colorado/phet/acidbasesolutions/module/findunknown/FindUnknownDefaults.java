/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.findunknown;

import edu.colorado.phet.acidbasesolutions.persistence.FindUnknownConfig;


/*
 * * FindUnknownDefaults contains default settings for FindUnknownModule.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FindUnknownDefaults {

    private static FindUnknownDefaults INSTANCE = new FindUnknownDefaults();

    public static FindUnknownDefaults getInstance() {
        return INSTANCE;
    }

    private final FindUnknownConfig config;

    private FindUnknownDefaults() {
        config = new FindUnknownConfig();
        //XXX call config setters
    }

    public FindUnknownConfig getConfig() {
        return config;
    }
}
