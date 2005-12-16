/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.util.ArrayList;

import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.umd.cs.piccolo.PNode;


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
    private boolean _textEnabled;
    
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
        _textEnabled = false;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
            energyDragHandle.setShowValueEnabled( _textEnabled );
            _energyDragHandles.add( energyDragHandle );
            addChild( energyDragHandle );
            
            // the last region has no boundary handle
            if ( i < numberOfRegions - 1 ) {
                RegionBoundaryDragHandle boundaryDragHandle = new RegionBoundaryDragHandle( _chartNode );
                boundaryDragHandle.setPotentialEnergy( potentialEnergy, i );
                boundaryDragHandle.setShowValueEnabled( _textEnabled );
                _boundaryDragHandles.add( boundaryDragHandle );
                addChild( boundaryDragHandle );
            }
        }
        
        updateDragBounds();
    }
    
    /**
     * Enables or disables the value shown on the drag handles.
     * 
     * @param enabled true or false
     */
    public void setShowValuesEnabled( boolean enabled ) {
        if ( enabled != _textEnabled ) {
            _textEnabled = enabled;
            for ( int i = 0; i < _energyDragHandles.size(); i++ ) {
                PotentialEnergyDragHandle energyDragHandle = (PotentialEnergyDragHandle) _energyDragHandles.get( i );
                energyDragHandle.setShowValueEnabled( _textEnabled );
            }
            for ( int i = 0; i < _boundaryDragHandles.size(); i++ ) {
                RegionBoundaryDragHandle boundaryDragHandle = (RegionBoundaryDragHandle) _boundaryDragHandles.get( i );
                boundaryDragHandle.setShowValueEnabled( _textEnabled );
            }
        }
    }
    
    /**
     * Is the value shown on drag handles?
     * 
     * @return true or false
     */
    public boolean isShowValuesEnabled() {
        return ( (PotentialEnergyDragHandle) _energyDragHandles.get( 0 ) ).isShowValueEnabled();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
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
