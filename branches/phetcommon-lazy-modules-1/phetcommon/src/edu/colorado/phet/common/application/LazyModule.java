/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.application;

/**
 * User: Sam Reid
 * Date: May 14, 2006
 * Time: 10:24:59 AM
 * Copyright (c) May 14, 2006 by Sam Reid
 */

public class LazyModule extends Module {
    boolean constructed = false;

    public void activate() {
        super.activate();
        if( !constructed )
    }

}
