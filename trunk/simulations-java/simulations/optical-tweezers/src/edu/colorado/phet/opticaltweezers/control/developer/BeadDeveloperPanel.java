/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.developer;

import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockListener;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.view.ChargeDistributionNode;

/**
 * BeadDeveloperPanel contains developer controls for the bead model.
 * This panel is for developers only, and it is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeadDeveloperPanel extends JPanel implements Observer, ConstantDtClockListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private OTClock _clock;
    private Bead _bead;
    private Laser _laser;
    private ChargeDistributionNode _chargeDistributionNode;

    private LinearValueControl _brownianMotionScaleControl;
    private LogarithmicValueControl _dtSubdivisionThresholdControl;
    private LinearValueControl _numberOfDtSubdivisions;
    private LogarithmicValueControl _verletDtSubdivisionThresholdControl;
    private LinearValueControl _verletNumberOfDtSubdivisions;
    private LogarithmicValueControl _verletMotionScaleControl;
    private LogarithmicValueControl _vacuumFastThresholdControl;
    private LogarithmicValueControl _vacuumFastDtControl;
    private LinearValueControl _vacuumFastPowerControl;
    private LinearValueControl _chargeMotionScaleControl;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BeadDeveloperPanel( String title, Font titleFont, Font controlFont, OTClock clock, 
            Bead bead, Laser laser, ChargeDistributionNode chargeDistributionNode, boolean showVacuumControls ) {
        super();

        _clock = clock;
        _clock.addConstantDtClockListener( this );

        _bead = bead;
        _bead.addObserver( this );

        _laser = laser;
        if ( _laser != null ) {
            _laser.addObserver( this );
        }

        _chargeDistributionNode = chargeDistributionNode;

        TitledBorder border = new TitledBorder( title );
        border.setTitleFont( titleFont );
        this.setBorder( border );

        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        this.setLayout( layout );
        int row = 0;
        int column = 0;

        double min, max;

        min = bead.getBrownianMotionScaleRange().getMin();
        max = bead.getBrownianMotionScaleRange().getMax();
        _brownianMotionScaleControl = new LinearValueControl( min, max, "Brownian scale:", "0.0", "" );
        _brownianMotionScaleControl.setUpDownArrowDelta( 0.1 );
        _brownianMotionScaleControl.setFont( controlFont );
        _brownianMotionScaleControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleBrownianMotionScaleControl();
            }
        } );
        _brownianMotionScaleControl.setValue( _bead.getBrownianMotionScale() );
        layout.addComponent( _brownianMotionScaleControl, row++, column );

        min = bead.getNumberOfDtSubdivisionsRange().getMin();
        max = bead.getNumberOfDtSubdivisionsRange().getMax();
        _numberOfDtSubdivisions = new LinearValueControl( min, max, "# dt subdivisions:", "###0", "" );
        _numberOfDtSubdivisions.setUpDownArrowDelta( 1 );
        _numberOfDtSubdivisions.setFont( controlFont );
        _numberOfDtSubdivisions.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleNumberOfDtDubdivisionsControl();
            }
        } );
        _numberOfDtSubdivisions.setValue( _bead.getNumberOfDtSubdivisions() );
        layout.addComponent( _numberOfDtSubdivisions, row++, column );

        min = bead.getDtSubdivisionThresholdRange().getMin();
        max = bead.getDtSubdivisionThresholdRange().getMax();
        _dtSubdivisionThresholdControl = new LogarithmicValueControl( min, max, "subdivision threshold:", "0.0E0", "" );
        _dtSubdivisionThresholdControl.setTextFieldColumns( 4 );
        _dtSubdivisionThresholdControl.setFont( controlFont );
        _dtSubdivisionThresholdControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleDtDubdivisionThresholdControl();
            }
        } );
        _dtSubdivisionThresholdControl.setValue( _bead.getDtSubdivisionThreshold() );
        layout.addComponent( _dtSubdivisionThresholdControl, row++, column );

        if ( showVacuumControls ) {

            min = bead.getVerletNumberOfDtSubdivisionsRange().getMin();
            max = bead.getVerletNumberOfDtSubdivisionsRange().getMax();
            _verletNumberOfDtSubdivisions = new LinearValueControl( min, max, "Verlet # dt subdivisions:", "###0", "" );
            _verletNumberOfDtSubdivisions.setUpDownArrowDelta( 1 );
            _verletNumberOfDtSubdivisions.setFont( controlFont );
            _verletNumberOfDtSubdivisions.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleVerletNumberOfDtDubdivisionsControl();
                }
            } );
            _verletNumberOfDtSubdivisions.setValue( _bead.getVerletNumberOfDtSubdivisions() );
            layout.addComponent( _verletNumberOfDtSubdivisions, row++, column );

            min = bead.getVerletDtSubdivisionThresholdRange().getMin();
            max = bead.getVerletDtSubdivisionThresholdRange().getMax();
            _verletDtSubdivisionThresholdControl = new LogarithmicValueControl( min, max, "Verlet subdivision threshold:", "0.0E0", "" );
            _verletDtSubdivisionThresholdControl.setTextFieldColumns( 4 );
            _verletDtSubdivisionThresholdControl.setFont( controlFont );
            _verletDtSubdivisionThresholdControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleVerletDtDubdivisionThresholdControl();
                }
            } );
            _verletDtSubdivisionThresholdControl.setValue( _bead.getVerletDtSubdivisionThreshold() );
            layout.addComponent( _verletDtSubdivisionThresholdControl, row++, column );

            min = bead.getVerletAccelerationScaleRange().getMin();
            max = bead.getVerletAccelerationScaleRange().getMax();
            _verletMotionScaleControl = new LogarithmicValueControl( min, max, "Verlet scale:", "0.0E0", "" );
            _verletMotionScaleControl.setUpDownArrowDelta( 0.000000001 );
            _verletMotionScaleControl.setFont( controlFont );
            _verletMotionScaleControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleVerletAccelerationScaleControl();
                }
            } );
            _verletMotionScaleControl.setValue( _bead.getVerletAccelerationScale() );
            layout.addComponent( _verletMotionScaleControl, row++, column );

            min = bead.getVacuumFastThresholdRange().getMin();
            max = bead.getVacuumFastThresholdRange().getMax();
            _vacuumFastThresholdControl = new LogarithmicValueControl( min, max, "vacuum fast threshold:", "0.0E0", "" );
            _vacuumFastThresholdControl.setUpDownArrowDelta( 0.000000001 );
            _vacuumFastThresholdControl.setFont( controlFont );
            _vacuumFastThresholdControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleVacuumFastThresholdControl();
                }
            } );
            _vacuumFastThresholdControl.setValue( _bead.getVacuumFastThreshold() );
            updateVaccumFastThresholdIndicator();
            layout.addComponent( _vacuumFastThresholdControl, row++, column );

            min = bead.getVacuumFastDtRange().getMin();
            max = bead.getVacuumFastDtRange().getMax();
            _vacuumFastDtControl = new LogarithmicValueControl( min, max, "vacuum fast dt:", "0.0E0", "" );
            _vacuumFastDtControl.setUpDownArrowDelta( 0.000000001 );
            _vacuumFastDtControl.setFont( controlFont );
            _vacuumFastDtControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleVacuumFastDtControl();
                }
            } );
            _vacuumFastDtControl.setValue( _bead.getVacuumFastDt() );
            layout.addComponent( _vacuumFastDtControl, row++, column );

            min = bead.getVacuumFastPowerRange().getMin();
            max = bead.getVacuumFastPowerRange().getMax();
            _vacuumFastPowerControl = new LinearValueControl( min, max, "vacuum fast power:", "##0", "mW" );
            _vacuumFastPowerControl.setUpDownArrowDelta( 1 );
            _vacuumFastPowerControl.setFont( controlFont );
            _vacuumFastPowerControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleVacuumFastPowerControl();
                }
            } );
            _vacuumFastPowerControl.setValue( _bead.getVacuumFastPower() );
            layout.addComponent( _vacuumFastPowerControl, row++, column );
        }

        if ( chargeDistributionNode != null ) {
            min = chargeDistributionNode.getChargeMotionScaleRange().getMin();
            max = chargeDistributionNode.getChargeMotionScaleRange().getMax();
            _chargeMotionScaleControl = new LinearValueControl( min, max, "charge motion scale:", "0.00", "" );
            _chargeMotionScaleControl.setUpDownArrowDelta( 0.01 );
            _chargeMotionScaleControl.setFont( controlFont );
            _chargeMotionScaleControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleChargeMotionScaleControl();
                }
            } );
            _chargeMotionScaleControl.setValue( _chargeDistributionNode.getChargeMotionScale() );
            layout.addComponent( _chargeMotionScaleControl, row++, column );
        }
    }

    public void cleanup() {
        _clock.removeConstantDtClockListener( this );
        _bead.deleteObserver( this );
        if ( _laser != null ) {
            _laser.deleteObserver( this );
        }
    }

    //----------------------------------------------------------------------------
    // Setters
    //----------------------------------------------------------------------------

    public void setChargeMotionScale( double chargeMotionScale ) {
        _chargeMotionScaleControl.setValue( chargeMotionScale );
        handleChargeMotionScaleControl();
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

    private void handleVerletDtDubdivisionThresholdControl() {
        double value = _verletDtSubdivisionThresholdControl.getValue();
        _bead.deleteObserver( this );
        _bead.setVerletDtSubdivisionThreshold( value );
        _bead.addObserver( this );
    }

    private void handleVerletNumberOfDtDubdivisionsControl() {
        int value = (int) Math.round( _verletNumberOfDtSubdivisions.getValue() );
        _bead.deleteObserver( this );
        _bead.setVerletNumberOfDtSubdivisions( value );
        _bead.addObserver( this );
    }

    private void handleVerletAccelerationScaleControl() {
        double value = _verletMotionScaleControl.getValue();
        _bead.deleteObserver( this );
        _bead.setVerletAccelerationScale( value );
        _bead.addObserver( this );
    }

    private void handleVacuumFastThresholdControl() {
        double value = _vacuumFastThresholdControl.getValue();
        _bead.deleteObserver( this );
        _bead.setVacuumFastThreshold( value );
        _bead.addObserver( this );
        updateVaccumFastThresholdIndicator();
    }

    private void handleVacuumFastDtControl() {
        double value = _vacuumFastDtControl.getValue();
        _bead.deleteObserver( this );
        _bead.setVacuumFastDt( value );
        _bead.addObserver( this );
    }

    private void handleVacuumFastPowerControl() {
        double value = _vacuumFastPowerControl.getValue();
        _bead.deleteObserver( this );
        _bead.setVacuumFastPower( value );
        _bead.addObserver( this );
    }

    private void handleChargeMotionScaleControl() {
        double value = _chargeMotionScaleControl.getValue();
        _chargeDistributionNode.setChargeMotionScale( value );
    }

    /*
     * Set the control's text field to green when the threshold is currently exceeded.
     */
    private void updateVaccumFastThresholdIndicator() {
        if ( _vacuumFastThresholdControl != null ) {
            if ( _clock.getDt() * _laser.getPower() > _vacuumFastThresholdControl.getValue() ) {
                _vacuumFastThresholdControl.getTextField().setBackground( Color.GREEN );
            }
            else {
                _vacuumFastThresholdControl.getTextField().setBackground( Color.WHITE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------

    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_BROWNIAN_MOTION_SCALE ) {
                _brownianMotionScaleControl.setValue( _bead.getBrownianMotionScale() );
            }
            else if ( arg == Bead.PROPERTY_DT_SUBDIVISION_THRESHOLD ) {
                _dtSubdivisionThresholdControl.setValue( _bead.getDtSubdivisionThreshold() );
            }
            else if ( arg == Bead.PROPERTY_NUMBER_OF_DT_SUBDIVISIONS ) {
                _numberOfDtSubdivisions.setValue( _bead.getNumberOfDtSubdivisions() );
            }
            else if ( arg == Bead.PROPERTY_VERLET_DT_SUBDIVISION_THRESHOLD ) {
                _verletDtSubdivisionThresholdControl.setValue( _bead.getVerletDtSubdivisionThreshold() );
            }
            else if ( arg == Bead.PROPERTY_VERLET_NUMBER_OF_DT_SUBDIVISIONS ) {
                _verletNumberOfDtSubdivisions.setValue( _bead.getVerletNumberOfDtSubdivisions() );
            }
            else if ( arg == Bead.PROPERTY_VERLET_ACCELERATION_SCALE ) {
                _verletMotionScaleControl.setValue( _bead.getVerletAccelerationScale() );
            }
            else if ( arg == Bead.PROPERTY_VACUUM_FAST_THRESHOLD ) {
                _vacuumFastThresholdControl.setValue( _bead.getVacuumFastThreshold() );
            }
            else if ( arg == Bead.PROPERTY_VACUUM_FAST_THRESHOLD ) {
                _vacuumFastDtControl.setValue( _bead.getVacuumFastDt() );
            }
            else if ( arg == Bead.PROPERTY_VACUUM_FAST_THRESHOLD ) {
                _vacuumFastPowerControl.setValue( _bead.getVacuumFastPower() );
            }
        }
        else if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POWER ) {
                updateVaccumFastThresholdIndicator();
            }
        }
    }

    //----------------------------------------------------------------------------
    // ConstantDtClockListener implementation
    //----------------------------------------------------------------------------

    public void delayChanged( ConstantDtClockEvent event ) {
    // do nothing
    }

    public void dtChanged( ConstantDtClockEvent event ) {
        updateVaccumFastThresholdIndicator();
    }
}
