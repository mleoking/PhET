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
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

public class MoleculeBucket extends Bucket {
    private final MoleculeShape shape;
    private final Dimension2D size;
    private final int initialQuantity;
    private final int maxQuantity;

    private static final double INFLATION_FACTOR = 1.1; // how much the radius is "inflated" to compensate for the visual look

    private final List<Molecule> molecules = new ArrayList<Molecule>();

    // fires to notify what stacking order (z-order) the molecules are in within the bucket
    public final Notifier<List<Molecule>> moleculeOrderNotifier = new Notifier<List<Molecule>>();

    public MoleculeBucket( final MoleculeShape shape, int initialQuantity ) {
        this( shape, initialQuantity, initialQuantity );
    }

    public MoleculeBucket( final MoleculeShape shape, int initialQuantity, int maxQuantity ) {
        this( shape, new PDimension( MODEL_VIEW_TRANSFORM.modelToViewDeltaX( bottomDimension( maxQuantity ) * 0.7 * shape.getBoundingCircleRadius() * ChemicalReactionsConstants.THIS_CONSTANT_SHOULD_NOT_EXIST + 200 ), 200 ), initialQuantity, maxQuantity );
    }

    public MoleculeBucket( final MoleculeShape shape, Dimension2D size, int initialQuantity, int maxQuantity ) {
        super( new Point2D.Double(), size, Color.GRAY, "" );
        this.shape = shape;
        this.size = size;
        this.initialQuantity = initialQuantity;
        this.maxQuantity = maxQuantity;

        addInitialMolecules();
    }

    public void addInitialMolecules() {
        FunctionalUtils.repeat( new Runnable() {
            public void run() {
                molecules.add( new Molecule( shape ) );
            }
        }, initialQuantity );
    }

    public void positionMoleculesInBucket() {
        calculateDestinations();

        for ( Molecule molecule : molecules ) {
            molecule.setPosition( molecule.getDestination() );
        }
    }

    public void addMolecule( Molecule molecule ) {
        molecules.add( molecule );

        calculateDestinations();
    }

    public void removeMolecule( Molecule molecule ) {
        molecules.remove( molecule );

        calculateDestinations();
    }

    private List<ImmutableVector2D> calculateSpots() {
        List<ImmutableVector2D> result = new ArrayList<ImmutableVector2D>();

        int d = bottomDimension( maxQuantity );
        int row = 0;
        int col = 0;
        double centerX = getPosition().getX();
        double baseY = getPosition().getY();

        double radius = shape.getBoundingCircleRadius() * INFLATION_FACTOR;
        double diameter = radius * 2;

        for ( Molecule molecule : molecules ) {
            result.add( new ImmutableVector2D(
                    centerX - diameter * ( (double) d ) / 2 + radius + diameter * col,
                    baseY + row * radius * 0.4
            ) );

            col++;
            if ( col >= d ) {
                row++;
                col = 0;
                d--;
            }
        }

        return result;
    }

    public void calculateDestinations() {
        List<Molecule> moleculeOrder = new ArrayList<Molecule>();
        List<Molecule> remainingMolecules = new ArrayList<Molecule>( molecules );

        // fill spots in order
        for ( ImmutableVector2D spot : calculateSpots() ) {
            if ( !remainingMolecules.isEmpty() ) {
                // first check to see if a molecule is in that spot
                Molecule filledMolecule = null;
                for ( Molecule molecule : remainingMolecules ) {
                    if ( molecule.getPosition().equals( spot ) ) {
                        filledMolecule = molecule;
                        break;
                    }
                }

                // if there is one there, don't change it, and continue with remaining molecules
                if ( filledMolecule != null ) {
                    moleculeOrder.add( filledMolecule );
                    remainingMolecules.remove( filledMolecule );
                    continue;
                }

                // otherwise, find the closest molecule to fill that spot
                Molecule closestMolecule = null;
                double smallestDistance = Double.POSITIVE_INFINITY;
                for ( Molecule molecule : remainingMolecules ) {
                    double distance = molecule.getPosition().distance( spot );
                    if ( distance < smallestDistance ) {
                        smallestDistance = distance;
                        closestMolecule = molecule;
                    }
                }

                assert closestMolecule != null; // there are remaining molecules, so this would probably signal NaN

                // move that molecule into place, and continue
                closestMolecule.setDestination( spot );
                moleculeOrder.add( closestMolecule );
                remainingMolecules.remove( closestMolecule );
            }
        }

        moleculeOrderNotifier.updateListeners( moleculeOrder );

    }

    private static int bottomDimension( int max ) {
        int d = 1;
        int q = 1;
        while ( q < max ) {
            d++;
            q += d;
        }
        return d;
    }

    public void attractMolecules( double dt ) {
        for ( Molecule molecule : molecules ) {
            molecule.outsidePlayAreaAcceleration( dt );
        }
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

    @Override public void setPosition( Point2D position ) {
        super.setPosition( position );

        calculateDestinations();

        for ( Molecule molecule : molecules ) {
            molecule.setPosition( molecule.getDestination() );
        }
    }
}
