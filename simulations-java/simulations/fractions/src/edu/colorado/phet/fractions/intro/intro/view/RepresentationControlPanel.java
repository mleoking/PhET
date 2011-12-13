// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

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
                    PNode highlighter = new PhetPPath( new RoundRectangle2D.Double( -2, -2, finalMaxWidth + 4, finalMaxHeight + 4, 20, 20 ), new Color( 242, 242, 242 ), new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), null ) {{

                        PNode node = new ZeroOffsetNode( element.getNode() );

                        addChild( node );
                        node.setOffset( finalMaxWidth / 2 - node.getFullBounds().getWidth() / 2, finalMaxHeight / 2 - node.getFullBounds().getHeight() / 2 );

                        selected.valueEquals( element.getRepresentation() ).addObserver( new VoidFunction1<Boolean>() {
                            public void apply( Boolean selected ) {
                                setStrokePaint( selected ? Color.yellow : new Color( 178, 178, 178 ) );
                                setStroke( selected ? new BasicStroke( 6 ) : new BasicStroke( 2 ) );
                            }
                        } );

                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mousePressed( PInputEvent event ) {
                                selected.set( element.getRepresentation() );
                            }
                        } );
                    }};

                    addChild( highlighter );
                }
//                setOffset( 30, title.getFullBounds().getMaxY() );
            }};
            addChild( representationLayer );
        }
    }
}