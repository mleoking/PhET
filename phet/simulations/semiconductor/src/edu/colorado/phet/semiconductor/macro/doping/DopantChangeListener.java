package edu.colorado.phet.semiconductor.macro.doping;

import edu.colorado.phet.semiconductor.macro.circuit.CircuitSection;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 8:32:11 AM
 * Copyright (c) Feb 9, 2004 by Sam Reid
 */
public interface DopantChangeListener {
    void dopingChanged( CircuitSection circuitSection );
}
