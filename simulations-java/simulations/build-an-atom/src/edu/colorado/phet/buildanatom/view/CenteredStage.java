package edu.colorado.phet.buildanatom.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;

public class CenteredStage implements PhetPCanvas.TransformStrategy {
    private PhetPCanvas canvas;
    private Dimension2D stageSize;

    public CenteredStage( PhetPCanvas canvas, Dimension2D stageSize ) {
        this.canvas = canvas;
        this.stageSize = stageSize;
    }

    public AffineTransform getTransform() {
        double sx = ( (double) canvas.getWidth() ) / stageSize.getWidth();
        double sy = ( (double) canvas.getHeight() ) / stageSize.getHeight();

        //use the smaller and maintain aspect ratio so that circles don't become ellipses
        double scale = sx < sy ? sx : sy;
        scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

        AffineTransform transform = new AffineTransform();
        double scaledStageWidth = scale * stageSize.getWidth();
        double scaledStageHeight = scale * stageSize.getHeight();
        //center it in width and height
        transform.translate( canvas.getWidth() / 2 - scaledStageWidth / 2, canvas.getHeight() / 2 - scaledStageHeight / 2 );
        transform.scale( scale, scale );

        return transform;
    }
}

