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
import edu.colorado.phet.boundstates.model.BSSquarePotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;


public class BSSquareHandleManager extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_VALUES = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSPotentialSpec _potentialSpec;
    private BSCombinedChartNode _chartNode;
    
    private BSSquareOffsetHandle _offsetHandle;
    private BSSquareHeightHandle _heightHandle;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareHandleManager( BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super();
        _potentialSpec = potentialSpec;
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSSquarePotential potential ) {

        removeAllChildren();

        _offsetHandle = new BSSquareOffsetHandle( potential, _potentialSpec, _chartNode );
        _offsetHandle.setValueVisible( DEBUG_SHOW_VALUES );
        addChild( _offsetHandle );

        _heightHandle = new BSSquareHeightHandle( potential, _potentialSpec, _chartNode );
        _heightHandle.setValueVisible( DEBUG_SHOW_VALUES );
        addChild( _heightHandle );
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        //XXX
    }
       
    public void updateDragBounds() {
        _offsetHandle.updateDragBounds();
        _heightHandle.updateDragBounds();
    }
}
