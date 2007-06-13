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
 * BeadMotionControlPanel is a set of developer controls for the bead motion model.
 * This panel is for developers only, and it not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeadMotionDeveloperPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean TOOL_TIPS_ENABLED = false;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;

    private LogarithmicValueControl _dtSubdivisionThresholdControl;
    private LinearValueControl _numberOfDtSubdivisions;
    private LinearValueControl _brownianMotionScaleControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BeadMotionDeveloperPanel( Font titleFont, Font controlFont, Bead bead ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        TitledBorder border = new TitledBorder( "Bead motion model" );
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
        
        if ( TOOL_TIPS_ENABLED ) {
            _dtSubdivisionThresholdControl.setToolTipText( 
                    "<html>When the clockstep is >= this value, the motion algorithm<br>" +
                    "is run multiple times each time the clock ticks</html>" );
            _numberOfDtSubdivisions.setToolTipText( 
                    "<html>Number of times to run the motion algorithm<br>" + 
                    "when subdividing the clockstep</html>" );
            _brownianMotionScaleControl.setToolTipText( 
                    "<html>How much to scale the Brownian motion.<br>" + 
                    "Larger values cause more exaggerated motion</html>" );
        }
        
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
    
    public void cleanup() {
        _bead.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
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
        }
    }
}
