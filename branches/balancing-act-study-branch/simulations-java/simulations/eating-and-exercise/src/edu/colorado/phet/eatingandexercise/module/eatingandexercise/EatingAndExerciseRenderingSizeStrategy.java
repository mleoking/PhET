// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;

/**
 * Created by: Sam
 * Apr 18, 2008 at 1:14:25 AM
 */
public class EatingAndExerciseRenderingSizeStrategy implements PhetPCanvas.TransformStrategy {
    private EatingAndExerciseCanvas canvas;

    public EatingAndExerciseRenderingSizeStrategy( EatingAndExerciseCanvas canvas ) {
        this.canvas = canvas;
    }

    //todo: remove magic numbers in layout
    public AffineTransform getTransform() {
        double availableHeight = canvas.getAvailableWorldHeight();
        double availableWidth = canvas.getAvailableWorldWidth();
        double maxVisibleHeight = EatingAndExerciseUnits.feetToMeters( 7.5 );//extra padding for scale node
        double maxVisibleWidth = EatingAndExerciseUnits.feetToMeters( 6 );//extra padding for scale node
        double scaleVert = availableHeight / maxVisibleHeight;
        double scaleHoriz = availableWidth / maxVisibleWidth;
//        System.out.println( "scaleVert = " + scaleVert );
//        System.out.println( "scaleHoriz = " + scaleHoriz );

        double scale = Math.min( scaleHoriz, scaleVert );
//        System.out.println( "scale = " + scale );

        AffineTransform transform = AffineTransform.getScaleInstance( scale, scale );
        transform.translate( maxVisibleHeight * 0.36,//center of human graphic is at x=0, translate onscreen
                             EatingAndExerciseUnits.feetToMeters( 6.5 ) );//translate down a bit to keep the scale onscreen
        return transform;
    }
}
