/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.view.BNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BConnectionsManager manages the connections between MVC components that are related to BModelElement.
 * The idea is to isolate all coupling here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BConnectionsManager {

    private boolean _connected;
    
    private BModelElement _modelElement;
    private BNode _node;
    private BControlPanel _controlPanel;
    private BNodePropertyChangeListener _propertyChangeListener;
    
    public BConnectionsManager( BModelElement modelElement, BNode node, BControlPanel controlPanel ) {
        super();
        _connected = false;
        _modelElement = modelElement;
        _node = node;
        _controlPanel = controlPanel;
        _propertyChangeListener = new BNodePropertyChangeListener( modelElement, node );
    }
    
    /**
     * Makes all the needed connections for things related to BModelElement.
     */
    public void connect() {
        if ( !_connected ) {
            _modelElement.addListener( _node );
            _modelElement.addListener( _controlPanel );
            _node.addPropertyChangeListener( _propertyChangeListener );
            _connected = true;
        }
    }
    
    /**
     * Tears down all the connections that were made in connect.
     */
    public void disconnect() {
        if ( _connected ) {
            _modelElement.removeListener( _node );
            _modelElement.removeListener( _controlPanel );
            _node.removePropertyChangeListener( _propertyChangeListener );
            _connected = false;
        }
    }
    
    /*
     * When a BNode is dragged by the user, Piccolo will fire a PropertyChangeEvent.
     * Note the new location of the node, and use it to set the model element's new position.
     */
    private class BNodePropertyChangeListener implements PropertyChangeListener {
        
        private BModelElement _modelElement;
        private PNode _node;
        
        public BNodePropertyChangeListener( BModelElement modelElement, BNode node ) {
            _modelElement = modelElement;
            _node = node;
        }
        
        public void propertyChange( PropertyChangeEvent event ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                _modelElement.setPosition( _node.getOffset() );
            }
        }
    }
}
