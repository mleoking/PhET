// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;

/**
 * Provides physical locations (positions) of the atoms within a Sucrose molecule.
 * Positions sampled from a 2d rasterized view of sucrose from JMol with ProjectorUtil
 *
 * @author Sam Reid
 */
public class SucrosePositions {
    private final String oxygen =
            "561, 562\n" +
            "295, 185\n" +
            "770, 150\n" +
            "964, 437\n" +
            "885, 586\n" +
            "633, 367\n" +
            "567, 636\n" +
            "648, 657\n" +
            "406, 404\n" +
            "374, 448\n" +
            "232, 666\n";
    private final String carbon =
            "385, 230\n" +
            "357, 369\n" +
            "454, 423\n" +
            "442, 570\n" +
            "432, 558\n" +
            "359, 674\n" +
            "586, 484\n" +
            "684, 538\n" +
            "801, 538\n" +
            "852, 426\n" +
            "759, 358\n" +
            "787, 223\n";

    private final String hydrogen =
            "211, 208\n" +
            "386, 171\n" +
            "485, 218\n" +
            "255, 376\n" +
            "457, 473\n" +
            "554, 385\n" +
            "504, 465\n" +
            "352, 620\n" +
            "205, 583\n" +
            "408, 760\n" +
            "627, 614\n" +
            "619, 696\n" +
            "686, 488\n" +
            "749, 418\n" +
            "807, 612\n" +
            "839, 645\n" +
            "1009, 493\n" +
            "857, 362\n" +
            "883, 213\n" +
            "718, 196\n" +
            "694, 188";
    private ImmutableVector2D origin;

    //Get the positions for a specific list of atom types.
    private ArrayList<ImmutableVector2D> getPositions( String text ) {
        ArrayList<ImmutableVector2D> list = new ArrayList<ImmutableVector2D>();
        StringTokenizer stringTokenizer = new StringTokenizer( text, "\n" );
        while ( stringTokenizer.hasMoreTokens() ) {
            String token = stringTokenizer.nextToken();
            StringTokenizer st = new StringTokenizer( token, ", " );
            double x = Double.parseDouble( st.nextToken() );
            double y = Double.parseDouble( st.nextToken() );
            list.add( new ImmutableVector2D( x, y ) );
        }
        return list;
    }

    public ImmutableVector2D getOrigin() {
        if ( origin == null ) {
            origin = getPositions( carbon ).get( 0 );
        }
        return origin;
    }

    public ArrayList<ImmutableVector2D> getCarbonPositions() {
        return normalize( getPositions( carbon ) );
    }

    //Normalize by subtracting the origin and scaling
    private ArrayList<ImmutableVector2D> normalize( final ArrayList<ImmutableVector2D> positions ) {
        return new ArrayList<ImmutableVector2D>() {{
            for ( ImmutableVector2D position : positions ) {
                add( position.minus( getOrigin() ).times( WaterMolecule.oxygenRadius * 10 / 800 * 0.7 ) );//OH distance in sugar should look like that in water
            }
        }};
    }

    public ArrayList<ImmutableVector2D> getHydrogenPositions() {
        return normalize( getPositions( hydrogen ) );
    }

    public ArrayList<ImmutableVector2D> getOxygenPositions() {
        return normalize( getPositions( oxygen ) );
    }
}