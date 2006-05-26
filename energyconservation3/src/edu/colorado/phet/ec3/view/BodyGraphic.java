/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    private PPath boundsDebugPPath;
    private ArrayList dragHistory = new ArrayList();
    private Point2D mouseLocation;
    private long t0 = System.currentTimeMillis();
    private int numHistoryPointsForThrow = 5;
    private PImage skater;
    private PPath centerDebugger;
    private JetPackGraphic jetPackGraphic;
//    private boolean debugCenter = false;
    private boolean debugCenter = true;

    public BodyGraphic( final EC3Module ec3Module, Body body ) {
        this.ec3Module = ec3Module;
        this.body = body;
        boundsDebugPPath = new PPath( body.getShape() );
        boundsDebugPPath.setStroke( null );
//        boundsDebugPPath.setPaint( Color.blue );
        boundsDebugPPath.setPaint( new Color( 0, 0, 255, 128 ) );
        jetPackGraphic = new JetPackGraphic( this );

        try {
//            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater-67.png" );
//            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater-phet2_0032.gif" );
//            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater-phet2_0032-b.gif" );
            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater3.png" );
            skater = new PImage( image );
            addChild( skater );

            centerDebugger = new PPath();
            centerDebugger.setStroke( null );
            centerDebugger.setPaint( Color.red );
            if( debugCenter ) {
                addChild( centerDebugger );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

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
                boolean okToTranslate = true;
                if( getBody().getLocatedShape().getBounds2D().getMinY() < 0 && delta.getHeight() < 0 ) {
                    okToTranslate = false;
                }
//                if( getFullBounds().getMinX() < 0 && delta.getWidth() < 0 ) {
//                    okToTranslate = false;
//                }
                PBounds b = getFullBounds();
                localToGlobal( b );
                ec3Module.getEnergyConservationCanvas().getLayer().globalToLocal( b );
//                System.out.println( "b = " + b );
//                System.out.println( "getFullBounds().getMaxX() = " + getFullBounds().getMaxX() +", canWidth="+ec3Module.getEnergyConservationCanvas().getWidth());
                if( b.getMaxX() > ec3Module.getEnergyConservationCanvas().getWidth() && delta.getWidth() > 0 ) {
                    okToTranslate = false;
                }
                if( b.getMinX() < 0 && delta.getWidth() < 0 ) {
                    okToTranslate = false;
                }
                if( okToTranslate ) {
                    getBody().translate( delta.getWidth(), delta.getHeight() );
                }

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
                getBody().setUserControlled( true );
                getBody().setVelocity( 0, 0 );
            }

            public void mouseReleased( PInputEvent event ) {
                getBody().setUserControlled( false );
            }

            public void mouseDragged( PInputEvent event ) {
                getBody().setUserControlled( true );
                getBody().setVelocity( 0, 0 );
            }
        } );
        addChild( jetPackGraphic );
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

//    private String toString( XYSeries xSeries ) {
//        String s = "Series=" + xSeries.getKey() + ": numItems=" + xSeries.getItemCount() + " ";
//        ArrayList pt = new ArrayList();
//        for( int i = 0; i < xSeries.getItemCount(); i++ ) {
//            pt.add( new Point2D.Double( xSeries.getDataItem( i ).getX().doubleValue(), xSeries.getDataItem( i ).getY().doubleValue() ) );
//        }
//        return s + pt;
//    }

    private void addHistoryPoint() {
        DataPoint o = new DataPoint( getTime(), mouseLocation.getX(), mouseLocation.getY() );
//        System.out.println( "Adding history point: o=" + o );
        dragHistory.add( o );
    }

    public Body getBody() {
        return body;
    }

    public void setBody( Body body ) {
        this.body = body;
        update();
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

    public PImage getSkater() {
        return skater;
    }

    public void update() {
        boundsDebugPPath.setPathTo( body.getLocatedShape() );

        skater.setTransform( new AffineTransform() );
        double dw = body.getShape().getBounds2D().getWidth() / skater.getImage().getWidth( null );
        double dh = body.getShape().getBounds2D().getHeight() / skater.getImage().getHeight( null );

        skater.transformBy( body.getTransform() );
        skater.translate( 0, -AbstractSpline.SPLINE_THICKNESS / 2.0 );
        skater.transformBy( AffineTransform.getScaleInstance( dw, dh ) );

        boolean facingRight = body.isFacingRight();
        if( facingRight ) {
            skater.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            skater.translate( -skater.getImage().getWidth( null ), 0 );
        }

        centerDebugger.setPathTo( new Rectangle2D.Double( body.getAttachPoint().getX(), body.getAttachPoint().getY(), 0.1, 0.1 ) );
        if( body.getThrust().getMagnitude() != 0 ) {
            setFlamesVisible( true );
        }
        else {
            setFlamesVisible( false );
        }
        updateFlames();
    }

    private void updateFlames() {
        jetPackGraphic.update();
    }

    private void setFlamesVisible( boolean flamesVisible ) {
        jetPackGraphic.setVisible( flamesVisible );
    }

    public boolean isBoxVisible() {
        return getChildrenReference().contains( boundsDebugPPath );
    }

    public void setBoxVisible( boolean v ) {
        if( v && !isBoxVisible() ) {
            addChild( boundsDebugPPath );
        }
        else if( !v && isBoxVisible() ) {
            removeChild( boundsDebugPPath );
        }
    }
}
