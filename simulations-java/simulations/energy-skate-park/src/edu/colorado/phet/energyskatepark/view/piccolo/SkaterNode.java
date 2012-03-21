// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Cursor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.LinearFloorSpline2D;
import edu.colorado.phet.energyskatepark.model.TraversalState;
import edu.colorado.phet.energyskatepark.view.SkaterCharacter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.skaterX;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.ParameterKeys.skaterY;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.SharedComponents.skater;

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
    protected final boolean debugCenter = true;

    private PNode jetPackNode;
    private BufferedImage jetPackImage;
    private BufferedImage skaterImage;
    private final Body.ListenerAdapter bodyListener = new Body.ListenerAdapter() {

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
        if ( body == null ) {
            throw new IllegalArgumentException( "Body cannot be null in " + getClass().getName() );
        }

        jetPackImage = EnergySkateParkResources.getImage( "rocket5.png" );
        jetPackNode = new PhetPNode( new PImage( jetPackImage ) );
        addChild( jetPackNode );

        skaterImage = EnergySkateParkResources.getImage( "skater3.png" );
        skaterImageNode = new PImage( skaterImage );
        addChild( skaterImageNode );

        centerDebugger = new PhetPPath( PhetColorScheme.RED_COLORBLIND );
        if ( debugCenter ) {
            addChild( centerDebugger );
        }

        addInputEventListener( new SimSharingDragHandler( skater, UserComponentTypes.sprite ) {

            public Point2D pressPoint;
            public SerializablePoint2D bodyPosition;

            @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( skaterX, body.getX() ).with( skaterY, body.getY() );
            }

            @Override protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                pressPoint = event.getPositionRelativeTo( SkaterNode.this );
                bodyPosition = getBody().getPosition();

                getBody().setUserControlled( true );
                getBody().setVelocity( 0, 0 );
            }

            @Override protected void drag( PInputEvent event ) {
                super.drag( event );
                Point2D dragPoint = event.getPositionRelativeTo( SkaterNode.this );
                Point2D delta = new Point2D.Double( dragPoint.getX() - pressPoint.getX(), dragPoint.getY() - pressPoint.getY() );

                //Don't allow to drag through the floor if any (no floor in space when g=0)
                final double proposedY = bodyPosition.getY() + delta.getY();
                final double y = body.getGravity() != 0 ? Math.max( 0, proposedY ) : proposedY;
                Point2D newBodyPosition = new Point2D.Double( bodyPosition.getX() + delta.getX(), y );
                getBody().setPosition( newBodyPosition.getX(), newBodyPosition.getY() );
                if ( newBodyPosition.getY() > 0 ) {
                    snapToTrackDuringDrag();
                }

                getBody().setUserControlled( true );
                getBody().setVelocity( 0, 0 );
            }

            @Override protected void endDrag( PInputEvent event ) {
                super.endDrag( event );
                getBody().setUserControlled( false );
            }
        } );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        getBody().addListener( bodyListener );
        update();
    }

    private void snapToTrackDuringDrag() {
        TraversalState state = getBody().getTrackMatch( 0, -1.0 / 3.0 );

        //Point at the track so the skater will have the right orientation for a smooth landing
        if ( state != null ) {
            ImmutableVector2D vector = state.getParametricFunction2D().getUnitNormalVector( state.getAlpha() );//todo: this code is highly similar to code in Particle.updateStateFrom1D
            double sign = state.isTop() ? 1.0 : -1.0;
            ImmutableVector2D v = vector.getInstanceOfMagnitude( sign );
            getBody().setAngle( v.getAngle() - Math.PI / 2 );

            //Don't attach to the floor--it's more important that the skater "picks up" immediately instead of sticking to the floor until the user passes a vertical threshold, and
            // it's okay if the skater falls and adds energy then
            if ( !( state.getParametricFunction2D() instanceof LinearFloorSpline2D ) ) {
                //Put it on the track so it will snap to the track
                final Point2D.Double x = vector.times( 1E-6 ).plus( state.getPosition().getX(), state.getPosition().getY() ).toPoint2D();
                getBody().setPosition( x.getX(), x.getY() );
            }
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
        if ( body.isFacingRight() ) {
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
        if ( body.isFacingRight() ) {
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
