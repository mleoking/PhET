package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.bernoulli.common.*;
import edu.colorado.phet.bernoulli.spline.segments.Segment;
import edu.colorado.phet.bernoulli.spline.segments.SegmentPath;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.arrows.Arrow;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 9:31:25 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class VolumeGraphic implements Graphic, SimpleObserver, TransformListener {
    CrossSectionalVolume volume;
    ModelViewTransform2d transform;
    private RepaintManager repainter;
    Shape shape;
    CircleGraphic cg;
    CircleGraphic bottom;
    private Color color;
    public static boolean showDebuggingGraphics = false;
    public static boolean showWidthAndHeight = false;
    private TexturePaint texturePaint;
    private Rectangle textureRectangle;
    private BufferedImage textureImage;

    public VolumeGraphic( CrossSectionalVolume volume, ModelViewTransform2d transform, RepaintManager repainter ) {
        this.volume = volume;
        this.transform = transform;
        this.repainter = repainter;
        transform.addTransformListener( this );
        volume.addObserver( this );
        cg = new CircleGraphic( new Point2D.Double( 0, 0 ), .2, Color.green, transform );
        bottom = new CircleGraphic( new Point2D.Double( 0, 0 ), .2, Color.green, transform );

        color = Color.orange;

        textureImage = new ImageLoader().loadBufferedImage( "images/bernoulli/drop15blue.gif" );
        textureRectangle = new Rectangle( 0, 0, 15, 15 );
        texturePaint = new TexturePaint( textureImage, textureRectangle );

        transformChanged( transform );
        update();
    }

    public void paint( Graphics2D g ) {
        if( shape != null ) {
            g.setPaint( texturePaint );
            g.fill( shape );
        }
        if( showDebuggingGraphics ) {
            cg.paint( g );
            bottom.paint( g );

            SegmentPath path = volume.getTopPath();
            SegmentPathGraphic spg = new SegmentPathGraphic( path, transform );
            spg.paint( g );

            SegmentPath bot = volume.getBottomPath();
            new SegmentPathGraphic( bot, transform ).paint( g );
            Point2D.Double loc = volume.getCenter();
            new CircleGraphic( loc, .41, Color.magenta, transform ).paint( g );
            Segment topSeg = volume.getTopIntersectionSegment();
            new SegmentGraphic( transform, topSeg.getStartPoint().getX(), topSeg.getStartPoint().getY(),
                                topSeg.getFinishPoint().getX(), topSeg.getFinishPoint().getY(),
                                Color.yellow, new BasicStroke( 10 ) ).paint( g );
            Point2D.Double topIntersection = volume.getTopIntersection();
            if( topIntersection != null ) {
                new CircleGraphic( topIntersection, .3, Color.cyan, transform ).paint( g );
            }
            else {
                g.setColor( Color.black );
                g.setFont( new Font( "Dialog", 0, 30 ) );
                g.drawString( "Null top intersection point.", 100, 100 );
            }
        }
        if( showWidthAndHeight ) {
            showWidthAndHeight( g );
        }
    }

    private void showWidthAndHeight( Graphics2D g ) {
        Arrow a = new Arrow( Color.black, 3 );
        Segment topSeg = volume.getTopIntersectionSegment();
        Point2D.Double topIntersection = volume.getTopIntersection();
        Point2D.Double bottomIntersection = volume.getBottomIntersection();
        if( topIntersection == null || bottomIntersection == null ) {
            return;
        }

        PhetVector toBase = new PhetVector( bottomIntersection.getX(), bottomIntersection.getY() );
        PhetVector vector = new PhetVector( topIntersection.getX() - bottomIntersection.getX(), topIntersection.getY() - bottomIntersection.getY() );

        double magBase = toBase.getMagnitude();
        magBase += .035;
        toBase.setMagnitude( magBase );

        PhetVector endpoint = toBase.getAddedInstance( vector );
        new ArrowGraphicTransform( a, toBase.getX(), toBase.getY(),
                                   endpoint.getX(), endpoint.getY(), transform ).paint( g );
        Point middle = transform.modelToView( topSeg.getStartPoint().getX(), topSeg.getStartPoint().getY() );
        Font font = ( new Font( "dialog", Font.BOLD, 38 ) );
        g.setFont( font );

        g.setColor( Color.yellow );
        DecimalFormat df = new DecimalFormat( "#0.00" );
        String val = df.format( volume.getWidth() );
        String text = "Height= " + val + " m";
        new OutlinedText().paint( g, text, middle.x, middle.y, Color.black, Color.yellow, new BasicStroke( 4 ), font );

        double width = volume.getVolumeWidth();
        String widthStr = df.format( width );
        String widthText = "Width= " + widthStr + " m";

        Point bottomView = transform.modelToView( toBase.getX(), toBase.getY() );
        new OutlinedText().paint( g, widthText, bottomView.x, bottomView.y, Color.black, Color.yellow, new BasicStroke( 4 ), font );
    }

    public void update() {
        shape = transform.toAffineTransform().createTransformedShape( volume.getVolume() );
        Point2D.Double topIntersection = volume.getTopIntersection();
        if( topIntersection != null ) {
            cg.setLocation( topIntersection );
        }
        Point2D.Double bottomIntersection = volume.getBottomIntersection();
        if( bottomIntersection != null ) {
            bottom.setLocation( bottomIntersection );
        }
        repainter.update();
        Rectangle bounds = shape.getBounds();
        textureRectangle.x = bounds.x;
//        textureRectangle.y=bounds.y;
        texturePaint = new TexturePaint( textureImage, textureRectangle );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
        cg.transformChanged( mvt );
        bottom.transformChanged( mvt );
    }
}
