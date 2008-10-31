/**
 * Class: ApplicationModel
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 10, 2003
 */
package edu.colorado.phet.common_microwaves.model;

import edu.colorado.phet.common.phetcommon.model.Command;
import edu.colorado.phet.common.phetcommon.model.CommandQueue;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * There should be only one of these per PhetApplication. It contains at any time a single
 * instance of BaseModel, which is the root of the all the ModelElements in the active
 * model.
 * <p/>
 * The only class that talks to this is PhetApplication.
 */
public class ApplicationModel {
    IClock clock;
    CommandQueue commandQueue = new CommandQueue();
    BaseModel currentBaseModel;

    public ApplicationModel( IClock clock ) {
        this.clock = clock;
    }

    public BaseModel getBaseModel() {
        return currentBaseModel;
    }

    public void setBaseModel( BaseModel model ) {

        if( this.currentBaseModel != model ) {
            if( this.currentBaseModel != null ) {
                clock.removeClockListener( this.currentBaseModel );
            }
            clock.addClockListener( model );
            this.currentBaseModel = model;
        }
    }

    public synchronized void execute( Command c ) {
        commandQueue.addCommand( c );
    }

    public void setRunning( boolean b ) {
        if ( b ) {
            clock.start();
        }
        else {
            clock.pause();
        }
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
        clock.stepClockWhilePaused();
    }
}
