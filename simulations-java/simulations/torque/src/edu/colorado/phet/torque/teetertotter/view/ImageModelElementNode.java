// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.torque.teetertotter.model.weights.ImageWeight;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class defines a Piccolo node that represents a model element in the
 * view, and the particular model element that it represents contains an image
 * that is used in the representation.
 *
 * @author John Blanco
 */
public class ImageModelElementNode extends PNode {
    public ImageModelElementNode( ImageWeight imageWeight ) {
        final PImage imageNode = new PImage();
        imageWeight.imageProperty.addObserver( new VoidFunction1<Image>() {
            public void apply( Image image ) {
                imageNode.setImage( image );
            }
        } );
    }
}
