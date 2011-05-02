// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

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
    public SaltShakerNode( final ModelViewTransform transform, final VoidFunction1<ImmutableVector2D> addSalt ) {
        //Show the image of the shaker
        addChild( new PImage( RESOURCES.getImage( "shaker.png" ) ) );

        //Add interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                //Move the shaker
                translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );

                //If it was a proper shake, emit salt crystals
                if ( event.getCanvasDelta().getHeight() < 0 ) {
                    addSalt.apply( new ImmutableVector2D( transform.viewToModel( event.getCanvasPosition() ) ) );
                }
            }
        } );
    }
}