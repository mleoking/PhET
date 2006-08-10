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

import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSSquareDragManager manages drag handles and markers for 
 * a potential composed of Square wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potentialSpec describes ranges for potential's attributes
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSSquareDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
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
        removeAllHandlesAndMarkers();      
        if ( potential != null ) {
            
            BSPotentialSpec potentialSpec = getPotentialSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( !potentialSpec.getSeparationRange().isZero() ) {
                BSAbstractMarker separationMarkers = new BSSquareSeparationMarker( potential, chartNode );
                addMarker( separationMarkers );

                BSAbstractHandle separationHandle = new BSSquareSeparationHandle( potential, potentialSpec, chartNode );
                addHandle( separationHandle );
            }

            if ( !potentialSpec.getOffsetRange().isZero() ) {
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
        }
    }
}
