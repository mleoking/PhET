// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel that lets you see and choose from different representation types.  The buttons look like toggle buttons and behave like real radio pushbuttons (like in an old car radio)
 * where pushing one in pops the others out.  Only one can be selected at a time.
 * <p/>
 * Designed by Ariel Paul for Fractions and generalized when it was needed for Fluid Pressure and Flow Tab 1
 *
 * @author Sam Reid
 */
public class RadioButtonStripControlPanelNode<T> extends ControlPanelNode {

    public static class Element<T> {
        public final PNode node;
        public final T value;
        public final IUserComponent component;

        public Element( final PNode node, final T value, final IUserComponent component ) {
            this.node = node;
            this.value = value;
            this.component = component;
        }
    }

    public RadioButtonStripControlPanelNode( SettableProperty<T> selected, List<Element<T>> elements, int padding ) {
        super( new RadioButtonStripNode<T>( selected, elements, padding ), new Color( 230, 230, 230 ), new BasicStroke( 2 ), new Color( 102, 102, 102 ) );
    }

    //Inner class enables us to wrap the parent in ControlPanelNode.  Public in case clients want to use it without the control panel exterior decoration.
    public static class RadioButtonStripNode<T> extends PNode {
        public RadioButtonStripNode( final SettableProperty<T> selected, final List<Element<T>> elements, int padding ) {

            double maxWidth = 0;
            double maxHeight = 0;

            for ( Element<T> element : elements ) {
                PNode pNode = element.node;
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
                for ( final Element<T> element : elements ) {

                    PNode button = new PhetPPath( new RoundRectangle2D.Double( -2, -2, finalMaxWidth + 4, finalMaxHeight + 4, 20, 20 ), null ) {{

                        final ZeroOffsetNode theNode = new ZeroOffsetNode( element.node ) {{
                            final Point2D.Double origOffset = new Point2D.Double( finalMaxWidth / 2 - getFullWidth() / 2, finalMaxHeight / 2 - getFullHeight() / 2 );
                            setOffset( origOffset );
                        }};
                        addChild( theNode );
                    }};

                    addChild( new ToggleButtonNode( button, selected.valueEquals( element.value ), new VoidFunction0() {
                        public void apply() {
                            SimSharingManager.sendUserMessage( element.component, UserComponentTypes.button, UserActions.pressed );
                            selected.set( element.value );
                        }
                    } ) );
                }
            }};
            addChild( representationLayer );
        }
    }
}