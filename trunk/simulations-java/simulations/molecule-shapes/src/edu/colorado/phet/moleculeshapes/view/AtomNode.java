// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.model.Atom3D;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealPairGroup;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Displays an atom in the 3d view
 */
public class AtomNode extends Geometry {

    public final PairGroup pair; // referenced pair (or null)
    public final Property<Vector3D> position; // position property
    private final float radius;

    /**
     * Construct an AtomNode for use within the VSEPR Model view
     *
     * @param pairOption   An electron pair if applicable. If no pair is given, it is ASSUMED to be the center atom, and is colored differently
     * @param assetManager Asset manager
     */
    public AtomNode( Option<PairGroup> pairOption, AssetManager assetManager ) {
        this(
                // if position exists, use it. Otherwise, assume the origin
                pairOption.isSome() ? pairOption.get().position : new Property<Vector3D>( new Vector3D() ),
                pairOption,

                // change color based on whether we have an assocated pair group
                pairOption.isSome()
                ? ( ( pairOption.get() instanceof RealPairGroup ) ? new Property<ColorRGBA>( JMEUtils.convertColor( ( (RealPairGroup) ( pairOption.get() ) ).getElement().getColor() ) ) : MoleculeShapesColor.ATOM.getRGBAProperty() )
                : MoleculeShapesColor.ATOM_CENTER.getRGBAProperty(),

                MoleculeShapesConstants.MODEL_ATOM_RADIUS,
                assetManager );
    }

    /**
     * Construct an AtomNode for use within a general real 3D molecule view
     *
     * @param atom         The atom to show
     * @param fixedRadius  Whether to show a fixed radius, or to have it depend on the atomic radius
     * @param assetManager Asset manager
     */
    public AtomNode( Atom3D atom, boolean fixedRadius, AssetManager assetManager ) {
        this(
                atom.position,
                new None<PairGroup>(),
                JMEUtils.convertColor( atom.getColor() ),

                // use either a fixed radius, or the atomic radius
                fixedRadius ? MoleculeShapesConstants.MOLECULE_ATOM_RADIUS : (float) ( 0.8 * 2 * atom.getRadius() / 100 ), // 100x is picometer=>angstrom
                assetManager );
    }

    public AtomNode( final Property<Vector3D> position, Option<PairGroup> pairOption, final ColorRGBA color, final float radius, AssetManager assetManager ) {
        this( position, pairOption, new Property<ColorRGBA>( color ), radius, assetManager );
    }

    public AtomNode( final Property<Vector3D> position, Option<PairGroup> pairOption, final Property<ColorRGBA> color, final float radius, AssetManager assetManager ) {
        super( "Atom", new Sphere( MoleculeShapesProperties.sphereSamples.get(), MoleculeShapesProperties.sphereSamples.get(), radius ) {{
            setTextureMode( Sphere.TextureMode.Projected ); // better quality on spheres
            TangentBinormalGenerator.generate( this );           // for lighting effect
        }} );
        this.radius = radius;
        this.pair = pairOption.isSome() ? pairOption.get() : null;
        this.position = position;

        MoleculeShapesProperties.sphereSamples.addObserver(
                new SimpleObserver() {
                    public void update() {
                        setMesh( new Sphere( MoleculeShapesProperties.sphereSamples.get(), MoleculeShapesProperties.sphereSamples.get(), radius ) {{
                            setTextureMode( Sphere.TextureMode.Projected ); // better quality on spheres
                            TangentBinormalGenerator.generate( this );           // for lighting effect
                        }} );
                    }
                }, false );

        setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );

            color.addObserver( JMEUtils.jmeObserver( new Runnable() {
                public void run() {
                    setColor( "Diffuse", color.get() );
                }
            } ) );
            setFloat( "Shininess", 1f ); // [0,128]
        }} );

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                setLocalTranslation( (float) position.get().getX(), (float) position.get().getY(), (float) position.get().getZ() );
            }
        } );
    }

    public float getRadius() {
        return radius;
    }
}
