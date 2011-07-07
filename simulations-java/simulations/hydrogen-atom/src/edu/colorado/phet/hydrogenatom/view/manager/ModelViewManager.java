// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.view.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.Model;
import edu.colorado.phet.hydrogenatom.model.Model.ModelListener;
import edu.umd.cs.piccolo.PNode;

/**
 * ModelViewManager manages the creation of PNodes for ModelElements.
 * Supports multiple views of a ModelElement.
 * <p/>
 * As indicated in package.html:
 * In hindsight, this is an overly-complex framework, borrowed (and ported to Piccolo)
 * from some of the other particle-based simulations. Reuse in other sims in not recommended.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
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
    private HashMap<Class, ArrayList<NodeFactory>> _factoriesMap;

    /*
    * Maps a ModelElement instance to an ArrayList of NodeRecord.
    * Each NodeRecord describes how to remove the view of the ModelElement instance
    * from the Piccolo scenegraph.
    */
    private HashMap<ModelElement, ArrayList<NodeRecord>> _nodeRecordsMap;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param model
     */
    public ModelViewManager( Model model ) {
        _model = model;
        _model.addModelListener( this );
        _factoriesMap = new HashMap<Class, ArrayList<NodeFactory>>();
        _nodeRecordsMap = new HashMap<ModelElement, ArrayList<NodeRecord>>();
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
        ArrayList<NodeFactory> factoryList = _factoriesMap.get( modelElementClass );
        if ( factoryList == null ) {
            factoryList = new ArrayList<NodeFactory>();
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
        ArrayList<NodeFactory> factoryList = _factoriesMap.get( modelElementClass );
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
     */
    public void modelElementAdded( ModelElement modelElement ) {
        Class modelElementClass = modelElement.getClass();
        ArrayList<NodeFactory> factoryList = _factoriesMap.get( modelElementClass );
        if ( factoryList != null ) {
            ArrayList<NodeRecord> nodeRecordList = null;
            for ( NodeFactory factory : new ArrayList<NodeFactory>( factoryList ) ) {
                if ( nodeRecordList == null ) {
                    nodeRecordList = new ArrayList<NodeRecord>();
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

    /*
     * Called when a ModelElement has been removed from the model.
     * Removes all views of the ModelElement.
     */
    public void modelElementRemoved( ModelElement modelElement ) {
        ArrayList<NodeRecord> nodeRecordList = _nodeRecordsMap.get( modelElement );
        if ( nodeRecordList != null ) {
            for ( NodeRecord nodeRecord : new ArrayList<NodeRecord>( nodeRecordList ) ) {
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
