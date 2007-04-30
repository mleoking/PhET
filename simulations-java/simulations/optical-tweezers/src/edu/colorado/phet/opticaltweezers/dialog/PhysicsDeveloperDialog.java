/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;

/**
 * Developer controls for PhysicsModule.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsDeveloperDialog extends JDialog {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _module;
    
    private LinearValueControl _brownianMotionScaleControl;
    private LogarithmicValueControl _dtSubdivisionThresholdControl;
    private LinearValueControl _numberOfDtSubdivisions;
   
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhysicsDeveloperDialog( Frame owner, PhysicsModule module ) {
        super( owner );
        setTitle( "Developer Controls");
        
        _module = module;
        
        JPanel inputPanel = createInputPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        setResizable( false );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {
        
        JPanel beadMotionPanel = new JPanel();
        {
            Bead bead = _module.getPhysicsModel().getBead();
            
            TitledBorder titledBorder = new TitledBorder( "Bead motion algorithm" );
            titledBorder.setTitleFont( TITLE_FONT );
            beadMotionPanel.setBorder( titledBorder );
            
            _brownianMotionScaleControl = new LinearValueControl( 0, 5, "Brownian motion scale:", "0.00", "" );
            _brownianMotionScaleControl.setValue( bead.getBrownianMotionScale() );
            _brownianMotionScaleControl.setUpDownArrowDelta( 0.01 );
            
            double dtMin = PhysicsDefaults.CLOCK_DT_RANGE.getMin();
            double dtMax = PhysicsDefaults.CLOCK_DT_RANGE.getMax();
            _dtSubdivisionThresholdControl = new LogarithmicValueControl( dtMin, dtMax, "dt subdivision threshold:", "0.0E0", "" );
            _dtSubdivisionThresholdControl.setValue( bead.getDtSubdivisionThreshold() );
            _dtSubdivisionThresholdControl.setTextFieldColumns( 4 );
            
            _numberOfDtSubdivisions = new LinearValueControl( 1, 1000, "number of dt subdivisions:", "###0", "" );
            _numberOfDtSubdivisions.setValue( bead.getNumberOfDtSubdivisions() );
            _numberOfDtSubdivisions.setUpDownArrowDelta( 1 );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( beadMotionPanel );
            layout.setInsets( new Insets( 0, 0, 0, 0 ) );
            beadMotionPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( _brownianMotionScaleControl, row++, column );
            layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _dtSubdivisionThresholdControl, row++, column );
            layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _numberOfDtSubdivisions, row++, column );
        }
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        {
           layout.addComponent( beadMotionPanel, row++, column );
        }
        
        // Event handling
        EventListener listener = new EventListener();
        _brownianMotionScaleControl.addChangeListener( listener );
        _dtSubdivisionThresholdControl.addChangeListener( listener );
        _numberOfDtSubdivisions.addChangeListener( listener );
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            if ( source == _brownianMotionScaleControl ) {
                handleBrownianMotionScaleControl();
            }
            else if ( source == _dtSubdivisionThresholdControl ) {
                handleDtDubdivisionThresholdControl();
            }
            else if ( source == _numberOfDtSubdivisions ) {
                handleNumberOfDtDubdivisionsControl();
            }
            else {
                throw new UnsupportedOperationException( "unsupported ChangeEvent source: " + source );
            }
        }
    }
    
    private void handleBrownianMotionScaleControl() {
        double value = _brownianMotionScaleControl.getValue();
        Bead bead = _module.getPhysicsModel().getBead();
        bead.setBrownianMotionScale( value );
    }
    
    private void handleDtDubdivisionThresholdControl() {
        double value = _dtSubdivisionThresholdControl.getValue();
        Bead bead = _module.getPhysicsModel().getBead();
        bead.setDtSubdivisionThreshold( value );
    }
    
    private void handleNumberOfDtDubdivisionsControl() {
        int value = (int)_numberOfDtSubdivisions.getValue();
        Bead bead = _module.getPhysicsModel().getBead();
        bead.setNumberOfDtSubdivisions( value );
    }
}
