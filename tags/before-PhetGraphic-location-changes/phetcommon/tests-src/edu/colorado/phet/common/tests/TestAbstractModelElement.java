/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.model.AbstractModelElement;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;

import java.util.EventObject;
import java.util.EventListener;

/**
 * TestAbstractModelElement
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestAbstractModelElement {

    static class MyModelElement extends AbstractModelElement {
        int numSteps;

        public void stepInTime(double dt) {
            numSteps++;
            fireEvent( new MyEvent( this ) );
        }

        public int getNumSteps() {
            return numSteps;
        }
    }

    static class MyEvent extends EventObject {
        public MyEvent(Object source) {
            super(source);
        }

        public int getNumSteps() {
            return ((MyModelElement)getSource()).getNumSteps();
        }
    }

    interface MyEventListener extends EventListener {
        void myEventHappened( MyEvent event );
    }


    public static void main(String[] args) {
        AbstractClock clock = new SwingTimerClock( 10, 500 );
        BaseModel model = new BaseModel();
        clock.addClockTickListener( model );
        MyModelElement modelElement = new MyModelElement();
        model.addModelElement( modelElement );

        // Add a listener to the model element that prints out the number of steps made so far
        modelElement.addListener( new MyEventListener() {
            public void myEventHappened(MyEvent event) {
                System.out.println("numSteps = " + event.getNumSteps() );
            }
        });

        // Add a listener to the model element that stops the program after 20 steps
        modelElement.addListener( new MyEventListener() {
            public void myEventHappened(MyEvent event) {
                if( event.getNumSteps() > 20 ) {
                    System.exit( 0 );
                }
            }
        });

        clock.start();
        // Spin until the listener kills us
        while( true );
    }

}
