// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
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

    public WaterCanvas( final WaterModel waterModel, final SugarAndSaltSolutionsColorScheme config ) {
        //Use the background color specified in the backgroundColor, since it is changeable in the developer menu
        config.backgroundColor.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                setBackground( config.backgroundColor.get() );
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
                new PSwing( new PropertyCheckBox( "Hide water", waterModel.hideWater ) {{
                    setFont( new PhetFont( 16 ) );
                }} ),

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
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}