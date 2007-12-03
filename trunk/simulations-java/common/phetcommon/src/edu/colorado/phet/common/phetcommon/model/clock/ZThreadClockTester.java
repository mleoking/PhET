package edu.colorado.phet.common.phetcommon.model.clock;

public class ZThreadClockTester extends ZAbstractClockTester {
    public ZThreadClockTester() {
        super(new ClockFactory() {
            public Clock createInstance(int defaultDelay, double v) {
                return new ThreadClock(defaultDelay, v);
            }
        });
    }
}
