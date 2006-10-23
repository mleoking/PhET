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

import edu.colorado.phet.hydrogenatom.model.IModelObject;
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
    private HashMap _modelObjectClassToNodeFactoryMap; // maps model element classes to NodeFactory instances
    private HashMap _modelObjectToNodeRecordMap; // maps model element instances to NodeRecord instances
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param model
     */
    public ModelViewManager( Model model ) {

        _modelObjectClassToNodeFactoryMap = new HashMap();
        _modelObjectToNodeRecordMap = new HashMap();
        
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
        _modelObjectClassToNodeFactoryMap.put( factory.getModelObjectClass(), factory );
    }
    
    /**
     * Removes a NodeFactory.
     * @param factory
     */
    public void removeNodeFactory( NodeFactory factory ) {
        _modelObjectClassToNodeFactoryMap.remove( factory.getModelObjectClass() );
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
    public void modelObjectAdded( ModelEvent event ) {

//        System.out.println( "ModelViewManager.modelObjectAdded " + event.getModelObject() );//XXX
        
        IModelObject modelObject = event.getModelObject();
        Class modelObjectClass = modelObject.getClass();
        NodeFactory factory = (NodeFactory) _modelObjectClassToNodeFactoryMap.get( modelObjectClass );

        if ( factory != null ) {
            PNode node = factory.createNode( modelObject );
            PNode parent = factory.getParent();
            _modelObjectToNodeRecordMap.put( modelObject, new NodeRecord( node, parent ) );
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
    public void modelObjectRemoved( ModelEvent event ) {
        
//        System.out.println( "ModelViewManager.modelObjectRemoved " + event.getModelObject() );//XXX
        
        IModelObject modelObject = event.getModelObject();
        NodeRecord nodeRecord = (NodeRecord) _modelObjectToNodeRecordMap.get( modelObject );
        
        if ( nodeRecord != null ) {
            PNode node = nodeRecord.getNode();
            PNode parent = nodeRecord.getParent();
            parent.removeChild( node );
            _modelObjectToNodeRecordMap.remove( modelObject );
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

        public abstract PNode createNode( IModelObject modelObject );
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
