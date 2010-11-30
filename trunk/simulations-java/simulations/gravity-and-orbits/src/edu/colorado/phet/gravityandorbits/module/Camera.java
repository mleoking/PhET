package edu.colorado.phet.gravityandorbits.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * @author Sam Reid
 */
public class Camera {
    //    private Property<Double> scale = new Property<Double>( 1.0 );
    private double deltaScale = 0.75;
    private double targetScale;

    //    private Property<ImmutableVector2D> centerModelPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    private final double deltaTranslate = GravityAndOrbitsModule.PLANET_ORBIT_RADIUS / 20;
    private ImmutableVector2D targetCenterModelPoint;

    private final Property<ModelViewTransform> modelViewTransformProperty;
    private Timer timer;
    private double WIDTH = GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.60;
    private double HEIGHT = GravityAndOrbitsCanvas.STAGE_SIZE.height;

    private Rectangle2D.Double modelRectangle;
    private Function.LinearFunction functionLeft;
    private Function.LinearFunction functionRight;
    private Function.LinearFunction functionTop;
    private Function.LinearFunction functionBottom;
    private double t = 0;//parametric parameter between 0 and 1 that governs the camera transform

    private ModelViewTransform createTransform() {
        return ModelViewTransform.createRectangleInvertedYMapping( modelRectangle, new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ) );
    }

    private Rectangle2D.Double getTargetRectangle() {
        double z = targetScale * 1.5E-9;
        double modelWidth = WIDTH / z;
        double modelHeight = HEIGHT / z;
        return new Rectangle2D.Double( -modelWidth / 2 + targetCenterModelPoint.getX(), -modelHeight / 2 + targetCenterModelPoint.getY(), modelWidth, modelHeight );
    }

    public Camera() {
        this( 1, new ImmutableVector2D( 0, 0 ) );
    }

    public Camera( final double _targetScale, final ImmutableVector2D _targetCenterModelPoint ) {
        this.targetScale = _targetScale;
        this.targetCenterModelPoint = _targetCenterModelPoint;
        modelRectangle = getTargetRectangle();
        this.modelViewTransformProperty = new Property<ModelViewTransform>( createTransform() );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( t <= 1 ) {
                    t = t + 0.1;
                    if ( t > 1 ) {
                        t = 1;
                    }
                    final double x = functionLeft.evaluate( t );
                    final double y = functionTop.evaluate( t );
                    final double right = functionRight.evaluate( t );
                    final double bottom = functionBottom.evaluate( t );

                    final double w = right - x;
                    final double h = bottom - y;
//                    System.out.println( "x = " + x + ", y = " + y + ", w = " + w + ", h = " + h );
                    modelRectangle = new Rectangle2D.Double( x, y, w, h );

                    modelViewTransformProperty.setValue( createTransform() );
                }
            }
        } );
    }

    public void reset() {
        modelViewTransformProperty.reset();
//        scale.reset();
//        centerModelPoint.reset();
    }

    public Property<ModelViewTransform> getModelViewTransformProperty() {
        return modelViewTransformProperty;
    }

    public void zoomTo( double targetScale, ImmutableVector2D targetOffset ) {
        this.targetScale = targetScale;
        this.targetCenterModelPoint = targetOffset;
        Rectangle2D.Double targetRectangle = getTargetRectangle();
        this.functionLeft = new Function.LinearFunction( 0, 1, modelRectangle.getMinX(), targetRectangle.getMinX() );
        this.functionRight = new Function.LinearFunction( 0, 1, modelRectangle.getMaxX(), targetRectangle.getMaxX() );
        this.functionTop = new Function.LinearFunction( 0, 1, modelRectangle.getMinY(), targetRectangle.getMinY() );
        this.functionBottom = new Function.LinearFunction( 0, 1, modelRectangle.getMaxY(), targetRectangle.getMaxY() );
        this.t = 0;
        timer.start();
    }
}
