/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls for the weak acid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class WeakAcidControls extends JPanel {
    
    private final WeakAcid weakAcid;
    private final LogarithmicValueControl concentrationControl, strengthControl;
    private final ColorControl solutionColorControl;
    
    public WeakAcidControls( final JFrame parentFrame, final WeakAcid weakAcid ) {
        setBorder( new TitledBorder( "Solution (weak acid)" ) );
        
        this.weakAcid = weakAcid;
        weakAcid.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        DoubleRange concentrationRange = MGPConstants.WEAK_ACID_CONCENTRATION_RANGE;
        concentrationControl = new LogarithmicValueControl( concentrationRange.getMin(), concentrationRange.getMax(), "concentration:", "0.0E0", "mol/L", new HorizontalLayoutStrategy() );
        concentrationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                weakAcid.setConcentration( concentrationControl.getValue() );
            }
        });
        
        DoubleRange strengthRange = MGPConstants.WEAK_ACID_STRENGTH_RANGE;
        strengthControl = new LogarithmicValueControl( strengthRange.getMin(), strengthRange.getMax(), "strength:", "0.0E0", "", new HorizontalLayoutStrategy() );
        strengthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                weakAcid.setStrength( strengthControl.getValue() );
            }
        });
        
        solutionColorControl = new ColorControl( parentFrame, "color:", weakAcid.getColor() );
        solutionColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                weakAcid.setColor( solutionColorControl.getColor() );
            }
        } );
        
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
        row++;
        column = 0;
        layout.addComponent( solutionColorControl, row, column++ );
        
        // default state
        updateControls();
    }
    
    private void updateControls() {
        concentrationControl.setValue( weakAcid.getConcentration() );
        strengthControl.setValue( weakAcid.getStrength() );
        solutionColorControl.setColor( weakAcid.getColor() );
    }
}
