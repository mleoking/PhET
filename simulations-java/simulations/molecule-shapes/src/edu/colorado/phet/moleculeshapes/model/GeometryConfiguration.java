// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

/**
 * Contains the "optimal" molecule structures (pair group directions stored as unit vectors),
 * in an order such that higher-repulsion pair groups (triple bonds, double bonds, lone pairs)
 * will tend to occupy the 1st slots, and single bonds will occupy the later slots.
 */
public class GeometryConfiguration {

    private static final double TETRA_CONST = Math.PI * -19.471220333 / 180;

    /**
     * Map from steric number => optical geometry
     */
    private static final Map<Integer, GeometryConfiguration> GEOMETRY_MAP = new HashMap<Integer, GeometryConfiguration>() {{
        put( 0, new GeometryConfiguration(
                Strings.GEOMETRY__EMPTY )
        );
        put( 1, new GeometryConfiguration(
                Strings.GEOMETRY__DIATOMIC,

                new ImmutableVector3D( 1, 0, 0 )
        ) );
        put( 2, new GeometryConfiguration(
                Strings.GEOMETRY__LINEAR,

                new ImmutableVector3D( 1, 0, 0 ),
                new ImmutableVector3D( -1, 0, 0 )
        ) );
        put( 3, new GeometryConfiguration(
                Strings.GEOMETRY__TRIGONAL_PLANAR,

                new ImmutableVector3D( 1, 0, 0 ),
                new ImmutableVector3D( Math.cos( Math.PI * 2 / 3 ), Math.sin( Math.PI * 2 / 3 ), 0 ),
                new ImmutableVector3D( Math.cos( Math.PI * 4 / 3 ), Math.sin( Math.PI * 4 / 3 ), 0 )
        ) );
        put( 4, new GeometryConfiguration(
                Strings.GEOMETRY__TETRAHEDRAL,

                new ImmutableVector3D( 0, 0, 1 ),
                new ImmutableVector3D( Math.cos( 0 ) * Math.cos( TETRA_CONST ), Math.sin( 0 ) * Math.cos( TETRA_CONST ), Math.sin( TETRA_CONST ) ),
                new ImmutableVector3D( Math.cos( Math.PI * 2 / 3 ) * Math.cos( TETRA_CONST ), Math.sin( Math.PI * 2 / 3 ) * Math.cos( TETRA_CONST ), Math.sin( TETRA_CONST ) ),
                new ImmutableVector3D( Math.cos( Math.PI * 4 / 3 ) * Math.cos( TETRA_CONST ), Math.sin( Math.PI * 4 / 3 ) * Math.cos( TETRA_CONST ), Math.sin( TETRA_CONST ) )
        ) );
        put( 5, new GeometryConfiguration(
                Strings.GEOMETRY__TRIGONAL_BIPYRAMIDAL,

                // equitorial (fills up with lone pairs first)
                new ImmutableVector3D( 0, 1, 0 ),
                new ImmutableVector3D( 0, Math.cos( Math.PI * 2 / 3 ), Math.sin( Math.PI * 2 / 3 ) ),
                new ImmutableVector3D( 0, Math.cos( Math.PI * 4 / 3 ), Math.sin( Math.PI * 4 / 3 ) ),

                // axial
                new ImmutableVector3D( 1, 0, 0 ),
                new ImmutableVector3D( -1, 0, 0 )
        ) );
        put( 6, new GeometryConfiguration(
                Strings.GEOMETRY__OCTAHEDRAL,

                // opposites first
                new ImmutableVector3D( 0, 0, 1 ),
                new ImmutableVector3D( 0, 0, -1 ),
                new ImmutableVector3D( 0, 1, 0 ),
                new ImmutableVector3D( 0, -1, 0 ),
                new ImmutableVector3D( 1, 0, 0 ),
                new ImmutableVector3D( -1, 0, 0 )
        ) );
    }};

    public static GeometryConfiguration getConfiguration( int numberOfGroups ) {
        return GEOMETRY_MAP.get( numberOfGroups );
    }

    /*---------------------------------------------------------------------------*
    * instance data and methods
    *----------------------------------------------------------------------------*/

    public List<ImmutableVector3D> unitVectors;
    public final String name;

    private GeometryConfiguration( String name, ImmutableVector3D... unitVectors ) {
        this.name = name;
        this.unitVectors = Arrays.asList( unitVectors );
    }

}
