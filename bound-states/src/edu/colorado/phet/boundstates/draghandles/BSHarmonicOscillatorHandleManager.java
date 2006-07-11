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

import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BSHarmonicOscillatorHandleManager
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorHandleManager extends BSAbstractHandleManager {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSHarmonicOscillatorOffsetHandle _offsetHandle;
    private BSHarmonicOscillatorAngularFrequencyHandle _angularFrequencyHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potentialSpec, chartNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSHarmonicOscillatorPotential potential ) {
        clear();
        if ( potential != null ) {

            BSPotentialSpec potentialSpec = getPotentialSpec();
            BSCombinedChartNode chartNode = getChartNode();
            
            if ( !potentialSpec.getOffsetRange().isZero() ) {
                _offsetHandle = new BSHarmonicOscillatorOffsetHandle( potential, potentialSpec, chartNode );
                addChild( _offsetHandle );
            }

            if ( !potentialSpec.getAngularFrequencyRange().isZero() ) {
                _angularFrequencyHandle = new BSHarmonicOscillatorAngularFrequencyHandle( potential, potentialSpec, chartNode );
                addChild( _angularFrequencyHandle );
            }
        }
    }
    
    private void clear() {
        removeAllChildren();
        _offsetHandle = null;
        _angularFrequencyHandle = null;
    }
    
    //----------------------------------------------------------------------------
    // IHandleManager implementation
    //----------------------------------------------------------------------------
    
    public void updateLayout() {
        updateClip();
        if ( _offsetHandle != null ) {
            _offsetHandle.updateDragBounds();
        }
        if ( _angularFrequencyHandle != null ) {
            _angularFrequencyHandle.updateDragBounds();
        }
    }
    
    public PNode getHelpNode() {
        return _offsetHandle;
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        if ( _offsetHandle != null ) {
            _offsetHandle.setColorScheme( colorScheme );
        }
        if ( _angularFrequencyHandle != null ) {
            _angularFrequencyHandle.setColorScheme( colorScheme );
        }
    }
}
