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
import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;


public class BSHarmonicOscillatorHandleManager extends PNode {
    
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
//    private BSHarmonicOscillatorWidthHandle _widthHandle;
    
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

//            _widthHandle = new BSHarmonicOscillatorWidthHandle( potential, _potentialSpec, _chartNode );
//            _widthHandle.setValueVisible( DEBUG_SHOW_VALUES );
//            addChild( _widthHandle );
//            _widthHandle( _potentialSpec.getWidthRange().getMin() != _potentialSpec.getWidthRange().getMax() );
        }
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        //XXX
    }
       
    public void updateDragBounds() {
        if ( _offsetHandle != null ) {
            _offsetHandle.updateDragBounds();
        }
//        if ( _widthHandle != null ) {
//            _widthHandle.updateDragBounds();
//        }
    }
}
