// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Carbon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Hydrogen;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NeutralOxygen;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;

/**
 * Provides physical locations (positions) of the atoms within a glucose molecule.
 * Positions sampled from a 2d rasterized view of glucose from JMol with ProjectorUtil
 * <p/>
 * C6H12O6
 *
 * @author Sam Reid
 */
public class GlucosePositions {
    private static final String text = "H 465, 311\n" +
                                       "H 558, 513\n" +
                                       "H 344, 500\n" +
                                       "H 768, 389\n" +
                                       "H 680, 665\n" +
                                       "C 509, 331\n" +
                                       "C 625, 398\n" +
                                       "O 697, 339\n" +
                                       "C 603, 526\n" +
                                       "C 505, 588\n" +
                                       "C 394, 516\n" +
                                       "O 435, 394\n" +
                                       "C 288, 572\n" +
                                       "O 254, 690\n" +
                                       "O 467, 692\n" +
                                       "H 220, 509\n" +
                                       "H 185, 720\n" +
                                       "H 336, 596\n" +
                                       "H 424, 742\n" +
                                       "H 549, 625\n" +
                                       "H 681, 402\n" +
                                       "O 532, 213\n" +
                                       "H 577, 231\n" +
                                       "O 704, 591";

    //Data structure that has the type of the atom, its element identifier and the position in model space
    public static abstract class Atom {
        public final String type;
        public final ImmutableVector2D position;

        Atom( String type, ImmutableVector2D position ) {
            this.type = type;
            this.position = position;
        }

        //Create the SphericalParticle corresponding to this atom type that can be used in the model
        public abstract SphericalParticle createConstituent();
    }

    //Get the positions for a specific list of atom types.
    public ArrayList<Atom> getAtoms() {
        ArrayList<Atom> list = new ArrayList<Atom>();
        StringTokenizer stringTokenizer = new StringTokenizer( text, "\n" );

        //Iterate over the list and convert each line to an atom instance
        while ( stringTokenizer.hasMoreTokens() ) {
            list.add( parseAtom( stringTokenizer.nextToken() ) );
        }
        return list;
    }

    //Reads a line from the string and converts to an Atom instance at the right model location
    private Atom parseAtom( String line ) {
        StringTokenizer st = new StringTokenizer( line, ", " );

        //Read the type and location
        String type = st.nextToken();
        double x = Double.parseDouble( st.nextToken() );
        double y = Double.parseDouble( st.nextToken() );

        //Add an atom instance based on the type and location
        return new Atom( type, toModel( new ImmutableVector2D( x, y ) ) ) {
            @Override public SphericalParticle createConstituent() {
                if ( type.equals( "H" ) ) { return new Hydrogen(); }
                if ( type.equals( "C" ) ) { return new Carbon(); }
                if ( type.equals( "O" ) ) { return new NeutralOxygen();}
                throw new RuntimeException();
            }
        };
    }

    //Use the position of the first atom as the origin, which other positions will be based on.  Origin is in pixel coordinates and converted to model coordinates in toModel
    public ImmutableVector2D getOrigin() {
        StringTokenizer st = new StringTokenizer( text.substring( 0, text.indexOf( '\n' ) ), ", " );

        //Throw away the type
        st.nextToken();

        //Read the position
        return new ImmutableVector2D( Double.parseDouble( st.nextToken() ), Double.parseDouble( st.nextToken() ) );
    }

    private ImmutableVector2D toModel( ImmutableVector2D position ) {
        return position.minus( getOrigin() ).times( WaterMolecule.oxygenRadius * 10 / 800 * 0.7 );
    }
}