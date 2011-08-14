// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Displays an atom in the 3d view
 */
public class AtomNode extends Geometry {

    public final PairGroup pair; // referenced pair (or null)
    public final Property<ImmutableVector3D> position; // position property

    /**
     * @param pairOption   An electron pair if applicable. If no pair is given, it is ASSUMED to be the center atom, and is colored differently
     * @param assetManager Asset manager
     */
    public AtomNode( Option<PairGroup> pairOption, AssetManager assetManager ) {
        super( "Atom", new Sphere( 32, 32, 2f ) {{
            setTextureMode( Sphere.TextureMode.Projected ); // better quality on spheres
            TangentBinormalGenerator.generate( this );           // for lighting effect
        }} );
        this.pair = pairOption.isSome() ? pairOption.get() : null;
        this.position = pairOption.isSome() ? pairOption.get().position : new Property<ImmutableVector3D>( new ImmutableVector3D() );

        setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );

            setColor( "Diffuse", pair != null ? MoleculeShapesConstants.COLOR_ATOM : MoleculeShapesConstants.COLOR_ATOM_CENTER );
            setFloat( "Shininess", 1f ); // [0,128]
        }} );

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                setLocalTranslation( (float) position.get().getX(), (float) position.get().getY(), (float) position.get().getZ() );
            }
        } );

        //rotate( 1.6f, 0, 0 );
    }
}
