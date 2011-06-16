// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.GlobalSettings;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteControlNode;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;

/**
 * Canvas for the Water tab
 *
 * @author Sam Reid
 */
public class WaterCanvas extends PhetPCanvas {

    //Default size of the canvas.  Sampled at runtime on a large res screen from a sim with multiple tabs
    public static final Dimension canvasSize = new Dimension( 1008, 676 );

    //Where the content is shown
    private PNode rootNode = new PNode();

    //Separate layer for the particles so they are always behind the control panel
    private ParticleWindowNode particleWindowNode;

    public WaterCanvas( final WaterModel waterModel, final GlobalSettings settings ) {
        //Use the background color specified in the backgroundColor, since it is changeable in the developer menu
        settings.colorScheme.backgroundColor.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                setBackground( color );
            }
        } );

        //Set the stage size according to the same aspect ratio as used in the model

        //Gets the ModelViewTransform used to go between model coordinates (SI) and stage coordinates (roughly pixels)
        //Create the transform from model (SI) to view (stage) coordinates
        double inset = 40;
        final ModelViewTransform transform = createRectangleInvertedYMapping( new Rectangle2D.Double( -waterModel.beakerWidth / 2, 0, waterModel.beakerWidth, waterModel.beakerHeight ),
                                                                              new Rectangle2D.Double( -inset, -inset, canvasSize.getWidth() + inset * 2, canvasSize.getHeight() + inset * 2 ) );

        // Root of our scene graph
        addWorldChild( rootNode );

        //Add the region with the particles
        particleWindowNode = new ParticleWindowNode( waterModel, transform ) {{
            setOffset( canvasSize.getWidth() - getFullBounds().getWidth() - 50, 0 );
        }};
        rootNode.addChild( particleWindowNode );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, canvasSize ) );

        final MiniBeakerNode miniBeakerNode = new MiniBeakerNode() {{
            translate( 0, 300 );
        }};
        addChild( miniBeakerNode );

        //Show a graphic that shows the particle frame to be a zoomed in part of the mini beaker
        addChild( new ZoomIndicatorNode( miniBeakerNode, particleWindowNode ) );

        //Control panel
        addChild( new ControlPanelNode( new VBox(

                //button to add a salt
                new HTMLImageButtonNode( "Add Salt" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.addSalt();
                        }
                    } );

                    //disable the "add salt" button if there are already 2 salts
                    waterModel.sodiumList.count.lessThan( 2 ).addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean lessThanTwoSodiums ) {
                            setEnabled( lessThanTwoSodiums );
                        }
                    } );
                }},

                //button to add a sugar
                new HTMLImageButtonNode( "Add Sugar" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.addSugar();
                        }
                    } );

                    //disable the "add sugar" button if there are already 2 sugars
                    waterModel.sugarMoleculeList.count.lessThan( 2 ).addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean lessThanTwoSugars ) {
                            setEnabled( lessThanTwoSugars );
                        }
                    } );
                }},

                //Allow the user to show individual atoms within the sugar molecule, but only if a sugar molecule is in the scene
                new PSwing( new PropertyCheckBox( "Show sugar atoms", waterModel.showSugarAtoms ) {{
                    setFont( new PhetFont( 16 ) );
                    waterModel.sugarMoleculeList.count.greaterThanOrEqualTo( 1 ).addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean enabled ) {
                            setEnabled( enabled );
                        }
                    } );
                }} ),

                //Allow the user to hide the water graphics
                //KL said "I do not think students should be able to hide water, since the whole point of the tab is what the water is doing."
                //But I'll leave this in just in case
//                new PSwing( new PropertyCheckBox( "Hide water", waterModel.hideWater ) {{
//                    setFont( new PhetFont( 16 ) );
//                }} ),

                //If development version, show button to launch developer controls
                settings.config.isDev() ? new TextButtonNode( "Developer Controls" ) {{
                    addActionListener( new ActionListener() {
                        DeveloperControlDialog dialog = null;

                        public void actionPerformed( ActionEvent e ) {
                            if ( dialog == null ) {
                                dialog = new DeveloperControlDialog( SwingUtilities.getWindowAncestor( WaterCanvas.this ), waterModel );
                                SwingUtils.centerInParent( dialog );
                            }
                            dialog.setVisible( true );
                        }
                    } );
                }} : new PNode(),

                //Add a reset all button that resets this tab
                new HTMLImageButtonNode( "Reset All" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.reset();
                        }
                    } );
                }}
        ) ) {{
            setOffset( MacroCanvas.INSET, canvasSize.getHeight() - getFullBounds().getHeight() - MacroCanvas.INSET );
        }} );
        waterModel.k.trace( "k" );
        waterModel.pow.trace( "pow" );
        waterModel.randomness.trace( "randomness" );

        //Add a bucket with salt that can be dragged into the play area
        //The transform must have inverted Y so the bucket is upside-up.
        final Rectangle referenceRect = new Rectangle( 0, 0, 1, 1 );
        final BucketView bucketView = new BucketView( new Bucket( new Point2D.Double( canvasSize.getWidth() / 2, -canvasSize.getHeight() + 115 ), new Dimension2DDouble( 200, 130 ), Color.blue, "Salt" ), ModelViewTransform.createRectangleInvertedYMapping( referenceRect, referenceRect ) );
        addChild( bucketView.getHoleNode() );
        addChild( new DraggableSaltCrystalNode( waterModel, transform, particleWindowNode ) {{
            centerFullBoundsOnPoint( bucketView.getHoleNode().getFullBounds().getCenterX(), bucketView.getHoleNode().getFullBounds().getCenterY() );
        }} );
        addChild( bucketView.getFrontNode() );

        //Add the "remove salt and sugar" buttons
        addChild( new RemoveSoluteControlNode( waterModel ) {{
            setOffset( particleWindowNode.getFullBounds().getMaxX() - getFullBounds().getWidth() - MacroCanvas.INSET, particleWindowNode.getFullBounds().getMaxY() - getFullBounds().getHeight() - MacroCanvas.INSET );
        }} );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}