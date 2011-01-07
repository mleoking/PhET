// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSCoulomb3DDragManager manages drag handles for 
 * a potential composed of 3D Coulomb wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param moduleSpec
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSCoulomb3DDragManager( BSAbstractModuleSpec moduleSpec, BSCombinedChartNode chartNode ) {
        super( moduleSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Attaches drag handles to the specified potential.
     * Any existing handles are deleted.
     * 
     * @param potential
     */
    public void setPotential( BSCoulomb3DPotential potential ) {
        removeAllHandlesAndMarkers();
        if ( potential != null ) {
            
            BSAbstractModuleSpec moduleSpec = getModuleSpec();
            BSPotentialSpec potentialSpec = moduleSpec.getCoulomb3DSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( moduleSpec.isOffsetControlSupported() ) {
                BSAbstractHandle offsetHandle = new BSCoulomb3DOffsetHandle( potential, potentialSpec, chartNode );
                addHandle( offsetHandle );
            }
        }
    }
}
