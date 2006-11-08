/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:21 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class BodyGraphic extends PNode {
    private Body body;
    private EnergySkateParkModule ec3Module;
    private PPath boundsDebugPPath;
    private PImage pImage;

    private PPath centerDebugger;
    protected boolean debugCenter = false;

    private PPath feetDebugger;
    private boolean debugFeet = false;

    public BodyGraphic( final EnergySkateParkModule ec3Module, Body body ) {
        this.ec3Module = ec3Module;
        this.body = body;
        boundsDebugPPath = new PPath( new Rectangle2D.Double( 0, 0, body.getWidth(), body.getHeight() ) );
        boundsDebugPPath.setStroke( null );
        boundsDebugPPath.setPaint( new Color( 0, 0, 255, 128 ) );
//        jetPackGraphic = new JetPackGraphic( this );
//        addChild( jetPackGraphic );
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater3.png" );
            pImage = new PImage( image );
            addChild( pImage );

            centerDebugger = new PPath();
            centerDebugger.setStroke( null );
            centerDebugger.setPaint( Color.red );
            if( debugCenter ) {
                addChild( centerDebugger );
            }

            feetDebugger = new PhetPPath( Color.green );
            if( debugFeet ) {
                addChild( feetDebugger );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        ec3Module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
        body.addListener( new Body.Listener() {
            public void thrustChanged() {
            }

            public void doRepaint() {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( BodyGraphic.this );
                boolean okToTranslate = true;
                if( getBody().getShape().getBounds2D().getMinY() < 0 && delta.getHeight() < 0 ) {
                    okToTranslate = false;
                }
                PBounds b = getFullBounds();
                localToGlobal( b );
                ec3Module.getEnergyConservationCanvas().getLayer().globalToLocal( b );
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

        update();
    }

    protected void setImage( Image image ) {
        pImage.setImage( image );
        update();
    }

    public Body getBody() {
        return body;
    }

    public void setBody( Body body ) {
        setBodyNoUpdate( body );
        update();
    }

    protected void setBodyNoUpdate( Body body ) {
        this.body = body;
    }

    public PImage getpImage() {
        return pImage;
    }

    public void update() {
        boundsDebugPPath.setPathTo( body.getShape() );

        pImage.setTransform( createSkaterTransform() );
        centerDebugger.setPathTo( new Rectangle2D.Double( body.getAttachPoint().getX(), body.getAttachPoint().getY(), 0.1, 0.1 ) );
        feetDebugger.setPathTo( body.getFeetShape() );
//        invalidateFullBounds();
    }

    public static AffineTransform createTransform( Body body, AffineTransform bodyTransform, double objWidth, double objHeight, int imageWidth, int imageHeight ) {
        AffineTransform t = new AffineTransform();
        t.concatenate( bodyTransform );
        t.translate( -objWidth / 2, 0 );
        t.translate( 0, -AbstractSpline.SPLINE_THICKNESS / 2.0 );
        t.scale( objWidth / imageWidth, objHeight / imageHeight );

        if( body.isFacingRight() ) {
            t.concatenate( AffineTransform.getScaleInstance( -1, 1 ) );
            t.translate( -imageWidth * 3 / 2.0, 0 );
        }
        else {
//            t.concatenate( AffineTransform.getScaleInstance( -1, 1 ) );
            t.translate( imageWidth / 2, 0 );
        }
        return t;
    }

    public boolean isFacingRight() {
        return body.isFacingRight();
    }

    public AffineTransform createSkaterTransform() {
        return createTransform( body, body.getTransform(), getBodyModelWidth(),
                                getBodyModelHeight(),
                                pImage.getImage().getWidth( null ),
                                pImage.getImage().getHeight( null ) );
    }

    public double getBodyModelHeight() {
        return body.getHeight();
    }

    public double getBodyModelWidth() {
        return body.getWidth();
    }

//    private void updateFlames() {
//        jetPackGraphic.update();
//    }
//
//    private void setFlamesVisible( boolean flamesVisible ) {
//        jetPackGraphic.setVisible( flamesVisible );
//    }

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
