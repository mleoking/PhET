package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.common.RepaintManager;
import edu.colorado.phet.bernoulli.spline.Spline;
import edu.colorado.phet.bernoulli.spline.SplineGraphic;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.transform.CompositeTransformListener;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 11:56:01 AM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class PipeGraphic extends CompositeGraphic implements TransformListener, InteractiveGraphic, SimpleObserver {
    ModelViewTransform2d transform;
    Pipe pipe;
    private RepaintManager rm;
    SplineGraphic topGraphic;
    ArrayList topVertices = new ArrayList();

    SplineGraphic bottomGraphic;
    ArrayList bottomVertices = new ArrayList();

    ArrayList flowGraphics = new ArrayList();
    CompositeTransformListener transformListener = new CompositeTransformListener();
    Shape fillShape;
    private BufferedImage endImage;
    private Point topPointLoc;
    private AffineTransform pipeTransform;
    private LeftPipeEndGraphic leftEnd;
    private RightPipeEndGraphic rightEnd;

    public RepaintManager getRepaintManager() {
        return rm;
    }

    public PipeGraphic( ModelViewTransform2d transform, Pipe pipe, RepaintManager rm, int numFlowLines, BernoulliModule module ) {
        endImage = new ImageLoader().loadBufferedImage( "images/bernoulli/pipe.gif" );
        this.transform = transform;
        this.pipe = pipe;
        this.rm = rm;
        Stroke borderStroke = new BasicStroke( 4 );
        topGraphic = new SplineGraphic( pipe.getTopSpline(), transform, borderStroke, Color.black );
        transformListener.addTransformListener( topGraphic );

        bottomGraphic = new SplineGraphic( pipe.getBottomSpline(), transform, borderStroke, Color.black );
        transformListener.addTransformListener( bottomGraphic );
        addGraphic( topGraphic, 0 );
        addGraphic( bottomGraphic, 0 );

        transform.addTransformListener( this );

        addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                for( int i = 0; i < flowGraphics.size(); i++ ) {
                    SplineGraphic splineGraphic = (SplineGraphic)flowGraphics.get( i );
                    splineGraphic.paint( g );
                }
            }
        }, 3 );

        Stroke flowStroke = new BasicStroke( 1 );
        Color flowColor = Color.black;
        for( int i = 0; i < pipe.numFlowLines(); i++ ) {
            flowGraphics.add( new SplineGraphic( pipe.flowLineAt( i ), transform, flowStroke, flowColor ) );
        }

        topVertices = new ArrayList();
        double pipeControlPointRadius = .255;
        Point2D.Double[] toppts = pipe.getTopControlPoints();
        for( int i = 0; i < toppts.length; i++ ) {
            PipeControlPointGraphic graphic = new PipeControlPointGraphic( this, toppts[i].x, toppts[i].y, pipeControlPointRadius, transform, Color.red, true, i, module );
            topVertices.add( graphic );
            addGraphic( graphic, 10 );
        }

        bottomVertices = new ArrayList();
        Point2D.Double[] bottompts = pipe.getBottomControlPoints();
        for( int i = 0; i < bottompts.length; i++ ) {
            PipeControlPointGraphic graphic = new PipeControlPointGraphic( this, bottompts[i].x, bottompts[i].y, pipeControlPointRadius, transform, Color.red, false, i, module );
            bottomVertices.add( graphic );
            addGraphic( graphic, 10 );
        }

        Graphic blueBackground = new Graphic() {
            public void paint( Graphics2D g ) {
                if( fillShape == null ) {
                    return;
                }
                g.setColor( Color.blue );
                g.fill( fillShape );
            }
        };
        addGraphic( blueBackground, 0 );
        leftEnd = new LeftPipeEndGraphic( endImage, transform, topGraphic, bottomGraphic );
        addGraphic( leftEnd, 1 );

        BufferedImage flipped = new AffineTransformOp( AffineTransform.getRotateInstance( Math.PI, endImage.getWidth() / 2, endImage.getHeight() / 2 ),
                                                       AffineTransformOp.TYPE_BILINEAR ).filter( endImage, null );
        rightEnd = new RightPipeEndGraphic( flipped, transform, topGraphic, bottomGraphic );
        addGraphic( rightEnd, 1 );

        pipe.addObserver( this );
        update();
    }

    public PipeControlPointGraphic bottomControlPointAt( int i ) {
        return (PipeControlPointGraphic)bottomVertices.get( i );
    }

    public void update() {
        topGraphic.update();
        bottomGraphic.update();
        this.fillShape = transform.toAffineTransform().createTransformedShape( pipe.getShape() );
        leftEnd.update();
        rightEnd.update();

        rm.update();
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        transformListener.transformChanged( mvt );
        this.fillShape = transform.toAffineTransform().createTransformedShape( pipe.getShape() );
        leftEnd.transformChanged( mvt );
        rightEnd.transformChanged( mvt );
//        update();
    }

    public void translatePoint( Point2D.Double modelDX, boolean istop, int index ) {
        if( istop ) {
            pipe.translateTopPoint( index, modelDX );
        }
        else {
            pipe.translateBottomPoint( index, modelDX );
        }
        //Fix the flowlines.
        update();
    }

    public Pipe getPipe() {
        return pipe;
    }

    public Spline getMiddleFlowLineSpline() {
        int midindex = flowGraphics.size() / 2;
        SplineGraphic flowLine = (SplineGraphic)flowGraphics.get( midindex );
        return flowLine.getSpline();
    }

}
