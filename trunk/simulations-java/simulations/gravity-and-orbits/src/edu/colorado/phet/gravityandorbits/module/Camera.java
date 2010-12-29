package edu.colorado.phet.gravityandorbits.module;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * @author Sam Reid
 */
public class Camera {

    private static final double WIDTH = GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.60;
    private static final double HEIGHT = GravityAndOrbitsCanvas.STAGE_SIZE.height;

    private final Property<ModelViewTransform> modelViewTransformProperty;

    private static ModelViewTransform createTransform( Rectangle2D modelRectangle ) {
        return ModelViewTransform.createRectangleInvertedYMapping( modelRectangle, new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ) );
    }

    private static Rectangle2D.Double getTargetRectangle( double targetScale, ImmutableVector2D targetCenterModelPoint ) {
        double z = targetScale * 1.5E-9;
        double modelWidth = WIDTH / z;
        double modelHeight = HEIGHT / z;
        return new Rectangle2D.Double( -modelWidth / 2 + targetCenterModelPoint.getX(), -modelHeight / 2 + targetCenterModelPoint.getY(), modelWidth, modelHeight );
    }

    public Camera() {
        this( 1, new ImmutableVector2D( 0, 0 ) );
    }

    private Camera( final double targetScale, final ImmutableVector2D targetCenterModelPoint ) {
        Rectangle2D modelRectangle = getTargetRectangle( targetScale, targetCenterModelPoint );
        this.modelViewTransformProperty = new Property<ModelViewTransform>( createTransform( modelRectangle ) );
    }

    public Property<ModelViewTransform> getModelViewTransformProperty() {
        return modelViewTransformProperty;
    }

    public void zoomTo( double targetScale, ImmutableVector2D targetOffset ) {
        Rectangle2D.Double targetRectangle = getTargetRectangle( targetScale, targetOffset );
        final double x = targetRectangle.getMinX();
        final double y = targetRectangle.getMinY();
        final double w = targetRectangle.getMaxX() - x;
        final double h = targetRectangle.getMaxY() - y;
        modelViewTransformProperty.setValue( createTransform( new Rectangle2D.Double( x, y, w, h ) ) );
    }
}
