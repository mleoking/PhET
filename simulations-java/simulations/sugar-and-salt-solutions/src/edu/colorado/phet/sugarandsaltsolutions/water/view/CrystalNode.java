// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.SphericalParticleNode;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * The node for sugar crystals that will be shown in the bucket that the user can grab.
 * This class requires a crystal type so that it can work crystals or molecules (modeled as crystals with 1 molecule)
 * or with particles (modeled as molecules with one atom in crystals with one molecule).
 * <p/>
 * This class is built on:
 * SucroseCrystal made of Sucrose made of many SphericalParticles
 * or
 * SodiumChlorideCrystal made of SaltIon made of one SphericalParticle
 *
 * @author Sam Reid
 */
public class CrystalNode<CompoundType extends Compound<SphericalParticle>, CrystalType extends Crystal<CompoundType>> extends PNode {
    protected PNode crystalNode;
    private ModelViewTransform transform;
    private BucketView bucketNode;
    private CrystalType crystal;
    private boolean inBucket = true;

    public CrystalNode( final ModelViewTransform transform, final WaterModel model, BucketView bucketNode, final PNode sugarBucketParticleLayer, final WaterCanvas canvas, final CrystalType crystal,

                        //Methods for adding or removing the molecule to/from the model, called when the user drops or grabs the pnode
                        final VoidFunction1<CompoundType> addToModel, final VoidFunction1<CompoundType> removeFromModel,

                        //Flag to indicate whether color is shown for charge or identity of the atom.  This is also used for the "show sugar atoms" feature
                        ObservableProperty<Boolean> showChargeColor ) {
        this.transform = transform;
        this.bucketNode = bucketNode;
        this.crystal = crystal;

        crystalNode = new PNode();

        //Flag to determine whether the user dragged the crystal out of the bucket; if so, it:
        //1. moves into the top layer (instead of between the buckets), so it doesn't look like it is sandwiched between the front and back of the bucket layers
        //2. grows larger
        final Property<Boolean> startedDragging = new Property<Boolean>( false );

        //Transform the particles from the crystal's molecule's particles into nodes
        for ( CompoundType compound : crystal ) {
            for ( SphericalParticle atom : compound ) {
                crystalNode.addChild( new SphericalParticleNode( transform, atom, showChargeColor ) );
            }
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {

                //When dragging, remove from the model (if it was in the model) so box2d won't continue to propagate it
                for ( CompoundType sucrose : crystal ) {
                    removeFromModel.apply( sucrose );
                }

                //When the user drags the node initially, grow it to full size and move it to the top layer
                if ( !startedDragging.get() ) {
                    startedDragging.set( true );
                    setIcon( false );
                    sugarBucketParticleLayer.removeChild( CrystalNode.this );
                    canvas.addChild( CrystalNode.this );

                    //Re-center the node since it will have a different location at its full scale
                    if ( inBucket ) {
                        centerInBucket();
                        inBucket = false;
                    }
                }

                //Translate the node during the drag
                final Dimension2D modelDelta = transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
                crystal.translate( modelDelta.getWidth(), modelDelta.getHeight() );
            }

            //If contained within the particle window, drop it there and create it in the model, otherwise return to the sugar bucket
            @Override public void mouseReleased( PInputEvent event ) {
                Rectangle2D modelBounds = transform.viewToModel( crystalNode.getFullBounds() ).getBounds2D();
                if ( model.particleWindow.contains( modelBounds ) ) {

                    //Add each sucrose molecule to the model
                    for ( CompoundType sucrose : crystal ) {
                        addToModel.apply( sucrose );
                    }

                    //Remove the node the user was dragging
                    canvas.removeChild( CrystalNode.this );
                }
                else {

                    //Shrink the node and send it back to the bucket
                    setIcon( true );
                    centerInBucket();
                    inBucket = true;
                    canvas.removeChild( CrystalNode.this );
                    sugarBucketParticleLayer.addChild( CrystalNode.this );

                    //Initialize for dragging out of the bucket on next mouse press
                    startedDragging.set( false );
                }
            }
        } );

        addChild( crystalNode );

        //By default, this node is used for the bucket, so it should start in small icon mode
        setIcon( true );
    }


    //Sets whether this node should be shown as a small icon (for use in the bucket) or shown as a large crystal while the user is dragging or while in the model/play area
    public void setIcon( boolean icon ) {
        //Shrink it to be a small icon version so it will fit in the bucket
        crystalNode.setScale( icon ? 0.36 : 1 );
    }

    //Center the node in the bucket, must be called after scaling and attaching to the parent.
    public void centerInBucket() {
        Point2D crystalCenter = crystalNode.getGlobalFullBounds().getCenter2D();
        Point2D bucketCenter = bucketNode.getHoleNode().getGlobalFullBounds().getCenter2D();
        crystal.translate( transform.viewToModelDelta( new ImmutableVector2D( crystalCenter, bucketCenter ).times( 1.0 / crystalNode.getScale() ) ) );
    }

    public void setInBucket( boolean inBucket ) {
        this.inBucket = inBucket;
    }
}