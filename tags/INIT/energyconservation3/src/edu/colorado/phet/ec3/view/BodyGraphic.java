/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:21 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class BodyGraphic extends PNode {
    private Body body;
    private EC3Module ec3Module;
    private PNode shape;
    private ArrayList dragHistory = new ArrayList();
    private Point2D mouseLocation;
    long t0 = System.currentTimeMillis();
    private int numHistoryPointsForThrow = 5;

    public BodyGraphic( EC3Module ec3Module, final Body body ) {
        this.ec3Module = ec3Module;
        this.body = body;
        shape = new PPath( body.getShape() );
        shape.setPaint( Color.blue );
        addChild( shape );
        update();
        ec3Module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( BodyGraphic.this );
//                System.out.println( "delta = " + delta );
                body.translate( delta.getWidth(), delta.getHeight() );
            }
        } );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

//        PActivity recordActivity = new PActivity( 0, 30, -1 ){
//            protected void activityStep( long elapsedTime ) {
//                super.activityStep( elapsedTime );
//                addHistoryPoint();
//            }
//        };
        final Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                System.out.println( "BodyGraphic.actionPerformed" );
                addHistoryPoint();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
//                t0 = System.currentTimeMillis();
                mouseLocation = event.getPositionRelativeTo( BodyGraphic.this );
                dragHistory.clear();
                timer.start();
            }

            public void mouseDragged( PInputEvent event ) {
                mouseLocation = event.getPositionRelativeTo( BodyGraphic.this );
                addHistoryPoint();
            }

            public void mouseReleased( PInputEvent event ) {
                timer.stop();
                throwObject();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                body.setUserControlled( true );
                body.setVelocity( 0, 0 );
            }

            public void mouseReleased( PInputEvent event ) {
                body.setUserControlled( false );
            }
        } );
    }

    private double getTime() {
        return ( System.currentTimeMillis() - t0 ) / 1000.0;
    }

    private void throwObject() {
//        XYSeries xSeries = new XYSeries( "x" );
//        XYSeries ySeries = new XYSeries( "y" );
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries( xSeries );
//        dataset.addSeries( ySeries );
//
////        Regression.getOLSRegression()
//        double currentTime = getTime();
//        System.out.println( "currentTime = " + currentTime );
//        List history = dragHistory.subList( dragHistory.size() - numHistoryPointsForThrow, dragHistory.size() );
//
//        for( int i = 0; i < history.size(); i++ ) {
//            DataPoint dataPoint = (DataPoint)history.get( i );
//            if( ( currentTime - dataPoint.time ) < 0.1 ) {
//                xSeries.add( dataPoint.time, dataPoint.x );
//                ySeries.add( dataPoint.time, dataPoint.y );
//            }
//        }
//        System.out.println( "dragHistory = " + dragHistory );
//        System.out.println( "history = " + history );
//        System.out.println( "xSeries = " + toString( xSeries ) );
//        System.out.println( "ySeries = " + toString( ySeries ) );
//        double[] bmX = Regression.getOLSRegression( dataset, 0 );//y=a+bx
//        double[] bmY = Regression.getOLSRegression( dataset, 1 );
//        double vx = bmX[1];
//        double vy = bmY[1];
//        System.out.println( "vx=" + vx + ", vy = " + vy );
//        vx /= 50;
//        vy /= 50;
//        Vector2D.Double vecADouble = new Vector2D.Double( vx, vy );
//        double threshold = 0.27;
//        System.out.println( "vecADouble.getMagnitude() = " + vecADouble.getMagnitude() );
//        if( vecADouble.getMagnitude() > threshold ) {
//            body.setVelocity( vx, vy );
//        }
        dragHistory.clear();
    }

    private String toString( XYSeries xSeries ) {
        String s = "Series=" + xSeries.getKey() + ": numItems=" + xSeries.getItemCount() + " ";
        ArrayList pt = new ArrayList();
        for( int i = 0; i < xSeries.getItemCount(); i++ ) {
            pt.add( new Point2D.Double( xSeries.getDataItem( i ).getX().doubleValue(), xSeries.getDataItem( i ).getY().doubleValue() ) );
        }
        return s + pt;
    }

    private void addHistoryPoint() {
        DataPoint o = new DataPoint( getTime(), mouseLocation.getX(), mouseLocation.getY() );
//        System.out.println( "Adding history point: o=" + o );
        dragHistory.add( o );
    }

    private class DataPoint {
        double time;
        double x;
        double y;

        public DataPoint( double time, double x, double y ) {
            this.time = time;
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "time=" + time + ", point=" + new Point2D.Double( x, y );
        }
    }

    private void update() {
        setOffset( body.getX(), body.getY() );
    }
}
