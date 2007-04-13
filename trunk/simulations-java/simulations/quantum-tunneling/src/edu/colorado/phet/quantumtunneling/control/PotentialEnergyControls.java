/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.view.QTCombinedChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEventListener;


/**
 * PotentialEnergyControls is the parent node that manages all of the
 * drag handles attached to a potential energy space. The drag handles
 * are superimposed on top of an energy chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialEnergyControls extends PNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTCombinedChartNode _chartNode;
    private ArrayList _energyDragHandles; // array of PotentialEnergyDragHandle
    private ArrayList _boundaryDragHandles; // array of RegionBoundaryDragHandle
    private boolean _valueVisible;
    private Color _valueColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param chartNode the chart that the drag handles will be drawn on top of
     */
    public PotentialEnergyControls( QTCombinedChartNode chartNode ) {
        _chartNode = chartNode;
        _energyDragHandles = new ArrayList();
        _boundaryDragHandles = new ArrayList();
        _valueVisible = false;
        _valueColor = Color.BLACK;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color used to display values.
     * 
     * @param color
     */
    public void setValueColor( Color color ) {
        _valueColor = color;
        for ( int i = 0; i < _energyDragHandles.size(); i++ ) {
            PotentialEnergyDragHandle energyDragHandle = (PotentialEnergyDragHandle) _energyDragHandles.get( i );
            energyDragHandle.setValueColor( _valueColor );
        }
        for ( int i = 0; i < _boundaryDragHandles.size(); i++ ) {
            RegionBoundaryDragHandle boundaryDragHandle = (RegionBoundaryDragHandle) _boundaryDragHandles.get( i );
            boundaryDragHandle.setValueColor( _valueColor );
        }
    }
    
    /**
     * Sets the potential energy associated with this set of drag handles.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        
        // Dispose of existing drag handles.
        removeAllChildren();
        for ( int i = 0; i < _energyDragHandles.size(); i++ ) {
            PotentialEnergyDragHandle energyDragHandle = (PotentialEnergyDragHandle) _energyDragHandles.get( i );
            energyDragHandle.cleanup();
        }
        _energyDragHandles.clear();
        for ( int i = 0; i < _boundaryDragHandles.size(); i++ ) {
            RegionBoundaryDragHandle boundaryDragHandle = (RegionBoundaryDragHandle) _boundaryDragHandles.get( i );
            boundaryDragHandle.cleanup();
        }
        _boundaryDragHandles.clear();

        // Create new drag handles.
        int numberOfRegions = potentialEnergy.getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {

            PotentialEnergyDragHandle energyDragHandle = new PotentialEnergyDragHandle( _chartNode );
            energyDragHandle.setPotentialEnergy( potentialEnergy, i );
            energyDragHandle.setValueVisible( _valueVisible );
            energyDragHandle.setValueColor( _valueColor );
            _energyDragHandles.add( energyDragHandle );
            addChild( energyDragHandle );
            
            // the last region has no boundary handle
            if ( i < numberOfRegions - 1 ) {
                RegionBoundaryDragHandle boundaryDragHandle = new RegionBoundaryDragHandle( _chartNode );
                boundaryDragHandle.setPotentialEnergy( potentialEnergy, i );
                boundaryDragHandle.setValueVisible( _valueVisible );
                boundaryDragHandle.setValueColor( _valueColor );
                _boundaryDragHandles.add( boundaryDragHandle );
                addChild( boundaryDragHandle );
            }
        }
        
        updateDragBounds();
    }
    
    /**
     * Shows/hides the values shown on the drag handles.
     * 
     * @param visible true or false
     */
    public void setValuesVisible( boolean visible ) {
        if ( visible != _valueVisible ) {
            _valueVisible = visible;
            for ( int i = 0; i < _energyDragHandles.size(); i++ ) {
                PotentialEnergyDragHandle energyDragHandle = (PotentialEnergyDragHandle) _energyDragHandles.get( i );
                energyDragHandle.setValueVisible( _valueVisible );
            }
            for ( int i = 0; i < _boundaryDragHandles.size(); i++ ) {
                RegionBoundaryDragHandle boundaryDragHandle = (RegionBoundaryDragHandle) _boundaryDragHandles.get( i );
                boundaryDragHandle.setValueVisible( _valueVisible );
            }
        }
    }
    
    /**
     * Are the values shown on drag handles?
     * 
     * @return true or false
     */
    public boolean isValuesVisible() {
        return ( (PotentialEnergyDragHandle) _energyDragHandles.get( 0 ) ).isValueVisible();
    }
    
    /**
     * Gets the energy drag handle for region i.
     * 
     * @param i
     * @return
     */
    public PotentialEnergyDragHandle getPotentialEnergyDragHandle( int i ) {
        if ( i >= 0 && i < _energyDragHandles.size() ) {
            return (PotentialEnergyDragHandle) _energyDragHandles.get( i );
        }
        else {
            return null;
        }
    }

    /**
     * Gets the region boundary drag handle for the boundary between 
     * regions i and i+1.
     * 
     * @param i
     * @return
     */
    public RegionBoundaryDragHandle getRegionBoundaryDragHandle( int i ) {
        if ( i >= 0 && i < _boundaryDragHandles.size() ) {
            return (RegionBoundaryDragHandle) _boundaryDragHandles.get( i );
        }
        else {
            return null;
        }
    }
    
    /**
     * Adds an InputEventListener to each of the drag handles.
     * 
     * @param listener
     */
    public void addInputEventListener( PInputEventListener listener ) {
        Iterator i = _boundaryDragHandles.iterator();
        while ( i.hasNext() ) {
            PNode node = (PNode) i.next();
            node.addInputEventListener( listener );
        }
        Iterator j = _energyDragHandles.iterator();
        while ( j.hasNext() ) {
            PNode node = (PNode) j.next();
            node.addInputEventListener( listener );
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds for all of the drag handles.
     */
    public void updateDragBounds() {
        // update the energy drag handles
        for ( int i = 0; i < _energyDragHandles.size(); i++ ) {
            PotentialEnergyDragHandle energyDragHandle = (PotentialEnergyDragHandle) _energyDragHandles.get( i );
            energyDragHandle.updateDragBounds();
        }
        
        // update the boundary drag handles
        for ( int i = 0; i < _boundaryDragHandles.size(); i++ ) {
            RegionBoundaryDragHandle boundaryDragHandle = (RegionBoundaryDragHandle) _boundaryDragHandles.get( i );
            boundaryDragHandle.updateDragBounds();
        }
    }
}
