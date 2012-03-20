// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.Box;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;

/**
 * Control for choosing a form of the slope-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptFormControl extends PNode {

    public static enum SlopeInterceptForm {Y_FORM, X_FORM}

    private static final PhetFont FONT = new PhetFont( 20 );
    private static final String PATTERN = "{0} = {1}{2} + {3}"; // eg, y = mx + b

    // y = mx + b
    private static String Y_FORM = MessageFormat.format( PATTERN,
                                                         Strings.SYMBOL_VERTICAL_AXIS,
                                                         Strings.SYMBOL_SLOPE,
                                                         Strings.SYMBOL_HORIZONTAL_AXIS,
                                                         Strings.SYMBOL_INTERCEPT );

    // x = my + b
    private static String X_FORM = MessageFormat.format( PATTERN,
                                                         Strings.SYMBOL_HORIZONTAL_AXIS,
                                                         Strings.SYMBOL_SLOPE,
                                                         Strings.SYMBOL_VERTICAL_AXIS,
                                                         Strings.SYMBOL_INTERCEPT );

    public SlopeInterceptFormControl( Property<SlopeInterceptForm> slopeInterceptForm ) {

        PropertyRadioButton<SlopeInterceptForm> yFormRadioButton =
                new PropertyRadioButton<SlopeInterceptForm>( UserComponents.yFormSlopeInterceptRadioButton,
                                                             Y_FORM,
                                                             slopeInterceptForm,
                                                             SlopeInterceptForm.Y_FORM );

        PropertyRadioButton<SlopeInterceptForm> xFormRadioButton =
                new PropertyRadioButton<SlopeInterceptForm>( UserComponents.xFormSlopeInterceptRadioButton,
                                                             X_FORM,
                                                             slopeInterceptForm,
                                                             SlopeInterceptForm.X_FORM );

        yFormRadioButton.setFont( FONT );
        xFormRadioButton.setFont( FONT );

        GridPanel panel = new GridPanel();
        panel.setGridY( 0 ); // vertical
        panel.setInsets( new Insets( 5, 5, 5, 5 ) );
        panel.add( yFormRadioButton );
        panel.add( Box.createHorizontalStrut( 10 ) );
        panel.add( xFormRadioButton );

        addChild( new ControlPanelNode( panel ) );
    }
}
