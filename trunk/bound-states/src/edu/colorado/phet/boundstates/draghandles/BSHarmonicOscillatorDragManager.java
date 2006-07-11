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

import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSHarmonicOscillatorDragManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSHarmonicOscillatorPotential potential ) {
        removeAllHandlesAndMarkers();
        if ( potential != null ) {

            BSPotentialSpec potentialSpec = getPotentialSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( !potentialSpec.getOffsetRange().isZero() ) {
                BSAbstractHandle offsetHandle = new BSHarmonicOscillatorOffsetHandle( potential, potentialSpec, chartNode );
                addHandle( offsetHandle );
            }

            if ( !potentialSpec.getAngularFrequencyRange().isZero() ) {
                BSAbstractHandle angularFrequencyHandle = new BSHarmonicOscillatorAngularFrequencyHandle( potential, potentialSpec, chartNode );
                addHandle( angularFrequencyHandle );
            }
        }
    }
}
