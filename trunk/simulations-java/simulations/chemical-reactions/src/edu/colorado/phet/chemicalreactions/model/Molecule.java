// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import edu.colorado.phet.chemicalreactions.box2d.BodyModel;
import edu.colorado.phet.chemicalreactions.model.MoleculeShape.AtomSpot;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.IBucketSphere;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.BOX2D_DENSITY;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.BOX2D_MODEL_TRANSFORM;

/**
 * A molecule in this simulation. When a reaction happens, this instance would be discarded and replaced with new ones.
 */
public class Molecule extends BodyModel {

    public final MoleculeShape shape;

    private final List<Atom> atoms = new ArrayList<Atom>();
    private final Map<Atom, AtomSpot> atomMap = new HashMap<Atom, AtomSpot>();

    public final VoidNotifier disposeNotifier = new VoidNotifier();

    public final Property<ImmutableVector2D> destination = new Property<ImmutableVector2D>( new ImmutableVector2D() );

    public Molecule( MoleculeShape shape ) {
        super( new BodyDef() {{
            type = BodyType.DYNAMIC;
        }}, BOX2D_MODEL_TRANSFORM );
        this.shape = shape;

        final SimpleObserver updateObserver = new SimpleObserver() {
            public void update() {
                updatePositions();
            }
        };
        position.addObserver( updateObserver, false );
        angle.addObserver( updateObserver, false );

        for ( final AtomSpot spot : shape.spots ) {
            final Atom atom = new Atom( spot.element, new Property<ImmutableVector2D>( spot.position ) );
            addAtom( atom );
            atomMap.put( atom, spot );

            addFixtureDef( new Box2dModel.ChemicalReactionFixtureDef() {{
                density = BOX2D_DENSITY;
                shape = new CircleShape() {{
                    m_radius = (float) BOX2D_MODEL_TRANSFORM.viewToModelDeltaX( atom.getRadius() );
                    ImmutableVector2D box2dPosition = BOX2D_MODEL_TRANSFORM.viewToModelDelta( spot.position );
                    m_p.set( (float) box2dPosition.getX(), (float) box2dPosition.getY() );
                }};
            }} );
        }
    }

    public void outsidePlayAreaAcceleration( double dt ) {
        if ( getPosition().getDistance( getDestination() ) != 0 ) {
            // Move towards the current destination.
            double distanceToTravel = 1500 * dt;
            double distanceToTarget = getPosition().getDistance( getDestination() );

            double farDistanceMultiple = 10; // if we are this many times away, we speed up

            // if we are far from the target, let's speed up the velocity
            if ( distanceToTarget > distanceToTravel * farDistanceMultiple ) {
                double extraDistance = distanceToTarget - distanceToTravel * farDistanceMultiple;
                distanceToTravel *= 1 + extraDistance / 300;
            }

            if ( distanceToTravel >= distanceToTarget ) {
                // Closer than one step, so just go there.
                setPosition( getDestination() );
                setAngle( 0 );
            }
            else {
                // Move towards the destination.
                double angle = Math.atan2( getDestination().getY() - getPosition().getY(),
                                           getDestination().getX() - getPosition().getX() );
                setPosition( getPosition().plus( new ImmutableVector2D(
                        distanceToTravel * Math.cos( angle ),
                        distanceToTravel * Math.sin( angle )
                ) ) );

                double angleDelta = Reaction.angleDifference( 0, getAngle() );
                setAngle( (float) ( getAngle() + angleDelta * distanceToTravel / distanceToTarget ) );
            }
        }
    }

    private void updatePositions() {
        for ( Atom atom : atoms ) {
            AtomSpot spot = atomMap.get( atom );
            ImmutableVector2D rotatedOffset = spot.position.getRotatedInstance( angle.get() );
            atom.position.set( Molecule.this.position.get().plus( rotatedOffset ) );
        }
    }

    public void addAtom( Atom atom ) {
        atoms.add( atom );
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    @Override public void intraStep() {
        super.intraStep();

        // disabled the brownian motion for now
//        float multiplier = 3;
//        if ( !userControlled.get() ) {
//            final Vec2 v = getBody().getLinearVelocity();
//            final float r1 = (float) ( Math.random() - 0.5 ) * multiplier;
//            final float r2 = (float) ( Math.random() - 0.5 ) * multiplier;
//            final float r3 = (float) ( Math.random() - 0.5 ) * multiplier;
//            getBody().setLinearVelocity( new Vec2( v.x + r1 / 20, v.y + r2 / 20 ) );
//            getBody().setAngularVelocity( getBody().getAngularVelocity() + r3 / 100 );
//        }
    }

    public boolean isMovingCloserTo( Molecule other ) {
        // this also measures the degree to how the distance between the two is CURRENTLY changing
        return ( getPosition().minus( other.getPosition() ) ).dot( getVelocity().minus( other.getVelocity() ) ) < 0;
    }

    public ImmutableVector2D getDestination() {
        return destination.get();
    }

    public void setDestination( ImmutableVector2D position ) {
        destination.set( position );
    }
}
