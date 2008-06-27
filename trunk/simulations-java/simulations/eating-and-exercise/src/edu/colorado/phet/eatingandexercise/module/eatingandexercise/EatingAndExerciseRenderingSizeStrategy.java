package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Created by: Sam
 * Apr 18, 2008 at 1:14:25 AM
 */
public class EatingAndExerciseRenderingSizeStrategy extends PhetPCanvas.RenderingSizeStrategy {
    private EatingAndExerciseCanvas canvas;

    public EatingAndExerciseRenderingSizeStrategy( EatingAndExerciseCanvas canvas, double CANVAS_WIDTH, double CANVAS_HEIGHT ) {
        super( canvas, new PDimension( CANVAS_WIDTH*1.2, CANVAS_HEIGHT*1.2) );//todo: remove the need for layout magic numbers
        this.canvas = canvas;
    }

    protected AffineTransform getPreprocessedTransform() {
        return AffineTransform.getTranslateInstance( canvas.getWidth() * 0.15, canvas.getHeight() - canvas.getHumanControlPanelHeight() - 50 );//todo: remove magic numbers in layout
    }

    public AffineTransform getTransform() {
        return super.getTransform();
    }
}
