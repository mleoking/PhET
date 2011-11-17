// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractions.intro.intro.view.FractionsIntroCanvas.CONTROL_FONT;

/**
 * @author Sam Reid
 */
public class RepresentationControlPanel extends ControlPanelNode {
    public RepresentationControlPanel( Property<Fill> fill ) {
        super( new RepresentationControlPanelContentNode( fill ) );
    }

    private static class RepresentationControlPanelContentNode extends PNode {
        private RepresentationControlPanelContentNode( Property<Fill> fill ) {
            final PhetPText title = new PhetPText( "Representation", CONTROL_FONT );
            addChild( title );
            final HBox representationLayer = new HBox( 10, new HorizontalBarElement(), new VerticalBarElement(), new PieElement(), new SquareElement(), new NumberLineElement() ) {{
                setOffset( title.getFullBounds().getCenterX(), title.getFullBounds().getMaxY() );
            }};
            addChild( representationLayer );
            addChild( new HBox( new PSwing( new PropertyRadioButton<Fill>( "In order", fill, Fill.SEQUENTIAL ) {{setFont( CONTROL_FONT );}} ),
                                new PSwing( new PropertyRadioButton<Fill>( "Random", fill, Fill.RANDOM ) {{setFont( CONTROL_FONT );}} ) ) {{
                setOffset( representationLayer.getFullBounds().getMaxX() - getFullBounds().getWidth() / 2, 0 );
            }} );
        }
    }

    public static class Fill {
        public static Fill RANDOM = new Fill();
        public static Fill SEQUENTIAL = new Fill();
    }

}
