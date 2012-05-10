// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsResources.Strings;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static edu.colorado.phet.chemistry.model.Element.*;

public class MoleculeShape {

    public final List<AtomSpot> spots;
    public final String name;

    private MoleculeShape( String name, List<AtomSpot> spots ) {
        this.name = name;
        this.spots = Collections.unmodifiableList( spots );
    }

    public static class AtomSpot {
        public final Element element;
        public final ImmutableVector2D position;

        public AtomSpot( Element element, ImmutableVector2D position ) {
            this.element = element;
            this.position = position;
        }
    }

    /*---------------------------------------------------------------------------*
    * various molecule shapes
    *----------------------------------------------------------------------------*/

    public static final MoleculeShape H2 = new MoleculeShape( Strings.BUCKET___H_2, Arrays.asList(
            new AtomSpot( H, new ImmutableVector2D( -H.getRadius(), 0 ) ),
            new AtomSpot( H, new ImmutableVector2D( H.getRadius(), 0 ) )
    ) );


    public static final MoleculeShape O2 = new MoleculeShape( Strings.BUCKET___O_2, Arrays.asList(
            new AtomSpot( O, new ImmutableVector2D( -O.getRadius(), 0 ) ),
            new AtomSpot( O, new ImmutableVector2D( O.getRadius(), 0 ) )
    ) );

    public static final MoleculeShape Cl2 = new MoleculeShape( Strings.BUCKET___CL_2, Arrays.asList(
            new AtomSpot( Cl, new ImmutableVector2D( -Cl.getRadius(), 0 ) ),
            new AtomSpot( Cl, new ImmutableVector2D( Cl.getRadius(), 0 ) )
    ) );

    public static final MoleculeShape N2 = new MoleculeShape( Strings.BUCKET___N_2, Arrays.asList(
            new AtomSpot( N, new ImmutableVector2D( -N.getRadius(), 0 ) ),
            new AtomSpot( N, new ImmutableVector2D( N.getRadius(), 0 ) )
    ) );

    public static final MoleculeShape H2O = new MoleculeShape( Strings.BUCKET___H_2_O, Arrays.asList(
            new AtomSpot( O, new ImmutableVector2D( 0, 0 ) ),
            new AtomSpot( H, new ImmutableVector2D( ( O.getRadius() + H.getRadius() ), 0 ) ),
            new AtomSpot( H, new ImmutableVector2D( -( O.getRadius() + H.getRadius() ), 0 ) )
    ) );

    public static final MoleculeShape HCl = new MoleculeShape( Strings.BUCKET___H_CL, Arrays.asList(
            new AtomSpot( H, new ImmutableVector2D( -Cl.getRadius(), 0 ) ),
            new AtomSpot( Cl, new ImmutableVector2D( -H.getRadius(), 0 ) )
    ) );

    public static final MoleculeShape NH3 = new MoleculeShape( Strings.BUCKET___NH_3, Arrays.asList(
            new AtomSpot( N, new ImmutableVector2D( -H.getRadius(), 0 ) ),

            // left
            new AtomSpot( H, new ImmutableVector2D( -N.getRadius(), 0 ) ),

            // top
            new AtomSpot( H, new ImmutableVector2D( -H.getRadius(), -( N.getRadius() + H.getRadius() ) ) ),

            // bottom
            new AtomSpot( H, new ImmutableVector2D( -H.getRadius(), N.getRadius() + H.getRadius() ) )
    ) );

}
