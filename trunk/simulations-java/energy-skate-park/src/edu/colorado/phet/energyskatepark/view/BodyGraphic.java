/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.test.phys1d.ParticleImageNode;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
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
import java.awt.geom.Area;
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
    private EnergySkateParkModule energySkateParkModule;
    private PPath boundsDebugPPath;
    private PImage pImage;

    private PPath centerDebugger;
    protected boolean debugCenter = false;

    private PPath feetDebugger;
    private boolean debugFeet = false;

//    private ParticleImageNode particleImageNode;

    public BodyGraphic( final EnergySkateParkModule ec3Module, final Body body ) {
        this.energySkateParkModule = ec3Module;
        this.body = body;
        boundsDebugPPath = new PPath( new Rectangle2D.Double( 0, 0, body.getWidth(), body.getHeight() ) );
        boundsDebugPPath.setStroke( null );
        boundsDebugPPath.setPaint( new Color( 0, 0, 255, 128 ) );
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater3.png" );
            pImage = new PImage( image );
            addChild( pImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        feetDebugger = new PhetPPath( Color.green );
        if( debugFeet ) {
            addChild( feetDebugger );
        }

        centerDebugger = new PhetPPath( Color.red );
        if( debugCenter ) {
            addChild( centerDebugger );
        }

        ec3Module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
        body.addListener( new Body.ListenerAdapter() {
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
                okToTranslate=true;
                if( okToTranslate ) {
                    getBody().translate( delta.getWidth(), delta.getHeight() );
                    updateDragAngle();
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
//        particleImageNode=new ParticleImageNode( body.getParticle());
        update();
    }

    private void updateDragAngle() {
        AbstractSpline spline = getGrabSpline( body );
        if( spline != null ) {
            Point2D center = body.getCenterOfMass();
            double alongSpline = spline.getDistAlongSpline( center, 0, spline.getLength(), 100 );
            Point2D splineLocation = spline.evaluateAnalytical( alongSpline );
            Vector2D vec = new Vector2D.Double( splineLocation, center );
            double offsetAngle = 0.0;
            AbstractVector2D unitNormal = spline.getUnitNormalVector( alongSpline );
            if( vec.dot( unitNormal ) > 0 ) {
                offsetAngle = Math.PI / 2;
            }
            else {
                offsetAngle = -Math.PI / 2;
            }
//            body.setCMRotation( unitNormal.getAngle() + offsetAngle );
        }
    }

    private AbstractSpline getGrabSpline( Body body ) {
        double bestScore = Double.POSITIVE_INFINITY;
        AbstractSpline bestSpline = null;
        ArrayList allSplines = getEnergySkateParkModel().getAllSplines();
        for( int i = 0; i < allSplines.size(); i++ ) {
            double score = getGrabScore( (EnergySkateParkSpline)allSplines.get( i ), body );
            if( score < bestScore ) {
                bestScore = score;
                bestSpline = (AbstractSpline)allSplines.get( i );
            }
        }
        return bestSpline;
    }

    private EnergySkateParkModel getEnergySkateParkModel() {
        return energySkateParkModule.getEnergySkateParkModel();
    }

    private double getGrabScore( EnergySkateParkSpline spline, Body body ) {
        Area feet = new Area( body.getFeetShape() );
        feet.add( new Area( AffineTransform.getTranslateInstance( 0, body.getFeetShape().getBounds2D().getHeight() ).createTransformedShape( body.getFeetShape() ) ) );
        feet.add( new Area( AffineTransform.getTranslateInstance( 0, body.getFeetShape().getBounds2D().getHeight() * 2 ).createTransformedShape( body.getFeetShape() ) ) );
//        Area splineArea = new Area( spline.getArea() );
        Area splineArea = new Area( );//todo: add area for spline? This may be handled by Particle physics implementation
        splineArea.intersect( feet );
        boolean collide = !splineArea.isEmpty();
        return collide ? 0 : Double.POSITIVE_INFINITY;
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
//        centerDebugger.setPathTo( new Rectangle2D.Double( body.getAttachPoint().getX(), body.getAttachPoint().getY(), 0.1, 0.1 ) );
        centerDebugger.setPathTo( new Rectangle2D.Double( body.getCenterOfMass().getX(), body.getCenterOfMass().getY(), 0.1, 0.1 ) );
        feetDebugger.setPathTo( body.getFeetShape() );
        
//        particleImageNode.update();
//        System.out.println( "particleImageNode.getGlobalFullBounds() = " + particleImageNode.getGlobalFullBounds() );
//        invalidateFullBounds();
    }

    public static AffineTransform createTransform( Body body, AffineTransform bodyTransform, double objWidth, double objHeight, int imageWidth, int imageHeight ) {
        AffineTransform t = new AffineTransform();
        t.concatenate( bodyTransform );
//        t.translate( -objWidth / 2, 0 );
        t.translate( -objWidth / 2, -objHeight);
        t.translate( 0, -AbstractSpline.SPLINE_THICKNESS / 2.0 );
        t.scale( objWidth / imageWidth, objHeight / imageHeight );

        if( !body.isFacingRight() ) {
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
