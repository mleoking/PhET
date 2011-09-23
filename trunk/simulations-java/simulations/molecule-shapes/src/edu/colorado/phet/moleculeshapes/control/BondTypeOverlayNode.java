// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColors;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.view.MoleculeModelNode;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class BondTypeOverlayNode extends MoleculeModelNode {
    public BondTypeOverlayNode( final MoleculeModel molecule, final JMEView view, final JMEInputHandler inputHandler, final MoleculeShapesModule module, final Property<Boolean> enabled ) {
        super( molecule, inputHandler, null, module, view.getCamera() );

        getCenterAtomNode().setCullHint( CullHint.Always );

        // shadow used for the "disabled" view
        final double shadowWidth = PairGroup.BONDED_PAIR_DISTANCE + MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * 5;
        final float shadowHeight = MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * 10;
        final Geometry shadowQuad = new Geometry( "Disable shadow",
                                                  new Quad( (float) shadowWidth,
                                                            shadowHeight ) ) {{
            setLocalTranslation( 0, -shadowHeight / 2, MoleculeShapesConstants.MOLECULE_ATOM_RADIUS * 5 );
            setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
                MoleculeShapesColors.BACKGROUND.getRGBAProperty().addObserver( new SimpleObserver() {
                    public void update() {
                        ColorRGBA rgba = MoleculeShapesColors.BACKGROUND.getRGBA();
                        setColor( "Color", new ColorRGBA( rgba.r, rgba.g, rgba.b, 0.3f ) ); // TODO: improve alpha based on color? for projector, this doesn't look good
                    }
                } );

                // allow transparency
                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
            setQueueBucket( Bucket.Transparent );
        }};
        attachChild( shadowQuad );
        enabled.addObserver( new SimpleObserver() {
            public void update() {
                shadowQuad.setCullHint( enabled.get() ? CullHint.Always : CullHint.Never );
            }
        } );

    }
}
