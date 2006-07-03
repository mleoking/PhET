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
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;
import edu.umd.cs.piccolo.PNode;


public class BSAsymmetricHandleManager extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_VALUES = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSCombinedChartNode _chartNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAsymmetricHandleManager( BSCombinedChartNode chartNode ) {
        super();
        _chartNode = chartNode;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotential( BSAsymmetricPotential potential ) {

        removeAllChildren();
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        //XXX
    }
       
    public void updateDragBounds() {
    }
}
