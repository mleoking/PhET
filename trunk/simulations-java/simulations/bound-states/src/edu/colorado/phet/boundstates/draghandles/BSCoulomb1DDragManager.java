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
public class BSCoulomb1DDragManager extends BSAbstractDragManager {
    
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
        removeAllHandlesAndMarkers();
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
                
                BSAbstractHandle spacingHandle = new BSCoulomb1DSpacingHandle( potential, potentialSpec, chartNode );
                addHandle( spacingHandle );
            }
        }
    }
}
