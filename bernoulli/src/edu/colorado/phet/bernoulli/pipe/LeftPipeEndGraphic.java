package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.spline.SplineGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 9, 2003
 * Time: 2:37:40 AM
 * Copyright (c) Sep 9, 2003 by Sam Reid
 */
public class LeftPipeEndGraphic implements Graphic {
    private BufferedImage endImage;
    private ModelViewTransform2d transform;
    private SplineGraphic topGraphic;
    private SplineGraphic bottomGraphic;
    private AffineTransform pipeTransform;
    private Point topPointLoc;

    public LeftPipeEndGraphic( BufferedImage endImage, ModelViewTransform2d transform, SplineGraphic topGraphic, SplineGraphic bottomGraphic ) {
        this.endImage = endImage;
        this.transform = transform;
        this.topGraphic = topGraphic;
        this.bottomGraphic = bottomGraphic;
    }

    public void paint( Graphics2D g ) {
        if( pipeTransform != null ) {
            g.drawRenderedImage( endImage, pipeTransform );//AffineTransform.getTranslateInstance(topPointLoc.x, topPointLoc.y));
        }
    }

    public void update() {
        double topModelX = topGraphic.getSpline().controlPointAt( 0 ).getX();
        double topModelY = topGraphic.getSpline().controlPointAt( 0 ).getY();
        double bottomModelY = bottomGraphic.getSpline().controlPointAt( 0 ).getY();

        double pipeWidth = .6;
        int pipeViewWidth = Math.abs( transform.modelToViewDifferentialX( pipeWidth ) );
        double pipeHeight = Math.abs( topModelY - bottomModelY );
        double insetFraction = .8;
        int pipeViewHeight = (int)( Math.abs( transform.modelToViewDifferentialY( pipeHeight ) ) / insetFraction );
        topPointLoc = transform.modelToView( topModelX, topModelY );

        double widthScale = ( (double)pipeViewWidth ) / endImage.getWidth();
        double heightScale = ( (double)pipeViewHeight ) / endImage.getHeight();
        pipeTransform = AffineTransform.getScaleInstance( widthScale, heightScale );
        double targetX = topPointLoc.x - pipeViewWidth;
        double targetY = topPointLoc.y - pipeViewHeight * ( 1 - insetFraction ) / 2.0 * 1.2;
        pipeTransform.translate( targetX / widthScale, targetY / heightScale );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
    }
}
