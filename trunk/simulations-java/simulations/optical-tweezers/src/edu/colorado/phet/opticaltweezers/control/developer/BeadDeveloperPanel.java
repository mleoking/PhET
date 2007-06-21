/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Font;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.model.Bead;

/**
 * BeadDeveloperPanel contains developer controls for the bead model.
 * This panel is for developers only, and it is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeadDeveloperPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;

    private LogarithmicValueControl _dtSubdivisionThresholdControl;
    private LinearValueControl _numberOfDtSubdivisions;
    private LinearValueControl _brownianMotionScaleControl;
    private LogarithmicValueControl _verletMotionScaleControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BeadDeveloperPanel( Font titleFont, Font controlFont, Bead bead ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        TitledBorder border = new TitledBorder( "Bead model" );
        border.setTitleFont( titleFont );
        this.setBorder( border );
        
        double min = bead.getDtSubdivisionThresholdRange().getMin();
        double max = bead.getDtSubdivisionThresholdRange().getMax();
        _dtSubdivisionThresholdControl = new LogarithmicValueControl( min, max, "dt threshold:", "0.0E0", "" );
        _dtSubdivisionThresholdControl.setTextFieldColumns( 4 );
        _dtSubdivisionThresholdControl.setFont( controlFont );
        _dtSubdivisionThresholdControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleDtDubdivisionThresholdControl();
            }
        });
        
        min = bead.getNumberOfDtSubdivisionsRange().getMin();
        max = bead.getNumberOfDtSubdivisionsRange().getMax();
        _numberOfDtSubdivisions = new LinearValueControl( min, max, "# subdivisions:", "###0", "" );
        _numberOfDtSubdivisions.setUpDownArrowDelta( 1 );
        _numberOfDtSubdivisions.setFont( controlFont );
        _numberOfDtSubdivisions.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleNumberOfDtDubdivisionsControl();
            }
        });
        
        min = bead.getBrownianMotionScaleRange().getMin();
        max = bead.getBrownianMotionScaleRange().getMax();
        _brownianMotionScaleControl = new LinearValueControl( min, max, "Brownian scale:", "0.0", "" );
        _brownianMotionScaleControl.setUpDownArrowDelta( 0.1 );
        _brownianMotionScaleControl.setFont( controlFont );
        _brownianMotionScaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleBrownianMotionScaleControl();
            }
        });
        
        min = bead.getVerletAccelerationScaleRange().getMin();
        max = bead.getVerletAccelerationScaleRange().getMax();
        _verletMotionScaleControl = new LogarithmicValueControl( min, max, "Verlet scale:", "0.0E0", "" );
        _verletMotionScaleControl.setUpDownArrowDelta( 0.000000001 );
        _verletMotionScaleControl.setFont( controlFont );
        _verletMotionScaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleVerletAccelerationScaleControl();
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
        layout.addComponent( _verletMotionScaleControl, row++, column );
        
        // Default state
        _dtSubdivisionThresholdControl.setValue( _bead.getDtSubdivisionThreshold() );
        _numberOfDtSubdivisions.setValue( _bead.getNumberOfDtSubdivisions() );
        _brownianMotionScaleControl.setValue( _bead.getBrownianMotionScale() );
        _verletMotionScaleControl.setValue( _bead.getVerletAccelerationScale() );
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleBrownianMotionScaleControl() {
        double value = _brownianMotionScaleControl.getValue();
        _bead.deleteObserver( this );
        _bead.setBrownianMotionScale( value );
        _bead.addObserver( this );
    }
    
    private void handleDtDubdivisionThresholdControl() {
        double value = _dtSubdivisionThresholdControl.getValue();
        _bead.deleteObserver( this );
        _bead.setDtSubdivisionThreshold( value );
        _bead.addObserver( this );
    }
    
    private void handleNumberOfDtDubdivisionsControl() {
        int value = (int) Math.round( _numberOfDtSubdivisions.getValue() );
        _bead.deleteObserver( this );
        _bead.setNumberOfDtSubdivisions( value );
        _bead.addObserver( this );
    }
    
    private void handleVerletAccelerationScaleControl() {
        double value = _verletMotionScaleControl.getValue();
        _bead.deleteObserver( this );
        _bead.setVerletAccelerationScale( value );
        _bead.addObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_DT_SUBDIVISION_THRESHOLD ) {
                _dtSubdivisionThresholdControl.setValue( _bead.getDtSubdivisionThreshold() );
            }
            else if ( arg == Bead.PROPERTY_NUMBER_OF_DT_SUBDIVISION ) {
                _numberOfDtSubdivisions.setValue( _bead.getNumberOfDtSubdivisions() );
            }
            else if ( arg == Bead.PROPERTY_BROWNIAN_MOTION_SCALE ) {
                _brownianMotionScaleControl.setValue( _bead.getBrownianMotionScale() );
            }
            else if ( arg == Bead.PROPERTY_VERLET_ACCELERATION_SCALE ) {
                _verletMotionScaleControl.setValue( _bead.getVerletAccelerationScale() );
            }
        }
    }
}
