// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.KitPanel;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Common canvas for Build a Molecule. It features kits shown at the bottom. Can be extended to add other parts
 * TODO: handle changing kits
 */
public class BuildAMoleculeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Frame parentFrame;

    // Model
    private final Property<KitCollectionModel> modelProperty;
    private Map<Kit, KitView> kitMap = new HashMap<Kit, KitView>(); // so we can pull our kit view for a particular kit

    // TODO: (possible) separate canvases for different kits?

    // View
    private final PNode _rootNode;

    // Model-View transform.
    private final ModelViewTransform mvt;

    private final PNode bottomLayer = new PNode();
    private final PNode metadataLayer = new PNode();
    private final PNode atomLayer = new PNode();
    private final PNode topLayer = new PNode();

    private List<SimpleObserver> fullyLayedOutObservers = new LinkedList<SimpleObserver>();

    protected boolean singleCollectionMode; // TODO: find solution for LargerMoleculesCanvas so that we don't need this boolean and the separate constructor

    protected void addChildren() {

    }

    public BuildAMoleculeCanvas( Frame parentFrame, KitCollectionModel initialModel ) {
        this( parentFrame, initialModel, true );
    }

    public BuildAMoleculeCanvas( Frame parentFrame, KitCollectionModel initialModel, boolean singleCollectionMode ) {
        this.singleCollectionMode = singleCollectionMode;
        this.parentFrame = parentFrame;

        modelProperty = new Property<KitCollectionModel>( initialModel );

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAMoleculeConstants.STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAMoleculeConstants.STAGE_SIZE.width * 0.5 ),
                           (int) Math.round( BuildAMoleculeConstants.STAGE_SIZE.height * 0.5 ) ),
                0.3 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BuildAMoleculeConstants.CANVAS_BACKGROUND_COLOR );

//        // TODO: Temp - add an image that represents the tab.
//        PNode tempImage = new PImage( BuildAMoleculeResources.getImage( "tab-1-temp-sketch.png" ) );
//        addWorldChild( tempImage );

        addChildren();

        addWorldChild( bottomLayer );
        addWorldChild( atomLayer );
        addWorldChild( metadataLayer );
        addWorldChild( topLayer );

//        PNode locationTestNode = new PhetPPath( new Rectangle2D.Double(-20, -20, 40, 40), Color.PINK );
//        locationTestNode.setOffset( mvt.modelToView( new Point2D.Double(0, 0) ) );
//        addWorldChild( locationTestNode );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        buildFromModel();

        for ( SimpleObserver observer : fullyLayedOutObservers ) {
            observer.update();
        }
    }

    protected void buildFromModel() {
        // bottom-most layer is our kit panel
        bottomLayer.addChild( new KitPanel( getModel(), mvt ) );

        for ( final Kit kit : getModel().getKits() ) {
            KitView kitView = new KitView( parentFrame, kit, mvt );
            kitMap.put( kit, kitView );
            bottomLayer.addChild( kitView.getBottomLayer() );
            atomLayer.addChild( kitView.getAtomLayer() );
            metadataLayer.addChild( kitView.getMetadataLayer() );
            topLayer.addChild( kitView.getTopLayer() );
        }
    }

    // TODO: add destruct so we can switch models

    public KitCollectionModel getModel() {
        return modelProperty.getValue();
    }

    public ModelViewTransform getModelViewTransform() {
        return mvt;
    }

    public void addFullyLayedOutObserver( SimpleObserver observer ) {
        fullyLayedOutObservers.add( observer );
    }

    /*
    * Updates the layout of stuff on the canvas.
    */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        //XXX lay out nodes
    }
}
