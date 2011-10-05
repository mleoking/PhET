// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.shapes.PointArc;
import edu.colorado.phet.jmephet.shapes.Sector;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.BOND_ANGLE_SAMPLES;

/**
 * Displays an angle between two bonds. It is shaded, has an outline, and a label
 */
public class BondAngleNode extends Node {
    private PointArc arc;
    private boolean initialized = false;
    private final MoleculeShapesModule module;
    private final MoleculeModel molecule;
    private final PairGroup aGroup;
    private final PairGroup bGroup;

    private static final float radius = 5;
    private Sector sector;
    private Sector oppositeSector;
    private Property<Float> alpha = new Property<Float>( 0f );

    public BondAngleNode( final MoleculeShapesModule module, MoleculeModel molecule, PairGroup a, PairGroup b ) {
        super( "Bond Angle" );
        this.module = module;
        this.molecule = molecule;
        this.aGroup = a;
        this.bGroup = b;

        setQueueBucket( Bucket.Transparent ); // allow it to be transparent

        // only show these when they should be visible
        MoleculeShapesProperties.showBondAngles.addObserver( new SimpleObserver() {
            public void update() {
                setCullHint( MoleculeShapesProperties.showBondAngles.get() ? CullHint.Never : CullHint.Always );
            }
        } );
    }

    public void initialize( Vector3f a, Vector3f b, Vector3f lastMidpoint ) {
        arc = new PointArc( a, b, radius, BOND_ANGLE_SAMPLES, lastMidpoint ) {{
            setLineWidth( 2 );
        }};

        attachChild( new Geometry( "Bond Arc", arc ) {{
            setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
                final Runnable updateColor = new Runnable() {
                    public void run() {
                        ColorRGBA colorRGBA = MoleculeShapesColor.BOND_ANGLE_ARC.getRGBA();
                        setColor( "Color", new ColorRGBA( colorRGBA.r, colorRGBA.g, colorRGBA.b, alpha.get() ) );
                    }
                };

                // update on color change
                MoleculeShapesColor.BOND_ANGLE_ARC.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
                    public void apply( ColorRGBA colorRGBA ) {
                        updateColor.run();
                    }
                } );

                // update on alpha change
                alpha.addObserver( new SimpleObserver() {
                    public void update() {
                        updateColor.run();
                    }
                } );

                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
        }} );

        final Material sectorMaterial = new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            final Runnable updateColor = new Runnable() {
                public void run() {
                    ColorRGBA colorRGBA = MoleculeShapesColor.BOND_ANGLE_SWEEP.getRGBA();
                    setColor( "Color", new ColorRGBA( colorRGBA.r, colorRGBA.g, colorRGBA.b, alpha.get() / 2 ) );
                }
            };

            // update on color change
            MoleculeShapesColor.BOND_ANGLE_SWEEP.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
                public void apply( ColorRGBA colorRGBA ) {
                    updateColor.run();
                }
            } );

            // update on alpha change
            alpha.addObserver( new SimpleObserver() {
                public void update() {
                    updateColor.run();
                }
            } );

            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }};

        // do two swoops for transparent two-sidedness
        // TODO: render only one. look up double-sidedness
        sector = new Sector( arc, false );
        attachChild( new Geometry( "Bond Sector A=>B", sector ) {{
            setMaterial( sectorMaterial );
        }} );
        oppositeSector = new Sector( arc, true );
        attachChild( new Geometry( "Bond Sector B=>A", oppositeSector ) {{
            setMaterial( sectorMaterial );
        }} );
    }

    public PairGroup getA() {
        return aGroup;
    }

    public PairGroup getB() {
        return bGroup;
    }

    public void updateView( Vector3f localCameraPosition, Vector3f lastMidpoint ) {
        ImmutableVector3D aDir = aGroup.position.get().normalized();
        ImmutableVector3D bDir = bGroup.position.get().normalized();

        alpha.set( calculateBrightness( aDir, bDir, localCameraPosition, molecule.getBondedGroups().size() ) );

        Vector3f a = JMEUtils.convertVector( aDir );
        Vector3f b = JMEUtils.convertVector( bDir );

        if ( !initialized ) {
            initialized = true;

            initialize( a, b, lastMidpoint );
        }
        else {
            arc.updateView( a, b, lastMidpoint );
            sector.updateView();
            oppositeSector.updateView();
        }
    }

    private static final float[] lowThresholds = new float[] { 0, 0, 0, 0, 0.35f, 0.45f, 0.5f };
    private static final float[] highThresholds = new float[] { 0, 0, 0, 0.5f, 0.55f, 0.65f, 0.75f };

    public static float calculateBrightness( ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f localCameraPosition, int bondQuantity ) {
        // if there are less than 3 bonds, always show the bond angle. (experimental)
        if ( bondQuantity <= 2 ) {
            return 1;
        }

        // a combined measure of how close the angles are AND how orthogonal they are to the camera
        float brightness = (float) Math.abs( aDir.cross( bDir ).dot( JMEUtils.convertVector( localCameraPosition ) ) );

        // DEV version for finding good-looking constants
//        float lowThreshold = MoleculeShapesProperties.minimumBrightnessFade.get().floatValue();
//        float highThreshold = MoleculeShapesProperties.maximumBrightnessFade.get().floatValue();
        float lowThreshold = lowThresholds[bondQuantity];
        float highThreshold = highThresholds[bondQuantity];

        float interpolatedValue = brightness / ( highThreshold - lowThreshold ) - lowThreshold / ( highThreshold - lowThreshold );

        return (float) MathUtil.clamp( 0, interpolatedValue, 1 );
    }

    public Vector3f getCenter() {
        return new Vector3f();
    }

    public Vector3f getMidpoint() {
        return arc.getMidpoint();
    }

    public PointArc getArc() {
        return arc;
    }
}
