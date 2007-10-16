/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.model.MVCModel;
import edu.colorado.phet.mvcexample.model.ModelViewTransform;
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
    
    public BConnectionsManager( BModelElement modelElement, ModelViewTransform modelViewTransform,
            BNode node, BControlPanel controlPanel ) {
        super();
        _connected = false;
        _modelElement = modelElement;
        _node = node;
        _controlPanel = controlPanel;
        _propertyChangeListener = new BNodePropertyChangeListener( modelElement, modelViewTransform, node );
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
        private ModelViewTransform _modelViewTransform;
        private PNode _node;
        
        public BNodePropertyChangeListener( BModelElement modelElement, ModelViewTransform modelViewTransform, BNode node ) {
            _modelElement = modelElement;
            _modelViewTransform = modelViewTransform;
            _node = node;
        }
        
        public void propertyChange( PropertyChangeEvent event ) {
            if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
                updateModelPosition();
            }
        }
        
        private void updateModelPosition() {
            Point2D viewPoint = _node.getOffset();
            Point2D modelPoint = _modelViewTransform.viewToModel( viewPoint );
            _modelElement.setPosition( modelPoint );
        }
    }
}
