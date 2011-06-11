// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;

/**
 * @author Sam Reid
 */
public class SoluteControlPanelNode extends WhiteControlPanelNode {
    public SoluteControlPanelNode( final Property<DispenserType> dispenserType ) {
        super( new VBox(
                new PText( "Solute" ) {{setFont( SugarAndSaltSolutionsCanvas.TITLE_FONT );}},
                new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ),//spacer
                new PSwing( new VerticalLayoutPanel() {{
                    add( new PropertyRadioButton<DispenserType>( "Salt", dispenserType, SALT ) {{setFont( CONTROL_FONT );}} );
                    add( new PropertyRadioButton<DispenserType>( "Sugar", dispenserType, SUGAR ) {{setFont( CONTROL_FONT );}} );
                }} ) )
        );
    }
}