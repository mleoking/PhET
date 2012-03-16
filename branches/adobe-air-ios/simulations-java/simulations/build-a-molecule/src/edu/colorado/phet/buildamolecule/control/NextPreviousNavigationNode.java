//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows a central node surrounded with next/previous arrows. Need to implement next(),previous(),
 * and when availability changes modify hasNext and hasPrevious
 */
public abstract class NextPreviousNavigationNode extends PNode {

    public final Property<Boolean> hasNext = new Property<Boolean>( false );
    public final Property<Boolean> hasPrevious = new Property<Boolean>( false );

    private static final double ARROW_PADDING = 8;

    public NextPreviousNavigationNode( PNode centerNode, final Color arrowColor, final Color arrowStrokeColor, final double arrowWidth, final double arrowHeight ) {

        /*---------------------------------------------------------------------------*
        * previous
        *----------------------------------------------------------------------------*/

        final PhetPPath previousKitNode = new PhetPPath( new DoubleGeneralPath() {{
            // triangle pointing to the left
            moveTo( 0, arrowHeight / 2 );
            lineTo( arrowWidth, 0 );
            lineTo( arrowWidth, arrowHeight );
            closePath();
        }}.getGeneralPath() ) {{
            setPaint( arrowColor );
            setStrokePaint( arrowStrokeColor );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    if ( hasPrevious.get() ) {
                        previous();
                    }
                }
            } );
            hasPrevious.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( hasPrevious.get() );
                }
            } );
        }};
        addChild( previousKitNode );

        /*---------------------------------------------------------------------------*
        * center
        *----------------------------------------------------------------------------*/

        addChild( centerNode );

        /*---------------------------------------------------------------------------*
        * next
        *----------------------------------------------------------------------------*/

        final PhetPPath nextKitNode = new PhetPPath( new DoubleGeneralPath() {{
            // triangle pointing to the right
            moveTo( arrowWidth, arrowHeight / 2 );
            lineTo( 0, 0 );
            lineTo( 0, arrowHeight );
            closePath();
        }}.getGeneralPath() ) {{
            setPaint( arrowColor );
            setStrokePaint( arrowStrokeColor );
            addInputEventListener( new CursorHandler() {
                @Override
                public void mouseClicked( PInputEvent event ) {
                    if ( hasNext.get() ) {
                        next();
                    }
                }
            } );
            hasNext.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( hasNext.get() );
                }
            } );
        }};
        addChild( nextKitNode );

        /*---------------------------------------------------------------------------*
        * positioning
        *----------------------------------------------------------------------------*/

        double maxHeight = Math.max( arrowHeight, centerNode.getFullBounds().getHeight() );

        previousKitNode.setOffset( 0, ( maxHeight - arrowHeight ) / 2 );
        centerNode.setOffset( arrowWidth + ARROW_PADDING, ( maxHeight - centerNode.getFullBounds().getHeight() ) / 2 );
        nextKitNode.setOffset( centerNode.getFullBounds().getMaxX() + ARROW_PADDING, ( maxHeight - arrowHeight ) / 2 );
    }

    protected abstract void next();

    protected abstract void previous();
}
