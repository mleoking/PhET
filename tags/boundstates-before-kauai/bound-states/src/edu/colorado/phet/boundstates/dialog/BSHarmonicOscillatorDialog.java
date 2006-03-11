/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSHarmonicOscillatorWell;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSHarmonicOscillatorDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSHarmonicOscillatorWell _potential;
    
    private SliderControl _offsetSlider;
    private SliderControl _angularFrequencySlider;
    private JButton _closeButton;
    private EventListener _eventListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSHarmonicOscillatorDialog( Frame parent, BSHarmonicOscillatorWell potential ) {
        super( parent );
        setModal( false );
        setResizable( false );
        setTitle( SimStrings.get( "BSHarmonicOscillatorDialog.title" ) );
        
        _potential = potential;
        _potential.addObserver( this );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        createUI( parent );
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
            _potential = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Private initializers
    //----------------------------------------------------------------------------

    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        System.out.println( "BSHarmonicOscillator.createInputPanel" );//XXX
        String angularFrequencyUnits = SimStrings.get( "units.angularFrequency" );
        String energyUnits = SimStrings.get( "units.energy" );

        // Offset
        {
            double value = _potential.getOffset();
            double min = BSConstants.MIN_WELL_OFFSET;
            double max = BSConstants.MAX_WELL_OFFSET;
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String labelFormat = SimStrings.get( "label.wellOffset" ) + " {0} " + energyUnits;
            _offsetSlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, labelFormat, SLIDER_INSETS );
        }
        System.out.println( "BSHarmonicOscillator.createInputPanel offset done" );//XXX

        // Angular Frequency
        {
            double value = _potential.getSpacing();
            double min = BSConstants.MIN_WELL_ANGULAR_FREQUENCY;
            double max = BSConstants.MAX_WELL_ANGULAR_FREQUENCY;
            double tickSpacing = Math.abs( max - min );
            int tickPrecision = 1;
            int labelPrecision = 1;
            String labelFormat = "<html>" + SimStrings.get( "label.wellAngularFrequency" ) + " {0} x 10<sup>15</sup>" + angularFrequencyUnits + "</html>";
            _angularFrequencySlider = new SliderControl( value, min, max, tickSpacing, tickPrecision, labelPrecision, labelFormat, SLIDER_INSETS );
        }
        System.out.println( "BSHarmonicOscillator.createInputPanel angFreq done" );//XXX
        
        updateControls();
        System.out.println( "BSHarmonicOscillator.createInputPanel control updated" );//XXX
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _offsetSlider, row, col );
        row++;
        layout.addComponent( _angularFrequencySlider, row, col );
        row++;

        // Interction
        setEventHandlingEnabled( true );
        
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {
        System.out.println( "BSHarmonicOscillator.createActionsPanel" );//XXX
        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _eventListener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 1 ) );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update( Observable o, Object arg ) {
        updateControls();
    }
    
    private void updateControls() {
        // Sync values
        _offsetSlider.setValue( _potential.getOffset() );
        _angularFrequencySlider.setValue( _potential.getAngularFrequency() );
    }
    
    private void setEventHandlingEnabled( boolean enabled ) {
        if ( enabled ) {
            _offsetSlider.addChangeListener( _eventListener );
            _angularFrequencySlider.addChangeListener( _eventListener );
        }
        else {
            _offsetSlider.removeChangeListener( _eventListener );
            _angularFrequencySlider.removeChangeListener( _eventListener );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Dispatches events to the appropriate handler method.
     */
    private class EventListener extends WindowAdapter implements ActionListener, ChangeListener {

        public void windowClosing( WindowEvent event ) {
            handleCloseAction();
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _closeButton ) {
                handleCloseAction();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _offsetSlider ) {
                handleOffsetChange();
            }
            else if ( event.getSource() == _angularFrequencySlider ) {
                handleAngularFrequencyChange();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    private void handleCloseAction() {
        cleanup();
        dispose();
    }
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        _potential.setOffset( offset );
    }
    
    private void handleAngularFrequencyChange() {
        final double angularFrequency = _angularFrequencySlider.getValue();
        _potential.setAngularFrequency( angularFrequency );
    }

}
