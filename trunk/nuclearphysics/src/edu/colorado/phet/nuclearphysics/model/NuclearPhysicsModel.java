/**
 * Class: NuclearPhysicsModel
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 14, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;

import java.util.LinkedList;
import java.util.List;
import java.util.EventObject;
import java.util.EventListener;

public class NuclearPhysicsModel extends BaseModel {
    private LinkedList nuclearModelElements = new LinkedList();

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof NuclearModelElement ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)modelElement;
            nuclearModelElements.remove( nuclearModelElement );
            nuclearModelElement.leaveSystem();
        }

        if( modelElement instanceof Nucleus ) {
            nucleusListenerProxy.nucleusRemoved( new ChangeEvent( (Nucleus)modelElement ) );
        }
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof NuclearModelElement ) {
            nuclearModelElements.add( modelElement );
        }

        if( modelElement instanceof Nucleus ) {
            nucleusListenerProxy.nucleusAdded( new ChangeEvent( (Nucleus)modelElement ) );
        }
    }

    public void removeNuclearPartilces() {
        while( nuclearModelElements.size() > 0 ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)nuclearModelElements.get( 0 );
            this.removeModelElement( nuclearModelElement );
            nuclearModelElements.remove( nuclearModelElement );
        }
    }

    public List getNuclearModelElements() {
        return nuclearModelElements;
    }


    //--------------------------------------------------------------------------------------------------
    // ChangeListener definition
    //--------------------------------------------------------------------------------------------------
    EventChannel changeEventChannel = new EventChannel( NucleusListener.class );
    NucleusListener nucleusListenerProxy = (NucleusListener)changeEventChannel.getListenerProxy();

    public void addNucleusListener( NucleusListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeNucleusListener( NucleusListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Nucleus source ) {
            super( source );
        }

        public Nucleus getNucleus() {
            return (Nucleus)getSource();
        }
    }

    public interface NucleusListener extends EventListener {
        void nucleusAdded( ChangeEvent event );

        void nucleusRemoved( ChangeEvent event );
    }

}
