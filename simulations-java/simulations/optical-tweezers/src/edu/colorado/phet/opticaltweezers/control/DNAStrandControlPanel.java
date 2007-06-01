/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.model.DNAStrand;

/**
 * DNAStrandControlPanel constains developer controls for the DNA strand model.
 * This panel is for developers only, and it not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrandControlPanel extends JPanel {

    private DNAStrand _dnaStrand;
    
    private LinearValueControl _springConstantControl;
    private LinearValueControl _dampingConstantControl;
    private LinearValueControl _kickConstant;
    
    public DNAStrandControlPanel( Font titleFont, Font controlFont, DNAStrand dnaStrand ) {
        super();
        
        _dnaStrand = dnaStrand;
        
        TitledBorder border = new TitledBorder( "DNA strand behavior:" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        _springConstantControl = new LinearValueControl( 2.0, 10.0, "spring:", "0.00", "" );
        _springConstantControl.setUpDownArrowDelta( 0.01 );
        _springConstantControl.setFont( controlFont );
        _springConstantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleSpringConstantControl();
            }
        });
        
        _dampingConstantControl = new LinearValueControl( 0.1, 0.3, "damping:", "0.00", "" );
        _dampingConstantControl.setUpDownArrowDelta( 0.01 );
        _dampingConstantControl.setFont( controlFont );
        _dampingConstantControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleDampingConstantControl();
            }
        });
        
        _kickConstant = new LinearValueControl( 0.25, 0.75, "kick:", "0.00", "" );
        _kickConstant.setUpDownArrowDelta( 0.01 );
        _kickConstant.setFont( controlFont );
        _kickConstant.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleKickControl();
            }
        });
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _springConstantControl, row++, column );
        layout.addComponent( _dampingConstantControl, row++, column );
        layout.addComponent( _kickConstant, row++, column );
        
        // Default state
        _springConstantControl.setValue( _dnaStrand.getSpringConstant() );
        _dampingConstantControl.setValue( _dnaStrand.getDampingConstant() );
        _kickConstant.setValue( _dnaStrand.getKickConstant() );
    }
    
    private void handleSpringConstantControl() {
        double value = _springConstantControl.getValue();
        _dnaStrand.setSpringConstant( value );
    }
    
    private void handleDampingConstantControl() {
        double value = _dampingConstantControl.getValue();
        _dnaStrand.setDampingConstant( value );
    }
    
    private void handleKickControl() {
        double value = _kickConstant.getValue();
        _dnaStrand.setKickConstant( value );
    }
}
