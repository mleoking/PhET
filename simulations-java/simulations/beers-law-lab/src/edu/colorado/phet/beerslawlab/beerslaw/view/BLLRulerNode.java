// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.Ruler;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.MovableDragHandler;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;

/**
 * A ruler, constrained to drag within the bounds of the stage.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BLLRulerNode extends PNode {

    /**
     * Constructor.
     * @param ruler model element
     * @param mvt transform between model and view coordinate frames
     */
    public BLLRulerNode( final Ruler ruler, final ModelViewTransform mvt ) {

        // Compute tick labels, 1 major tick for every 0.5 unit of length, labels on the ticks that correspond to integer values.
        String[] majorTicks = new String[ ( 2 * ruler.length ) + 1];
        for ( int i = 0; i < majorTicks.length; i++ ) {
            majorTicks[i] = ( i % 2 == 0 ) ? String.valueOf( i / 2 ) : "";
        }

        // Use ruler node from common framework
        addChild( new RulerNode( mvt.modelToViewDeltaX( ruler.length ), mvt.modelToViewDeltaY( ruler.height ), majorTicks, Strings.UNITS_CENTIMETERS, 4, 18 ) );

        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MovableDragHandler( UserComponents.ruler, ruler, this, mvt ) );

        // sync view with model
        ruler.location.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                setOffset( mvt.modelToView( ruler.location.get().toPoint2D() ) );
            }
        });
    }
}
