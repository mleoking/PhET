// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.view.SpinnerButtonPanelVBox;

import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.maxSpinnerDownButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.maxSpinnerUpButton;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys.max;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.sendMessageAndApply;

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
                                                            sendMessageAndApply( maxSpinnerUpButton, max, value, +1 ), value.lessThan( 6 ),
                                                            sendMessageAndApply( maxSpinnerDownButton, max, value, -1 ), value.greaterThan( 1 ) ) )
        ) );
    }
}