// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

public class MoleculeBucket extends Bucket {
    private final MoleculeShape shape;
    private final Dimension2D size;

    private static final double MOTION_VELOCITY = 1500; // In picometers per second of sim time.

    private final List<Molecule> molecules = new ArrayList<Molecule>();

    public MoleculeBucket( final MoleculeShape shape, int initialQuantity ) {
        this( shape, new PDimension( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( shape.getBoundingCircleRadius() * 0.7 * ChemicalReactionsConstants.THIS_CONSTANT_SHOULD_NOT_EXIST + 200 ), 200 ), initialQuantity );
    }

    public MoleculeBucket( final MoleculeShape shape, Dimension2D size, int initialQuantity ) {
        super( new Point2D.Double(), size, Color.GRAY, "" );
        this.shape = shape;
        this.size = size;

        FunctionalUtils.repeat( new Runnable() {
            public void run() {
                molecules.add( new Molecule( shape ) );
            }
        }, initialQuantity );
    }

    public void addMolecule( Molecule molecule ) {
        molecules.add( molecule );
    }

    public void removeMolecule( Molecule molecule ) {
        molecules.remove( molecule );
    }

    public void attractMolecules( double dt ) {
        for ( Molecule molecule : molecules ) {
            accelerateMoleculeIntoPosition( molecule, getDestinationFor( molecule ), dt );
        }
    }

    private ImmutableVector2D getDestinationFor( Molecule molecule ) {
        int p = molecules.indexOf( molecule );
        System.out.println( "p = " + p );
        return new ImmutableVector2D( getPosition() ).plus( new ImmutableVector2D(
                0,
                p * 20
        ) );
    }

    public MoleculeShape getShape() {
        return shape;
    }

    public List<Molecule> getMolecules() {
        return molecules;
    }

    public List<Atom> getAtoms() {
        List<Atom> result = new ArrayList<Atom>();
        for ( Molecule molecule : molecules ) {
            result.addAll( molecule.getAtoms() );
        }
        return result;
    }

    public Dimension2D getSize() {
        return size;
    }

    private void accelerateMoleculeIntoPosition( Molecule molecule, ImmutableVector2D destination, double dt ) {
        if ( molecule.getPosition().getDistance( destination ) != 0 ) {
            // Move towards the current destination.
            double distanceToTravel = MOTION_VELOCITY * dt;
            double distanceToTarget = molecule.getPosition().getDistance( destination );

            double farDistanceMultiple = 10; // if we are this many times away, we speed up

            // if we are far from the target, let's speed up the velocity
            if ( distanceToTarget > distanceToTravel * farDistanceMultiple ) {
                double extraDistance = distanceToTarget - distanceToTravel * farDistanceMultiple;
                distanceToTravel *= 1 + extraDistance / 300;
            }

            if ( distanceToTravel >= distanceToTarget ) {
                // Closer than one step, so just go there.
                molecule.setPosition( destination );
                molecule.setAngle( 0 );
            }
            else {
                // Move towards the destination.
                double angle = Math.atan2( destination.getY() - molecule.getPosition().getY(),
                                           destination.getX() - molecule.getPosition().getX() );
                molecule.setPosition( molecule.getPosition().plus( new ImmutableVector2D(
                        distanceToTravel * Math.cos( angle ),
                        distanceToTravel * Math.sin( angle )
                ) ) );

                double angleDelta = Reaction.angleDifference( 0, molecule.getAngle() );
                molecule.setAngle( (float) ( molecule.getAngle() + angleDelta * distanceToTravel / distanceToTarget ) );
            }
        }
    }

    @Override public void setPosition( Point2D position ) {
        super.setPosition( position );

        for ( Molecule molecule : molecules ) {
            molecule.setPosition( new ImmutableVector2D( getPosition().getX(), getPosition().getY() ) );
        }
    }
}
