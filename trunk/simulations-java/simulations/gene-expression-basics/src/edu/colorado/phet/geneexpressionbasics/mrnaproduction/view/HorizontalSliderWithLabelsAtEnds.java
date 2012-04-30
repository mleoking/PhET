// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode2;
import edu.colorado.phet.common.piccolophet.nodes.slider.KnobNode2;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Convenience class for a horizontal slider with labels at ends instead of
 * having tick marks with labels below them.
 *
 * @author John Blanco
 */
class HorizontalSliderWithLabelsAtEnds extends PNode {
    private static final double OVERALL_WIDTH = 150;
    private static final Font LABEL_FONT = new PhetFont( 12 );

    //TODO: There is an incorrect behavior based on which side the slider knob is on.
    //This bug seems like it was introduced with changes to the HSliderNode, but I can't tell why it was working in 0.00.18
    //To compensate, I have increased the inter element spacing, but it makes one label further from the slider than the other
    private static final double INTER_ELEMENT_SPACING = 5;

    HorizontalSliderWithLabelsAtEnds( UserComponent userComponent, Property<Double> doubleProperty, double min, double max, String leftLabel, String rightLabel ) {
        PText leftLabelNode = new PText( leftLabel ) {{
            setFont( LABEL_FONT );
        }};
        PText rightLabelNode = new PText( rightLabel ) {{
            setFont( LABEL_FONT );
        }};
        double sliderWidth = OVERALL_WIDTH - leftLabelNode.getFullBoundsReference().width -
                             rightLabelNode.getFullBoundsReference().width - ( 2 * INTER_ELEMENT_SPACING );
        PNode sliderNode = new HSliderNode2( userComponent,
                                             min,
                                             max,
                                             new KnobNode2( 30, KnobNode2.Style.POINTED_RECTANGLE, new KnobNode2.ColorScheme( new Color( 142, 229, 238 ) ) ),
                                             5,
                                             sliderWidth,
                                             doubleProperty,
                                             new BooleanProperty( true ) );
        addChild( new HBox( INTER_ELEMENT_SPACING, leftLabelNode, sliderNode, rightLabelNode ) );
    }
}
