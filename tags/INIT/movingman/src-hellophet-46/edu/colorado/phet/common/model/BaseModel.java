/**
 * Class: HelloPhetModel
 * Package: edu.colorado.phet.common.examples.hellophet.model
 * Author: Another Guy
 * Date: May 20, 2003
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.model.command.CommandQueue;
import edu.colorado.phet.common.model.observation.Creation;
import edu.colorado.phet.common.model.observation.Deletion;

import java.util.Observer;

/**Manages a Clock, CommandQueue and a list of Observers.  Subclasses must fill in the implementation details
 * of being a ModelElement.
 * TODO: As of 6/17/03 nobody is using Creation and Deletion messages. If this
 * situation doesn't change, they should be removed from the system.
 */
public class BaseModel extends ModelElement implements ClockTickListener
//        implements IRunAndStop
 {

    // Clock owns the ModelElement it ticks to
//    private Clock clock;
    private CommandQueue commandList = new CommandQueue();
    private CompositeModelElement compositeModelElement;

    public BaseModel() {
        commonInit();
    }

    public BaseModel(Observer o) {
        this(new Observer[]{o});
        commonInit();
    }

    public BaseModel(Observer[] o) {
        for (int i = 0; i < o.length; i++) {
            addObserver(o[i]);
        }
        commonInit();
    }

    private void commonInit() {
        compositeModelElement = new CompositeModelElement();
//        clock = new Clock(this, 1, 20, ThreadPriority.NORMAL);
    }

    public void addModelElement(ModelElement m) {
        compositeModelElement.addModelElement(m);
        updateObservers(new Creation(m));
    }

    public void removeModelElement(ModelElement ts) {
        compositeModelElement.removeModelElement(ts);
        updateObservers(new Deletion(ts));
    }

    protected void removeAllModelElements() {
        while (compositeModelElement.numModelElements() > 0) {
            removeModelElement(compositeModelElement.modelElementAt(0));
        }
    }

    public ModelElement modelElementAt(int i) {
        return compositeModelElement.modelElementAt(i);
    }

    public int numModelElements() {
        return compositeModelElement.numModelElements();
    }

    //Not allowed to mess with the way we call our abstract method.
    public final void stepInTime(double dt) {
//        executeQueue();
        compositeModelElement.stepInTime(dt);
        updateObservers();
    }

    public synchronized void execute(Command mmc) {
        commandList.addCommand(mmc);
//        System.out.println( "commandList = " + commandList.size() );
    }

    public synchronized void executeQueue() {
        commandList.doIt();
    }

    public int getNumModelElements() {
        return this.compositeModelElement.numModelElements();
    }

    public void clockTicked(Clock c, double dt) {
        executeQueue();
        stepInTime(dt);
        updateObservers();
    }

}
