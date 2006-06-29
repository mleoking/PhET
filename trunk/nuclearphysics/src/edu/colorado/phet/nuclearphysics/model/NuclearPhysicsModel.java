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

import java.util.*;

public class NuclearPhysicsModel extends BaseModel {
    private List nuclearModelElements = new ArrayList();
    private List neutrons = new ArrayList();
    private List modelElements = new ArrayList();

    public List getModelElements() {
        return modelElements;
    }

    public void removeAllModelElements() {
        for( int i = 0; i < modelElements.size(); i++ ) {
            ModelElement modelElement = (ModelElement)modelElements.get( i );
            removeModelElement( modelElement );
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        modelElements.remove( modelElement );
        if( modelElement instanceof NuclearModelElement ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)modelElement;
            nuclearModelElements.remove( nuclearModelElement );
            nuclearModelElement.leaveSystem();
        }

        if( modelElement instanceof Nucleus ) {
            nucleusListenerProxy.nucleusRemoved( new ChangeEvent( (Nucleus)modelElement ) );
        }

        if( modelElement instanceof Neutron ) {
            neutrons.remove( modelElement );
        }
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        modelElements.add( modelElement );
        if( modelElement instanceof NuclearModelElement ) {
            nuclearModelElements.add( modelElement );
        }

        if( modelElement instanceof Nucleus ) {
            nucleusListenerProxy.nucleusAdded( new ChangeEvent( (Nucleus)modelElement ) );
        }
//
//        if( modelElement instanceof Neutron ) {
//            neutrons.add( modelElement );
//        }
    }

    public void removeNuclearParticles() {
        while( nuclearModelElements.size() > 0 ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)nuclearModelElements.get( 0 );
            this.removeModelElement( nuclearModelElement );
            nuclearModelElements.remove( nuclearModelElement );
        }
    }

    public List getNuclearModelElements() {
        return nuclearModelElements;
    }

    public List getNeutrons() {
        return neutrons;
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
