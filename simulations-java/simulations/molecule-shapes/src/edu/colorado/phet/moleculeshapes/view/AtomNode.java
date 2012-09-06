// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;

import org.lwjgl.util.glu.Sphere;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.model.Atom3D;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealPairGroup;

import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColorMaterial;

/**
 * Displays an atom in the 3d view
 */
public class AtomNode extends GLNode {

    public final PairGroup pair; // referenced pair (or null)
    public final Property<Vector3D> position; // position property
    private final Property<Color> color;
    private final float radius;

    /**
     * Construct an AtomNode for use within the VSEPR Model view
     *
     * @param pairOption An electron pair if applicable. If no pair is given, it is ASSUMED to be the center atom, and is colored differently
     */
    public AtomNode( Option<PairGroup> pairOption ) {
        this(
                // if position exists, use it. Otherwise, assume the origin
                pairOption.isSome() ? pairOption.get().position : new Property<Vector3D>( new Vector3D() ),
                pairOption,

                // change color based on whether we have an assocated pair group
                pairOption.isSome()
                ? ( ( pairOption.get() instanceof RealPairGroup ) ? new Property<Color>( ( (RealPairGroup) ( pairOption.get() ) ).getElement().getColor() ) : MoleculeShapesColor.ATOM.getProperty() )
                : MoleculeShapesColor.ATOM_CENTER.getProperty(),

                MoleculeShapesConstants.MODEL_ATOM_RADIUS );
    }

    /**
     * Construct an AtomNode for use within a general real 3D molecule view
     *
     * @param atom        The atom to show
     * @param fixedRadius Whether to show a fixed radius, or to have it depend on the atomic radius
     */
    public AtomNode( Atom3D atom, boolean fixedRadius ) {
        this(
                atom.position,
                new None<PairGroup>(),
                atom.getColor(),

                // use either a fixed radius, or the atomic radius
                fixedRadius ? MoleculeShapesConstants.MOLECULE_ATOM_RADIUS : (float) ( 0.8 * 2 * atom.getRadius() / 100 ) // 100x is picometer=>angstrom
        );
    }

    public AtomNode( final Property<Vector3D> position, Option<PairGroup> pairOption, final Color color, final float radius ) {
        this( position, pairOption, new Property<Color>( color ), radius );
    }

    public AtomNode( final Property<Vector3D> position, Option<PairGroup> pairOption, final Property<Color> color, final float radius ) {
        this.color = color;
        this.radius = radius;
        this.pair = pairOption.isSome() ? pairOption.get() : null;
        this.position = position;

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                transform.set( Matrix4F.translation( (float) position.get().getX(), (float) position.get().getY(), (float) position.get().getZ() ) );
            }
        } );

        requireEnabled( GL_COLOR_MATERIAL );
        requireEnabled( GL_CULL_FACE );
        requireEnabled( GL_LIGHTING );
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        // TODO: add a specular component. noted 1f [0,128]
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        LWJGLUtils.color4f( color.get() );
        new Sphere().draw( radius, MoleculeShapesProperties.sphereSamples.get(), MoleculeShapesProperties.sphereSamples.get() );
    }

    public float getRadius() {
        return radius;
    }
}
