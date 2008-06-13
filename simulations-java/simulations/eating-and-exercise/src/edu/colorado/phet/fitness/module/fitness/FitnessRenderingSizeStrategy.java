package edu.colorado.phet.fitness.module.fitness;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Created by: Sam
 * Apr 18, 2008 at 1:14:25 AM
 */
public class FitnessRenderingSizeStrategy extends PhetPCanvas.RenderingSizeStrategy {
    private FitnessCanvas canvas;

    public FitnessRenderingSizeStrategy( FitnessCanvas canvas, double CANVAS_WIDTH, double CANVAS_HEIGHT ) {
        super( canvas, new PDimension( CANVAS_WIDTH, CANVAS_HEIGHT ) );
        this.canvas = canvas;
    }

    protected AffineTransform getPreprocessedTransform() {
        return AffineTransform.getTranslateInstance( canvas.getWidth() * 0.15, canvas.getHeight() - canvas.getHumanControlPanelHeight() - 50 );//todo: remove magic numbers in layout
    }

    public AffineTransform getTransform() {
        return super.getTransform();
    }
}
