/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.Bead;

/**
 * BeadMotionControlPanel is a set of developer controls for the bead motion model.
 * This panel is for developers only, and it not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeadMotionControlPanel extends JPanel {

    private Bead _bead;

    private LogarithmicValueControl _dtSubdivisionThresholdControl;
    private LinearValueControl _numberOfDtSubdivisions;
    private LinearValueControl _brownianMotionScaleControl;
    
    public BeadMotionControlPanel( Font titleFont, Font controlFont, Bead bead ) {
        super();
        
        _bead = bead;
        
        TitledBorder border = new TitledBorder( "Bead motion algorithm:" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        double dtMin = PhysicsDefaults.CLOCK_DT_RANGE.getMax() * 1E-2;
        double dtMax = PhysicsDefaults.CLOCK_DT_RANGE.getMax();
        _dtSubdivisionThresholdControl = new LogarithmicValueControl( dtMin, dtMax, "dt subdivision threshold:", "0.0E0", "" );
        _dtSubdivisionThresholdControl.setTextFieldColumns( 4 );
        _dtSubdivisionThresholdControl.setFont( controlFont );
        _dtSubdivisionThresholdControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleDtDubdivisionThresholdControl();
            }
        });
        
        _numberOfDtSubdivisions = new LinearValueControl( 1, 1000, "# of dt subdivisions:", "###0", "" );
        _numberOfDtSubdivisions.setUpDownArrowDelta( 1 );
        _numberOfDtSubdivisions.setFont( controlFont );
        _numberOfDtSubdivisions.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleNumberOfDtDubdivisionsControl();
            }
        });
        
        _brownianMotionScaleControl = new LinearValueControl( 0, 5, "Brownian motion scale:", "0.00", "" );
        _brownianMotionScaleControl.setUpDownArrowDelta( 0.01 );
        _brownianMotionScaleControl.setFont( controlFont );
        _brownianMotionScaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleBrownianMotionScaleControl();
            }
        });
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 0, 0, 0 ) );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _dtSubdivisionThresholdControl, row++, column );
        layout.addComponent( _numberOfDtSubdivisions, row++, column );
        layout.addComponent( _brownianMotionScaleControl, row++, column );
        
        // Default state
        _dtSubdivisionThresholdControl.setValue( _bead.getDtSubdivisionThreshold() );
        _numberOfDtSubdivisions.setValue( _bead.getNumberOfDtSubdivisions() );
        _brownianMotionScaleControl.setValue( _bead.getBrownianMotionScale() );
    }
    
    private void handleBrownianMotionScaleControl() {
        double value = _brownianMotionScaleControl.getValue();
        _bead.setBrownianMotionScale( value );
    }
    
    private void handleDtDubdivisionThresholdControl() {
        double value = _dtSubdivisionThresholdControl.getValue();
        _bead.setDtSubdivisionThreshold( value );
    }
    
    private void handleNumberOfDtDubdivisionsControl() {
        int value = (int)_numberOfDtSubdivisions.getValue();
        _bead.setNumberOfDtSubdivisions( value );
    }
}
