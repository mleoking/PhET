// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.SphericalParticleNode;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;

/**
 * The node for sugar crystals that will be shown in the bucket that the user can grab
 * TODO: can this class be reused for rendering sucroses in the model?
 *
 * @author Sam Reid
 */
public class SucroseCrystalNode extends PNode {

    protected PNode crystalNode;
    private ModelViewTransform transform;
    private BucketView sugarBucket;
    private SucroseCrystal crystal;
    private boolean inBucket = true;

    public SucroseCrystalNode( final ModelViewTransform transform, final WaterModel model, BucketView sugarBucket, final PNode sugarBucketParticleLayer, final WaterCanvas canvas, final SucroseCrystal crystal ) {
        this.transform = transform;
        this.sugarBucket = sugarBucket;
        this.crystal = crystal;

        crystalNode = new PNode();

        //Flag to determine whether the user dragged the crystal out of the bucket; if so, it:
        //1. moves into the top layer (instead of between the buckets), so it doesn't look like it is sandwiched between the front and back of the bucket layers
        //2. grows larger
        final Property<Boolean> startedDragging = new Property<Boolean>( false );

        //Transform the particles from the crystal's molecule's particles into nodes
        for ( Sucrose sucrose : crystal ) {
            for ( SphericalParticle atom : sucrose ) {
                crystalNode.addChild( new SphericalParticleNode( transform, atom, not( model.showSugarAtoms ) ) );
            }
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {

                //When dragging, remove from the model (if it was in the model) so box2d won't continue to propagate it
                for ( Sucrose sucrose : crystal ) {
                    model.removeSucrose( sucrose );
                }

                //When the user drags the node initially, grow it to full size and move it to the top layer
                if ( !startedDragging.get() ) {
                    startedDragging.set( true );
                    setIcon( false );
                    sugarBucketParticleLayer.removeChild( SucroseCrystalNode.this );
                    canvas.addChild( SucroseCrystalNode.this );

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
                    for ( Sucrose sucrose : crystal ) {
                        model.addSucroseMolecule( sucrose );
                    }

                    //Remove the node the user was dragging
                    canvas.removeChild( SucroseCrystalNode.this );
                }
                else {

                    //Shrink the node and send it back to the bucket
                    setIcon( true );
                    centerInBucket();
                    inBucket = true;
                    canvas.removeChild( SucroseCrystalNode.this );
                    sugarBucketParticleLayer.addChild( SucroseCrystalNode.this );

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
        crystalNode.setScale( icon ? 0.4 : 1 );
    }

    //Center the node in the bucket, must be called after scaling and attaching to the parent.
    public void centerInBucket() {
        Point2D crystalCenter = crystalNode.getGlobalFullBounds().getCenter2D();
        Point2D bucketCenter = sugarBucket.getHoleNode().getGlobalFullBounds().getCenter2D();
        crystal.translate( transform.viewToModelDelta( new ImmutableVector2D( crystalCenter, bucketCenter ).times( 1.0 / crystalNode.getScale() ) ) );
    }

    public void setInBucket( boolean inBucket ) {
        this.inBucket = inBucket;
    }
}