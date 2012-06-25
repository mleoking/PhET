// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Button that can be pressed in, and stays in when selected like a real radio button in an old car stereo.
 * Used by RadioButtonStrip but public in case other usages need it
 *
 * @author Sam Reid
 */
public class ToggleButtonNode extends PNode {
    private final DynamicCursorHandler cursorHandler = new DynamicCursorHandler();

    //Nice shade of green to show for selected (pressed in) toggle button nodes.
    public static final Color FAINT_GREEN = new Color( 215, 237, 218 );
    public static final Color DEFAULT_BACKGROUND_COLOR = new Color( 242, 242, 242 );

    public ToggleButtonNode( final PNode node, final ObservableProperty<Boolean> selected, final VoidFunction0 pressed ) {
        this( node, selected, pressed, DEFAULT_BACKGROUND_COLOR, true );
    }

    public ToggleButtonNode( final PNode node, final ObservableProperty<Boolean> selected, final VoidFunction0 pressed, final Color pressedInColor, final boolean disableCursorWhenPressedIn ) {

        //We have to handle the pickability since the cursor changes, so disable on the target node
        node.setPickable( false );
        node.setChildrenPickable( false );

        final double pressAmountX = 4;
        final double pressAmountY = 6;
        final PhetPPath hiddenBorder = new PhetPPath( RectangleUtils.expand( node.getFullBounds(), pressAmountX, pressAmountY ), null );
        final RoundRectangle2D.Double shape = new RoundRectangle2D.Double( node.getFullBounds().getMinX(), node.getFullBounds().getMinY(), node.getFullBounds().getWidth(), node.getFullBounds().getHeight(), 20, 20 );
        final PhetPPath buttonBackground = new PhetPPath( shape, DEFAULT_BACKGROUND_COLOR, new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), null );
        final PhetPPath shadow = new PhetPPath( shape, Color.darkGray );

        selected.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean selected ) {
                buttonBackground.setStrokePaint( new Color( 120, 120, 120 ) );
                buttonBackground.setPaint( selected ? pressedInColor : DEFAULT_BACKGROUND_COLOR );
                node.setOffset( selected ? new Point( 0, 0 ) : new Point2D.Double( -pressAmountX, -pressAmountY ) );
                buttonBackground.setOffset( selected ? new Point( 0, 0 ) : new Point2D.Double( -pressAmountX, -pressAmountY ) );

                //If the button got pressed in, change from being a hand to an arrow so it doesn't like you can still press the button and vice versa
                if ( disableCursorWhenPressedIn ) {
                    cursorHandler.setCursor( selected ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR );
                }
            }
        } );

        addInputEventListener( cursorHandler );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                pressed.apply();
            }
        } );

        addChild( hiddenBorder );
        addChild( shadow );
        addChild( buttonBackground );
        addChild( node );
        node.centerFullBoundsOnPoint( buttonBackground.getFullBounds().getCenterX(), buttonBackground.getFullBounds().getCenterY() );
    }
}