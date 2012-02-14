// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.view.Representation;
import edu.colorado.phet.fractionsintro.intro.view.ToggleButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel that lets you see and choose from different representation types for the fraction.
 *
 * @author Sam Reid
 */
public class RepresentationControlPanel extends ControlPanelNode {
    public RepresentationControlPanel( SettableProperty<Representation> chosenRepresentation ) {
        super( new RepresentationControlPanelContentNode( chosenRepresentation ), new Color( 230, 230, 230 ), new BasicStroke( 2 ), new Color( 102, 102, 102 ) );
    }

    private static class RepresentationControlPanelContentNode extends PNode {
        private RepresentationControlPanelContentNode( final SettableProperty<Representation> selected ) {
            final RepresentationIcon[] elements = new RepresentationIcon[] {
                    new PieIcon( selected ),
                    new HorizontalBarIcon( selected ),
                    new VerticalBarIcon( selected ),
                    new WaterGlassIcon( selected ),
                    new CakeIcon( selected ),
                    new NumberLineIcon( selected ) {{
                        scale( 1.2 );
                    }},
            };

            double maxWidth = 0;
            double maxHeight = 0;

            for ( RepresentationIcon representationIcon : elements ) {
                PNode pNode = representationIcon.getNode();
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
                for ( final RepresentationIcon element : elements ) {

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
            }};
            addChild( representationLayer );
        }
    }
}