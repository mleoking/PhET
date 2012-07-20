// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.*;
import static edu.colorado.phet.chemistry.model.Element.*;

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

    public static final ReactionShape H2_N2_TO_NH3 = new ReactionShape() {{
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
    }};

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
