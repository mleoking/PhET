// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;

import static java.awt.Color.blue;
import static java.awt.Color.yellow;

/**
 * Button for moving forward or backward through the kits
 *
 * @author Sam Reid
 */
public class ArrowButton extends HTMLImageButtonNode {
    public static final PhetResources RESOURCES = new PhetResources( "piccolo-phet" );

    //Create an arrow button for moving forward or background through the kits
    public ArrowButton( BufferedImage image ) {

        //Make the arrow blue
        super( new MakeDuotoneImageOp( blue ).filter( image, null ) );

        //Show a cursor hand for the button
        addInputEventListener( new CursorHandler() );

        //Make the background yellow
        setBackground( yellow );
    }
}