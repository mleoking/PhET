/**
 * Class: ApplicationModel
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.model.command.CommandQueue;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;

// TODO: This looks like class-side behavior for BaseModel. I think it is confusing
// to have it as a separate class
/**
 * There should be only one of these per PhetApplication. It contains at any time a single
 * instance of BaseModel, which is the root of the all the ModelElements in the active
 * model.
 *
 * The only class that talks to this is PhetApplication.
 */
public class ApplicationModel {
    AbstractClock clock;
    CommandQueue commandQueue = new CommandQueue();
    BaseModel currentBaseModel;

    public ApplicationModel( AbstractClock clock ) {
        this.clock = clock;
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                ApplicationModel.this.clockTicked();
            }
        } );
    }

    public BaseModel getBaseModel() {
        return currentBaseModel;
    }

    public void setBaseModel( BaseModel model ) {
        this.currentBaseModel=model;
//        if( this.currentBaseModel != model ) {
//            if( this.currentBaseModel != null ) {
//                clock.removeClockTickListener( this.currentBaseModel );
//            }
//            clock.addClockTickListener( model );
//            this.currentBaseModel = model;
//        }
    }

    public synchronized void execute( Command c ) {
        commandQueue.addCommand( c );
    }

    public void setPaused( boolean isPaused ) {
        clock.setPaused( isPaused );
    }

    public void start() {
        clock.start();
    }

    public AbstractClock getClock() {
        return clock;
    }

    public synchronized void clockTicked() {
        commandQueue.doIt();
    }

    public void tickOnce() {
        clock.tickOnce();
    }

}
