/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.colorado.phet.piccolo.PhetPNode;
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

public class SkaterNode extends PNode {
    private Body body;
    private EnergySkateParkModule energySkateParkModule;
    private PPath boundsDebugPPath;
    private PImage skaterImageNode;

    private PPath centerDebugger;
    protected boolean debugCenter = true;

    private PPath feetDebugger;
    private boolean debugFeet = false;
    private PNode jetPackNode;
    private BufferedImage jetPackImage;
    private BufferedImage skaterImage;

    public SkaterNode( final EnergySkateParkModule ec3Module, final Body body ) {
        this.energySkateParkModule = ec3Module;
        this.body = body;
        boundsDebugPPath = new PPath( new Rectangle2D.Double( 0, 0, body.getWidth(), body.getHeight() ) );
        boundsDebugPPath.setStroke( null );
        boundsDebugPPath.setPaint( new Color( 0, 0, 255, 128 ) );

        try {
            jetPackImage = ImageLoader.loadBufferedImage( "images/rocket5.png" );
            jetPackNode = new PhetPNode( new PImage( jetPackImage ) );
            addChild( jetPackNode );

            skaterImage = ImageLoader.loadBufferedImage( "images/skater3.png" );
            skaterImageNode = new PImage( skaterImage );
            addChild( skaterImageNode );
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
                PDimension delta = event.getDeltaRelativeTo( SkaterNode.this );
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
                okToTranslate = true;
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
        Area splineArea = new Area();//todo: add area for spline? This may be handled by Particle physics implementation
        splineArea.intersect( feet );
        boolean collide = !splineArea.isEmpty();
        return collide ? 0 : Double.POSITIVE_INFINITY;
    }

    protected void setImage( Image image ) {
        skaterImageNode.setImage( image );
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

    public void update() {
        boundsDebugPPath.setPathTo( body.getShape() );
        updateSkaterTransform();

        jetPackNode.setVisible( body.getThrust().getMagnitude() > 0 );
        updateJetPackTransform();
        centerDebugger.setPathTo( new Rectangle2D.Double( body.getCenterOfMass().getX(), body.getCenterOfMass().getY(), 0.1, 0.1 ) );
        feetDebugger.setPathTo( body.getFeetShape() );
    }

    private void updateJetPackTransform() {
        jetPackNode.setTransform( new AffineTransform() );
        jetPackNode.setOffset( skaterImageNode.getFullBounds().getCenter2D() );
        jetPackNode.transformBy( AffineTransform.getScaleInstance( 2 * body.getWidth() / skaterImage.getWidth(), -2 * body.getHeight() / skaterImage.getHeight() ) );
        jetPackNode.translate( -jetPackImage.getWidth() / 2, -jetPackImage.getHeight() / 2 );
        jetPackNode.rotateAboutPoint( -body.getThrust().getAngle() + Math.PI / 2, jetPackImage.getWidth() / 2, jetPackImage.getHeight() / 2 );
        if( body.isFacingRight() ) {
            jetPackNode.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            jetPackNode.translate( -jetPackImage.getWidth(),0);
        }
    }

    private void updateSkaterTransform() {
        skaterImageNode.setTransform( new AffineTransform() );
        skaterImageNode.setOffset( body.getX(), body.getY() );
        skaterImageNode.transformBy( AffineTransform.getScaleInstance( body.getWidth() / skaterImage.getWidth(), -body.getHeight() / skaterImage.getHeight() ) );
        skaterImageNode.rotate( -body.getRotation() );
        skaterImageNode.translate( -skaterImage.getWidth() / 2, -skaterImage.getHeight() );
        if( body.isFacingRight() ) {
            skaterImageNode.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            skaterImageNode.translate( -skaterImage.getWidth(),0);
        }
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
