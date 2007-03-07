package edu.colorado.phet.semiconductor_semi.macro.doping;

import edu.colorado.phet.semiconductor_semi.macro.circuit.CircuitSection;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 8:32:11 AM
 * Copyright (c) Feb 9, 2004 by Sam Reid
 */
public interface DopantChangeListener {
    void dopingChanged( CircuitSection circuitSection );
}
