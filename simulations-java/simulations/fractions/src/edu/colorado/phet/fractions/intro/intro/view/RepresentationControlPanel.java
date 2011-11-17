// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.intro.intro.view.FractionsIntroCanvas.CONTROL_FONT;

/**
 * @author Sam Reid
 */
public class RepresentationControlPanel extends ControlPanelNode {
    public RepresentationControlPanel() {
        super( new RepresentationControlPanelContentNode() );
    }

    private static class RepresentationControlPanelContentNode extends PNode {
        private RepresentationControlPanelContentNode() {
            final PhetPText title = new PhetPText( "Representation", CONTROL_FONT );
            addChild( title );
            final HBox representationLayer = new HBox( 30, new HorizontalBarElement(), new VerticalBarElement(), new PieElement(), new SquareElement(), new NumberLineElement() ) {{
                setOffset( 30, title.getFullBounds().getMaxY() );
            }};
            addChild( representationLayer );
        }
    }

}
