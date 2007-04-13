package edu.colorado.phet.bernoulli.meter;

import edu.colorado.phet.common.bernoulli.view.graphics.InteractiveGraphic;
import edu.colorado.phet.coreadditions.bernoulli.graphics.DifferentialDragHandler;
import edu.colorado.phet.coreadditions.bernoulli.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.bernoulli.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.bernoulli.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 10:25:20 AM
 * To change this template use Options | File Templates.
 */
public class BarometerView implements InteractiveGraphic, SimpleObserver {
    Barometer barometer;
    Point pt;
    int width;
    int height;
    private ModelViewTransform2d transform;
    boolean init;
    String messageString;

    Color textColor = Color.black;
    private MeasuringGraphicNew measuringGraphic = new MeasuringGraphicNew();
    private ArrayList data = new ArrayList();
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.0#" );
//    private MeasuringGraphic measuringGraphic = new MeasuringGraphic();

    public BarometerView( Barometer barometer, int width, int height, ModelViewTransform2d transform ) {
        this.barometer = barometer;
        this.width = width;
        this.height = height;
        this.transform = transform;
        update();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                update();
            }
        } );
        barometer.addObserver( this );

        data.clear();
        data.add( new MeasuringGraphicNew.Datum( "pressure", "???" ) );
        data.add( new MeasuringGraphicNew.Datum( "height", new Double( 0.0 ) ) );
        barometer.updateObservers();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return measuringGraphic.contains( event.getPoint() );
    }

    DifferentialDragHandler dragHandler = null;

    public void mousePressed( MouseEvent event ) {
        dragHandler = new DifferentialDragHandler( event.getPoint() );
    }

    public void mouseDragged( MouseEvent event ) {
        Point dr = dragHandler.getDifferentialLocationAndReset( event.getPoint() );
        Point2D.Double modelDR = transform.viewToModelDifferential( dr );
        barometer.translate( modelDR.x, modelDR.y );
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public void update() {
        this.pt = transform.modelToView( barometer.getLocation() );
        this.messageString = "Pressure = " + barometer.getPressure();
        init = true;
    }

    public void paint( Graphics2D g ) {
        if( init ) {
            data.clear();
//            data.add(new MeasuringGraphicNew.Datum("pressure", decimalFormat.format(barometer.getX())));
            data.add( new MeasuringGraphicNew.Datum( "water pressure", decimalFormat.format( barometer.getPressure() ) + " Pa" ) );
            data.add( new MeasuringGraphicNew.Datum( "height", decimalFormat.format( barometer.getY() ) + " m" ) );
            measuringGraphic.paint( g, pt, data );
        }
    }

}
