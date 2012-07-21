// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.Permutation;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.model.AttractorModel.ResultMapping;

public class VseprConfiguration {
    public final int x;
    public final int e;

    public final List<Vector3D> bondedUnitVectors;
    public final List<Vector3D> lonePairUnitVectors;

    public final GeometryConfiguration geometry;

    public final String name;

    public VseprConfiguration( int x, int e ) {
        this.x = x;
        this.e = e;

        geometry = GeometryConfiguration.getConfiguration( x + e );
        bondedUnitVectors = new ArrayList<Vector3D>();
        lonePairUnitVectors = new ArrayList<Vector3D>();
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
            name = Strings.SHAPE__EMPTY;
        }
        else if ( x == 1 ) {
            name = Strings.SHAPE__DIATOMIC;
        }
        else if ( x == 2 ) {
            if ( e == 0 || e == 3 || e == 4 ) {
                name = Strings.SHAPE__LINEAR;
            }
            else if ( e == 1 || e == 2 ) {
                name = Strings.SHAPE__BENT;
            }
            else {
                throw new RuntimeException( "invalid x: " + x + ", e: " + e );
            }
        }
        else if ( x == 3 ) {
            if ( e == 0 ) {
                name = Strings.SHAPE__TRIGONAL_PLANAR;
            }
            else if ( e == 1 ) {
                name = Strings.SHAPE__TRIGONAL_PYRAMIDAL;
            }
            else if ( e == 2 || e == 3 ) {
                name = Strings.SHAPE__T_SHAPED;
            }
            else {
                throw new RuntimeException( "invalid x: " + x + ", e: " + e );
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
                throw new RuntimeException( "invalid x: " + x + ", e: " + e );
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
                throw new RuntimeException( "invalid x: " + x + ", e: " + e );
            }
        }
        else if ( x == 6 ) {
            if ( e == 0 ) {
                name = Strings.SHAPE__OCTAHEDRAL;
            }
            else {
                throw new RuntimeException( "invalid x: " + x + ", e: " + e );
            }
        }
        else {
            name = null;
        }
    }

    public List<Vector3D> getAllUnitVectors() {
        return geometry.unitVectors;
    }

    public List<Vector3D> getIdealBondUnitVectors() {
        return new ArrayList<Vector3D>() {{
            for ( int i = e; i < x + e; i++ ) {
                add( geometry.unitVectors.get( i ) );
            }
        }};
    }

    // for finding ideal rotations including matching for "bond-vs-bond" and "lone pair-vs-lone pair"
    public ResultMapping getIdealGroupRotationToPositions( List<PairGroup> groups ) {
        assert ( x + e ) == groups.size();
        return AttractorModel.findClosestMatchingConfiguration( AttractorModel.getOrientationsFromOrigin( groups ), geometry.unitVectors, LocalShape.vseprPermutations( groups ) );
    }

    // for finding ideal rotations exclusively using the "bonded" portions
    public ResultMapping getIdealBondRotationToPositions( List<PairGroup> groups ) {
        // ideal vectors excluding lone pairs (just for the bonds)
        assert ( x ) == groups.size();
        List<Vector3D> idealModelBondVectors = getIdealBondUnitVectors();

        return AttractorModel.findClosestMatchingConfiguration( AttractorModel.getOrientationsFromOrigin( groups ), idealModelBondVectors, Permutation.permutations( idealModelBondVectors.size() ) );
    }

    /*---------------------------------------------------------------------------*
    * matching of electron positions to shape
    *----------------------------------------------------------------------------*/

    public boolean matchesElectronPairs( List<PairGroup> pairs, double epsilon ) {
        List<PairGroup> bondedGroups = new ArrayList<PairGroup>();
        List<PairGroup> lonePairs = new ArrayList<PairGroup>();
        for ( PairGroup pair : pairs ) {
            if ( pair.isLonePair ) {
                lonePairs.add( pair );
            }
            else {
                bondedGroups.add( pair );
            }
        }

        if ( x != bondedGroups.size() || e != lonePairs.size() ) {
            return false;
        }

        List<PairGroup> orderedGroups = new ArrayList<PairGroup>();
        orderedGroups.addAll( lonePairs );
        orderedGroups.addAll( bondedGroups );

        double[][] configAngles = new double[x + e][x + e];
        double[][] groupAngles = new double[x + e][x + e];

        List<Integer> indices = new ArrayList<Integer>();
        List<Integer> assignments = new ArrayList<Integer>();

        // yes, it double-computes angles here. could remove in the future, but change recur
        for ( int i = 0; i < pairs.size(); i++ ) {
            indices.add( i );
            for ( int k = 0; k < pairs.size(); k++ ) {
                configAngles[i][k] = Math.acos( geometry.unitVectors.get( i ).dot( geometry.unitVectors.get( k ) ) );
                groupAngles[i][k] = Math.acos( orderedGroups.get( i ).position.get().getNormalizedInstance().dot( orderedGroups.get( k ).position.get().getNormalizedInstance() ) );
            }
        }

        return recurMatch( indices, assignments, configAngles, groupAngles, epsilon );
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
        for ( Integer group : new ArrayList<Integer>( remainingPairs ) ) {
            boolean isLonePair = group < e;
            if ( isLonePair != isLoneSpot ) {
                // can't put a lone pair in a bonded spot, and vice versa
                continue;
            }

            boolean ok = true;
            for ( int k = 0; k < i; k++ ) {
                // check that the angles are similar enough
                double angleDifference = Math.abs( configAngles[i][k] - pairAngles[group][assignments.get( k )] );
                if ( angleDifference > epsilon ) {
                    ok = false;
                    break;
                }
            }
            if ( ok ) {
                remainingPairs.remove( group );
                assignments.add( group );
                boolean success = recurMatch( remainingPairs, assignments, configAngles, pairAngles, epsilon );
                remainingPairs.add( group );
                assignments.remove( group ); // it's an Integer, not an int. so this should remove the element, NOT the location..
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
