/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * NotifyDragHandler notifies listeners when a drag begins, occurs and ends.
 * This is useful if you need to do some processing at when a drag starts or 
 * ends, and want to attach one handler instance to multiple nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NotifyDragHandler extends PBasicInputEventHandler {

    private Object eventSource;
    private EventListenerList listenerList;
    
    public NotifyDragHandler() {
        this( null );
    }
    
    public NotifyDragHandler( Object eventSource ) {
        super();
        listenerList = new EventListenerList();
        this.eventSource = eventSource;
        if ( eventSource == null ) {
            this.eventSource = this;
        }
    }
    
    public void mousePressed( PInputEvent event ) {
        fireDragBegin( new DragEvent( eventSource ) );
    }
    
    public void mouseDragged( PInputEvent event ) {
        fireDragged( new DragEvent( eventSource ) );
    }
    
    public void mouseReleased( PInputEvent event ) {
        fireDragEnd( new DragEvent( eventSource ) );
    }
    
    public void addDragEventListener( DragEventListener listener ) {
        listenerList.add( DragEventListener.class, listener );
    }
    
    public void removeDragEventListener( DragEventListener listener ) {
        listenerList.remove( DragEventListener.class, listener );
    }
    
    private void fireDragBegin( DragEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == DragEventListener.class ) {
                ( (DragEventListener)listeners[i + 1] ).dragBegin( event );
            }
        }
    }
    
    private void fireDragged( DragEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == DragEventListener.class ) {
                ( (DragEventListener)listeners[i + 1] ).dragged( event );
            }
        }
    }
    
    private void fireDragEnd( DragEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == DragEventListener.class ) {
                ( (DragEventListener)listeners[i + 1] ).dragEnd( event );
            }
        }
    }
    
    public static class DragEvent extends EventObject {
        public DragEvent( Object source ) {
            super( source );
        }
    }
    
    public interface DragEventListener extends EventListener {
        public void dragBegin( DragEvent event );
        public void dragged( DragEvent event );
        public void dragEnd( DragEvent event );
    }
    
    public static class DragEventAdapter implements DragEventListener {
        public void dragBegin( DragEvent event ) {}
        public void dragEnd( DragEvent event ) {}
        public void dragged( DragEvent event ) {}
    }
}
