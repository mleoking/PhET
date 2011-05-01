// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class SaltShakerNode extends PNode {
    public SaltShakerNode() {
        //Show the image of the shaker
        addChild( new PImage( RESOURCES.getImage( "shaker.png" ) ) );

        //Add interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                translate( event.getCanvasDelta().getWidth(), event.getCanvasDelta().getHeight() );
            }
        } );
    }
}