/**
 * Class: ApplicationModel
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.model.command.CommandQueue;

public class ApplicationModel implements ClockTickListener {
    Clock clock;
    CommandQueue commandQueue = new CommandQueue();
    BaseModel currentBaseModel;

    public ApplicationModel(double dt, int waitTime, ThreadPriority priority) {
        clock = new Clock(this, dt, waitTime, priority);
    }

    public ApplicationModel() {
        this(1, 20, ThreadPriority.NORMAL);
    }

    public void setBaseModel(BaseModel model) {

        if (this.currentBaseModel != model) {
            if (this.currentBaseModel != null) {
                clock.removeClockTickListener(this.currentBaseModel);
            }
            clock.addClockTickListener(model);
            this.currentBaseModel = model;
        }
    }

    public synchronized void execute(Command c) {
        commandQueue.addCommand(c);
    }

    public void setRunning(boolean b) {
        clock.setRunning(b);
    }

    public void start() {
        clock.start();
    }

    public Clock getClock() {
        return clock;
    }

    public synchronized void clockTicked(Clock c, double dt) {
        commandQueue.doIt();
    }

    public void tickOnce() {
        clock.tickOnce();
    }

    public double getDt() {
        return clock.getDt();
    }

    public void setDt(double dt) {
        clock.setDt(dt);
    }

}
