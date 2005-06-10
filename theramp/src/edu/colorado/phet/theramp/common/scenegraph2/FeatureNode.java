package edu.colorado.phet.theramp.common.scenegraph2;

import java.awt.geom.Rectangle2D;

/**
 * Use this class to change features of the Graphics2D as a sibling (rather than as the graphic itself.)
 */
public class FeatureNode extends AbstractGraphic {

    public void render( RenderEvent renderEvent ) {
        setup( renderEvent );
    }

    public Rectangle2D getLocalBounds() {
        return new Rectangle2D.Double();
    }
}
