package edu.colorado.phet.gravityandorbits.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * @author Sam Reid
 */
public class Camera {
    private Property<Double> scale = new Property<Double>( 1.0 );
    private double deltaScale = 0.75;
    private double targetScale;

    private Property<ImmutableVector2D> centerModelPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );
    private final double deltaTranslate = GravityAndOrbitsModule.PLANET_ORBIT_RADIUS / 20;
    private ImmutableVector2D targetCenterModelPoint;

    private final Property<ModelViewTransform> modelViewTransformProperty;
    private Timer timer;

    private ModelViewTransform createTransform() {
//        return ModelViewTransform.createSinglePointScaleInvertedYMapping( centerModelPoint.getValue().toPoint2D(), new Point2D.Double( GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.30, GravityAndOrbitsCanvas.STAGE_SIZE.height * 0.5 ), 1.5E-9 * scale.getValue() );

        double z = scale.getValue() * 1.5E-9;
        final double w = GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.60;
        final double h = GravityAndOrbitsCanvas.STAGE_SIZE.height;
        double modelWidth = w / z;
        double modelHeight = h / z;
        return ModelViewTransform.createRectangleInvertedYMapping( new Rectangle2D.Double( -modelWidth / 2 + centerModelPoint.getValue().getX(), -modelHeight / 2 + centerModelPoint.getValue().getY(), modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, w, h ) );
    }

    public Camera() {
        this( 1, new ImmutableVector2D( 0, 0 ) );
    }

    public Camera( final double _targetScale, final ImmutableVector2D _targetCenterModelPoint ) {
        this.targetScale = _targetScale;
        this.targetCenterModelPoint = _targetCenterModelPoint;
        this.modelViewTransformProperty = new Property<ModelViewTransform>( createTransform() );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //translate first
                if ( centerModelPoint.getValue().getDistance( targetCenterModelPoint ) > deltaTranslate ) {
                    ImmutableVector2D d = targetCenterModelPoint.getSubtractedInstance( centerModelPoint.getValue() );
                    centerModelPoint.setValue( centerModelPoint.getValue().getAddedInstance( d.getNormalizedInstance().getScaledInstance( deltaTranslate * Math.pow( scale.getValue(), 1.0 / 3 ) ) ) );
                }
                else {
                    //then scale
                    if ( Math.abs( scale.getValue() - targetScale ) > deltaScale ) {
                        double sign = targetScale - scale.getValue() > 0 ? +1 : -1;
                        scale.setValue( scale.getValue() + sign * deltaScale );
                    }
                }

                modelViewTransformProperty.setValue( createTransform() );
            }
        } );
    }

    public void reset() {
        modelViewTransformProperty.reset();
        scale.reset();
        centerModelPoint.reset();
    }

    public Property<ModelViewTransform> getModelViewTransformProperty() {
        return modelViewTransformProperty;
    }

    public void zoomTo( double targetScale, ImmutableVector2D targetOffset ) {
        this.targetScale = targetScale;
        this.targetCenterModelPoint = targetOffset;
        timer.start();
    }
}
