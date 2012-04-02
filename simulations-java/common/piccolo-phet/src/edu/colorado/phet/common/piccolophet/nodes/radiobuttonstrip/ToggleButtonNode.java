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

    public ToggleButtonNode( final PNode node, final ObservableProperty<Boolean> selected, final VoidFunction0 pressed ) {

        //We have to handle the pickability since the cursor changes, so disable on the target node
        node.setPickable( false );
        node.setChildrenPickable( false );

        final double pressAmountX = 4;
        final double pressAmountY = 6;
        final PhetPPath hiddenBorder = new PhetPPath( RectangleUtils.expand( node.getFullBounds(), pressAmountX, pressAmountY ), null );
        final PhetPPath border = new PhetPPath( new RoundRectangle2D.Double( node.getFullBounds().getMinX(), node.getFullBounds().getMinY(), node.getFullBounds().getWidth(), node.getFullBounds().getHeight(), 20, 20 ), new Color( 242, 242, 242 ), new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), null );
        final PhetPPath shadow = new PhetPPath( new RoundRectangle2D.Double( node.getFullBounds().getMinX(), node.getFullBounds().getMinY(), node.getFullBounds().getWidth(), node.getFullBounds().getHeight(), 20, 20 ), Color.darkGray );

        selected.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean selected ) {
                border.setStrokePaint( new Color( 178, 178, 178 ) );

                //Show a highlight around the button border when selected?  Looks awkward when the buttons toggle in
//                border.setStrokePaint( selected ? Color.yellow : new Color( 178, 178, 178 ) );
                border.setStroke( selected ? new BasicStroke( 2 ) : new BasicStroke( 2 ) );
                node.setOffset( selected ? new Point( 0, 0 ) : new Point2D.Double( -pressAmountX, -pressAmountY ) );
                border.setOffset( selected ? new Point( 0, 0 ) : new Point2D.Double( -pressAmountX, -pressAmountY ) );

                //If the button got pressed in, change from being a hand to an arrow so it doesn't like you can still press the button
                //And vice versa
                cursorHandler.setCursor( selected ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR );
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
        addChild( border );
        addChild( node );
        node.centerFullBoundsOnPoint( border.getFullBounds().getCenterX(), border.getFullBounds().getCenterY() );
    }
}