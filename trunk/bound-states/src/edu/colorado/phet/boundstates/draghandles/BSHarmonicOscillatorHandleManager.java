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


public class BSHarmonicOscillatorHandleManager extends PNode implements IHandleManager {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_VALUES = true;
    
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

        removeAllChildren();
        
        if ( potential != null ) {
            _offsetHandle = new BSHarmonicOscillatorOffsetHandle( potential, _potentialSpec, _chartNode );
            _offsetHandle.setValueVisible( DEBUG_SHOW_VALUES );
            addChild( _offsetHandle );
            _offsetHandle.setVisible( _potentialSpec.getOffsetRange().getMin() != _potentialSpec.getOffsetRange().getMax() );

            _angularFrequencyHandle = new BSHarmonicOscillatorAngularFrequencyHandle( potential, _potentialSpec, _chartNode );
            _angularFrequencyHandle.setValueVisible( DEBUG_SHOW_VALUES );
            addChild( _angularFrequencyHandle );
            _angularFrequencyHandle.setVisible( _potentialSpec.getAngularFrequencyRange().getMin() != _potentialSpec.getAngularFrequencyRange().getMax() );
        }
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
        //XXX
    }
}
