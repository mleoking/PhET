/* Copyright 2006-2008, University of Colorado */

package edu.colorado.phet.boundstates.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.control.DoubleSpinner;
import edu.colorado.phet.boundstates.model.BSEigenstate;
import edu.colorado.phet.boundstates.model.BSModel;
import edu.colorado.phet.boundstates.model.BSSuperpositionCoefficients;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


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
    
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    private static final int SPINNER_COLUMNS = 4;
    
    /*
     * The amount of normalization error we're willing to accept 
     * is related to the precision of the coefficients that we're 
     * displaying. For example, if the user may only be able to
     * enter 0.54, when the actual coefficient should be 0.543.
     */
    private static final double NORMALIZATION_ERROR = BSConstants.COEFFICIENT_STEP;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSModel _model;
    
    // Local copy of coefficients. Changes are made here and applied to the model's coefficients.
    private BSSuperpositionCoefficients _localCoefficients;
    
    private JPanel _dynamicPanel;
    private ArrayList _spinners; // array of DoubleSpinner
    private JButton _clearButton;
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
        setTitle( BSResources.getString( "BSSuperpositionStateDialog.title" ) );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        _model = model;
        _localCoefficients = new BSSuperpositionCoefficients( _model.getSuperpositionCoefficients() );
        
        _changed = false;
        _selectionColor = colorScheme.getEigenstateSelectionColor();
        _normalColor = Color.WHITE;
        
        createUI( parent );
        
        // After everything is initialized...
        _model.getSuperpositionCoefficients().addObserver( this );
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        _model.getSuperpositionCoefficients().deleteObserver( this );
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
        
        JLabel instructions = new JLabel( BSResources.getString( "label.superposition.instructions" ) );
           
        final int numberOfCoefficients = _localCoefficients.getNumberOfCoefficients();
        BSEigenstate[] eigenstates = _model.getEigenstates();
        assert( eigenstates.length == numberOfCoefficients );
        
        int groundStateSubscript = _model.getPotential().getGroundStateSubscript();
        String es = createSuperpositionEquation( groundStateSubscript );
        JLabel equation = new JLabel( es );
         
        // Create the coefficient spinners...
        ArrayList labels = new ArrayList();
        _spinners = new ArrayList();
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            int subscript = eigenstates[i].getSubscript();
            String label = "<html>" + BSResources.getString( "label.superpositionCoefficient" ) + "<sub>" + subscript + "</sub>:</html>";
            labels.add( new JLabel( label ) );
            final double value = _localCoefficients.getCoefficient( i );
            DoubleSpinner spinner = new DoubleSpinner( value, BSConstants.COEFFICIENT_MIN, BSConstants.COEFFICIENT_MAX, 
                    BSConstants.COEFFICIENT_STEP, BSConstants.COEFFICIENT_PATTERN, SPINNER_SIZE );
            spinner.addChangeListener( _eventListener );
            _spinners.add( spinner );
        }
        updateSpinnersColor();

        // Layout the spinners in columns so that tab traversal works in column-major order...
        JPanel[] columns = new JPanel[ SPINNER_COLUMNS ];
        {
            EasyGridBagLayout[] layouts = new EasyGridBagLayout[ SPINNER_COLUMNS ];
            
            for ( int i = 0; i < SPINNER_COLUMNS; i++ ) {
                JPanel column = new JPanel();
                EasyGridBagLayout layout = new EasyGridBagLayout( column );
                column.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                
                columns[i] = column;
                layouts[i] = layout;
            }

            int row = 0;
            int numberOfSpinnersPerColumn = _spinners.size() / SPINNER_COLUMNS;
            if ( _spinners.size() % SPINNER_COLUMNS != 0 ) {
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

        _clearButton = new JButton( BSResources.getString( "button.clear" ) );
        _clearButton.addActionListener( _eventListener );
        
        _normalizeButton = new JButton( BSResources.getString( "button.normalize" ) );
        _normalizeButton.addActionListener( _eventListener );
      
        _applyButton = new JButton( BSResources.getString( "button.apply" ) );
        _applyButton.addActionListener( _eventListener );
        
        _closeButton = new JButton( BSResources.getString( "button.close" ) );
        _closeButton.addActionListener( _eventListener );

        final int rows = 1;
        final int columns = 4; /* same as number of buttons! */
        final int hgap = 5;
        final int vgap = 0;
        JPanel buttonPanel = new JPanel( new GridLayout( rows, columns, hgap, vgap ) );
        buttonPanel.add( _clearButton );
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
    
    private static String createSuperpositionEquation( final int groundStateSubscript ) {
        int i = groundStateSubscript;
        String s = "<html>" + PSI + "(x) = " + 
            "c<sub>" + i + "</sub>" + PSI + "<sub>" + i++ + "</sub>(x) + " +
            "c<sub>" + i + "</sub>" + PSI + "<sub>" + i++ + "</sub>(x) + ... + " + 
            "c<sub>n</sub>" + PSI + "<sub>n</sub>(x)";
        return s;
    }
 
    private static String createNormalizationEquation( final int groundStateSubscript ) {
        int i = groundStateSubscript;
        String s = "c<sub>" + i++ + "</sub><sup>2</sup> + " +
                   "c<sub>" + i++ + "</sub><sup>2</sup> + ... + " + 
                   "c<sub>n</sub><sup>2</sup> = 1";
        return s;
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
        if ( o == _model.getSuperpositionCoefficients() ) {
            _localCoefficients = new BSSuperpositionCoefficients( _model.getSuperpositionCoefficients() );
            final int numberOfCoefficients = _localCoefficients.getNumberOfCoefficients();
            if ( _spinners.size() == numberOfCoefficients ) {
                // same number of coefficients, reuse the existing input panel...
                updateSpinners();
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
     * Applies the local coefficient values to the model.
     */
    private void apply() {
        if ( _localCoefficients.isNormalized( NORMALIZATION_ERROR ) ) {
            BSSuperpositionCoefficients modelCoefficients = _model.getSuperpositionCoefficients();
            modelCoefficients.setNotifyEnabled( false );
            final int numberOfCoefficients = _localCoefficients.getNumberOfCoefficients();
            for ( int i = 0; i < numberOfCoefficients; i++ ) {
                modelCoefficients.setCoefficient( i, _localCoefficients.getCoefficient( i ) );
            }
            _changed = false;
            modelCoefficients.setNotifyEnabled( true );
        }
        else {
            throw new IllegalStateException( "attempt to apply unnormalized" );
        }
    }
    
    /*
     * Enables and disabled buttons in the action area based on 
     * the state of the local coefficients.
     */
    private void updateButtons() { 
        _applyButton.setEnabled( _changed && !isSumZero() );
        _normalizeButton.setEnabled( !isSumZero() );
        // Close button is always enabled.
    }
    
    /*
     * Updates spinners to match local coefficients.
     */
    private void updateSpinners() {
        final int numberOfCoefficients = _localCoefficients.getNumberOfCoefficients();
        for ( int i = 0; i < numberOfCoefficients; i++ ) {
            DoubleSpinner spinner = (DoubleSpinner) _spinners.get( i );
            spinner.setDoubleValue( _localCoefficients.getCoefficient( i ) );
            updateSpinnerColor( spinner );
        }
    }
    
    /*
     * Normalizes the local coefficient values and updates the spinners.
     */
    private void normalize() {
        _localCoefficients.normalize();
        updateSpinners();
    }
    
    /*
     * Determines if the local coefficients values have a sum of zero.
     */
    private boolean isSumZero() {
        return ( _localCoefficients.getSum() == 0 );
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
            if ( event.getSource() == _clearButton ) {
                handleClearAction();
            }
            else if ( event.getSource() == _normalizeButton ) {
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
    
    private void handleClearAction() {
       _localCoefficients.setAllZero();
       updateSpinners();
       updateButtons();
    }
    
    private void handleNormalizeAction() {
        normalize();
        updateButtons();
    }
    
    private void handleApplyAction() {

        if ( _localCoefficients.isNormalized( NORMALIZATION_ERROR ) ) {
            apply();
        }
        else {
            String pattern = BSResources.getString( "message.confirmNormalizeApply" );
            int groundStateSubscript = _model.getPotential().getGroundStateSubscript();
            String equation = createNormalizationEquation( groundStateSubscript );
            Object[] objs = { equation };
            MessageFormat format = new MessageFormat( pattern );
            String message = format.format( objs );
            String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
            int reply = JOptionPane.showConfirmDialog( this, message, title, JOptionPane.YES_NO_CANCEL_OPTION );
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
        
        if ( !_changed || isSumZero() ) {
            dispose();
        }
        else {
            String message = BSResources.getString( "message.confirmApplyClose" );
            if ( !_localCoefficients.isNormalized( NORMALIZATION_ERROR ) ) {
                message = BSResources.getString( "message.confirmNormalizeApplyClose" );
            }
            String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
            int reply = JOptionPane.showConfirmDialog( this, message, title, JOptionPane.YES_NO_CANCEL_OPTION );
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
    
    /*
     * When a spinner's value changes, update our local copy of the coefficients.
     */
    private void handleCoefficientChange( DoubleSpinner spinner ) {
        final int index = getSpinnerIndex( spinner );
        final double value = spinner.getDoubleValue();
        if ( value < BSConstants.COEFFICIENT_MIN || value > BSConstants.COEFFICIENT_MAX ) {
            warnInvalidInput();
            // Restore the value
            spinner.setDoubleValue( _localCoefficients.getCoefficient( index ) );
        }
        else {
            // Change the coefficient in our local copy of the model
            _localCoefficients.setCoefficient( index, value );
            _changed = true;
        }
        updateSpinnerColor( spinner );
        updateButtons();
    }
    
    private int getSpinnerIndex( DoubleSpinner spinner ) {
        int index = -1;
        final int numberOfSpinners = _spinners.size();
        for ( int i = 0; i < numberOfSpinners; i++ ) {
            if ( _spinners.get( i ) == spinner ) {
                index = i;
                break;
            }
        }
        assert( index != -1 );
        return index;
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
