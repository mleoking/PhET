// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;

/**
 * This control panel lets the user choose between solute dispenser types (i.e. sugar vs salt)
 *
 * @author Sam Reid
 */
public class RadioButtonSoluteControlPanelNode extends SoluteControlPanelNode {
    public RadioButtonSoluteControlPanelNode( final Property<DispenserType> dispenserType, PSwingCanvas canvas ) {
        super( new PSwing( new VerticalLayoutPanel() {{
            add( new PropertyRadioButton<DispenserType>( SugarAndSaltSolutionsResources.Strings.SALT, dispenserType, SALT ) {{setFont( CONTROL_FONT );}} );
            add( new PropertyRadioButton<DispenserType>( SugarAndSaltSolutionsResources.Strings.SUGAR, dispenserType, SUGAR ) {{setFont( CONTROL_FONT );}} );
        }} ) );
    }
}