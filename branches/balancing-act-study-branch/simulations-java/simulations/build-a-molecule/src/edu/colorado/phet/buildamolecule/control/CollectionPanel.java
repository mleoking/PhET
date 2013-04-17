//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.CompositeLayoutMethod;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.HorizontalAlignMethod;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.HorizontalAlignMethod.Align;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.LayoutMethod;
import edu.colorado.phet.buildamolecule.control.GeneralLayoutNode.VerticalLayoutMethod;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.CollectionList.Adapter;
import edu.colorado.phet.buildamolecule.model.CollectionList.Listener;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.NextPreviousNavigationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.*;

/**
 * A panel that shows collection areas for different collections, and allows switching between those collections
 */
public class CollectionPanel extends PNode {

    public static final int CONTAINER_PADDING = 15;
    private GeneralLayoutNode layoutNode = new GeneralLayoutNode();
    private final PNode collectionAreaHolder = new PNode();
    private final PNode backgroundHolder = new PNode();

    private final Map<KitCollection, CollectionAreaNode> collectionAreaMap = new HashMap<KitCollection, CollectionAreaNode>();
    private VoidFunction1<SimpleObserver> addCollectionAttachmentListener;

    /**
     * Constructs a collection area panel
     *
     * @param collectionList       List of collections to handle (mutable)
     * @param singleCollectionMode Whether we use single or multiple style collection boxes
     * @param addCollectionAttachmentListener
     *                             Function to add an attachment listener
     * @param toModelBounds        Function to compute model coordinates from a PNode in the view
     */
    public CollectionPanel( final CollectionList collectionList, final boolean singleCollectionMode, VoidFunction1<SimpleObserver> addCollectionAttachmentListener, final Function1<PNode, Rectangle2D> toModelBounds ) {
        this.addCollectionAttachmentListener = addCollectionAttachmentListener;

        LayoutMethod method = new CompositeLayoutMethod( new VerticalLayoutMethod(), new HorizontalAlignMethod( Align.Centered ) );

        // move it over so the background will have padding
        layoutNode.translate( CONTAINER_PADDING, CONTAINER_PADDING );

        // "Your Molecule Collection"
        layoutNode.addChild( new HTMLNode( BuildAMoleculeStrings.COLLECTION_AREA_YOUR_MOLECULE_COLLECTION ) {{
                                 setFont( new PhetFont( 22 ) );
                             }}, method, 0, 0, 5, 0 );

        // "Collection X" with arrows
        layoutNode.addChild( new NextPreviousNavigationNode( new PText() {{
                                 collectionList.currentCollection.addObserver( new SimpleObserver() {
                                     public void update() {
                                         setFont( new PhetFont( 16, true ) );
                                         setText( MessageFormat.format( BuildAMoleculeStrings.COLLECTION_LABEL, collectionList.getCurrentIndex() + 1 ) );
                                     }
                                 } );
                             }}, Color.YELLOW, Color.BLACK, 14, 18 ) {
                                 {
                                     // update when the collection stuff might change.
                                     final SimpleObserver updater = new SimpleObserver() {
                                         public void update() {
                                             hasNext.set( collectionList.hasNextCollection() );
                                             hasPrevious.set( collectionList.hasPreviousCollection() );
                                         }
                                     };
                                     collectionList.currentCollection.addObserver( updater );
                                     collectionList.addListener( new Listener() {
                                         public void addedCollection( KitCollection collection ) {
                                             updater.update();
                                         }

                                         public void removedCollection( KitCollection collection ) {
                                             updater.update();
                                         }
                                     } );
                                 }

                                 @Override protected void next() {
                                     collectionList.switchToNextCollection();
                                 }

                                 @Override protected void previous() {
                                     collectionList.switchToPreviousCollection();
                                 }
                             }, method, 0, 0, 10, 0 );

        // all of the collection boxes themselves
        layoutNode.addChild( collectionAreaHolder, method, 0, 0, 5, 0 );

        // sound on/off
        layoutNode.addChild( new SoundOnOffNode(), method );


        // add our two layers: background and controls
        addChild( backgroundHolder );
        addChild( layoutNode );

        // anonymous function here, so we don't create a bunch of fields
        final VoidFunction1<KitCollection> createCollectionNode = new VoidFunction1<KitCollection>() {
            public void apply( KitCollection collection ) {
                collectionAreaMap.put( collection, new CollectionAreaNode( collection, singleCollectionMode, toModelBounds ) );
            }
        };

        // create nodes for all current collections
        for ( KitCollection collection : collectionList.getCollections() ) {
            createCollectionNode.apply( collection );
        }

        // if a new collection is added, create one for it
        collectionList.addListener( new Adapter() {
            public void addedCollection( KitCollection collection ) {
                createCollectionNode.apply( collection );
            }
        } );

        // use the current collection
        useCollection( collectionList.currentCollection.get() );

        collectionList.currentCollection.addObserver( new ChangeObserver<KitCollection>() {
            public void update( KitCollection newCollection, KitCollection oldCollection ) {
                useCollection( newCollection );
            }
        } );
    }

    public void useCollection( KitCollection collection ) {
        // swap out the inner collection area
        collectionAreaHolder.removeAllChildren();
        final CollectionAreaNode collectionAreaNode = collectionAreaMap.get( collection );
        collectionAreaHolder.addChild( collectionAreaNode );

        layoutNode.updateLayout();

        // if we are hooked up, update the box locations. otherwise, listen to the canvas for when it is
        if ( hasCanvasAsParent() ) {
            collectionAreaNode.updateCollectionBoxLocations();
        }
        else {
            // we need to listen for this because the update needs to use canvas' global/local/view coordinate transformations
            addCollectionAttachmentListener.apply( new SimpleObserver() {
                public void update() {
                    collectionAreaNode.updateCollectionBoxLocations();
                }
            } );
        }

        /*---------------------------------------------------------------------------*
        * draw new background
        *----------------------------------------------------------------------------*/
        backgroundHolder.removeAllChildren();
        PPath background = PPath.createRectangle( 0, 0, (float) getPlacementWidth(), (float) getPlacementHeight() );
        background.setPaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BACKGROUND );
        background.setStrokePaint( BuildAMoleculeConstants.MOLECULE_COLLECTION_BORDER );
        backgroundHolder.addChild( background );
    }

    /**
     * Walk up the scene graph, looking to see if we are a (grand)child of a canvas
     *
     * @return If an ancestor is a BuildAMoleculeCanvas
     */
    private boolean hasCanvasAsParent() {
        PNode node = this;
        while ( node.getParent() != null ) {
            node = node.getParent();
            if ( node instanceof PhetRootPNode ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used to get the panel width so that we can construct the model (and thus kit) beforehand
     *
     * @param singleCollectionMode Whether we are on single (1st tab) or multiple (2nd tab) mode
     * @return Width of the entire collection panel
     */
    public static double getCollectionPanelModelWidth( boolean singleCollectionMode ) {
        // construct a dummy collection panel and check its width
        CollectionPanel collectionPanel = new CollectionPanel( new CollectionList( new KitCollection() {{
            addCollectionBox( new CollectionBox( MoleculeList.H2O, 1 ) ); // collection box so it gets the width correctly
        }}, new LayoutBounds( false, 0 ) ), singleCollectionMode, new VoidFunction1<SimpleObserver>() {
            public void apply( SimpleObserver simpleObserver ) {
            }
        }, new Function1<PNode, Rectangle2D>() {
            public Rectangle2D apply( PNode pNode ) {
                return null;
            }
        }
        );
        double result = MODEL_VIEW_TRANSFORM.viewToModelDeltaX( collectionPanel.getFullBounds().getWidth() );
        return result;
    }

    private double getPlacementWidth() {
        return layoutNode.getFullBounds().getWidth() + CONTAINER_PADDING * 2;
    }

    private double getPlacementHeight() {
        // how much height we need with proper padding
        double requiredHeight = layoutNode.getLayoutBounds().getHeight() + CONTAINER_PADDING * 2;

        // how much height we will take up to fit our vertical size perfectly
        double fixedHeight = STAGE_SIZE.getHeight() - VIEW_PADDING * 2; // we will have padding above and below

        if ( requiredHeight > fixedHeight ) {
            System.out.println( "Warning: collection panel is too tall. required: " + requiredHeight + ", but has: " + fixedHeight );
        }

        return fixedHeight;
    }
}
