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

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BSSquareDragManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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
