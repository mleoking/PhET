// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Base class for energy sources, converters, and users.
 *
 * @author John Blanco
 */
public abstract class EnergySystemElement extends PositionableModelElement {

    // List of images, if any, that are used in the view to represent this element. 
    private final List<ModelElementImage> imageList = new ArrayList<ModelElementImage>();

    // Image that is used as an icon in the view to represent this element.
    private final Image iconImage;

    protected EnergySystemElement( Image iconImage ) {
        this( iconImage, new ArrayList<ModelElementImage>() );
    }

    protected EnergySystemElement( Image iconImage, List<ModelElementImage> images ) {
        this.iconImage = iconImage;
        this.imageList.addAll( images );
    }

    public List<ModelElementImage> getImageList() {
        return imageList;
    }

    public Image getIconImage() {
        return iconImage;
    }

    public abstract IUserComponent getUserComponent();
}
