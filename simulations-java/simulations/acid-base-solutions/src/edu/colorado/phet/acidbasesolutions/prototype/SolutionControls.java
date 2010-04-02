/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls for the solution (weak acid).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SolutionControls extends JPanel {
    
    private final WeakAcid weakAcid;
    private final LogarithmicValueControl concentrationControl, strengthControl;
    
    public SolutionControls( WeakAcid solution ) {
        setBorder( new TitledBorder( "Solution (weak acid)" ) );
        
        this.weakAcid = solution;
        
        DoubleRange concentrationRange = ProtoConstants.WEAK_ACID_CONCENTRATION_RANGE;
        concentrationControl = new LogarithmicValueControl( concentrationRange.getMin(), concentrationRange.getMax(), "concentration:", "##0E0", "mol/L", new HorizontalLayoutStrategy() );
        
        DoubleRange strengthRange = ProtoConstants.WEAK_ACID_STRENGTH_RANGE;
        strengthControl = new LogarithmicValueControl( strengthRange.getMin(), strengthRange.getMax(), "strength:", "##0E0", "", new HorizontalLayoutStrategy() );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( concentrationControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( strengthControl, row, column++ );
        
        // default state
        concentrationControl.setValue( solution.getConcentration() );
        strengthControl.setValue( solution.getStrength() );
    }
    
    private void updateSolution() {
        weakAcid.setConcentration( concentrationControl.getValue() );
        weakAcid.setStrength( strengthControl.getValue() );
    }
}
