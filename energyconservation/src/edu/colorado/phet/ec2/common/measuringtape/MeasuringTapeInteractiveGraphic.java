/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.measuringtape;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.controllers.DefaultControlGraphic;
import edu.colorado.phet.coreadditions.controllers.MouseHandler;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.graphics2.*;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 11:44:33 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class MeasuringTapeInteractiveGraphic extends CompositeGraphic {
//    public static boolean visible = true;
    public static boolean visible = false;

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    Stroke stroke;
    Color color;
    private MeasuringTape tape;
    private ModelViewTransform2d transform;
    private Component repaint;

    FilledShapeGraphic segment;
    MouseHandler segmentController;

    FilledShapeGraphic tab;
    MouseHandler tabController;

    BufferedImageGraphic imageGraphic;
    MouseHandler imageController;

//    TextGraphic textGraphic;
    TextGraphicOnBackground textGraphic;
    DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );


    public MeasuringTapeInteractiveGraphic( Stroke stroke, Color color, final MeasuringTape tape, ModelViewTransform2d transform, Component repaint ) {
        this.stroke = stroke;
        this.color = color;
        this.tape = tape;
        this.transform = transform;
        this.repaint = repaint;
        segment = new FilledShapeGraphic( color );
        segmentController = new TranslationController( segment, new Translatable() {
            public void translate( double dx, double dy ) {
                tape.translate( dx, dy );
            }
        }, transform );
        DefaultControlGraphic segmentGraphic = new DefaultControlGraphic( segment, segmentController );
        addGraphic( segmentGraphic, 0 );

        tab = new FilledShapeGraphic( color );
        tabController = new TranslationController( tab, new Translatable() {
            public void translate( double dx, double dy ) {
                tape.translateDestination( dx, dy );
            }
        }, transform );
        DefaultControlGraphic tabGraphic = new DefaultControlGraphic( tab, tabController );
        addGraphic( tabGraphic, 1 );

        imageGraphic = new BufferedImageGraphic( new ImageLoader().loadBufferedImage( "images/phet-tape.gif" ) );
        imageController = new TranslationController( imageGraphic, new Translatable() {
            public void translate( double dx, double dy ) {
                tape.translate( dx, dy );
            }
        }, transform );
        DefaultControlGraphic imageGr = new DefaultControlGraphic( imageGraphic, imageController );
        addGraphic( imageGr, 0 );

        textGraphic = new TextGraphicOnBackground( new Font( "Lucida Sans", 0, 24 ), Color.blue, new Color( 0, 200, 14 ), 4, 4 );
        addGraphic( textGraphic, -1 );

        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                stateChanged();
            }
        } );
        tape.addObserver( new SimpleObserver() {
            public void update() {
                stateChanged();
            }
        } );
        stateChanged();
    }

    public void paint( Graphics2D g2 ) {
        if( visible ) {
            super.paint( g2 );
        }
    }

    private void stateChanged() {
        Point src = transform.modelToView( tape.getStartX(), tape.getStartY() );
        Point dst = transform.modelToView( tape.getEndX(), tape.getEndY() );
        segment.setShape( stroke.createStrokedShape( new Line2D.Float( src.x, src.y, dst.x, dst.y ) ) );
        tab.setShape( new Rectangle( dst.x, dst.y, 10, 10 ) );

        AffineTransform imageTrf = new AffineTransform();
        imageTrf.preConcatenate( AffineTransform.getTranslateInstance( src.x - imageGraphic.getImageWidth(), src.y - imageGraphic.getImageHeight() ) );
        imageGraphic.setTransform( imageTrf );
        double dist = tape.getMagnitude();
        String text = decimalFormat.format( dist );
        textGraphic.setText( text + " meters" );
        int height = textGraphic.getBoundsHeight();
        textGraphic.setPosition( src.x, src.y + height );

        repaint.repaint();
    }
}
