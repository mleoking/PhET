// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.*;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.jmolphet.JmolDialog;
import edu.colorado.phet.common.jmolphet.Molecule;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
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
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.jmolphet.JmolDialog.displayMolecule3D;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.RESOURCES;

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

    //Make it easy to enable/disable buckets for testing each way
    private boolean useBuckets = true;
    private BucketView saltBucket;
    private BucketView sugarBucket;
    private PNode saltBucketParticleLayer;
    private PNode sugarBucketParticleLayer;
    private JmolDialog jmolDialog;

    public WaterCanvas( final WaterModel waterModel, final GlobalState state ) {
        //Use the background color specified in the backgroundColor, since it is changeable in the developer menu
        state.colorScheme.backgroundColor.addObserver( new VoidFunction1<Color>() {
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
                useBuckets ? new PNode() :
                new HTMLImageButtonNode( "Add Salt" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.addSalt( new Point2D.Double( 0, waterModel.beakerWidth / 2 ) );
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
                useBuckets ? new PNode() :
                new HTMLImageButtonNode( "Add Sugar" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.addSugar( new Point2D.Double( 0, waterModel.beakerWidth / 2 ) );
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
                new PSwing( new PropertyCheckBox( SugarAndSaltSolutionsResources.Strings.SHOW_SUGAR_ATOMS, waterModel.showSugarAtoms ) {{
                    setFont( new PhetFont( 16 ) );
//                    waterModel.sugarMoleculeList.count.greaterThanOrEqualTo( 1 ).addObserver( new VoidFunction1<Boolean>() {
//                        public void apply( Boolean enabled ) {
//                            setEnabled( enabled );
//                        }
//                    } );
                }} ),

                //Allow the user to hide the water graphics
                //KL said "I do not think students should be able to hide water, since the whole point of the tab is what the water is doing."
                //But I'll leave this in just in case
//                new PSwing( new PropertyCheckBox( "Hide water", waterModel.hideWater ) {{
//                    setFont( new PhetFont( 16 ) );
//                }} ),

                //If development version, show button to launch developer controls
                state.config.isDev() ? new TextButtonNode( "Developer Controls" ) {{
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

                new TextButtonNode( SugarAndSaltSolutionsResources.Strings.SHOW_SUGAR_IN_3_D ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            if ( jmolDialog == null ) {
                                jmolDialog = displayMolecule3D( state.frame, new Molecule() {
                                                                    public String getDisplayName() {
                                                                        return SugarAndSaltSolutionsResources.Strings.SUGAR;
                                                                    }

                                                                    public int getCID() {
                                                                        return 5988;
                                                                    }

                                                                    public String getCmlData() {
                                                                        return readPDB();
                                                                    }

                                                                    public void fixJmolColors( JmolViewer viewer ) {
                                                                    }
                                                                }, "Space fill", "Ball and stick", "Loading..." );
                                jmolDialog.addWindowFocusListener( new WindowFocusListener() {
                                    public void windowGainedFocus( WindowEvent e ) {
                                    }

                                    public void windowLostFocus( WindowEvent e ) {
                                        jmolDialog.setVisible( false );
                                    }
                                } );
                            }
                            else {
                                jmolDialog.setVisible( true );
                            }
                        }
                    } );
                }},

                //Add a reset all button that resets this tab
                new HTMLImageButtonNode( "Reset All" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.reset();

                            //When the module is reset, but the salt and sugar back in the buckets
                            addSaltToBucket( waterModel, transform );
                            addSugarToBucket( waterModel, transform );

                            //When the module is reset, close the jmol dialog if it is open
                            if ( jmolDialog != null ) {
                                jmolDialog.setVisible( false );
                                jmolDialog = null;//Set it to null so that when it is opened again it will be in the startup location instead of saved location
                            }
                        }
                    } );
                }}
        ) ) {{
            setOffset( MacroCanvas.INSET, canvasSize.getHeight() - getFullBounds().getHeight() - MacroCanvas.INSET * 10 );
        }} );
        waterModel.k.trace( "k" );
        waterModel.pow.trace( "pow" );
        waterModel.randomness.trace( "randomness" );

        //Add a bucket with salt that can be dragged into the play area
        //The transform must have inverted Y so the bucket is upside-up.
        if ( useBuckets ) {
            final Rectangle referenceRect = new Rectangle( 0, 0, 1, 1 );

            saltBucket = new BucketView( new Bucket( new Point2D.Double( canvasSize.getWidth() / 2, -canvasSize.getHeight() + 115 ), new Dimension2DDouble( 200, 130 ), Color.blue, SugarAndSaltSolutionsResources.Strings.SALT ), ModelViewTransform.createRectangleInvertedYMapping( referenceRect, referenceRect ) );
            addChild( saltBucket.getHoleNode() );

            saltBucketParticleLayer = new PNode();
            addChild( saltBucketParticleLayer );
            addChild( saltBucket.getFrontNode() );

            addSaltToBucket( waterModel, transform );

            sugarBucket = new BucketView( new Bucket( new Point2D.Double( canvasSize.getWidth() / 2 + 210, -canvasSize.getHeight() + 115 ), new Dimension2DDouble( 200, 130 ), Color.green, SugarAndSaltSolutionsResources.Strings.SUGAR ), ModelViewTransform.createRectangleInvertedYMapping( referenceRect, referenceRect ) );
            addChild( sugarBucket.getHoleNode() );

            sugarBucketParticleLayer = new PNode();
            addChild( sugarBucketParticleLayer );
            addChild( sugarBucket.getFrontNode() );

            addSugarToBucket( waterModel, transform );
        }

        waterModel.addResetListener( new VoidFunction0() {
            public void apply() {
                addSaltToBucket( waterModel, transform );
                addSugarToBucket( waterModel, transform );
            }
        } );
    }

    private String readPDB() {
        try {
            BufferedReader structureReader = new BufferedReader( new InputStreamReader( RESOURCES.getResourceAsStream( "sucrose.pdb" ) ) );
            String s = "";
            String line = structureReader.readLine();
            while ( line != null ) {
                s = s + line + "\n";
                line = structureReader.readLine();
            }
            return s;
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private void addSaltToBucket( final WaterModel waterModel, final ModelViewTransform transform ) {
        saltBucketParticleLayer.removeAllChildren();
        saltBucketParticleLayer.addChild( new DraggableSaltCrystalNode( waterModel, transform, particleWindowNode ) {{
            centerFullBoundsOnPoint( saltBucket.getHoleNode().getFullBounds().getCenterX(), saltBucket.getHoleNode().getFullBounds().getCenterY() );
        }} );
    }

    private void addSugarToBucket( final WaterModel waterModel, final ModelViewTransform transform ) {
        sugarBucketParticleLayer.removeAllChildren();
        sugarBucketParticleLayer.addChild( new DraggableSugarCrystalNode( waterModel, transform, particleWindowNode, waterModel.showSugarAtoms ) {{
            centerFullBoundsOnPoint( sugarBucket.getHoleNode().getFullBounds().getCenterX(), sugarBucket.getHoleNode().getFullBounds().getCenterY() );
        }} );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}