/**
 * Class: CompositeClockTickListener
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.greenhouse.phetcommon.model;

import java.util.ArrayList;

public class CompositeClockTickListener implements ClockTickListener {
    ArrayList list = new ArrayList();

    public void addClockTickListener(ClockTickListener cl) {
        list.add(cl);
    }

    public ClockTickListener clockTickListenerAt(int i) {
        return (ClockTickListener) list.get(i);
    }

    public int numClockTickListeners() {
        return list.size();
    }

    public void clockTicked(IClock c, double dt) {
        for (int i = 0; i < list.size(); i++) {
            ClockTickListener clockListener = (ClockTickListener) list.get(i);
            clockListener.clockTicked(c, dt);
        }
    }

    public void removeClockTickListener(ClockTickListener cl) {
        list.remove(cl);
    }
}
