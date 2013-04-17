// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.Cl2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.H2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.H2O;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.HCl;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.N2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.NH3;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.O2;
import static edu.colorado.phet.chemistry.model.Element.Cl;
import static edu.colorado.phet.chemistry.model.Element.H;
import static edu.colorado.phet.chemistry.model.Element.N;
import static edu.colorado.phet.chemistry.model.Element.O;

public class ReactionShape {

    // positions of the reactant molecules at the "start" of a reaction
    public final List<MoleculeSpot> reactantSpots = new ArrayList<MoleculeSpot>();

    // positions of the product molecules at the "end" of a reaction
    public final List<MoleculeSpot> productSpots = new ArrayList<MoleculeSpot>();

    private ReactionShape() {
    }

    public static class MoleculeSpot {
        public final MoleculeShape shape;
        public final Vector2D position;
        public final double rotation; // radians

        public MoleculeSpot( MoleculeShape shape, Vector2D position, double rotation ) {
            this.shape = shape;
            this.position = position;
            this.rotation = rotation;
        }
    }

    /**
     * Some reaction "reactant target" shapes need to have additional symmetries programmed in so that we can match all possible combinations
     * successfully. For example, in the ammonia reaction we need to handle reflection.
     *
     * @param reactants A list of reactants in the proper (matching) order to our reactant spots.
     * @return Permutations of the reactants list that handle all the necessary symmetries that are valid
     */
    public List<List<Molecule>> getSymmetries( final List<Molecule> reactants ) {
        // by default, just list the identity
        return new ArrayList<List<Molecule>>() {{
            add( reactants );
        }};
    }

    // returns a map that counts how many times a particular molecule shape appears in the reactants
    public Map<MoleculeShape, Integer> getMoleculeShapeCounts() {
        Map<MoleculeShape, Integer> result = new HashMap<MoleculeShape, Integer>();
        for ( MoleculeSpot spot : reactantSpots ) {
            MoleculeShape shape = spot.shape;
            if ( result.containsKey( shape ) ) {
                result.put( shape, result.get( shape ) + 1 );
            }
            else {
                result.put( shape, 1 );
            }
        }
        return result;
    }

    // the ordering of our molecule shapes inside the molecule
    public List<MoleculeShape> getMoleculeShapeOrdering() {
        List<MoleculeShape> result = new ArrayList<MoleculeShape>();
        for ( MoleculeSpot reactantSpot : reactantSpots ) {
            if ( !result.contains( reactantSpot.shape ) ) {
                result.add( reactantSpot.shape );
            }
        }
        return result;
    }

    protected void addReactantSpot( MoleculeSpot spot ) {
        reactantSpots.add( spot );
    }

    protected void addProductSpot( MoleculeSpot spot ) {
        productSpots.add( spot );
    }

    /*---------------------------------------------------------------------------*
    * possible reaction types
    *----------------------------------------------------------------------------*/

    public static final ReactionShape H2_O2_TO_H2O = new ReactionShape() {{
        // 2H2 + O2 => 2H2O

        // computed X offset for the molecules to be flush. just use the pythagorean theorem
        double xOffset = 2 * Math.sqrt( O.getRadius() * H.getRadius() );

        addReactantSpot( new MoleculeSpot( O2, new Vector2D(), Math.PI / 2 ) );
        addReactantSpot( new MoleculeSpot( H2, new Vector2D( -xOffset, 0 ), Math.PI / 2 ) );
        addReactantSpot( new MoleculeSpot( H2, new Vector2D( xOffset, 0 ), Math.PI / 2 ) );

        // essentially stacked on top
        addProductSpot( new MoleculeSpot( H2O, new Vector2D( 0, O.getRadius() ), 0 ) );
        addProductSpot( new MoleculeSpot( H2O, new Vector2D( 0, -O.getRadius() ), 0 ) );
    }};

    public static final ReactionShape H2_N2_TO_NH3 = new ReactionShape() {
        {
            // 3H2 + N2 => 2NH3

            double distalPairDistance = Math.sqrt( N.getRadius() * N.getRadius() + 2 * N.getRadius() * H.getRadius() );

            double magicAngle = Math.acos( ( N.getRadius() - H.getRadius() ) / ( N.getRadius() + H.getRadius() ) );

            // rotation for upper-right
            double rotationA = magicAngle - Math.PI / 4;

            // rotation for upper-left
            double rotationB = Math.PI - magicAngle + Math.PI / 4;

            // computed Y offset for the bottom (central) H2 to be flush. just use the pythagorean theorem
            double yOffset = 2 * Math.sqrt( N.getRadius() * H.getRadius() );

            addReactantSpot( new MoleculeSpot( N2, new Vector2D(), 0 ) );
            addReactantSpot( new MoleculeSpot( H2, new Vector2D( 0, -yOffset ), 0 ) );
            addReactantSpot( new MoleculeSpot( H2, new Vector2D( distalPairDistance, 0 ).getRotatedInstance( rotationA ).plus( N.getRadius(), 0 ), rotationA - Math.PI / 2 ) );
            addReactantSpot( new MoleculeSpot( H2, new Vector2D( distalPairDistance, 0 ).getRotatedInstance( rotationB ).plus( -N.getRadius(), 0 ), rotationB - Math.PI / 2 ) );

            // essentially stacked on top
            addProductSpot( new MoleculeSpot( NH3, new Vector2D( N.getRadius(), 0 ), magicAngle - Math.PI ) );
            addProductSpot( new MoleculeSpot( NH3, new Vector2D( -N.getRadius(), 0 ), Math.PI - magicAngle ) );
        }

        @Override public List<List<Molecule>> getSymmetries( List<Molecule> reactants ) {
            return Arrays.asList(
                    // identity
                    Arrays.asList( reactants.get( 0 ), reactants.get( 1 ), reactants.get( 2 ), reactants.get( 3 ) ),

                    // reflection (swapped two hydrogen positions)
                    Arrays.asList( reactants.get( 0 ), reactants.get( 1 ), reactants.get( 3 ), reactants.get( 2 ) )
            );
        }
    };

    public static final ReactionShape H2_Cl2_TO_HCl = new ReactionShape() {{
        // H2 + Cl2 => 2HCl

        // computed X offset for the molecules to be flush. just use the pythagorean theorem
        double xOffset = 2 * Math.sqrt( Cl.getRadius() * H.getRadius() );

        double totalWidth = H.getRadius() + xOffset + Cl.getRadius();

        addReactantSpot( new MoleculeSpot( Cl2, new Vector2D( totalWidth / 2 - Cl.getRadius(), 0 ), Math.PI / 2 ) );
        addReactantSpot( new MoleculeSpot( H2, new Vector2D( -totalWidth / 2 + H.getRadius(), 0 ), Math.PI / 2 ) );

        double theta = Math.acos( ( Cl.getRadius() - H.getRadius() ) / ( Cl.getRadius() + H.getRadius() ) );
        double phi = Math.PI / 2 - theta;
        Vector2D centerOffset = new Vector2D(
                H.getRadius() + Math.cos( phi ) * Cl.getRadius(),
                H.getRadius() + Math.sin( phi ) * Cl.getRadius()
        );
        Vector2D baseOffset = new Vector2D(
                H.getRadius() + Cl.getRadius(),
                0
        );
        Vector2D diff = centerOffset.minus( baseOffset );

        // top
        addProductSpot( new MoleculeSpot( HCl, new Vector2D( diff.getX() / 2, diff.getY() ), phi ) );

        // bottom
        addProductSpot( new MoleculeSpot( HCl, new Vector2D( diff.getX() / 2, -diff.getY() ), -phi ) );
    }};
}
