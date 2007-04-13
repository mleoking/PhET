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
 * BSHarmonicOscillatorDragManager manages drag handles for 
 * a potential composed of Harmonic Oscillator wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorDragManager extends BSAbstractDragManager {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potentialSpec describes ranges for potential's attributes
     * @param chartNode the chart that the drag handles and markers pertain to
     */
    public BSHarmonicOscillatorDragManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
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
