// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class RepresentationControlPanel extends ControlPanelNode {
    public RepresentationControlPanel( Property<ChosenRepresentation> chosenRepresentation ) {
        super( new RepresentationControlPanelContentNode( chosenRepresentation ), new Color( 230, 230, 230 ), new BasicStroke( 2 ), new Color( 102, 102, 102 ) );
    }

    private static class RepresentationControlPanelContentNode extends PNode {
        private RepresentationControlPanelContentNode( final Property<ChosenRepresentation> selected ) {
            final RepIcon[] elements = new RepIcon[] { new HorizontalBarElement( selected ), new VerticalBarElement( selected ), new PieElement( selected ), new SquareElement( selected ), new NumberLineElement( selected ) {{
                scale( 1.2 );
            }} };

            double maxWidth = 0;
            double maxHeight = 0;

            for ( RepIcon repIcon : elements ) {
                PNode pNode = repIcon.getNode();
                if ( pNode.getFullBounds().getWidth() > maxWidth ) {
                    maxWidth = pNode.getFullBounds().getWidth();
                }
                if ( pNode.getFullBounds().getHeight() > maxHeight ) {
                    maxHeight = pNode.getFullBounds().getHeight();
                }
            }

            final double finalMaxHeight = maxHeight;
            final double finalMaxWidth = maxWidth;
            final HBox representationLayer = new HBox( 10 ) {{
                for ( final RepIcon element : elements ) {

                    PNode button = new PhetPPath( new RoundRectangle2D.Double( -2, -2, finalMaxWidth + 4, finalMaxHeight + 4, 20, 20 ), null ) {{

                        final ZeroOffsetNode theNode = new ZeroOffsetNode( element.getNode() ) {{
                            final Point2D.Double origOffset = new Point2D.Double( finalMaxWidth / 2 - getFullWidth() / 2, finalMaxHeight / 2 - getFullHeight() / 2 );
                            setOffset( origOffset );
                        }};
                        addChild( theNode );
                    }};

                    addChild( new ToggleButtonNode( button, selected.valueEquals( element.getRepresentation() ), new VoidFunction0() {
                        public void apply() {
                            selected.set( element.getRepresentation() );
                        }
                    } ) );
                }
//                setOffset( 30, title.getFullBounds().getMaxY() );
            }};
            addChild( representationLayer );
        }

        //Add padding around the node so it doesn't disturb the HBox layout when it moves within the padded node
        private class PaddedNode extends PNode {
            public PaddedNode( final PNode node, double dx, double dy, ValueEquals<ChosenRepresentation> isSelected ) {
                addChild( new PhetPPath( RectangleUtils.expand( node.getFullBoundsReference(), dx, dy ), null ) );
                addChild( node );

                isSelected.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean selected ) {
                        node.setOffset( selected ? new Point( 5, 5 ) : new Point( 0, 0 ) );
                    }
                } );
            }
        }
    }
}