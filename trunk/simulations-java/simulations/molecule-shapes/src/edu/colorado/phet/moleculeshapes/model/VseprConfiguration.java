// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;

public class VseprConfiguration {
    public final int x;
    public final int e;

    public final List<ImmutableVector3D> bondedUnitVectors;
    public final List<ImmutableVector3D> lonePairUnitVectors;

    public final GeometryConfiguration geometry;

    public final String name;

    public VseprConfiguration( int x, int e ) {
        this.x = x;
        this.e = e;

        geometry = GeometryConfiguration.getConfiguration( x + e );
        bondedUnitVectors = new ArrayList<ImmutableVector3D>();
        lonePairUnitVectors = new ArrayList<ImmutableVector3D>();
        for ( int i = 0; i < x + e; i++ ) {
            if ( i < e ) {
                // fill up the lone pair unit vectors first
                lonePairUnitVectors.add( geometry.unitVectors.get( i ) );
            }
            else {
                bondedUnitVectors.add( geometry.unitVectors.get( i ) );
            }
        }

        // figure out what the name is
        if ( x == 0 ) {
            name = null;
        }
        else if ( x == 1 ) {
            name = Strings.SHAPE__DIATOMIC;
        }
        else if ( x == 2 ) {
            if ( e == 0 || e == 3 ) {
                name = Strings.SHAPE__LINEAR;
            }
            else if ( e == 1 || e == 2 ) {
                name = Strings.SHAPE__BENT;
            }
            else {
                name = null; // not standard
            }
        }
        else if ( x == 3 ) {
            if ( e == 0 ) {
                name = Strings.SHAPE__TRIGONAL_PLANAR;
            }
            else if ( e == 1 ) {
                name = Strings.SHAPE__TRIGONAL_PYRAMIDAL;
            }
            else if ( e == 2 ) {
                name = Strings.SHAPE__T_SHAPED;
            }
            else {
                name = null;
            }
        }
        else if ( x == 4 ) {
            if ( e == 0 ) {
                name = Strings.SHAPE__TETRAHEDRAL;
            }
            else if ( e == 1 ) {
                name = Strings.SHAPE__SEESAW;
            }
            else if ( e == 2 ) {
                name = Strings.SHAPE__SQUARE_PLANAR;
            }
            else {
                name = null;
            }
        }
        else if ( x == 5 ) {
            if ( e == 0 ) {
                name = Strings.SHAPE__TRIGONAL_BIPYRAMIDAL;
            }
            else if ( e == 1 ) {
                name = Strings.SHAPE__SQUARE_PYRAMIDAL;
            }
            else {
                name = null;
            }
        }
        else if ( x == 6 ) {
            if ( e == 0 ) {
                name = Strings.SHAPE__OCTAHEDRAL;
            }
            else {
                name = null;
            }
        }
        else {
            name = null;
        }
    }

    /*---------------------------------------------------------------------------*
    * matching of electron positions to shape
    *----------------------------------------------------------------------------*/

    public boolean matchesElectronPairs( List<ElectronPair> pairs, double epsilon ) {
        List<ElectronPair> bondedPairs = new ArrayList<ElectronPair>();
        List<ElectronPair> lonePairs = new ArrayList<ElectronPair>();
        for ( ElectronPair pair : pairs ) {
            if ( pair.isLonePair ) {
                lonePairs.add( pair );
            }
            else {
                bondedPairs.add( pair );
            }
        }

        if ( x != bondedPairs.size() || e != lonePairs.size() ) {
            return false;
        }

        List<ElectronPair> orderedPairs = new ArrayList<ElectronPair>();
        orderedPairs.addAll( lonePairs );
        orderedPairs.addAll( bondedPairs );

        double[][] configAngles = new double[x + e][x + e];
        double[][] pairAngles = new double[x + e][x + e];

        List<Integer> indices = new ArrayList<Integer>();
        List<Integer> assignments = new ArrayList<Integer>();

        // yes, it double-computes angles here. could remove in the future, but change recur
        for ( int i = 0; i < pairs.size(); i++ ) {
            indices.add( i );
            for ( int k = 0; k < pairs.size(); k++ ) {
                configAngles[i][k] = Math.acos( geometry.unitVectors.get( i ).dot( geometry.unitVectors.get( k ) ) );
                pairAngles[i][k] = Math.acos( orderedPairs.get( i ).position.get().normalized().dot( orderedPairs.get( k ).position.get().normalized() ) );
            }
        }

        return recurMatch( indices, assignments, configAngles, pairAngles, epsilon );
    }

    private boolean recurMatch( List<Integer> remainingPairs, List<Integer> assignments, double[][] configAngles, double[][] pairAngles, double epsilon ) {
        // if we have matched all of the pairs, we are successful!
        if ( remainingPairs.isEmpty() ) {
            return true;
        }

        // our new spot to assign a pair to
        int i = assignments.size();

        // whether the new spot is lone-pair or bonded-pair
        boolean isLoneSpot = i < e;

        // try to assign each remaining pair to this spot
        for ( Integer pair : new ArrayList<Integer>( remainingPairs ) ) {
            boolean isLonePair = pair < e;
            if ( isLonePair != isLoneSpot ) {
                // can't put a lone pair in a bonded spot, and vice versa
                continue;
            }

            boolean ok = true;
            for ( int k = 0; k < i; k++ ) {
                // check that the angles are similar enough
                double angleDifference = Math.abs( configAngles[i][k] - pairAngles[pair][assignments.get( k )] );
                if ( angleDifference > epsilon ) {
                    ok = false;
                    break;
                }
            }
            if ( ok ) {
                remainingPairs.remove( pair );
                assignments.add( pair );
                boolean success = recurMatch( remainingPairs, assignments, configAngles, pairAngles, epsilon );
                remainingPairs.add( pair );
                assignments.remove( pair ); // it's an Integer, not an int. so this should remove the element, NOT the location..
                if ( success ) {
                    return true;
                }
            }
        }
        return false;
    }

    /*---------------------------------------------------------------------------*
    * equality / hash
    *----------------------------------------------------------------------------*/

    @Override public int hashCode() {
        return x + e * 10;
    }

    @Override public boolean equals( Object obj ) {
        if ( obj instanceof VseprConfiguration ) {
            VseprConfiguration other = (VseprConfiguration) obj;
            return x == other.x && e == other.e;
        }
        else {
            return false;
        }
    }
}
