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

import edu.colorado.phet.boundstates.model.BSCoulomb3DPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSCoulomb3DDragManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulomb3DDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSCoulomb3DPotential potential ) {
        removeAllHandlesAndMarkers();
        if ( potential != null ) {
            
            BSPotentialSpec potentialSpec = getPotentialSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( !potentialSpec.getOffsetRange().isZero() ) {
                BSAbstractHandle offsetHandle = new BSCoulomb3DOffsetHandle( potential, potentialSpec, chartNode );
                addHandle( offsetHandle );
            }
        }
    }
}
