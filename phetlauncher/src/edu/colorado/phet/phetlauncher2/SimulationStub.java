/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import netx.jnlp.ParseException;

import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 2:50:41 AM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */

public class SimulationStub {
    private PhetLauncher phetLauncher;
    private SimulationXMLEntry entry;

    public SimulationStub( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException {
        this.phetLauncher = phetLauncher;
        this.entry = entry;
    }

    public String getUrl() {
        return entry.getUrl();
    }

    public SimulationXMLEntry getEntry() {
        return entry;
    }
}
