package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.H2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.H2O;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.O2;
import static edu.colorado.phet.chemistry.model.Element.H;
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
        public final ImmutableVector2D position;
        public final double rotation; // radians

        public MoleculeSpot( MoleculeShape shape, ImmutableVector2D position, double rotation ) {
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

        addReactantSpot( new MoleculeSpot( O2, new ImmutableVector2D(), Math.PI / 2 ) );
        addReactantSpot( new MoleculeSpot( H2, new ImmutableVector2D( -xOffset, 0 ), Math.PI / 2 ) );
        addReactantSpot( new MoleculeSpot( H2, new ImmutableVector2D( xOffset, 0 ), Math.PI / 2 ) );

        // essentially stacked on top
        addProductSpot( new MoleculeSpot( H2O, new ImmutableVector2D( 0, O.getRadius() ), 0 ) );
        addProductSpot( new MoleculeSpot( H2O, new ImmutableVector2D( 0, -O.getRadius() ), 0 ) );
    }};
}
