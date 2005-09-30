/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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
    private PPath shape;
    private ArrayList dragHistory = new ArrayList();
    private Point2D mouseLocation;
    long t0 = System.currentTimeMillis();
    private int numHistoryPointsForThrow = 5;
    private PImage skater;
    private PPath centerDebugger;
    private PImage flameGraphic;
    private final BufferedImage[] flames = new BufferedImage[3];
    private int flameFrame = 0;

    public BodyGraphic( EC3Module ec3Module, final Body body ) {
        this.ec3Module = ec3Module;
        this.body = body;

        shape = new PPath( body.getShape() );
        shape.setPaint( Color.blue );
//        addChild( shape );

        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater-67.png" );
//            BufferedImage image = ImageLoader.loadBufferedImage( "images/ferrari-side2.gif" );
//            BufferedImage image = ImageLoader.loadBufferedImage( "images/motorcycle.gif" );
            skater = new PImage( image );
            addChild( skater );

            centerDebugger = new PPath();
            centerDebugger.setPaint( Color.red );
//            addChild( centerDebugger );
//            shape.addChild( new PImage( image ) );
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

            public void mouseDragged( PInputEvent event ) {
                body.setUserControlled( true );
                body.setVelocity( 0, 0 );
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
//        setOffset( body.getX(), body.getY() );

        shape.setPathTo( body.getLocatedShape() );
        Point2D center = shape.getFullBounds().getCenter2D();
        skater.setTransform( new AffineTransform() );
//        skater.translate( body.getPosition().getX(), body.getPosition().getY() );
//        AbstractVector2D a = Vector2D.Double.parseAngleAndMagnitude( skater.getImage().getHeight( null ), body.getAngle() );
//        skater.translate( 0, -a.getY() );

        skater.translate( center.getX(), center.getY() );
        skater.rotate( body.getAngle() );
        skater.translate( -skater.getImage().getWidth( null ) / 2, -skater.getImage().getHeight( null ) + body.getHeight() / 2 );
        boolean facingRight = body.isFacingRight();
        if( facingRight ) {
            skater.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            skater.translate( -skater.getImage().getWidth( null ), 0 );
        }

        centerDebugger.setPathTo( new Rectangle( (int)body.getPosition().getX(), (int)body.getPosition().getY(), 5, 5 ) );
        if( body.getThrust().getMagnitude() != 0 ) {
            setFlamesVisible( true );
            updateFlames();
        }
        else {
            setFlamesVisible( false );
        }
//        System.out.println( "centerDebugger.getFullBounds() = " + centerDebugger.getFullBounds() );
    }

    private void updateFlames() {
//        Point2D center2D = centerDebugger.getFullBounds().getCenter2D();
//        Point2D pt = new Point2D.Double( body.getShape().getBounds2D().getWidth() / 2, body.getShape().getBounds2D().getHeight() );
//        Point2D tx = skater.getTransform().transform( pt, null );
//        Point2D loc = new Point2D.Double( center2D.getX() - flameGraphic.getWidth() / 2, center2D.getY() - flameGraphic.getHeight() / 2 + skater.getHeight() / 2 );
        flameGraphic.setTransform( new AffineTransform() );
//        flameGraphic.scale( 0.5 );
//        Point2D center = shape.getFullBounds().getCenter2D();
//        flameGraphic.translate( center.getX(), center.getY() );
//        flameGraphic.rotate( body.getAngle() );
//        flameGraphic.translate( -skater.getImage().getWidth( null ) / 2, -skater.getImage().getHeight( null ) + body.getHeight() / 2 );
//        flameGraphic.setOffset( tx.getX() - flameGraphic.getWidth() / 2 - sign( body.getThrust().getX() ) * flameGraphic.getWidth(), tx.getY() - flameGraphic.getHeight() / 2 );
//        flameGraphic.setOffset( tx);//.getX() - flameGraphic.getWidth() / 2 - sign( body.getThrust().getX() ) * flameGraphic.getWidth(), tx.getY() - flameGraphic.getHeight() / 2 );

        if( Math.random() < 0.4 ) {
            flameFrame = ( flameFrame + 1 ) % 3;
            flameGraphic.setImage( flames[flameFrame] );
        }

//        flameGraphic.setRotation( -skater.getRotation() );
        flameGraphic.rotateInPlace( Math.PI );
        flameGraphic.translate( 0, -skater.getHeight() + 3 );
    }

    private double sign( double x ) {
        if( x >= 0 ) {
            return 1;
        }
        else {
            return -1;
        }
    }

    private void setFlamesVisible( boolean b ) {
        if( b ) {

            if( flameGraphic == null ) {
                try {
                    flames[0] = ImageLoader.loadBufferedImage( "images/myflames/flames1.gif" );
                    flames[1] = ImageLoader.loadBufferedImage( "images/myflames/flames2.gif" );
                    flames[2] = ImageLoader.loadBufferedImage( "images/myflames/flames3.gif" );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                flameGraphic = new PImage( flames[0] );

            }
            if( !skater.getChildrenReference().contains( flameGraphic ) ) {
                skater.addChild( 0, flameGraphic );
            }
        }
        else {
            if( flameGraphic != null && skater.getChildrenReference().contains( flameGraphic ) ) {
                skater.removeChild( flameGraphic );
            }
        }
    }

    public boolean isBoxVisible() {
        return getChildrenReference().contains( shape );
    }

    public void setBoxVisible( boolean v ) {
        if( v && !isBoxVisible() ) {
            addChild( shape );
        }
        else if( !v && isBoxVisible() ) {
            removeChild( shape );
        }
    }
}
