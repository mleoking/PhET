// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.RESOURCES;

/**
 * Salt shaker, which can be shaken to emit salt crystals.
 *
 * @author Sam Reid
 */
public class SaltShakerNode extends PNode {
    private ArrayList<Double> historyDY = new ArrayList<Double>();

    public SaltShakerNode( final ModelViewTransform transform, final VoidFunction1<ImmutableVector2D> addSalt ) {
        //Show the image of the shaker
        addChild( new PImage( RESOURCES.getImage( "shaker.png" ) ) );

        //Add interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                //Move the shaker
                double dx = event.getCanvasDelta().getWidth();
                double dy = event.getCanvasDelta().getHeight();
                translate( dx, dy );

                //Keep track of the deltas in the y direction since they are used to determine if any salt should shake out
                if ( dy != 0 ) {
                    historyDY.add( dy );
                }

                //If it was a proper shake, emit salt crystals.  This means the salt shaker was moving down, then up
                if ( historyDY.size() > 2 ) {
                    double previousDY = historyDY.get( historyDY.size() - 2 );
                    if ( previousDY >= 0 && dy < 0 ) {
                        addSalt.apply( new ImmutableVector2D( transform.viewToModel( event.getCanvasPosition() ) ) );
                    }
                }
            }
        } );
    }
}