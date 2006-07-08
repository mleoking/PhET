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
import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;


public class BSCoulomb1DHandleManager extends BSAbstractHandleManager {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_VALUES = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSCoulomb1DOffsetHandle _offsetHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSCoulomb1DHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSCoulomb1DPotential potential ) {

        removeAllChildren();
        
        if ( potential != null ) {
            _offsetHandle = new BSCoulomb1DOffsetHandle( potential, _potentialSpec, _chartNode );
            _offsetHandle.setValueVisible( DEBUG_SHOW_VALUES );
            addChild( _offsetHandle );
            _offsetHandle.setVisible( _potentialSpec.getOffsetRange().getMin() != _potentialSpec.getOffsetRange().getMax() );
        }
    }
    
    //----------------------------------------------------------------------------
    // IHandleManager implementation
    //----------------------------------------------------------------------------
   
    public void updateDragBounds() {
        if ( _offsetHandle != null ) {
            _offsetHandle.updateDragBounds();
        }
    }
    
    public PNode getHelpNode() {
        return _offsetHandle; //XXX this doesn't exist for "Two" and "Many" modules!
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        //XXX
    }
}
