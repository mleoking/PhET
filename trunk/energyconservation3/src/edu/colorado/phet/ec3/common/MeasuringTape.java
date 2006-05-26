/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.BoundGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 20, 2005
 * Time: 10:42:18 PM
 * Copyright (c) May 20, 2005 by Sam Reid
 */

public class MeasuringTape extends PNode {
    private double scale;
    private Point2D.Double viewSrc;
    private Point2D.Double viewDst;
    private BodyGraphic bodyGraphic;
    private TapeGraphic tapeGraphic;
    private EndGraphic endGraphic;
    private ReadoutGraphic readoutGraphic;
    private PNode worldNode;

    public MeasuringTape( double scale, Point2D.Double modelSrc, PNode worldNode ) {
        this.scale = scale;
        this.viewSrc = modelSrc;
        this.worldNode = worldNode;
        this.viewDst = new Point2D.Double( modelSrc.x + 100, modelSrc.y );

        bodyGraphic = new BodyGraphic();
        tapeGraphic = new TapeGraphic();
        endGraphic = new EndGraphic();
        readoutGraphic = new ReadoutGraphic( "m" );

        addChild( tapeGraphic );
        addChild( bodyGraphic );
        addChild( endGraphic );
        addChild( readoutGraphic );

        update();
        worldNode.addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        worldNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
    }

    class BodyGraphic extends PNode {
        private PImage imageGraphic;

        public BodyGraphic() {
            try {
                imageGraphic = new PImage( ImageLoader.loadBufferedImage( "images/tape.gif" ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            addChild( imageGraphic );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    Dimension2D dx = getDelta( event );
                    translateAll( dx.getWidth(), dx.getHeight() );
                }
            } );

            int crossHairLength = 10;
            CrossHairGraphic crossHairGraphic = new CrossHairGraphic( crossHairLength );
            addChild( crossHairGraphic );
            crossHairGraphic.setOffset( imageGraphic.getWidth() - crossHairLength / 2, imageGraphic.getHeight() - crossHairLength / 2 );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        }

        public PImage getImageGraphic() {
            return imageGraphic;
        }
    }

    private Dimension2D getDelta( PInputEvent event ) {
        Dimension2D dx = event.getDeltaRelativeTo( this );
        return dx;
    }

    private void translateAll( double dx, double dy ) {
        viewSrc.x += dx;
        viewSrc.y += dy;
        viewDst.x += dx;
        viewDst.y += dy;
        update();
    }

    private void update() {
        Vector2D.Double viewVector = new Vector2D.Double( viewSrc, viewDst );

//        System.out.println( "bodyGraphic.getTransform() = " + bodyGraphic.getTransform() );
        Line2D.Double line = new Line2D.Double( viewSrc, viewDst );
        tapeGraphic.setLine( line );

        bodyGraphic.setTransform( new AffineTransform() );
        Point2D bodyLoc = new Point2D.Double( viewSrc.getX() - bodyGraphic.getImageGraphic().getWidth(),
                                              viewSrc.getY() - bodyGraphic.getImageGraphic().getHeight() );
        bodyGraphic.translate( bodyLoc.getX(), bodyLoc.getY() );
        bodyGraphic.rotateAboutPoint( viewVector.getAngle(), bodyGraphic.getImageGraphic().getWidth(), bodyGraphic.getImageGraphic().getHeight() );
        endGraphic.setOffset( viewDst.getX() - endGraphic.getShapeGraphic().getWidth() / 2, viewDst.getY() - endGraphic.getShapeGraphic().getHeight() / 2 );


        Point2D src = new Point2D.Double( viewSrc.getX(), viewSrc.getY() );
        localToGlobal( src );
        worldNode.globalToLocal( src );

        Point2D dst = new Point2D.Double( viewDst.getX(), viewDst.getY() );
        localToGlobal( dst );
        worldNode.globalToLocal( dst );

        double modelDistance = src.distance( dst ) * scale;

//        double viewDistance = new Vector2D.Double( this.viewSrc, this.viewDst ).getMagnitude();
        //convert to model distance.
//        PDimension dim = new PDimension( viewDistance, 0 );

//        globalToLocal()

//        DecimalFormat decimalFormat = new DecimalFormat( "0.000" );
//        readoutGraphic.phetShadowTextGraphic.setText( "model coords=" + decimalFormat.format( dst.getX() ) + ", " + decimalFormat.format( dst.getY() ) );


        readoutGraphic.setDistance( modelDistance );
        readoutGraphic.setOffset( viewSrc.getX(), (int)( viewSrc.getY() + readoutGraphic.getHeight() * 1.2 + 7 ) );
    }

    class TapeGraphic extends PNode {
        public PPath phetShapeGraphic;

        public TapeGraphic() {
            phetShapeGraphic = new PPath( null );
            phetShapeGraphic.setStroke( new BasicStroke( 2 ) );
            phetShapeGraphic.setPaint( Color.black );
            addChild( phetShapeGraphic );

        }

        public void setLine( Line2D.Double line ) {
            phetShapeGraphic.setPathTo( line );
        }
    }

    class EndGraphic extends PNode {
        private PPath phetShapeGraphic;

        public EndGraphic() {
            Ellipse2D.Double shape = new Ellipse2D.Double( 0, 0, 15, 15 );

            phetShapeGraphic = new PPath( shape );
            phetShapeGraphic.setPaint( Color.black );
            addChild( phetShapeGraphic );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    Dimension2D dx = getDelta( event );
                    MeasuringTape.this.translateEndPoint( dx.getWidth(), dx.getHeight() );
                }
            } );
            addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

            int crossHairSize = 10;
            CrossHairGraphic crossHairGraphic = new CrossHairGraphic( crossHairSize );
            crossHairGraphic.setPaint( Color.yellow );
            addChild( crossHairGraphic );

            crossHairGraphic.setOffset( phetShapeGraphic.getWidth() / 2 - crossHairSize / 2, phetShapeGraphic.getHeight() / 2 - crossHairSize / 2 );
        }

        public PPath getShapeGraphic() {
            return phetShapeGraphic;
        }
    }

    private void translateEndPoint( double dx, double dy ) {
        viewDst.x += dx;
        viewDst.y += dy;
//        System.out.println( "modelDst = " + viewDst );
        update();
    }

    class ReadoutGraphic extends PNode {
        DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
        private String units;
        public PText phetShadowTextGraphic;

        public ReadoutGraphic( String units ) {
            this.units = units;
            phetShadowTextGraphic = new PText( "" );
            phetShadowTextGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            phetShadowTextGraphic.setTextPaint( Color.black );

            BoundGraphic boundGraphic = new BoundGraphic( phetShadowTextGraphic, 2, 2 );
            boundGraphic.setStroke( new BasicStroke() );
            boundGraphic.setStrokePaint( Color.black );
            boundGraphic.setPaint( Color.green );
            addChild( boundGraphic );
            addChild( phetShadowTextGraphic );
        }

        public void setDistance( double modelDistance ) {
            String text = decimalFormat.format( modelDistance ) + " " + units;
            phetShadowTextGraphic.setText( text );
        }
    }

    static class CrossHairGraphic extends PNode {
        private int length;

        public CrossHairGraphic( int length ) {
            this.length = length;

            PPath verticalStroke = new PPath( new Line2D.Double( length / 2, 0, length / 2, length ) );
            verticalStroke.setStroke( new BasicStroke( 2 ) );
            verticalStroke.setStrokePaint( Color.red );

            PPath horizStroke = new PPath( new Line2D.Double( 0, length / 2, length, length / 2 ) );
            horizStroke.setStroke( new BasicStroke( 2 ) );
            horizStroke.setStrokePaint( Color.red );

            addChild( verticalStroke );
            addChild( horizStroke );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
