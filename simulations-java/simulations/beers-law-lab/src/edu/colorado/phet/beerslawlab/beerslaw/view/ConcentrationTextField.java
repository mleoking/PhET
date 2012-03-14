// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.SwingConstants;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJTextField;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import com.sun.xml.internal.rngom.digested.DDataPattern.Param;

/**
 * Text field that displays and edits concentration.
 * If something other than a number is entered, the value is reverted.
 * If a number that's out of range is entered, it is constrained to the range.
 * <p>
 * Concentration is stored the same for all solutions in the model, in moles per liter (M).
 * But each solution specifies the units to be used for displaying the concentration in
 * the view (eg, mM, uM). This class handles the conversion between model and view units.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ConcentrationTextField extends SimSharingJTextField {

    private static final int COLUMNS = 3;
    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private Property<BeersLawSolution> solutionProperty;
    private final SimpleObserver concentrationObserver;

    public ConcentrationTextField( final Property<BeersLawSolution> solution, PhetFont font ) {
        super( UserComponents.concentrationTextField, COLUMNS );
        setFont( font );
        setHorizontalAlignment( SwingConstants.RIGHT ); // right justified

        this.solutionProperty = solution;

        // commit the value when the user presses Enter
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                commitValue();
            }
        } );

        addFocusListener( new FocusAdapter() {

            @Override public void focusGained( FocusEvent e ) {
                selectAll();
            }

            // commit the value when focus is lost
            @Override public void focusLost( FocusEvent e ) {
                commitValue();
            }
        } );

        // support for up/down arrow keys, with data-collection messages
        addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    if ( solution.get().concentration.get() < solution.get().concentrationRange.getMax() ) {
                        final double viewConcentration = solution.get().concentrationTransform.modelToView( solution.get().concentration.get() ) + 1;
                        SimSharingManager.sendUserMessage( UserComponents.concentrationTextField, UserComponentTypes.textField, UserActions.upArrowPressed,
                                                           ParameterSet.parameterSet( ParameterKeys.value, solution.get().concentrationTransform.viewToModel( viewConcentration ) ) );
                        updateModel( viewConcentration );
                    }
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    if ( solution.get().concentration.get() > solution.get().concentrationRange.getMin() ) {
                        final double viewConcentration = solution.get().concentrationTransform.modelToView( solution.get().concentration.get() ) + 1;
                        SimSharingManager.sendUserMessage( UserComponents.concentrationTextField, UserComponentTypes.textField, UserActions.downArrowPressed,
                                                           ParameterSet.parameterSet( ParameterKeys.value, solution.get().concentrationTransform.viewToModel( viewConcentration ) ) );
                        updateModel( viewConcentration );
                    }
                }
            }
        } );

        // sync with model when concentration changes
        concentrationObserver = new SimpleObserver() {
            public void update() {
                updateView( solution.get().concentration.get() );
            }
        };
        solution.get().concentration.addObserver( concentrationObserver );

        // sync with model when solution changes
        solution.addObserver( new SimpleObserver() {
            public void update() {
                solution.get().concentration.removeObserver( concentrationObserver );
                solution.get().concentration.addObserver( concentrationObserver );
            }
        } );
    }

    // Sets the view value, converted to the view units.
    private void updateView( double modelValue ) {
        double viewValue = solutionProperty.get().concentrationTransform.modelToView( modelValue );
        super.setText( FORMAT.format( viewValue ) );
    }

    // Sets the model value, constrained to the range specified by the model.
    private void updateModel( double viewValue ) {
        Double modelValue = solutionProperty.get().concentrationTransform.viewToModel( viewValue );
        Double constrainedModelValue = constrainValue( modelValue, solutionProperty.get().concentrationRange );
        solutionProperty.get().concentration.set( constrainedModelValue );
        updateView( solutionProperty.get().concentration.get() ); // force the view to update in case constraining the model value resulted in no change to the model
    }

    // Commits the value in the text field. If the value is valid, it is propagated to the model. Otherwise it is reverted.
    private void commitValue() {
        try {
            updateModel( Double.valueOf( getText() ) );
        }
        catch ( NumberFormatException nfe ) {
            revertValue();
        }
    }

    // Constrains a value to some range.
    private double constrainValue( double value, DoubleRange range ) {
        if ( value > range.getMax() ) {
            badInput();
            return range.getMax();
        }
        else if ( value < range.getMin() ) {
            badInput();
            return range.getMin();
        }
        else {
            return value;
        }
    }

    // Reverts to the model value
    private void revertValue() {
        badInput();
        updateView( solutionProperty.get().concentration.get() );
    }

    // Alerts the user to bad input
    private void badInput() {
       Toolkit.getDefaultToolkit().beep();
    }
}
