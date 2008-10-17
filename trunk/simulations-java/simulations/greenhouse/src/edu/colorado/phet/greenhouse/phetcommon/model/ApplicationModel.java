/**
 * Class: ApplicationModel
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.greenhouse.phetcommon.model;

import edu.colorado.phet.common.phetcommon.model.Command;
import edu.colorado.phet.common.phetcommon.model.CommandQueue;

/**
 * There should be only one of these per PhetApplication. It contains at any time a single
 * instance of BaseModel, which is the root of the all the ModelElements in the active
 * model.
 *
 * The only class that talks to this is PhetApplication.
 */
public class ApplicationModel {
    IClock clock;
    CommandQueue commandQueue = new CommandQueue();
    BaseModel currentBaseModel;

    public ApplicationModel( IClock clock ) {
        this.clock = clock;
        clock.setParent(this);
    }

    public BaseModel getBaseModel() {
        return currentBaseModel;
    }

    public void setBaseModel( BaseModel model ) {

        if( this.currentBaseModel != model ) {
            if( this.currentBaseModel != null ) {
                clock.removeClockTickListener( this.currentBaseModel );
            }
            clock.addClockTickListener( model );
            this.currentBaseModel = model;
        }
    }

    public synchronized void execute( Command c ) {
        commandQueue.addCommand( c );
    }

    public void setRunning( boolean b ) {
        clock.setRunning( b );
    }

    public void start() {
        clock.start();
    }

    public IClock getClock() {
        return clock;
    }

    public synchronized void clockTicked() {
        commandQueue.doIt();
    }

    public void tickOnce() {
        clock.tickOnce(clock.getRequestedDT());
    }

    public double getRequestedDT() {
        return clock.getRequestedDT();
    }

    public void setRequestedDT( double dt ) {
        clock.setRequestedDT( dt );
    }
}
