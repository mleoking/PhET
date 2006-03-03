/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.quantumtunneling.model.RichardsonSolver;


/**
 * RichardsonControls is a set of developer controls for the Richardson algorithm.
 * The controls are displayed in a window, and nothing is localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RichardsonControls extends JDialog {

    private static final double MASS_MIN = 0.1;
    private static final double MASS_MAX = 100.0;
    private static final double MASS_DELTA = 0.1;
    private static final String MASS_FORMAT = "0.0";
   
    private static final double HBAR_MIN = 0.001;
    private static final double HBAR_MAX = 10.000;
    private static final double HBAR_DELTA = 0.001;
    private static final String HBAR_FORMAT = "0.000";
    
    private static final double DT_MIN = 0.0001;
    private static final double DT_MAX = 10.00;
    private static final double DT_DELTA = 0.0001;
    private static final String DT_FORMAT = "0.0000";
    
    private static final int STEPS_MIN = 1;
    private static final int STEPS_MAX = 100;
    private static final int STEPS_DELTA = 1;
    private static final String STEPS_FORMAT = "0";
    
    private static final Dimension SPINNER_SIZE = new Dimension( 125, 25 );
    
    private RichardsonSolver _solver;
    
    private DoubleSpinner _massSpinner;
    private DoubleSpinner _hbarSpinner;
    private DoubleSpinner _dtSpinner;
    private DoubleSpinner _stepsSpinner;

    public RichardsonControls( Frame owner, RichardsonSolver solver ) {
        super( owner, "Richardson controls" );
        
        _solver = solver;
        
        // Subpanel
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        {
            JLabel massLabel = new JLabel( "mass:" );
            _massSpinner = new DoubleSpinner( _solver.getMass(), MASS_MIN, MASS_MAX, MASS_DELTA, MASS_FORMAT, SPINNER_SIZE );
            DecimalFormat massFormat = new DecimalFormat( MASS_FORMAT );
            JLabel massRange = new JLabel( "(" + massFormat.format( MASS_MIN ) + "-" + massFormat.format( MASS_MAX ) + ")" );
            
            JLabel hbarLabel = new JLabel( "hbar:" );
            _hbarSpinner = new DoubleSpinner( _solver.getHbar(), HBAR_MIN, HBAR_MAX, HBAR_DELTA, HBAR_FORMAT, SPINNER_SIZE );
            DecimalFormat hbarFormat = new DecimalFormat( HBAR_FORMAT );
            JLabel hbarRange = new JLabel( "(" + hbarFormat.format( HBAR_MIN ) + "-" + hbarFormat.format( HBAR_MAX ) + ")" );
            
            JLabel dtLabel = new JLabel( "dt:" );
            _dtSpinner = new DoubleSpinner( _solver.getDt(), DT_MIN, DT_MAX, DT_DELTA, DT_FORMAT, SPINNER_SIZE );
            DecimalFormat dtFormat = new DecimalFormat( DT_FORMAT );
            JLabel dtRange = new JLabel( "(" + dtFormat.format( DT_MIN ) + "-" + dtFormat.format( DT_MAX ) + ")" );
            
            JLabel stepsLabel = new JLabel( "steps per tick:" );
            _stepsSpinner = new DoubleSpinner( _solver.getSteps(), STEPS_MIN, STEPS_MAX, STEPS_DELTA, STEPS_FORMAT, SPINNER_SIZE );
            DecimalFormat stepsFormat = new DecimalFormat( STEPS_FORMAT );
            JLabel stepsRange = new JLabel( "(" + stepsFormat.format( STEPS_MIN ) + "-" + stepsFormat.format( STEPS_MAX ) + ")" );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            panel.setLayout( layout );
            int row = 0;
            layout.addAnchoredComponent( massLabel, row, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _massSpinner, row, 1, GridBagConstraints.WEST );
            layout.addAnchoredComponent( massRange, row, 2, GridBagConstraints.WEST );
            row++;
            layout.addAnchoredComponent( hbarLabel, row, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _hbarSpinner, row, 1, GridBagConstraints.WEST );
            layout.addAnchoredComponent( hbarRange, row, 2, GridBagConstraints.WEST );
            row++;
            layout.addAnchoredComponent( dtLabel, row, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _dtSpinner, row, 1, GridBagConstraints.WEST );
            layout.addAnchoredComponent( dtRange, row, 2, GridBagConstraints.WEST );
            row++;
            layout.addAnchoredComponent( stepsLabel, row, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _stepsSpinner, row, 1, GridBagConstraints.WEST );
            layout.addAnchoredComponent( stepsRange, row, 2, GridBagConstraints.WEST );
            row++;
        }
        
        getContentPane().add( panel );
        pack();

        // Interactivity
        EventListener _eventListener = new EventListener();
        _massSpinner.addChangeListener( _eventListener );
        _hbarSpinner.addChangeListener( _eventListener );
        _dtSpinner.addChangeListener( _eventListener );
        _stepsSpinner.addChangeListener( _eventListener );
    }
    
    public void refresh() {
        _massSpinner.setDoubleValue( _solver.getMass() );
        _hbarSpinner.setDoubleValue( _solver.getHbar() );
        _dtSpinner.setDoubleValue( _solver.getDt() );
        _stepsSpinner.setDoubleValue( _solver.getSteps() );
    }
    
    /*
     * EventListener dispatches events for all controls in this control panel.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {
        
        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _massSpinner ) {
                handleMassChange();
            }
            if ( event.getSource() == _hbarSpinner ) {
                handleHbarChange();
            }
            if ( event.getSource() == _dtSpinner ) {
                handleDtChange();
            }
            if ( event.getSource() == _stepsSpinner ) {
                handleStepsChange();
            }
        }

        public void itemStateChanged( ItemEvent event ) {
        }
    }
    
    private void handleMassChange() {
        double mass = _massSpinner.getDoubleValue();
        if ( mass >= MASS_MIN && mass <= MASS_MAX ) {
            _solver.setMass( mass );
        }
        else {
            warnInvalidInput();
            _massSpinner.setDoubleValue( _solver.getMass() );
        }
    }
    
    private void handleHbarChange() {
        double hbar = _hbarSpinner.getDoubleValue();
        if ( hbar >= HBAR_MIN && hbar <= HBAR_MAX ) {
            _solver.setHbar( hbar );
        }
        else {
            warnInvalidInput();
            _hbarSpinner.setDoubleValue( _solver.getHbar() );
        }
    }
    
    private void handleDtChange() {
        double dt = _dtSpinner.getDoubleValue();
        if ( dt >= DT_MIN && dt <= DT_MAX ) {
            _solver.setDt( dt );
        }
        else {
            warnInvalidInput();
            _dtSpinner.setDoubleValue( _solver.getDt() );
        }
    }
    
    private void handleStepsChange() {
        int steps = (int) _stepsSpinner.getDoubleValue();
        if ( steps >= STEPS_MIN && steps <= STEPS_MAX ) {
        _solver.setSteps( steps );  
        }
        else {
            warnInvalidInput();
            _stepsSpinner.setDoubleValue( _solver.getSteps() );
        }
    }
    
    /*
     * Warns the user about invalid input.
     */
    private void warnInvalidInput() {
        Toolkit.getDefaultToolkit().beep();
    }
}
