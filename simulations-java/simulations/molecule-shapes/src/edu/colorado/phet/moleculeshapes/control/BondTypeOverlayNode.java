// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.module.MoleculeViewModule;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 * Displays a molecule fragment that can be enabled or disabled
 */
public class BondTypeOverlayNode extends MoleculeModelNode {
    public BondTypeOverlayNode( final MoleculeModel molecule, final JMEView view, final MoleculeViewModule module, final Property<Boolean> enabled ) {
        super( molecule, null, module, view.getCamera() );

        setScaleOverride( 1f );

        // don't show the center atom
        getCenterAtomNode().setCullHint( CullHint.Always );

        /*---------------------------------------------------------------------------*
        * Below is an ugly hackish way of making the molecule look disabled. specifically, it puts up a semi-transparent
        * quad in front of the molecule. Its position and dimensions were unfortunately hand-tuned.
        *
        * Let me know if there is a better way to handle this!
        *----------------------------------------------------------------------------*/

        final boolean isLonePair = !molecule.getLonePairs().isEmpty();

        // dimensions
        final double shadowWidth = ( isLonePair ? PairGroup.LONE_PAIR_DISTANCE : PairGroup.BONDED_PAIR_DISTANCE ) + MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * 5;
        final float shadowHeight = MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * ( isLonePair ? 13 : 10 );

        final Geometry shadowQuad = new Geometry( "Disable shadow", new Quad( (float) shadowWidth, shadowHeight ) ) {{
            // move it so it covers the molecule fragment
            setLocalTranslation( 0, -shadowHeight / 2, MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * ( isLonePair ? 8 : 5 ) );

            // give it that semi-transparent color
            setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
                MoleculeShapesColor.BACKGROUND.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
                    public void apply( ColorRGBA rgba ) {
                        float alpha = 0.3f + ( rgba.r + rgba.g + rgba.b ) * 0.3f / 3;
                        setColor( "Color", new ColorRGBA( rgba.r, rgba.g, rgba.b, alpha ) );
                    }
                } );

                // allow transparency
                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
            setQueueBucket( Bucket.Transparent );
        }};
        attachChild( shadowQuad );

        // ensure that when enabled, the shadow quad isn't visible
        enabled.addObserver( new SimpleObserver() {
            public void update() {
                shadowQuad.setCullHint( enabled.get() ? CullHint.Always : CullHint.Never );
            }
        } );
    }
}
