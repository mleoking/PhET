package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.EntryPoint;

/**
 * User: Sam Reid
 * Date: Mar 1, 2004
 * Time: 9:16:12 PM
 * Copyright (c) Mar 1, 2004 by Sam Reid
 */
public interface StateModel {

    void updateStates();

    EntryPoint[] getEntryPoints();
}
