// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.materials.ColorMaterial;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.Quad;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.Molecule;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;

/**
 * Displays a molecule fragment that can be enabled or disabled
 */
public class BondTypeOverlayNode extends MoleculeModelNode {
    public BondTypeOverlayNode( final Molecule molecule, final MoleculeViewTab module, final Property<Boolean> enabled ) {
        super( molecule, new GLNode(), module );

        setScaleOverride( 1f );

        // don't show the center atom
        getCenterAtomNode().setVisible( false );

        /*---------------------------------------------------------------------------*
        * Below is an ugly hackish way of making the molecule look disabled. specifically, it puts up a semi-transparent
        * quad in front of the molecule. Its position and dimensions were unfortunately hand-tuned.
        *
        * Let me know if there is a better way to handle this!
        *----------------------------------------------------------------------------*/

        final boolean isLonePair = !molecule.getRadialLonePairs().isEmpty();

        // dimensions
        final float shadowWidth = (float) ( ( isLonePair ? PairGroup.LONE_PAIR_DISTANCE : PairGroup.BONDED_PAIR_DISTANCE ) + MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * 5 );
        final float shadowHeight = MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * ( isLonePair ? 13 : 10 );

        final GLNode shadowQuad = new Quad( shadowWidth, shadowHeight ) {{
            // move it so it covers the molecule fragment
            translate( 0, -shadowHeight / 2, MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * ( isLonePair ? 8 : 5 ) );

            // give it that semi-transparent color
            MoleculeShapesColor.BACKGROUND.addLWJGLColorObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    float[] cmps = color.getColorComponents( new float[4] );
                    float alpha = 0.3f + cmps[0] + cmps[1] + cmps[2] * 0.3f / 3;
                    setMaterial( new ColorMaterial( cmps[0], cmps[1], cmps[2], (float) MathUtil.clamp( 0f, alpha, 1f ) ) );
                }
            } );

            setRenderPass( GLOptions.RenderPass.TRANSPARENCY );
            requireEnabled( GL11.GL_BLEND );
        }};
        addChild( shadowQuad );

        // ensure that when enabled, the shadow quad isn't visible
        enabled.addObserver( new SimpleObserver() {
            public void update() {
                shadowQuad.setVisible( !enabled.get() );
            }
        } );
    }
}
