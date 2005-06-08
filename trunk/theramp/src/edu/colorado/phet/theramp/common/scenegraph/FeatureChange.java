package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Use this class to change features of the Graphics2D as a sibling (rather than as the graphic itself.)
 */
public class FeatureChange extends AbstractGraphic {
    public void paint( Graphics2D graphics2D ) {
    }

    public Rectangle2D getLocalBounds() {
        return new Rectangle2D.Double();
    }
}
