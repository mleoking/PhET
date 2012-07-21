// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PDimension;

import static java.util.Arrays.asList;

/**
 * The node for sugar crystals that will be shown in the bucket that the user can grab.
 * This class uses a list of compounds such as sucrose molecules or salt ions so that it works uniformly for crystals or lone compounds.
 * It translates all particles together so they retain their crystal structure (if any)
 * <p/>
 * This class is built on:
 * Sucrose made of many SphericalParticles
 * or
 * SaltIon made of one SphericalParticle
 *
 * @author Sam Reid
 */
public class CompoundListNode<T extends Compound<SphericalParticle>> extends PNode {

    //Wrapped node that contains all the atoms
    protected final PNode atomLayer;

    //Transform from model (meters) to view (stage) coordinates
    private final ModelViewTransform transform;

    //The bucket, so the node can be moved back to the bucket hole
    private final BucketView bucketView;

    //The canvas this node will be displayed on, for purposes of converting global coordinates to stage for centering on the bucket
    private final WaterCanvas canvas;

    //The list of compounds such as sucrose molecules or salt ions
    private final T[] compounds;

    //Flag to keep track of whether the node was dragged from the bucket; if so, model elements will be created when dropped into the particle window
    public final Property<Boolean> inBucket = new Property<Boolean>( true );

    public CompoundListNode( final ModelViewTransform transform, final WaterModel model, BucketView bucketView, final PNode sugarBucketParticleLayer, final WaterCanvas canvas,

                             //Methods for adding or removing the molecule to/from the model, called when the user drops or grabs the PNode
                             final VoidFunction1<T> addToModel, final VoidFunction1<T> removeFromModel,

                             //Flag to indicate whether color is shown for charge or identity of the atom.  This is also used for the "show sugar atoms" feature
                             ObservableProperty<Boolean> showChargeColor,

                             //Optional label to show for each compound
                             final Option<Function1<T, PNode>> label,

                             //Flag to indicate whether the user can drag the compound from the particle frame back to the bucket
                             //Sucrose molecules can be dragged to the bucket but salt ions can't since you shouldn't have a lone ion.
                             //Likewise, fully formed NaCl crystals can be dragged from the bucket to the bucket, since that wouldn't leave any lone ions.
                             final boolean canReturnToBucket,

                             //Flag to indicate whether partial charge symbols should be shown.  In this sim, they are optional for sucrose
                             final ObservableProperty<Boolean> showPartialCharges,

                             //Whether the sim is running so that interaction can be disabled when the sim is paused
                             final ObservableProperty<Boolean> clockRunning,

                             final T... compounds ) {
        this.transform = transform;
        this.bucketView = bucketView;
        this.canvas = canvas;
        this.compounds = compounds;

        atomLayer = new PNode();

        //Flag to determine whether the user dragged the crystal out of the bucket; if so, it:
        //1. moves into the top layer (instead of between the buckets), so it doesn't look like it is sandwiched between the front and back of the bucket layers
        //2. grows larger
        final Property<Boolean> startedDragging = new Property<Boolean>( false );

        //Transform the particles from the crystal's molecule's particles into nodes
        for ( T compound : compounds ) {
            final PNode compoundNode = new PNode();
            for ( SphericalParticle atom : compound ) {
                compoundNode.addChild( new SphericalParticleNodeWithText( transform, atom, showChargeColor, showPartialCharges ) );
            }

            //If a label was specified, create and add it centered on the compound
            if ( label.isSome() ) {
                final PNode labelNode = label.get().apply( compound );
                compoundNode.addChild( labelNode );

                final PropertyChangeListener listener = new PropertyChangeListener() {
                    public void propertyChange( PropertyChangeEvent evt ) {
                        //Remove the label before determining the bounds on which it should be centered, so it isn't accounted for in the bounds
                        compoundNode.removeChild( labelNode );

                        //Determine where to center the label
                        final Point2D compoundCenter = compoundNode.getFullBounds().getCenter2D();

                        //Add back the label
                        compoundNode.addChild( labelNode );

                        //Center it on the compound
                        labelNode.centerFullBoundsOnPoint( compoundCenter.getX(), compoundCenter.getY() );
                    }
                };
                compoundNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, listener );

                //Get the initial location correct
                listener.propertyChange( null );
            }
            atomLayer.addChild( compoundNode );
        }

        final CursorHandler listener1 = new CursorHandler();
        final PBasicInputEventHandler listener2 = new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {

                //When dragging, remove from the model (if it was in the model) so box2d won't continue to propagate it
                for ( T sucrose : compounds ) {
                    removeFromModel.apply( sucrose );
                }

                //When the user drags the node initially, grow it to full size and move it to the top layer
                if ( !startedDragging.get() ) {
                    startedDragging.set( true );
                    setIcon( false );
                    sugarBucketParticleLayer.removeChild( CompoundListNode.this );
                    canvas.addChild( CompoundListNode.this );

                    //Re-center the node since it will have a different location at its full scale
                    if ( inBucket.get() ) {
                        moveToBucket();
                        inBucket.set( false );
                    }
                }

                //Translate the node during the drag
                final Dimension2D modelDelta = transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
                for ( T compound : compounds ) {
                    compound.translate( modelDelta.getWidth(), modelDelta.getHeight() );
                }
            }

            //If contained within the particle window, drop it there and create it in the model, otherwise return to the sugar bucket
            @Override public void mouseReleased( PInputEvent event ) {
                Rectangle2D modelBounds = transform.viewToModel( atomLayer.getFullBounds() ).getBounds2D();
                if ( model.particleWindow.contains( modelBounds ) ) {
                    moveToModel( addToModel );
                }
                else {

                    //Shrink the node and send it back to the bucket
                    if ( canReturnToBucket ) {
                        setIcon( true );
                        moveToBucket();
                        inBucket.set( true );
                        canvas.removeChild( CompoundListNode.this );
                        sugarBucketParticleLayer.addChild( CompoundListNode.this );

                        //Initialize for dragging out of the bucket on next mouse press
                        startedDragging.set( false );

                    }

                    //For salt ions, send back to the play area
                    else {
                        for ( T compound : compounds ) {
                            compound.setPosition( model.particleWindow.getCenter() );
                        }
                        moveToModel( addToModel );
                    }
                }
            }
        };

        //Not allowed to drag when the sim is paused
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean clockRunning ) {

                if ( clockRunning && !containsListener( listener1 ) ) {
                    addInputEventListener( listener1 );
                    addInputEventListener( listener2 );
                }
                else if ( !clockRunning && containsListener( listener1 ) ) {
                    removeInputEventListener( listener1 );
                    removeInputEventListener( listener2 );
                }

            }
        } );

        addChild( atomLayer );

        //By default, this node is used for the bucket, so it should start in small icon mode
        setIcon( true );
    }

    //Determine if this node contains the specified listener for purposes of toggling interaction when sim is paused
    private boolean containsListener( PInputEventListener listener ) {
        return asList( getInputEventListeners() ).contains( listener );
    }

    private void moveToModel( VoidFunction1<T> addToModel ) {
        //Add each sucrose molecule to the model
        for ( T sucrose : compounds ) {
            addToModel.apply( sucrose );
        }

        //Remove the node the user was dragging
        canvas.removeChild( this );
    }

    //Sets whether this node should be shown as a small icon (for use in the bucket) or shown as a large crystal while the user is dragging or while in the model/play area
    public void setIcon( boolean icon ) {

        //Shrink it to be a small icon version so it will fit in the bucket
        atomLayer.setScale( icon ? 0.45 : 1 );
    }

    //Center the node in the bucket, must be called after scaling and attaching to the parent.
    public void moveToBucket() {

        //Find out how far to translate the compounds to center on the top middle of the bucket hole
        Point2D crystalCenter = atomLayer.getGlobalFullBounds().getCenter2D();
        Point2D topCenterOfBucketHole = new Point2D.Double( bucketView.getHoleNode().getGlobalFullBounds().getCenterX(), bucketView.getHoleNode().getGlobalFullBounds().getMinY() );
        Vector2D globalDelta = new Vector2D( crystalCenter, topCenterOfBucketHole );

        //Convert to canvas coordinate frame (stage) to account for different frame sizes
        Dimension2D canvasDelta = canvas.getRootNode().globalToLocal( new PDimension( globalDelta.getX(), globalDelta.getY() ) );

        //Convert to model and update the compound model positions
        final Vector2D modelDelta = transform.viewToModelDelta( new Vector2D( canvasDelta.getWidth(), canvasDelta.getHeight() ).times( 1.0 / atomLayer.getScale() ) );
        for ( T compound : compounds ) {
            compound.translate( modelDelta );
        }
    }

    public void setInBucket( boolean inBucket ) {
        this.inBucket.set( inBucket );
    }
}