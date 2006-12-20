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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.Model;
import edu.colorado.phet.hydrogenatom.model.Model.ModelEvent;
import edu.colorado.phet.hydrogenatom.model.Model.ModelListener;
import edu.umd.cs.piccolo.PNode;

/**
 * ModelViewManager manages the creation of PNodes for ModelElements.
 * Supports multiple views of a ModelElement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ModelViewManager implements ModelListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    /* 
     * The model that we're listening to, interested in
     * when ModelElements are added and removed.
     */
    private Model _model;
    
    /* 
     * Maps a ModelElement class to an ArrayList of NodeFactory.
     * Each NodeFactory is the mechanism for creating a view of the ModelElement type.
     */
    private HashMap _factoriesMap;
    
    /*
     * Maps a ModelElement instance to an ArrayList of NodeRecord.
     * Each NodeRecord describes how to remove the view of the ModelElement instance
     * from the Piccolo scenegraph.
     */
    private HashMap _nodeRecordsMap;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param model
     */
    public ModelViewManager( Model model ) {
        _model = model;
        _model.addModelListener( this );
        _factoriesMap = new HashMap();
        _nodeRecordsMap = new HashMap();
    }
    
    //----------------------------------------------------------------------------
    // NodeFactory management
    //----------------------------------------------------------------------------
    
    /**
     * Adds a NodeFactory.
     * The factory is added to the list of factories for the ModelElement class.
     * If this is the first factory to be added for the ModelElement class,
     * the list is created and added to the map.
     * 
     * @param factory
     */
    public void addNodeFactory( NodeFactory factory ) {
        Class modelElementClass = factory.getModelElementClass();
        ArrayList factoryList = (ArrayList) _factoriesMap.get( modelElementClass );
        if ( factoryList == null ) {
            factoryList = new ArrayList();
            _factoriesMap.put( modelElementClass, factoryList );
        }
        factoryList.add( factory );
    }
    
    /**
     * Removes a NodeFactory.
     * The factory is removed from the list of factories for the ModelElement class.
     * If this is the last factory for the ModelElement class, the list is removed
     * from the map.
     * 
     * @param factory
     */
    public void removeNodeFactory( NodeFactory factory ) {
        Class modelElementClass = factory.getModelElementClass();
        ArrayList factoryList = (ArrayList) _factoriesMap.get( modelElementClass );
        if ( factoryList != null ) {
            factoryList.remove( factory );
            if ( factoryList.size() == 0 ) {
                _factoriesMap.remove( modelElementClass );  
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Called when a ModelElement has been added to the model.
     * Adds a view of the ModelElement for each NodeFactory that 
     * is registered for the ModelElement's class.
     * 
     * @param event
     */
    public void modelElementAdded( ModelEvent event ) {

        ModelElement modelElement = event.getModelElement();
        Class modelElementClass = modelElement.getClass();
        ArrayList factoryList = (ArrayList) _factoriesMap.get( modelElementClass );
        if ( factoryList != null ) {
            
            ArrayList nodeRecordList = null;
            
            Iterator i = factoryList.iterator();
            while ( i.hasNext() ) {
                NodeFactory factory = (NodeFactory) i.next();
                if ( nodeRecordList == null ) {
                    nodeRecordList = new ArrayList();
                }
                PNode node = factory.createNode( modelElement );
                PNode parent = factory.getParent();
                parent.addChild( node );
                nodeRecordList.add( new NodeRecord( node, parent ) );
            }

            if ( nodeRecordList != null ) {
                _nodeRecordsMap.put( modelElement, nodeRecordList );
            }
        }
    }
    
    /**
     * Called when a ModelElement has been removed from the model.
     * Removes all views of the ModelElement.
     * 
     * @param event
     */
    public void modelElementRemoved( ModelEvent event ) {
        
        ModelElement modelElement = event.getModelElement();
        ArrayList nodeRecordList = (ArrayList) _nodeRecordsMap.get( modelElement );
        if ( nodeRecordList != null ) {
            Iterator i = nodeRecordList.iterator();
            while ( i.hasNext() ) {
                NodeRecord nodeRecord = (NodeRecord) i.next();
                PNode node = nodeRecord.getNode();
                PNode parent = nodeRecord.getParent();
                parent.removeChild( node );
            }
            _nodeRecordsMap.remove( modelElement );
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
        
        private Class _modelElementClass;
        private PNode _parent;

        protected NodeFactory( Class modelObjectClass, PNode parent ) {
            _modelElementClass = modelObjectClass;
            _parent = parent;
        }

        public Class getModelElementClass() {
            return _modelElementClass;
        }

        public PNode getParent() {
            return _parent;
        }

        public abstract PNode createNode( ModelElement modelElement );
    }
    
    /*
     * NodeRecord encapsulates the relationship between a node
     * and its parent in the Piccolo scene graph.
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
