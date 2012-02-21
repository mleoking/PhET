// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;

import javax.swing.SwingConstants;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJTextField;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

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

    private static final int COLUMNS = 5;
    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private Property<BeersLawSolution> solutionProperty;
    private final SimpleObserver concentrationObserver;

    public ConcentrationTextField( final Property<BeersLawSolution> solutionProperty, PhetFont font ) {
        super( UserComponents.concentrationTextField, COLUMNS );
        setFont( font );
        setHorizontalAlignment( SwingConstants.RIGHT ); // right justified

        this.solutionProperty = solutionProperty;

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

        // sync with model when concentration changes
        concentrationObserver = new SimpleObserver() {
            public void update() {
                updateView( solutionProperty.get().concentration.get() );
            }
        };
        solutionProperty.get().concentration.addObserver( concentrationObserver );

        // sync with model when solution changes
        solutionProperty.addObserver( new SimpleObserver() {
            public void update() {
                solutionProperty.get().concentration.removeObserver( concentrationObserver );
                solutionProperty.get().concentration.addObserver( concentrationObserver );
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
