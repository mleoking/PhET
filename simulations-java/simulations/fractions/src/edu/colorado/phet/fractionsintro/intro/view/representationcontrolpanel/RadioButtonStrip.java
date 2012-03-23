// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel;

import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.view.ToggleButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel that lets you see and choose from different representation types.  The buttons look like toggle buttons and
 *
 * @author Sam Reid
 */
public class RadioButtonStrip<T> extends ControlPanelNode {
    public RadioButtonStrip( SettableProperty<T> selected, List<Pair<PNode, T>> elements, int padding ) {
        super( new ContentNode<T>( selected, elements, padding ), new Color( 230, 230, 230 ), new BasicStroke( 2 ), new Color( 102, 102, 102 ) );
    }

    //Inner class enables us to wrap the parent in ControlPanelNode
    private static class ContentNode<T> extends PNode {
        private ContentNode( final SettableProperty<T> selected, final List<Pair<PNode, T>> elements, int padding ) {

            double maxWidth = 0;
            double maxHeight = 0;

            for ( Pair<PNode, T> element : elements ) {
                PNode pNode = element._1;
                if ( pNode.getFullBounds().getWidth() > maxWidth ) {
                    maxWidth = pNode.getFullBounds().getWidth();
                }
                if ( pNode.getFullBounds().getHeight() > maxHeight ) {
                    maxHeight = pNode.getFullBounds().getHeight();
                }
            }

            final double finalMaxHeight = Math.max( maxWidth, maxHeight ) + padding;
            final double finalMaxWidth = finalMaxHeight;
            final HBox representationLayer = new HBox( 10 ) {{
                for ( final Pair<PNode, T> element : elements ) {

                    PNode button = new PhetPPath( new RoundRectangle2D.Double( -2, -2, finalMaxWidth + 4, finalMaxHeight + 4, 20, 20 ), null ) {{

                        final ZeroOffsetNode theNode = new ZeroOffsetNode( element._1 ) {{
                            final Point2D.Double origOffset = new Point2D.Double( finalMaxWidth / 2 - getFullWidth() / 2, finalMaxHeight / 2 - getFullHeight() / 2 );
                            setOffset( origOffset );
                        }};
                        addChild( theNode );
                    }};

                    addChild( new ToggleButtonNode( button, selected.valueEquals( element._2 ), new VoidFunction0() {
                        public void apply() {
                            selected.set( element._2 );
                        }
                    } ) );
                }
            }};
            addChild( representationLayer );
        }
    }
}