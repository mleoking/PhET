// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.mvcexample.model.BModelElement;
import edu.colorado.phet.mvcexample.view.BNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PDimension;

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
    private PInputEventListener _dragHandler;
    
    public BConnectionsManager( BModelElement modelElement, BNode node, BControlPanel controlPanel ) {
        super();
        _connected = false;
        _modelElement = modelElement;
        _node = node;
        _controlPanel = controlPanel;
        _dragHandler = new BNodeDragHandler( node, modelElement );
    }
    
    /**
     * Makes all the needed connections for things related to BModelElement.
     */
    public void connect() {
        if ( !_connected ) {
            _modelElement.addListener( _node );
            _modelElement.addListener( _controlPanel );
            _node.addInputEventListener( _dragHandler );
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
            _node.removeInputEventListener( _dragHandler );
            _connected = false;
        }
    }
    
    /*
     * When a BNode is dragged, use the event to set the model element's new position.
     */
    private static class BNodeDragHandler extends PBasicInputEventHandler {

        private PNode _node;
        private BModelElement _modelElement;
        
        public BNodeDragHandler( PNode node, BModelElement modelElement ) {
            super();
            _node = node;
            _modelElement = modelElement;
        }
        
        public void mouseDragged( PInputEvent event ) {
            PDimension delta = event.getDeltaRelativeTo( _node.getParent() );
            Point2D p = _modelElement.getPosition();
            Point2D pNew = new Point2D.Double( p.getX() + delta.getWidth(), p.getY() + delta.getHeight() );
            _modelElement.setPosition( pNew );
        }
    }
}
