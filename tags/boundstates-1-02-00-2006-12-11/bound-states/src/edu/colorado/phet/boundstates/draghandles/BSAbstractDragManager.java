/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * BSAbstractHandleManager is the base class for all drag managers.
 * It manages a set of drag handles and markers.
 * Drawing of drag handles and markers is clipped to the bounds
 * of the Energy plot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractDragManager extends PClip {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    private ArrayList _handles; // array of BSAbstractHandle
    private ArrayList _markers; // array of BSAbstractMarker
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potentialSpec describes ranges for potential's attributes
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSAbstractDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        _handles = new ArrayList();
        _markers = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    /*
     * Gets the range specification for the potential's attributes.
     * This is intended for use by subclasses.
     */
    protected BSPotentialSpec getPotentialSpec() {
        return _potentialSpec;
    }
    
    /*
     * Gets the chart node that the drag handles are related to.
     * This is intended for use by subclasses.
     */
    protected BSCombinedChartNode getChartNode() {
        return _chartNode;
    }
    
    /**
     * Updates the layout when visibility changes from invisible to visible.
     * Managed handles and markers are not updated while invisible.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( visible ) {
            updateLayout();
        }
        super.setVisible( visible );
        setChildrenPickable( visible );
    }
    
    /*
     * Adds a drag handle, for use by subclasses.
     * 
     * @param handle
     */
    protected void addHandle( BSAbstractHandle handle) {
        _handles.add( handle );
        super.addChild( handle );
    }
    
    /*
     * Adds a marker, for use by subclasses.
     * 
     * @param marker
     */
    protected void addMarker( BSAbstractMarker marker ) {
        _markers.add( marker );
        super.addChild( marker );
    }
    
    /*
     * Removes all handles and markers, for use by subclasses.
     */
    protected void removeAllHandlesAndMarkers() {
        removeAllChildren();
        _handles.clear();
        _markers.clear();
    }
    
    /**
     * Subclasses should be calling addHandle and addMarker,
     * so make this method unsupported.
     * 
     * @param node
     */
    public void addChild( PNode node ) {
        throw new UnsupportedOperationException( "use addHandle or addMarker" );
    }
    
    /**
     * Updates all of the managed handles and markers, and adjusts 
     * clipping to the bounds of the Energy plot.
     */
    public void updateLayout() {

        // clip to Energy plot bounds
        Rectangle2D energyPlotBounds = _chartNode.getEnergyPlotBounds();
        Rectangle2D globalBounds = _chartNode.localToGlobal( energyPlotBounds );
        Rectangle2D localBounds = globalToLocal( globalBounds );
        setPathTo( localBounds );

        // adjust markers
        Iterator m = _markers.iterator();
        while ( m.hasNext() ) {
            BSAbstractMarker marker = (BSAbstractMarker)m.next();
            marker.updateView();
        }
        
        // adjust handles
        Iterator h = _handles.iterator();
        while ( h.hasNext() ) {
            BSAbstractHandle handle = (BSAbstractHandle)h.next();
            handle.updateDragBounds();
        }
    }  
    
    /**
     * Sets the color scheme for all managed handles and markers.
     * 
     * @param colorScheme
     */
    public void setColorScheme( BSColorScheme colorScheme ) {

        Iterator h = _handles.iterator();
        while ( h.hasNext() ) {
            BSAbstractHandle handle = (BSAbstractHandle)h.next();
            handle.setColorScheme( colorScheme );
        }
        
        Iterator m = _markers.iterator();
        while ( m.hasNext() ) {
            BSAbstractMarker marker = (BSAbstractMarker)m.next();
            marker.setColorScheme( colorScheme );
        }
    }
    
    /**
     * Gets a handle, for the purposes of attaching a help item.
     * The first handle that was added via addHandle is returned.
     * 
     * @return PNode
     */
    public PNode getHelpNode() {
        PNode node = null;
        if ( _handles.size() > 0 ) {
            node = (PNode)_handles.get( 0 );
        }
        return node;
    }  
    
    /**
     * Gets the number of handles.
     * @return the number of handles, possibly zero
     */
    public int getHandleCount() {
        return _handles.size();
    }
    
    /**
     * Gets the number of markers.
     * @return the number of markers, possibly zero
     */
    public int getMarkerCount() {
        return _markers.size();
    }
}
