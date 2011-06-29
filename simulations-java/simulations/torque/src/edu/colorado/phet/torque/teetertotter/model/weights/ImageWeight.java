// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * This class defines a weight in the model that carries with it an associated
 * image that should be presented in the view.  The image can change at times,
 * such as when it is dropped on the balance.
 *
 * @author John Blanco
 */
public class ImageWeight {

    final public Property<Image> imageProperty;

    protected ImageWeight( Image image ) {
        imageProperty = new Property<Image>( image );
    }
}
