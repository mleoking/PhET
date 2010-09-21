/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.boundstates.draghandles;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSCoulomb1DDragManager manages drag handles and markers for 
 * a potential composed of 1D Coulomb wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DDragManager extends BSAbstractDragManager implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSCoulomb1DPotential _potential;
    private BSAbstractHandle _spacingHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param moduleSpec
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSCoulomb1DDragManager( BSAbstractModuleSpec moduleSpec, BSCombinedChartNode chartNode ) {
        super( moduleSpec, chartNode );
        _potential = null;
        _spacingHandle = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Attaches drag handles and markers to the specified potential.
     * Any existing handles and markers are deleted.
     * 
     * @param potential
     */
    public void setPotential( BSCoulomb1DPotential potential ) {

        // remove existing handles and markers
        removeAllHandlesAndMarkers();
        _spacingHandle = null;
        
        // rewire observer
        if ( _potential != null ) {
            _potential.deleteObserver( this );
        }
        _potential = potential;
        if ( _potential != null ) {
            _potential.addObserver( this );
        }
        
        // create new handles and markers
        if ( potential != null ) {
            
            BSAbstractModuleSpec moduleSpec = getModuleSpec();
            BSPotentialSpec potentialSpec = moduleSpec.getCoulomb1DSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( moduleSpec.isOffsetControlSupported() ) {
                BSAbstractHandle offsetHandle = new BSCoulomb1DOffsetHandle( potential, potentialSpec, chartNode );
                addHandle( offsetHandle );
            }
            
            if ( moduleSpec.getNumberOfWellsRange().getMax() > 1 ) {
                
                BSAbstractMarker spacingMarkers = new BSCoulomb1DSpacingMarker( potential, chartNode );
                addMarker( spacingMarkers );
                
                _spacingHandle = new BSCoulomb1DSpacingHandle( potential, potentialSpec, chartNode );
                addHandle( _spacingHandle );
                updateSpacingHandle();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        updateSpacingHandle();
    }
    
    private void updateSpacingHandle() {
        if ( _spacingHandle != null && _potential != null ) {
            _spacingHandle.setVisible( _potential.getNumberOfWells() > 1 );
        }
    }
}
