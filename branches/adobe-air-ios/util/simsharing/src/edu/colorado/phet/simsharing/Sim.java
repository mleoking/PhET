// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;
import edu.colorado.phet.common.phetcommon.simsharing.state.SimsharingApplication;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * @author Sam Reid
 */
public class Sim<U extends SimState, T extends SimsharingApplication<U>> {
    public final String project;
    public final String flavor;
    public final String name;
    public final Function0<T> launcher;

    public static Sim GRAVITY_AND_ORBITS = new Sim( "Gravity and Orbits", "gravity-and-orbits", "", SimHelper.GRAVITY_AND_ORBITS_LAUNCHER );
    public static Sim TEST_SIM = new Sim( "Test Sim", "simsharing-test-sim", "", SimHelper.TEST_SIM_LAUNCHER );

    public static Sim[] sims = new Sim[] { GRAVITY_AND_ORBITS, TEST_SIM };
    public static HashMap<String, Sim<? extends SimState, ? extends SimsharingApplication<?>>> simMap = new HashMap<String, Sim<? extends SimState, ? extends SimsharingApplication<?>>>() {{
        for ( Sim sim : sims ) {
            put( sim.name, sim );
        }
    }};

    public Sim( String name, String project, String flavor, Function0<T> launcher ) {
        this.launcher = launcher;
        this.name = name;
        this.project = project;
        this.flavor = flavor;
    }

    @Override public String toString() {
        return name;
    }
}
