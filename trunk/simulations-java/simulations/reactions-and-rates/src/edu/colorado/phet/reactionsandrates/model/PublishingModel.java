// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

import java.util.ArrayList;
import java.util.EventListener;

/**
 * PublishingModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PublishingModel extends BaseModel {

    //----------------------------------------------------------------
    // Publish/subscribe mechanism
    //----------------------------------------------------------------

    public static interface ModelListener extends EventListener {
        void modelElementAdded( ModelElement element );

        void modelElementRemoved( ModelElement element );

        /**
         * Called at the end of every time step
         *
         * @param model
         * @param event
         */
        void endOfTimeStep( PublishingModel model, ClockEvent event );
    }

    public static class ModelListenerAdapter implements ModelListener {
        public void modelElementAdded( ModelElement element ) {
        }

        public void modelElementRemoved( ModelElement element ) {
        }

        public void endOfTimeStep( PublishingModel model, ClockEvent event ) {
        }
    }

    private EventChannel eventChannel = new EventChannel( ModelListener.class );
    private ModelListener modelListenerProxy = (ModelListener)eventChannel.getListenerProxy();

    public void addListener( ModelListener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( ModelListener listener ) {
        eventChannel.removeListener( listener );
    }

    //----------------------------------------------------------------
    // ModelStepper
    //----------------------------------------------------------------

    private static class ModelStepper extends ClockAdapter {
        private PublishingModel model;

        public ModelStepper( PublishingModel model ) {
            this.model = model;
        }

        public void clockTicked( ClockEvent clockEvent ) {
            model.stepInTime( clockEvent.getSimulationTimeChange() );
            model.modelListenerProxy.endOfTimeStep( model, clockEvent );
        }
    }


    //--------------------------------------------------------------------------------------------------
    // The rest...
    //--------------------------------------------------------------------------------------------------
    private ArrayList modelElements = new ArrayList();

    /**
     * Constructor
     *
     * @param clock
     */
    public PublishingModel( IClock clock ) {
        clock.addClockListener( new ModelStepper( this ) );
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        modelElements.add( modelElement );
        modelListenerProxy.modelElementAdded( modelElement );
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        modelElements.remove( modelElement );
        modelListenerProxy.modelElementRemoved( modelElement );
    }

    /**
     * Overrides parent behavior so that listeners will be notified
     */
    public void removeAllModelElements() {
        while( modelElements.size() > 0 ) {
            removeModelElement( (ModelElement)modelElements.get( 0 ) );
        }
    }

    public ArrayList getModelElements() {
        return modelElements;
    }
}
