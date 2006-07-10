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
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSHarmonicOscillatorOffsetHandle _offsetHandle;
    private BSHarmonicOscillatorAngularFrequencyHandle _angularFrequencyHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSHarmonicOscillatorHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSHarmonicOscillatorPotential potential ) {
        clear();
        if ( potential != null ) {

            if ( !_potentialSpec.getOffsetRange().isZero() ) {
                _offsetHandle = new BSHarmonicOscillatorOffsetHandle( potential, _potentialSpec, _chartNode );
                addChild( _offsetHandle );
            }

            if ( !_potentialSpec.getAngularFrequencyRange().isZero() ) {
                _angularFrequencyHandle = new BSHarmonicOscillatorAngularFrequencyHandle( potential, _potentialSpec, _chartNode );
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
    
    public void updateDragBounds() {
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
