// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo canvas for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EFACIntroCanvas extends PhetPCanvas {

    public static Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;
    public static double EDGE_INSET = 10;

    private final BooleanProperty showEnergyOfObjects = new BooleanProperty( false );

    /**
     * Constructor.
     *
     * @param model
     */
    public EFACIntroCanvas( final EFACIntroModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.82 ) ),
                2200 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( new Color( 245, 246, 247 ) );

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Create some PNodes that will act as layers in order to create the
        // needed Z-order behavior.
        PNode backLayer = new PNode();
        rootNode.addChild( backLayer );
        PNode blockLayer = new PNode();
        rootNode.addChild( blockLayer );
        PNode frontLayer = new PNode();
        rootNode.addChild( frontLayer );

        // Add the tool box for the thermometers.
        ControlPanelNode toolBoxNode = new ControlPanelNode( new PhetPPath( mvt.modelToView( model.getThermometerToolBox() ), new BasicStroke( 0 ), new Color( 0, 0, 0, 0 ) ),
                                                             Color.LIGHT_GRAY,
                                                             new BasicStroke( 2 ),
                                                             Color.BLACK,
                                                             0 ) {{
            PNode title = new PhetPText( "Tool Box", new PhetFont( 20, false ) );
            if ( title.getFullBoundsReference().width > getFullBoundsReference().width ) {
                title.setScale( getFullBoundsReference().width / title.getFullBoundsReference().width * 0.9 );
            }
            title.centerFullBoundsOnPoint( getCenterX(), title.getFullBoundsReference().height / 2 );
            addChild( title );
            setOffset( mvt.modelToView( model.getThermometerToolBox().getMinX(), model.getThermometerToolBox().getMaxY() ) );
        }};
        backLayer.addChild( toolBoxNode );

        // Add the control for showing/hiding object energy. TODO: i18n
        {
            PropertyCheckBox showEnergyCheckBox = new PropertyCheckBox( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                        "Show energy of objects",
                                                                        showEnergyOfObjects );
            showEnergyCheckBox.setFont( new PhetFont( 20 ) );
            backLayer.addChild( new ControlPanelNode( showEnergyCheckBox ) {{
                setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - EDGE_INSET, EDGE_INSET );
            }} );
        }

        // Add the lab bench surface.
        backLayer.addChild( new ShelfNode( model.getLabBenchSurface(), mvt ) );

        // Add the burners.
        backLayer.addChild( new BurnerNode( model.getLeftBurner(), mvt ) );
        backLayer.addChild( new BurnerNode( model.getRightBurner(), mvt ) );

        // Add the movable objects.
        final PNode brickNode = new BlockNode( model, model.getBrick(), mvt );
        blockLayer.addChild( brickNode );
        final PNode leadNode = new BlockNode( model, model.getLeadBlock(), mvt );
        blockLayer.addChild( leadNode );
        BeakerView beakerView = new BeakerView( model, this, mvt );
        frontLayer.addChild( beakerView.getFrontNode() );
        backLayer.addChild( beakerView.getBackNode() );

        // Add the thermometers.
        frontLayer.addChild( new ThermometerNode( model.getThermometer1(), mvt, toolBoxNode ) );
        frontLayer.addChild( new ThermometerNode( model.getThermometer2(), mvt, toolBoxNode ) );

        // Create an observer that updates the Z-order of the blocks when the
        // user controlled state changes.
        SimpleObserver blockChangeObserver = new SimpleObserver() {
            public void update() {
                if ( model.getLeadBlock().isStackedUpon( model.getBrick() ) ) {
                    brickNode.moveToBack();
                }
                else if ( model.getBrick().isStackedUpon( model.getLeadBlock() ) ) {
                    leadNode.moveToBack();
                }
                else if ( model.getLeadBlock().getRect().getMinX() >= model.getBrick().getRect().getMaxX() ||
                          model.getLeadBlock().getRect().getMinY() >= model.getBrick().getRect().getMaxY() ) {
                    leadNode.moveToFront();
                }
                else if ( model.getBrick().getRect().getMinX() >= model.getLeadBlock().getRect().getMaxX() ||
                          model.getBrick().getRect().getMinY() >= model.getLeadBlock().getRect().getMaxY() ) {
                    brickNode.moveToFront();
                }
            }
        };

        // Update the Z-order of the blocks whenever the "userControlled" state
        // of either changes.
        model.getBrick().position.addObserver( blockChangeObserver );
        model.getLeadBlock().position.addObserver( blockChangeObserver );
    }
}
