// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro.view;

import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.RadioButtonStripControlPanelNode.Element;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.v;
import static edu.colorado.phet.common.phetcommon.view.util.ShapeUtils.createShapeFromPoints;
import static edu.colorado.phet.functions.intro.view.Representation.*;
import static fj.data.List.list;

/**
 * @author Sam Reid
 */
public class NavigationBar extends PNode {
    @SuppressWarnings("unchecked") public NavigationBar( IntegerProperty level ) {
        final PhetFont font = new PhetFont( 26, true );
        final PhetFont smallFont = new PhetFont( 14 );
        final List<Element<Representation>> icons = list( new Element<Representation>( new ShapeIcon(), SHAPES, null ),
                                                          new Element<Representation>( new PhetPText( "ABC", font ), TEXT, null ),
                                                          new Element<Representation>( new PhetPText( "123", font ), NUMBERS, null ),
                                                          new Element<Representation>( new VBox( 2, new ShapeIcon() {{scale( 0.4 );}},
                                                                                                 new PNode(),
                                                                                                 new PhetPText( "ABC", smallFont ),
                                                                                                 new PhetPText( "123", smallFont ) ), MIXED, null ) );
        final Property<Representation> selected = new Property<Representation>( SHAPES );

        //Code taken from RadioButtonStripControlPanelNode
        double maxWidth = 0;
        double maxHeight = 0;

        for ( Element<Representation> element : icons ) {
            PNode pNode = element.node;
            if ( pNode.getFullBounds().getWidth() > maxWidth ) {
                maxWidth = pNode.getFullBounds().getWidth();
            }
            if ( pNode.getFullBounds().getHeight() > maxHeight ) {
                maxHeight = pNode.getFullBounds().getHeight();
            }
        }

        int padding = 3;
        final double finalMaxHeight = Math.max( maxWidth, maxHeight ) + padding;
        final double finalMaxWidth = finalMaxHeight;

        HBox box = new HBox( 50 );
        IntegerProperty count = new IntegerProperty( 0 );
        for ( final Element<Representation> element : icons ) {
            PNode button = new PhetPPath( new RoundRectangle2D.Double( -2, -2, finalMaxWidth + 4, finalMaxHeight + 4, 20, 20 ), null ) {{

                final ZeroOffsetNode theNode = new ZeroOffsetNode( element.node ) {{
                    final Point2D.Double origOffset = new Point2D.Double( finalMaxWidth / 2 - getFullWidth() / 2, finalMaxHeight / 2 - getFullHeight() / 2 );
                    setOffset( origOffset );
                }};
                addChild( theNode );
            }};

            box.addChild( new VBox( 4, VBox.LEFT_ALIGNED, new ToggleButtonNode( button, selected.valueEquals( element.value ), new VoidFunction0() {
                public void apply() {
                    SimSharingManager.sendUserMessage( element.component, UserComponentTypes.button, UserActions.pressed );
                    selected.set( element.value );
                }
            } ), new CircleSetNode( level, count ) ) );
        }
        addChild( new ControlPanelNode( box, new Color( 230, 230, 230 ), new BasicStroke( 2 ), new Color( 102, 102, 102 ) ) );
    }

    public static class ShapeIcon extends PNode {
        public ShapeIcon() {
            double sideLength = 50;
            final double y = -sideLength * Math.sqrt( 3 ) / 2;
            addChild( new PhetPPath( createShapeFromPoints( list( v( 0, 0 ), v( sideLength, 0 ), v( sideLength / 2, y ) ) ), Color.red, new BasicStroke( 2 ), Color.black ) );
        }
    }

    private static class CircleSetNode extends PNode {
        private static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );

        public CircleSetNode( final IntegerProperty level, final IntegerProperty count ) {
            PhetPPath[] paths = new ArrayList<PhetPPath>() {{

                //Spacer to line up with buttons
                add( new PhetPPath( new Rectangle2D.Double( 0, 0, 1, 1 ), TRANSPARENT ) );

                for ( int i = 0; i < 5; i++ ) {
                    final Integer value = count.get();
                    final int circleRadius = 8;
                    add( new PhetPPath( new Ellipse2D.Double( 0, 0, circleRadius, circleRadius ), TRANSPARENT, new BasicStroke( 1 ), Color.black ) {{
                        level.addObserver( new VoidFunction1<Integer>() {
                            public void apply( final Integer integer ) {
                                setPaint( integer.equals( value ) ? Color.black : TRANSPARENT );
                            }
                        } );
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mousePressed( final PInputEvent event ) {
                                level.set( value );
                            }
                        } );
                    }} );
                    count.increment();
                }
            }}.toArray( new PhetPPath[new ArrayList<PhetPPath>() {{}}.size()] );
            addChild( new HBox( 5, paths ) );
        }
    }
}