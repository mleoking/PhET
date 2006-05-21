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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.control.DoubleSpinner;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.boundstates.util.DialogUtils;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSSuperpositionStateDialog is the "Superposition State" dialog.
 * It shows a set of coefficients, one for each eigenstate.
 * The use can change the coefficient values in the range [0,1].
 * Before the user's changes can be "applied", the coefficients
 * must be normalized so that their values sum to 1.  Normalization
 * is done by pressing the "Normalize" button.  When coefficients
 * are applied, the underlying model is updated.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSuperpositionStateDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final char PSI = BSConstants.LOWERCASE_PSI;
    
    private static final int NUMBER_OF_COLUMNS = 3; 
    
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    
    private static final double NORMALIZATION_ERROR = 0.00001;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    private BSSuperpositionCoefficients _coefficients;
    
    private JPanel _dynamicPanel;
    private ArrayList _spinners; // array of DoubleSpinner
    private JButton _normalizeButton;
    private JButton _applyButton;
    private JButton _closeButton;
    
    private EventListener _eventListener;
    private boolean _changed;
    
    private Color _selectionColor; // non-zero coefficients are shown with this color
    private Color _normalColor; // zero coefficients are shown with this color
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSSuperpositionStateDialog( Frame parent, BSModel model, BSColorScheme colorScheme ) {
        super( parent );
        
        setModal( false );
        setResizable( false );
        setTitle( SimStrings.get( "BSSuperpositionStateDialog.title" ) );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        _model = model;
        _coefficients = model.getSuperpositionCoefficients();
        
        _changed = false;
        _selectionColor = colorScheme.getEigenstateSelectionColor();
        _normalColor = Color.WHITE;
        
        createUI( parent );
        
        // After everything is initialized...
        _coefficients.addObserver( this );
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        if ( _coefficients != null ) {
            _coefficients.deleteObserver( this );
            _coefficients = null;
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
        
        _dynamicPanel = new JPanel();
        _dynamicPanel.add( createInputPanel() );
        JPanel actionsPanel = createActionsPanel();

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( _dynamicPanel, BorderLayout.CENTER );
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
           
        final int numberOfCoefficients = _coefficients.getNumberOfCoefficients();
        BSEigenstate[] eigenstates = _model.getEigenstates();
        assert( eigenstates.length == numberOfCoefficients );
        
        int groundStateSubscript = _model.getPotential().getGroundStateSubscript();
        String es = createEquationString( groundStateSubscript );
        JLabel equation = new JLabel( es );
         
        // Create the coefficient spinners...
        ArrayList labels = new ArrayList();
        _spinners = new ArrayList();
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            int subscript = eigenstates[i].getSubscript();
            String label = "<html>" + SimStrings.get( "label.superpositionCoefficient" ) + "<sub>" + subscript + "</sub>:</html>";
            labels.add( new JLabel( label ) );
            final double value = _coefficients.getCoefficient( i );
            DoubleSpinner spinner = new DoubleSpinner( value, BSConstants.COEFFICIENT_MIN, BSConstants.COEFFICIENT_MAX, 
                    BSConstants.COEFFICIENT_STEP, BSConstants.COEFFICIENT_PATTERN, SPINNER_SIZE );
            spinner.addChangeListener( _eventListener );
            _spinners.add( spinner );
        }
        updateSpinnersColor();

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

    //----------------------------------------------------------------------------
    // Equation creation
    //----------------------------------------------------------------------------
    
    private static String createEquationString( final int groundStateSubscript ) {
        String s = "<html>" + PSI + "(x) = " + 
            createTermString( groundStateSubscript ) + " + " + 
            createTermString( groundStateSubscript + 1 ) + " + ... + " + 
            createTermString( "n" );
        return s;
    }
    
    private static String createTermString( int subscript ) {
        return createTermString( Integer.toString( subscript ) );
    }
    
    private static String createTermString( String subscript ) {
        return "c<sub>" + subscript + "</sub>" + PSI + "<sub>" + subscript + "</sub>(x)";
    }
 
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        _selectionColor = colorScheme.getEigenstateSelectionColor();
        updateSpinnersColor();
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _coefficients ) {
            final int numberOfCoefficients = _coefficients.getNumberOfCoefficients();
            if ( _spinners.size() == numberOfCoefficients ) {
                // same number of coefficients, so just refresh values displayed...
                for ( int i = 0; i < numberOfCoefficients; i++ ) {
                    ((DoubleSpinner)_spinners.get(i)).setDoubleValue( _coefficients.getCoefficient( i ) );
                }
                updateSpinnersColor();
            }
            else {
                // different number of coefficients, rebuild the input panel...
                _dynamicPanel.removeAll();
                _dynamicPanel.add( createInputPanel() );
                pack();
            }
            _changed = false;
        }
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
    
    /*
     * Applies the coefficient values to the model.
     */
    private void apply() {
        if ( isNormalized() ) {
            _coefficients.setNotifyEnabled( false );
            for ( int i = 0; i < _spinners.size(); i++ ) {
                DoubleSpinner spinner = (DoubleSpinner)_spinners.get( i );
                double value = spinner.getDoubleValue();
                _coefficients.setCoefficient( i, value );
            }
            _changed = false;
            _coefficients.setNotifyEnabled( true );
        }
        else {
            throw new IllegalStateException( "attempt to apply unnormalized" );
        }
    }
    
    private void normalize() {
        if ( !isZero() ) {
            final double total = getCoefficientsTotal();
            Iterator i = _spinners.iterator();
            while ( i.hasNext() ) {
                DoubleSpinner spinner = (DoubleSpinner) i.next();
                double normalizedValue = spinner.getDoubleValue() / total;
                spinner.setDoubleValue( normalizedValue );
            }
        }
    }
    
    private void updateButtons() { 
        _applyButton.setEnabled( isChanged() && !isZero() );
        _normalizeButton.setEnabled( !isZero() );
        // Close button is always enabled.
    }

    private boolean isChanged() {
        return _changed;
    }
    
    private boolean isNormalized() {
        final double total = getCoefficientsTotal();
        return ( total > 0 && total < 1 + NORMALIZATION_ERROR && total > 1 - NORMALIZATION_ERROR );
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
        
        if ( !isChanged() || isZero() ) {
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
        if ( value < BSConstants.COEFFICIENT_MIN || value > BSConstants.COEFFICIENT_MAX ) {
            warnInvalidInput();
            //XXX restore the current value of cn from the model
        }
        else {
            _changed = true;
        }
        updateSpinnerColor( spinner );
        updateButtons();
    }
    
    private void updateSpinnersColor() {
        Iterator i = _spinners.iterator();
        while ( i.hasNext() ) {
            DoubleSpinner spinner = (DoubleSpinner) i.next();
            updateSpinnerColor( spinner );
        }
    }
    
    private void updateSpinnerColor( DoubleSpinner spinner ) {
        double value = spinner.getDoubleValue();
        if ( value != 0 ) {
            spinner.getFormattedTextField().setBackground( _selectionColor );
        }
        else {
            spinner.getFormattedTextField().setBackground( _normalColor );
        }
    }
    
    /*
     * Warns the user about invalid input.
     */
    private void warnInvalidInput() {
        Toolkit.getDefaultToolkit().beep();
    }
}
