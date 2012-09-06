// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.materials.GLMaterial;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.PointArc;
import edu.colorado.phet.lwjglphet.shapes.Sector;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.model.Molecule;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.BOND_ANGLE_SAMPLES;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_WRITEMASK;
import static org.lwjgl.opengl.GL11.GL_LINE_WIDTH;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;

/**
 * Displays an angle between two bonds. It is shaded, has an outline, and a label
 */
public class BondAngleNode extends GLNode {
    private PointArc arc;
    private boolean initialized = false;
    private final MoleculeViewTab module;
    private final Molecule molecule;
    private final PairGroup aGroup;
    private final PairGroup bGroup;

    private static final float radius = 5;
    private Sector sector;
    private Property<Float> alpha = new Property<Float>( 0f );

    public BondAngleNode( final MoleculeViewTab module, Molecule molecule, PairGroup a, PairGroup b ) {
        this.module = module;
        this.molecule = molecule;
        this.aGroup = a;
        this.bGroup = b;

        requireEnabled( GL_BLEND );

        setRenderPass( GLOptions.RenderPass.TRANSPARENCY );

        // only show these when they should be visible
        module.showBondAngles.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( module.showBondAngles.get() );
            }
        } );
    }

    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        // don't write to the depth buffer
        // prevention of a weird "wedge" bug where a triangle would disappear from the sector when overlapping other transparent objects
        glPushAttrib( GL_DEPTH_WRITEMASK );
        glDepthMask( false );
    }

    @Override protected void postRender( GLOptions options ) {

        // probably write to the depth buffer again
        glPopAttrib();

        super.postRender( options );
    }

    public void initialize( Vector3F a, Vector3F b, Vector3F lastMidpoint ) {
        arc = new PointArc( a, b, radius, BOND_ANGLE_SAMPLES, lastMidpoint ) {
            @Override protected void preRender( GLOptions options ) {
                super.preRender( options );

                glPushAttrib( GL_LINE_WIDTH );
                glLineWidth( 2 );

                Color color = MoleculeShapesColor.BOND_ANGLE_ARC.get();
                color4f( new Color(
                        ( (float) color.getRed() ) / 255,
                        ( (float) color.getGreen() ) / 255,
                        ( (float) color.getBlue() ) / 255,
                        alpha.get() / 2
                ) );
            }

            @Override protected void postRender( GLOptions options ) {
                glPopAttrib();

                super.postRender( options );
            }
        };

        addChild( arc );

        final GLMaterial sectorMaterial = new GLMaterial() {
            @Override public void before( GLOptions options ) {
                super.before( options );

                Color color = MoleculeShapesColor.BOND_ANGLE_SWEEP.get();
                color4f( new Color(
                        ( (float) color.getRed() ) / 255,
                        ( (float) color.getGreen() ) / 255,
                        ( (float) color.getBlue() ) / 255,
                        alpha.get() / 2
                ) );

//                glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
            }

            @Override public void after( GLOptions options ) {


                super.after( options );
            }
        };

        sector = new Sector( arc );
        sector.setMaterial( sectorMaterial );
        addChild( sector );
    }

    public PairGroup getA() {
        return aGroup;
    }

    public PairGroup getB() {
        return bGroup;
    }

    public void updateView( Vector3F localCameraPosition, Vector3F lastMidpoint ) {
        Vector3D aDir = aGroup.position.get().normalized();
        Vector3D bDir = bGroup.position.get().normalized();

        alpha.set( calculateBrightness( aDir, bDir, localCameraPosition, molecule.getRadialAtoms().size() ) );

        Vector3F a = aDir.to3F();
        Vector3F b = bDir.to3F();

        if ( !initialized ) {
            initialized = true;

            initialize( a, b, lastMidpoint );
        }
        else {
            arc.updateView( a, b, lastMidpoint );
            sector.updateView();
        }
    }

    private static final float[] lowThresholds = new float[]{0, 0, 0, 0, 0.35f, 0.45f, 0.5f};
    private static final float[] highThresholds = new float[]{0, 0, 0, 0.5f, 0.55f, 0.65f, 0.75f};

    public static float calculateBrightness( Vector3D aDir, Vector3D bDir, Vector3F localCameraPosition, int bondQuantity ) {
        // if there are less than 3 bonds, always show the bond angle. (experimental)
        if ( bondQuantity <= 2 ) {
            return 1;
        }

        // a combined measure of how close the angles are AND how orthogonal they are to the camera
        float brightness = (float) Math.abs( aDir.cross( bDir ).dot( localCameraPosition.to3D() ) );

        // DEV version for finding good-looking constants
//        float lowThreshold = MoleculeShapesProperties.minimumBrightnessFade.get().floatValue();
//        float highThreshold = MoleculeShapesProperties.maximumBrightnessFade.get().floatValue();
        float lowThreshold = lowThresholds[bondQuantity];
        float highThreshold = highThresholds[bondQuantity];

        float interpolatedValue = brightness / ( highThreshold - lowThreshold ) - lowThreshold / ( highThreshold - lowThreshold );

        return (float) MathUtil.clamp( 0, interpolatedValue, 1 );
    }

    public Vector3F getCenter() {
        return Vector3F.ZERO;
    }

    public Vector3F getMidpoint() {
        return arc.getMidpoint();
    }

    public PointArc getArc() {
        return arc;
    }
}
