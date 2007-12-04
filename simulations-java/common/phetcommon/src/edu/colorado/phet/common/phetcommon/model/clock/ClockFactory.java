/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.model.clock;

public interface ClockFactory {
    Clock createInstance(int defaultDelay, double v);
}
