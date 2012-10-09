// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;

import org.lwjgl.util.glu.Cylinder;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.QuaternionF;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;

import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_NORMALIZE;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * Displays a bond in the 3d view, of order 1, 2 or 3
 */
public class BondNode extends GLNode {

    // position properties
    private final Property<Vector3D> a;
    private final Property<Vector3D> b;

    private final int bondOrder;
    private final float bondRadius;
    private final Option<Float> maxLength;
    private final MoleculeViewTab tab;

    private final SingleBondNode[] aBonds;
    private final SingleBondNode[] bBonds;

    private boolean fixedCamera = false;

    public BondNode( final Property<Vector3D> a, final Property<Vector3D> b, int bondOrder, float bondRadius,
                     Option<Float> maxLength, final MoleculeViewTab tab ) {
        this( a, b, bondOrder, bondRadius, maxLength, tab, Color.WHITE, Color.WHITE );
    }

    // currently only called from above
    private BondNode( final Property<Vector3D> a, final Property<Vector3D> b, int bondOrder, float bondRadius,
                      Option<Float> maxLength, final MoleculeViewTab tab, Color aColor, Color bColor ) {
        this.a = a;
        this.b = b;
        this.bondOrder = bondOrder;
        this.bondRadius = bondRadius;
        this.maxLength = maxLength;
        this.tab = tab;

        // initialize the single bond nodes, for use later
        aBonds = new SingleBondNode[bondOrder];
        bBonds = new SingleBondNode[bondOrder];
        for ( int i = 0; i < bondOrder; i++ ) {
            // they will have unit height and unit radius! we will scale, rotate and translate them later
            aBonds[i] = new SingleBondNode( 1, 1, aColor );
            bBonds[i] = new SingleBondNode( 1, 1, bColor );

            // attach them
            addChild( aBonds[i] );
            addChild( bBonds[i] );
        }

        updateView();
    }

    public void setFixedCamera( boolean fixedCamera ) {
        this.fixedCamera = fixedCamera;

        updateView();
    }

    /**
     * Reposition all of the SingleBondNodes so that they form to make a bond with the correct physical alignment and position
     */
    public void updateView() {
        // transform our position and direction into the local coordinate frame. we will do our computations there
//        Vector3F click3d = camera.getWorldCoordinates( new Vector2f( 0, 0 ), 0f ).clone();
        Vector3F click3d = transform.inversePosition( Vector3F.ZERO );
        Vector3F cameraPosition = getGlobalTransform().inversePosition( tab.getCameraRay( 0, 0 ).pos );
        if ( fixedCamera ) {
            cameraPosition = new Vector3F( 0, 0, 45 );
        }

        // extract our start and end
        final Vector3F start = a.get().to3F();
        final Vector3F end = b.get().to3F();

        // unit vector point in the direction of the end from the start
        final Vector3F towardsEnd = end.minus( start ).normalized();

        // calculate the length of the bond. sometimes it can be length-limited, and we push the bond towards B
        float distance = start.distance( end );
        final float length;
        float overLength = 0;
        if ( maxLength.isSome() && distance > maxLength.get() ) {
            // our bond would be too long
            length = maxLength.get();
            overLength = distance - maxLength.get();
        }
        else {
            length = distance;
        }

        // find the center of our bond. we add in the "over" length if necessary to offset the bond from between A and B
        final Vector3F bondCenter = ( start.times( 0.5f ).plus( end.times( 0.5f ) ) ).plus( towardsEnd.times( overLength / 2 ) );

        // get a unit vector perpendicular to the bond direction and camera direction
        final Vector3F perpendicular = bondCenter.minus( end ).normalized().cross( bondCenter.minus( cameraPosition ).normalized() ).normalized();

        /*
         * Compute offsets from the "central" bond position, for showing double and triple bonds.
         * The offsets are basically the relative positions of the 1/2/3 cylinders that are displayed as a bond.
         */
        final Vector3F[] offsets;

        // how far bonds are apart. constant refined for visual appearance. triple-bonds aren't wider than atoms, most notably
        float bondSeparation = bondRadius * ( 12.0f / 5.0f );
        switch( bondOrder ) {
            case 1:
                offsets = new Vector3F[]{new Vector3F()};
                break;
            case 2:
                offsets = new Vector3F[]{perpendicular.times( bondSeparation / 2 ), perpendicular.times( -bondSeparation / 2 )};
                break;
            case 3:
                offsets = new Vector3F[]{Vector3F.ZERO, perpendicular.times( bondSeparation ), perpendicular.times( -bondSeparation )};
                break;
            default:
                throw new RuntimeException( "bad bond order: " + bondOrder );
        }

        // since we need to support two different colors (A-colored and B-colored), we need to compute the offsets from the bond center for each
        final Vector3F colorOffset = towardsEnd.times( length / 4 );

        for ( int i = 0; i < bondOrder; i++ ) {
            aBonds[i].transform.set(
                    Matrix4F.translation( bondCenter.plus( offsets[i] ).minus( colorOffset ) )

                            // point the cylinder in the direction of the bond. Cylinder is symmetric, so sign doesn't matter
                            .times( Matrix4F.fromMatrix3f( QuaternionF.getRotationQuaternion( Vector3F.Z_UNIT, towardsEnd ).toRotationMatrix() ) )

                                    // since our cylinders point along the Z direction and have unit-length and unit-radius, we scale it out to the desired radius and half-length
                            .times( Matrix4F.scaling( bondRadius, bondRadius, length / 2 ) ) );

            // this time, add in the color offset instead of subtracting
            bBonds[i].transform.set(
                    Matrix4F.translation( bondCenter.plus( offsets[i] ).plus( colorOffset ) )
                            .times( Matrix4F.fromMatrix3f( QuaternionF.getRotationQuaternion( Vector3F.Z_UNIT, towardsEnd ).toRotationMatrix() ) )
                            .times( Matrix4F.scaling( bondRadius, bondRadius, length / 2 ) ) );
        }
    }

    /**
     * Basically, a glorified cylinder.
     */
    public static class SingleBondNode extends GLNode {
        private final float length;
        private final float bondRadius;
        private final Color color;

        // NOTE: keep the ColorRGBA color here for the future, in case we want dual-colored bonds for another sim
        public SingleBondNode( final float length, final float bondRadius, final Color color ) {
            this.length = length;
            this.bondRadius = bondRadius;
            this.color = color;

            requireEnabled( GL_COLOR_MATERIAL );
            requireEnabled( GL_CULL_FACE );
            requireEnabled( GL_LIGHTING );
            requireEnabled( GL_NORMALIZE );
        }

        @Override public void renderSelf( GLOptions options ) {
            super.renderSelf( options );

            glColorMaterial( GL_FRONT, GL_DIFFUSE );
            LWJGLUtils.color4f( color );

            // since this cylinder draws from z=0 to z=length, we have to handle translation here
            glTranslatef( 0, 0, -length / 2 );
            new Cylinder().draw( bondRadius, bondRadius, length, MoleculeShapesProperties.cylinderSamples.get(), 1 );
            glTranslatef( 0, 0, length / 2 );
        }
    }
}
