/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.util.HashMap;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.Model;
import edu.colorado.phet.hydrogenatom.model.Model.ModelEvent;
import edu.colorado.phet.hydrogenatom.model.Model.ModelListener;
import edu.umd.cs.piccolo.PNode;

/**
 * ModelViewManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ModelViewManager implements ModelListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Model _model;
    private HashMap _modelElementClassToNodeFactoryMap; // maps model element classes to NodeFactory instances
    private HashMap _modelElementToNodeRecordMap; // maps model element instances to NodeRecord instances
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param model
     */
    public ModelViewManager( Model model ) {

        _modelElementClassToNodeFactoryMap = new HashMap();
        _modelElementToNodeRecordMap = new HashMap();
        
        _model = model;
        _model.addModelListener( this );
    }
    
    //----------------------------------------------------------------------------
    // NodeFactory management
    //----------------------------------------------------------------------------
    
    /**
     * Adds a NodeFactory.
     * @param factory
     */
    public void addNodeFactory( NodeFactory factory ) {
        _modelElementClassToNodeFactoryMap.put( factory.getModelObjectClass(), factory );
    }
    
    /**
     * Removes a NodeFactory.
     * @param factory
     */
    public void removeNodeFactory( NodeFactory factory ) {
        _modelElementClassToNodeFactoryMap.remove( factory.getModelObjectClass() );
    }
    
    //----------------------------------------------------------------------------
    // ModelListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when a model object has been added to the model.
     * Gets the NodeFactory that corresponds to the model object's class
     * creates a PNode to display the model object, and adds the PNode at
     * the proper place in the scene graph.
     * 
     * @param event
     */
    public void modelElementAdded( ModelEvent event ) {

        ModelElement modelElement = event.getModelElement();
        Class modelElementClass = modelElement.getClass();
        NodeFactory factory = (NodeFactory) _modelElementClassToNodeFactoryMap.get( modelElementClass );

        if ( factory != null ) {
            PNode node = factory.createNode( modelElement );
            PNode parent = factory.getParent();
            _modelElementToNodeRecordMap.put( modelElement, new NodeRecord( node, parent ) );
            parent.addChild( node );
        }
    }
    
    /**
     * Called when a model object has been removed from the model.
     * Gets the PNode that corresponds to the model object, and removes 
     * the PNode from the scene graph.
     * 
     * @param event
     */
    public void modelElementRemoved( ModelEvent event ) {
        
        ModelElement modelElement = event.getModelElement();
        NodeRecord nodeRecord = (NodeRecord) _modelElementToNodeRecordMap.get( modelElement );
        
        if ( nodeRecord != null ) {
            PNode node = nodeRecord.getNode();
            PNode parent = nodeRecord.getParent();
            parent.removeChild( node );
            _modelElementToNodeRecordMap.remove( modelElement );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * NodeFactory is the base class for factories that create
     * PNodes for a specific class of model element.
     */
    public static abstract class NodeFactory {
        
        private Class _modelObjectClass;
        private PNode _parent;

        protected NodeFactory( Class modelObjectClass, PNode parent ) {
            _modelObjectClass = modelObjectClass;
            _parent = parent;
        }

        public Class getModelObjectClass() {
            return _modelObjectClass;
        }

        public PNode getParent() {
            return _parent;
        }

        public abstract PNode createNode( ModelElement modelElement );
    }
    
    /*
     * NodeRecord encapsulates the relationship between a node and its parent.
     */
    private static class NodeRecord {
        
        private PNode _node;
        private PNode _parent;

        public NodeRecord( PNode node, PNode parent ) {
            _node = node;
            _parent = parent;
        }

        public PNode getNode() {
            return _node;
        }

        public PNode getParent() {
            return _parent;
        }
    }
}
