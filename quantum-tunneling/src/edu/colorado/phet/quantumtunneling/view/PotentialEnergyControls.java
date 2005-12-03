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

import edu.colorado.phet.quantumtunneling.model.AbstractPotentialSpace;
import edu.umd.cs.piccolo.PNode;


/**
 * PotentialEnergyControls is the parent node that manages all of the
 * drag handles attached to a potential energy space.
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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PotentialEnergyControls( QTCombinedChartNode chartNode ) {
        _chartNode = chartNode;
        _energyDragHandles = new ArrayList();
        _boundaryDragHandles = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotentialEnergy( AbstractPotentialSpace potentialEnergy ) {
        
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
            _energyDragHandles.add( energyDragHandle );
            addChild( energyDragHandle );
            
            // the last region has no boundary handle
            if ( i < numberOfRegions - 1 ) {
                RegionBoundaryDragHandle boundaryDragHandle = new RegionBoundaryDragHandle( _chartNode );
                boundaryDragHandle.setPotentialEnergy( potentialEnergy, i );
                _boundaryDragHandles.add( boundaryDragHandle );
                addChild( boundaryDragHandle );
            }
        }
        
        updateDragBounds();
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
