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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.control.DoubleSpinner;
import edu.colorado.phet.boundstates.util.DialogUtils;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSSuperpositionStateDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSuperpositionStateDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double COEFFICIENT_MIN = 0.00;
    private static final double COEFFICIENT_MAX = 1.00;
    private static final double COEFFICIENT_STEP = 0.01;
    private static final String COEFFICIENT_FORMAT = "0.00";
    
    private static final int NUMBER_OF_COLUMNS = 3;
    private static final int NUMBER_OF_COEFFICIENTS = 6; 
    
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _spinners; // array of DoubleSpinnerControl
    private JButton _normalizeButton;
    private JButton _applyButton;
    private JButton _closeButton;
    private EventListener _eventListener;
    private boolean _changed;
    private int _startingIndex;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSuperpositionStateDialog( Frame parent, int startingIndex ) {
        super( parent );
        setModal( false );
        setResizable( false );
        setTitle( SimStrings.get( "BSSuperpositionStateDialog.title" ) );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        _changed = false;
        _startingIndex = startingIndex;
        
        createUI( parent );
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        //XXX
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
        
        JLabel instructions = new JLabel( SimStrings.get( "label.superposition.instructions" ) );
        
        String es = createEquationString( NUMBER_OF_COEFFICIENTS, _startingIndex );
        JLabel equation = new JLabel( es );
         
        // Create the coefficient spinners...
        ArrayList labels = new ArrayList();
        _spinners = new ArrayList();
        for ( int i = 0; i < NUMBER_OF_COEFFICIENTS; i++ ) {
            int coefficientIndex = i + _startingIndex;
            String label = "<html>" + SimStrings.get( "label.superpositionCoefficient" ) + "<sub>" + coefficientIndex + "</sub>:</html>";
            labels.add( new JLabel( label ) );
            DoubleSpinner spinner = new DoubleSpinner( COEFFICIENT_MIN, COEFFICIENT_MIN, COEFFICIENT_MAX, COEFFICIENT_STEP, COEFFICIENT_FORMAT, SPINNER_SIZE );
            spinner.addChangeListener( _eventListener );
            _spinners.add( spinner );
        }

        // Layout the spinners in columns so that tab traversal works in column-major order...
        JPanel[] columns = new JPanel[ NUMBER_OF_COLUMNS ];
        {
            EasyGridBagLayout[] layouts = new EasyGridBagLayout[ NUMBER_OF_COLUMNS ];
            
            for ( int i = 0; i < NUMBER_OF_COLUMNS; i++ ) {
                JPanel column = new JPanel();
                EasyGridBagLayout layout = new EasyGridBagLayout( column );
                column.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                
                columns[i] = column;
                layouts[i] = layout;
            }

            int row = 0;
            int numberOfSpinnersPerColumn = _spinners.size() / NUMBER_OF_COLUMNS;
            if ( _spinners.size() % NUMBER_OF_COLUMNS != 0 ) {
                numberOfSpinnersPerColumn++;
            }
            for ( int i = 0; i < _spinners.size(); i++ ) {
                EasyGridBagLayout layout = layouts[(int) i / numberOfSpinnersPerColumn];
                JLabel label = (JLabel) labels.get( i );
                DoubleSpinner spinner = (DoubleSpinner) _spinners.get( i );
                layout.addAnchoredComponent( label, row, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( spinner, row, 1, GridBagConstraints.WEST );
                row++;
            }
        }

        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.NORTH );
        int row = 0;
        layout.addAnchoredComponent( instructions, row, 0, 100, 1, GridBagConstraints.WEST );
        row++;
        layout.addAnchoredComponent( equation, row, 0, 100, 1, GridBagConstraints.WEST );
        row++;
        for ( int i = 0; i < columns.length; i++ ) {
            layout.addComponent( columns[i], row, i );
        }
        row++;
        
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _normalizeButton = new JButton( SimStrings.get( "button.normalize" ) );
        _normalizeButton.addActionListener( _eventListener );
      
        _applyButton = new JButton( SimStrings.get( "button.apply" ) );
        _applyButton.addActionListener( _eventListener );
        
        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _eventListener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 3, 10, 0 ) );
        buttonPanel.add( _normalizeButton );
        buttonPanel.add( _applyButton );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        updateButtons();
        
        return actionPanel;
    }

    private static String createEquationString( final int numberOfCoefficients, final int startingIndex ) {
        String s;
        char psi = '\u03c8';
        s = "<html>" + psi + "(x) = ";
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            int n = i + startingIndex;
            s = s + "c<sub>" + n + "</sub>" + psi + "<sub>" + n + "</sub>(x)";
            if ( i < numberOfCoefficients - 1 ) {
                s = s + " + ";
            }
        }
        return s;
    }
 
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update( Observable o, Object arg ) {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // JDialog overrides
    //----------------------------------------------------------------------------
    
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    //----------------------------------------------------------------------------
    // Apply, Normalize
    //----------------------------------------------------------------------------
    
    private void apply() {
        if ( isNormalized() ) {
            //XXX apply changes to model
            _changed = false;
        }
        else {
            throw new IllegalStateException( "attempt to apply unnormalized" );
        }
    }
    
    private void normalize() {
        if ( !isZero() && !isNormalized() ) {
            final double total = getCoefficientsTotal();
            Iterator i = _spinners.iterator();
            while ( i.hasNext() ) {
                DoubleSpinner spinner = (DoubleSpinner) i.next();
                double normalizedValue = spinner.getDoubleValue() / total;
                //XXX handle roundoff error
                spinner.setDoubleValue( normalizedValue );
            }
        }
    }
    
    private void updateButtons() { 
        _applyButton.setEnabled( isChanged() );
        // Normalize button is always enabled.
        // Close button is always enabled.
    }

    private boolean isChanged() {
        return _changed;
    }
    
    private boolean isNormalized() {
        return ( getCoefficientsTotal() == 1 );
    }
    
    private boolean isZero() {
        return ( getCoefficientsTotal() == 0 );
    }
    
    private double getCoefficientsTotal() {
        double total = 0;
        Iterator i = _spinners.iterator();
        while ( i.hasNext() ) {
            DoubleSpinner spinner = (DoubleSpinner) i.next();
            total += spinner.getDoubleValue();
        }
        return total;
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
            if ( event.getSource() == _normalizeButton ) {
                handleNormalizeAction();
                handleNormalizeAction();
            }
            else if ( event.getSource() == _applyButton ) {
                handleApplyAction();
            }
            else if ( event.getSource() == _closeButton ) {
                handleCloseAction();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( _spinners.contains( event.getSource() ) ) {
                handleCoefficientChange( (DoubleSpinner) event.getSource() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    private void handleNormalizeAction() {
        normalize();
        updateButtons();
    }
    
    private void handleApplyAction() {

        if ( isNormalized() ) {
            apply();
        }
        else {
            String message = SimStrings.get( "message.confirmNormalizeApply" );
            int reply = DialogUtils.showConfirmDialog( this, message, JOptionPane.YES_NO_OPTION );
            if ( reply == JOptionPane.YES_OPTION) {
                // Yes -> normalize, apply
                normalize();
                apply();
            }
            else if ( reply == JOptionPane.NO_OPTION) {
                // No -> don't normalize, don't apply
            }
        }
        
        updateButtons();
    }
    
    private void handleCloseAction() {
        
        if ( !isChanged() ) {
            dispose();
        }
        else {
            String message = SimStrings.get( "message.confirmApplyClose" );
            if ( !isNormalized() ) {
                message = SimStrings.get( "message.confirmNormalizeApplyClose" );
            }
            int reply = DialogUtils.showConfirmDialog( this, message, JOptionPane.YES_NO_CANCEL_OPTION );
            if ( reply == JOptionPane.YES_OPTION ) {
                // Yes -> normalize, apply, close
                normalize();
                apply();
                dispose();
            }
            else if ( reply == JOptionPane.NO_OPTION ) {
                // No -> don't apply, close
                dispose();
            }
            else {
                // Cancel -> do nothing
            }
        }
        
        updateButtons();
    }
    
    private void handleCoefficientChange( DoubleSpinner spinner ) {
        double value = spinner.getDoubleValue();
        if ( value < COEFFICIENT_MIN || value > COEFFICIENT_MAX ) {
            warnInvalidInput();
            //XXX restore the current value of cn from the model
        }
        else {
            _changed = true;
        }
        updateButtons();
    }
    
    /*
     * Warns the user about invalid input.
     */
    private void warnInvalidInput() {
        Toolkit.getDefaultToolkit().beep();
    }
}
