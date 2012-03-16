// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.defaults;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * <p/>
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 *
 * @author John Blanco
 */
/* package private! */ class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {
    }

    // Clock
    public static final boolean CLOCK_RUNNING = true;
}
