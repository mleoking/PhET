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
 * BSAbstractHandleManager is the base class for all drag handle managers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractDragManager extends PClip {

    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    private ArrayList _handles; // array of BSAbstractHandle
    private ArrayList _markers; // array of BSAbstractMarker
    
    public BSAbstractDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
        _handles = new ArrayList();
        _markers = new ArrayList();
    }
    
    protected BSPotentialSpec getPotentialSpec() {
        return _potentialSpec;
    }
    
    protected BSCombinedChartNode getChartNode() {
        return _chartNode;
    }
    
    public void setVisible( boolean visible ) {
        if ( visible ) {
            updateLayout();
        }
        super.setVisible( visible );
    }
    
    protected void addHandle( BSAbstractHandle handle) {
        _handles.add( handle );
        super.addChild( handle );
    }
    
    protected void addMarker( BSAbstractMarker marker ) {
        _markers.add( marker );
        super.addChild( marker );
    }
    
    protected void removeAllHandlesAndMarkers() {
        removeAllChildren();
        _handles.clear();
        _markers.clear();
    }
    
    public void addChild( PNode node ) {
        throw new UnsupportedOperationException( "use addHandle or addMarker" );
    }
    
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
    
    public PNode getHelpNode() {
        PNode node = null;
        if ( _handles.size() > 0 ) {
            node = (PNode)_handles.get( 0 );
        }
        return node;
    }    
}
