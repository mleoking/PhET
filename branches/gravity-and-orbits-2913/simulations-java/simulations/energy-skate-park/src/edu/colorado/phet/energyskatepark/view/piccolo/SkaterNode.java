// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyskatepark.SkaterCharacter;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.TraversalState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:21 AM
 */

public class SkaterNode extends PNode {
    private Body body;
    private double heightDivisor = 1.0;
    private PImage skaterImageNode;

    private PPath centerDebugger;
    protected boolean debugCenter = true;

    private PNode jetPackNode;
    private BufferedImage jetPackImage;
    private BufferedImage skaterImage;
    private Body.ListenerAdapter bodyListener = new Body.ListenerAdapter() {

        public void thrustChanged() {
            update();
        }

        public void dimensionChanged() {
            update();
        }

        public void positionAngleChanged() {
            update();
        }

        public void skaterCharacterChanged() {
            setSkaterCharacter( body.getSkaterCharacter() );
            update();
        }
    };

    public SkaterNode( final Body body ) {
        this.body = body;
        if( body == null ) {
            throw new IllegalArgumentException( "Body cannot be null in " + getClass().getName() );
        }

        try {
            jetPackImage = ImageLoader.loadBufferedImage( "energy-skate-park/images/rocket5.png" );
            jetPackNode = new PhetPNode( new PImage( jetPackImage ) );
            addChild( jetPackNode );

            skaterImage = ImageLoader.loadBufferedImage( "energy-skate-park/images/skater3.png" );
            skaterImageNode = new PImage( skaterImage );
            addChild( skaterImageNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        centerDebugger = new PhetPPath( Color.red );
        if( debugCenter ) {
            addChild( centerDebugger );
        }

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( SkaterNode.this );
                boolean okToTranslate = true;
                getBody().translate( delta.getWidth(), delta.getHeight() );
                double y = getBody().getCenterOfMass().getY();
                if( y <= 0 ) {
                    okToTranslate = false;
                }
                getBody().translate( -delta.getWidth(), -delta.getHeight() );
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

        getBody().addListener( bodyListener );
        update();
    }

    private void updateDragAngle() {
        TraversalState state = getBody().getTrackMatch( 0, -2 );
        if( state != null ) {
            ImmutableVector2D vector = state.getParametricFunction2D().getUnitNormalVector( state.getAlpha() );//todo: this code is highly similar to code in Particle.updateStateFrom1D
            double sign = state.isTop() ? 1.0 : -1.0;
            ImmutableVector2D vect = vector.getInstanceOfMagnitude( sign );
            getBody().setAngle( vect.getAngle() - Math.PI / 2 );
        }
    }

    public Body getBody() {
        return body;
    }

    public void setBody( Body body ) {
        setBodyNoUpdate( body );
        update();
    }

    protected void setBodyNoUpdate( Body body ) {
        this.body.removeListener( bodyListener );
        this.body = body;
        this.body.addListener( bodyListener );
    }

    private void update() {
        updateSkaterTransform();

        jetPackNode.setVisible( body.getThrust().getMagnitude() > 0 );
        updateJetPackTransform();

        double ellipseWidth = 0.1 * 0.85;
        Ellipse2D.Double aShape = new Ellipse2D.Double( body.getCenterOfMass().getX() - ellipseWidth / 2, body.getCenterOfMass().getY() - ellipseWidth / 2, ellipseWidth, ellipseWidth );
        centerDebugger.setPathTo( aShape );
    }

    public Rectangle2D getRedDotGlobalFullBounds() {
        return centerDebugger.getGlobalFullBounds();
    }

    private void updateJetPackTransform() {
        jetPackNode.setTransform( new AffineTransform() );
        jetPackNode.setOffset( skaterImageNode.getFullBounds().getCenter2D() );
        jetPackNode.transformBy( AffineTransform.getScaleInstance( 2 * body.getWidth() / skaterImage.getWidth(), -2 * body.getHeight() / skaterImage.getHeight() ) );
        jetPackNode.translate( -jetPackImage.getWidth() / 2, -jetPackImage.getHeight() / 2 );
        jetPackNode.rotateAboutPoint( -body.getThrust().getAngle() + Math.PI / 2, jetPackImage.getWidth() / 2, jetPackImage.getHeight() / 2 );
        if( body.isFacingRight() ) {
            jetPackNode.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            jetPackNode.translate( -jetPackImage.getWidth(), 0 );
        }
    }

    private void updateSkaterTransform() {
        skaterImageNode.setTransform( new AffineTransform() );
        skaterImageNode.setOffset( body.getX(), body.getY() );
        skaterImageNode.transformBy( AffineTransform.getScaleInstance( body.getWidth() / skaterImage.getWidth(), -body.getHeight() / skaterImage.getHeight() ) );
        skaterImageNode.rotate( -body.getAngle() );
        skaterImageNode.translate( -skaterImage.getWidth() / 2, -skaterImage.getHeight() / heightDivisor );
        if( body.isFacingRight() ) {
            skaterImageNode.transformBy( AffineTransform.getScaleInstance( -1, 1 ) );
            skaterImageNode.translate( -skaterImage.getWidth(), 0 );
        }
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        skaterImage = skaterCharacter.getImage();
        skaterImageNode.setImage( skaterImage );
        heightDivisor = skaterCharacter.getHeightDivisor();
        update();
    }

    public void delete() {
        body.removeListener( bodyListener );
    }
}
