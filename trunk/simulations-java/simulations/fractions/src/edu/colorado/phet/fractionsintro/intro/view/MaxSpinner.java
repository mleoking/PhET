// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.view.SpinnerButtonPanelVBox;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.button;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.maxSpinnerDownButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.maxSpinnerUpButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys.max;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.sendMessage;

/**
 * Spinner that shows and allows the user to change the max value (max number of filled containers) for the sim between 1-6.
 *
 * @author Sam Reid
 */
public class MaxSpinner extends RichPNode {
    public MaxSpinner( final IntegerProperty value ) {
        final PhetFont maxTextFont = new PhetFont( 32 );
        final PhetFont maxNumberFont = new PhetFont( 36 );
        addChild( new VBox( 0, new PhetPText( "Max", maxTextFont, Color.black ),
                            new HBox( new PhetPText( maxNumberFont ) {{
                                value.addObserver( new VoidFunction1<Integer>() {
                                    @Override public void apply( Integer m ) {
                                        setText( m + "" );
                                    }
                                } );
                            }}, new SpinnerButtonPanelVBox( (int) ( 50.0 / 2.0 ),
                                                            sendMessage( maxSpinnerUpButton, button, pressed, update( value, +1 ) ).andThen( value.increment_() ), value.lessThan( 6 ),
                                                            sendMessage( maxSpinnerDownButton, button, pressed, update( value, -1 ) ).andThen( value.decrement_() ), value.greaterThan( 1 ) ) )
        ) );
    }

    private Function0<ParameterSet> update( final IntegerProperty value, final int delta ) {
        return new Function0<ParameterSet>() {
            @Override public ParameterSet apply() {
                return ParameterSet.parameterSet( max, value.get() + delta );
            }
        };
    }
}