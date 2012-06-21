package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Optimization1D;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.jamaphet.RigidMotionLeastSquares;
import edu.colorado.phet.jamaphet.collision.Collidable2D;
import edu.colorado.phet.jamaphet.collision.CollisionUtils;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.map;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.reduceLeft;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.zip;
import static edu.colorado.phet.jamaphet.RigidMotionLeastSquares.RigidMotionTransformation;

public class Reaction {

    public static final double MAX_ACCELERATION = 3000;
    public static final double MAX_ANGULAR_ACCELERATION = 500;

    public final ReactionShape shape;
    public final List<Molecule> reactants;

    // list of positions we need to target, for easy computational reference
    private final List<ImmutableVector2D> shapePositions = new ArrayList<ImmutableVector2D>();
    private final Kit kit;

    private double fitness;
    private double zeroError;
    private ReactionTarget target;

    public Reaction( Kit kit, ReactionShape shape, List<Molecule> reactants ) {
        this.kit = kit;
        this.shape = shape;
        this.reactants = reactants;

        // verify that the ordering of the molecule types are consistent
        for ( int i = 0; i < shape.reactantSpots.size(); i++ ) {
            assert shape.reactantSpots.get( i ).shape == reactants.get( i ).shape;
        }

        for ( ReactionShape.MoleculeSpot spot : shape.reactantSpots ) {
            shapePositions.add( spot.position );
        }
    }

    // slightly tweak the paths of the reactants
    public void tweak( double simulationTimeChange ) {
        assert target != null;
        assert fitness > 0;

        if ( zeroError < 15 ) {
            double averageVelocity = 0;
            for ( Molecule reactant : reactants ) {
                averageVelocity += reactant.getVelocity().getMagnitude();
            }
            averageVelocity /= reactants.size();

            final double finalAverageVelocity = averageVelocity;
            kit.completeReaction( this, map( shape.productSpots, new Function1<ReactionShape.MoleculeSpot, Molecule>() {
                public Molecule apply( final ReactionShape.MoleculeSpot spot ) {
                    return new Molecule( spot.shape ) {{
                        ImmutableVector2D transformedPosition = target.transformation.transformVector2D( spot.position );
                        ImmutableVector2D transformedOrigin = target.transformation.transformVector2D( new ImmutableVector2D() );
                        setPosition( transformedPosition );
                        setAngle( (float) target.rotation );

                        // approximately keep momentum with random direction of velocity
                        double randomSpeed = Math.random() * finalAverageVelocity * 2;
                        setVelocity( transformedPosition.minus( transformedOrigin ).getNormalizedInstance().times( randomSpeed ) );
                    }};
                }
            } ) );
            return;
        }

        for ( int i = 0; i < reactants.size(); i++ ) {
            Molecule molecule = reactants.get( i );
            double accelerationFactor = 2 / ( target.t * target.t );

            // how long our acceleration should last
            double effectiveTime = Math.min( simulationTimeChange, target.t );

            /*---------------------------------------------------------------------------*
            * position
            *----------------------------------------------------------------------------*/
            ImmutableVector2D targetPosition = target.transformedTargets.get( i );

            // compute the necessary (constant) acceleration to reach the target precisely on time
            ImmutableVector2D currentTrajectoryDestination = molecule.getPosition().plus( molecule.getVelocity().times( target.t ) );
            ImmutableVector2D acceleration = targetPosition.minus( currentTrajectoryDestination ).times( accelerationFactor );

            if ( acceleration.getMagnitude() > MAX_ACCELERATION ) {
                // TODO: can we prevent this and just 0-fitness this reaction out?
                acceleration = acceleration.times( MAX_ACCELERATION / acceleration.getMagnitude() );
            }

            // directly change the velocity TODO: use box2d's applyForce without torque to do this nicely once working
            molecule.setVelocity( molecule.getVelocity().plus( acceleration.times( effectiveTime ) ) );

            /*---------------------------------------------------------------------------*
            * rotation
            *----------------------------------------------------------------------------*/
            double targetAngle = fixAngle( target.rotation + shape.reactantSpots.get( i ).rotation ); // both the reaction AND molecule rotations
            double destinationAngle = fixAngle( molecule.getAngle() + molecule.getAngularVelocity() * target.t );

            // find the "closest" angle difference that we can use
            double closestDelta = Double.POSITIVE_INFINITY;
            for ( double symmetryAngle : molecule.shape.symmetryAngles ) {
                double delta = angleDifference( targetAngle + symmetryAngle, destinationAngle );
                if ( Math.abs( delta ) < Math.abs( closestDelta ) ) {
                    closestDelta = delta;
                }
            }

            double angularAcceleration = closestDelta * accelerationFactor;
            if ( Math.abs( angularAcceleration ) > MAX_ANGULAR_ACCELERATION ) {
                // TODO: can we prevent this and just 0-fitness this reaction out?
                angularAcceleration = angularAcceleration * MAX_ANGULAR_ACCELERATION / Math.abs( angularAcceleration );
            }
            molecule.setAngularVelocity( (float) ( molecule.getAngularVelocity() + angularAcceleration * effectiveTime ) );
        }
    }

    // return angle inclusive in [-pi, pi]
    private double fixAngle( double angle ) {
        double result = angle % ( 2 * Math.PI );
        if ( result < -Math.PI ) {
            return result + 2 * Math.PI;
        }
        else if ( result > Math.PI ) {
            return result - 2 * Math.PI;
        }
        else {
            return result;
        }
    }

    private double angleDifference( double a, double b ) {
        return fixAngle( a - b );
    }

    // compute the fitness and reaction target
    public void update() {
        // default for "null" reaction
        fitness = 0;

        // make sure every molecule is getting closer to every other molecule (filters out heavy computation that
        // we otherwise don't want to do)
        for ( Pair<Molecule, Molecule> moleculePair : FunctionalUtils.pairs( reactants ) ) {
            if ( !moleculePair._1.isMovingCloserTo( moleculePair._2 ) ) {
                return;
            }
        }

        ReactionTarget atZero = computeForTime( 0 );
        zeroError = atZero.error;
        ReactionTarget afterZero = computeForTime( 0.001 );
        if ( atZero.error < afterZero.error ) {
            // increasing error!
            return;
        }

        CollisionUtils.CollisionFit2D collisionFitting = CollisionUtils.bestFitIntersection2D( reactants );

        double bestT = Optimization1D.goldenSectionSearch( new Function1<Double, Double>() {
            public Double apply( Double t ) {
                return computeForTime( t ).error;
            }
        }, 0, MathUtil.clamp( 0.5, collisionFitting.t, 20 ), 0.1 ); // TODO: is this the right epsilon?

        target = computeForTime( bestT );
        fitness = Math.exp( -target.error ); // for now. consider in the future penalizing "long distance" and likely to collide before-hand reactions
    }

    private ReactionTarget computeForTime( final double t ) {
        // positions after time T
        List<ImmutableVector2D> positions = map( reactants, new Function1<Collidable2D, ImmutableVector2D>() {
            public ImmutableVector2D apply( Collidable2D object ) {
                return object.getPosition().plus( object.getVelocity().times( t ) );
            }
        } );

        // rigid motion transformation
        // don't allow reflections for now, since not all molecules can be reflected along their current axis
        final RigidMotionLeastSquares.RigidMotionTransformation transformation = RigidMotionLeastSquares.bestFitMotion2D( shapePositions, positions, false );

        // and closest targets at time T, based on the computed positions
        List<ImmutableVector2D> transformedTargets = map( shapePositions, new Function1<ImmutableVector2D, ImmutableVector2D>() {
            public ImmutableVector2D apply( ImmutableVector2D v ) {
                return transformation.transformVector2D( v );
            }
        } );

        List<ImmutableVector2D> differences = zip( positions, transformedTargets, new Function2<ImmutableVector2D, ImmutableVector2D, ImmutableVector2D>() {
            public ImmutableVector2D apply( ImmutableVector2D position, ImmutableVector2D target ) {
                return position.minus( target );
            }
        } );

        double averageError = reduceLeft( map( differences, new Function1<ImmutableVector2D, Double>() {
            public Double apply( ImmutableVector2D v ) {
                return v.getMagnitude();
            }
        } ), new Function2<Double, Double, Double>() {
            public Double apply( Double a, Double b ) {
                return a + b;
            }
        } ) / differences.size();

        ImmutableVector2D transformedAngle = transformation.transformVector2D( new ImmutableVector2D( 1, 0 ) )
                .minus( transformation.transformVector2D( new ImmutableVector2D( 0, 0 ) ) );
        double rotation = Math.atan2( transformedAngle.getY(), transformedAngle.getX() );

        return new ReactionTarget( t, transformation, transformedTargets, averageError, rotation );
    }

    public ReactionShape getShape() {
        return shape;
    }

    public List<Molecule> getReactants() {
        return reactants;
    }

    public List<ImmutableVector2D> getShapePositions() {
        return shapePositions;
    }

    public double getFitness() {
        return fitness;
    }

    public ReactionTarget getTarget() {
        return target;
    }

    public static class ReactionTarget {
        public final double t;
        public final RigidMotionTransformation transformation;
        public final List<ImmutableVector2D> transformedTargets;
        public final double error;
        public final double rotation;

        public ReactionTarget( double t, RigidMotionTransformation transformation, List<ImmutableVector2D> transformedTargets, double error, double rotation ) {
            this.t = t;
            this.transformation = transformation;
            this.transformedTargets = transformedTargets;
            this.error = error;
            this.rotation = rotation;
        }
    }
}
