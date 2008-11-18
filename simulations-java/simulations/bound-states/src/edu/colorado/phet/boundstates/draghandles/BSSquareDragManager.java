/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.boundstates.draghandles;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSSquareDragManager manages drag handles and markers for 
 * a potential composed of Square wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDragManager extends BSAbstractDragManager implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSSquarePotential _potential;
    private BSAbstractHandle _separationHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param moduleSpec
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSSquareDragManager( BSAbstractModuleSpec moduleSpec, BSCombinedChartNode chartNode ) {
        super( moduleSpec, chartNode );
        _potential = null;
        _separationHandle = null;
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
    public void setPotential( BSSquarePotential potential ) {
        
        // remove existing handles and markers
        removeAllHandlesAndMarkers(); 
        _separationHandle = null;
        
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
            BSPotentialSpec potentialSpec = moduleSpec.getSquareSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( moduleSpec.isOffsetControlSupported() ) {
                BSAbstractHandle offsetHandle = new BSSquareOffsetHandle( potential, potentialSpec, chartNode );
                addHandle( offsetHandle );
            }

            if ( !potentialSpec.getHeightRange().isZero() ) {
                BSAbstractHandle heightHandle = new BSSquareHeightHandle( potential, potentialSpec, chartNode );
                addHandle( heightHandle );
            }

            if ( !potentialSpec.getWidthRange().isZero() ) {
                BSAbstractHandle widthHandle = new BSSquareWidthHandle( potential, potentialSpec, chartNode );
                addHandle( widthHandle );
            }
            
            if ( moduleSpec.getNumberOfWellsRange().getMax() > 1 ) {
                BSAbstractMarker separationMarkers = new BSSquareSeparationMarker( potential, chartNode );
                addMarker( separationMarkers );

                _separationHandle = new BSSquareSeparationHandle( potential, potentialSpec, chartNode );
                addHandle( _separationHandle );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( _separationHandle != null ) {
            _separationHandle.setVisible( _potential.getNumberOfWells() > 1 );
        }
    }
}
