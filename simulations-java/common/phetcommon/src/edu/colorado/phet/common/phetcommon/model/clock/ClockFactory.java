package edu.colorado.phet.common.phetcommon.model.clock;

public interface ClockFactory {
    Clock createInstance(int defaultDelay, double v);
}
