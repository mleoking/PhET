// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.view.PropertySlider;
import edu.colorado.phet.sugarandsaltsolutions.water.MicroscopicModel.Barrier;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;

/**
 * Canvas for the Microscopic tab
 *
 * @author Sam Reid
 */
public class MicroscopicCanvas extends PhetPCanvas {

    //Default size of the canvas.  Sampled at runtime on a large res screen from a sim with multiple tabs
    public static final Dimension canvasSize = new Dimension( 1008, 676 );

    //Where the content is shown
    private PNode rootNode;

    public MicroscopicCanvas( final MicroscopicModel model, final SugarAndSaltSolutionsColorScheme config ) {
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
        final ModelViewTransform transform = createRectangleInvertedYMapping( new Rectangle2D.Double( -model.beakerWidth / 2, 0, model.beakerWidth, model.beakerHeight ),
                                                                              new Rectangle2D.Double( -inset, -inset, canvasSize.getWidth() + inset * 2, canvasSize.getHeight() + inset * 2 ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        //Adapter method for wiring up components to listen to when the model updates
        final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 listener ) {
                model.addFrameListener( listener );
            }
        };

        //Provide graphics for WaterMolecules
        new GraphicAdapter<WaterMolecule>( rootNode, new Function1<WaterMolecule, PNode>() {
            public PNode apply( WaterMolecule waterMolecule ) {
                return new WaterMoleculeNode( transform, waterMolecule, addFrameListener );
            }
        }, model.getWaterList(), new VoidFunction1<VoidFunction1<WaterMolecule>>() {
            public void apply( VoidFunction1<WaterMolecule> createNode ) {
                model.addWaterAddedListener( createNode );
            }
        }
        );

        //Provide graphics for SodiumIons
        new GraphicAdapter<DefaultParticle>( rootNode, new Function1<DefaultParticle, PNode>() {
            public PNode apply( DefaultParticle sodiumIon ) {
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, S3Element.NaIon );
            }
        }, model.getSodiumIonList(), new VoidFunction1<VoidFunction1<DefaultParticle>>() {
            public void apply( VoidFunction1<DefaultParticle> createNode ) {
                model.addSodiumIonAddedListener( createNode );
            }
        }
        );

        //Provide graphics for Chlorine Ions
        new GraphicAdapter<DefaultParticle>( rootNode, new Function1<DefaultParticle, PNode>() {
            public PNode apply( DefaultParticle sodiumIon ) {
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, S3Element.ClIon );
            }
        }, model.getChlorineIonList(), new VoidFunction1<VoidFunction1<DefaultParticle>>() {
            public void apply( VoidFunction1<DefaultParticle> createNode ) {
                model.addChlorineIonAddedListener( createNode );
            }
        }
        );

//        addChild( new BarrierNode( transform, model.bottomWall ) );
//        addChild( new BarrierNode( transform, model.leftWall ) );
//        addChild( new BarrierNode( transform, model.rightWall ) );

        //Gets a random number within the horizontal range of the beaker
        final Function0<Float> randomX = new Function0<Float>() {
            public Float apply() {
                return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * model.beakerWidth - model.beakerWidth / 2 );
            }
        };

        //Gets a random number within the vertical range of the beaker
        final Function0<Float> randomY = new Function0<Float>() {
            public Float apply() {
                return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * model.beakerHeight );
            }
        };

        //Control panel
        addChild( new ControlPanelNode( new VBox(
                //Add a reset all button that resets this tab
                new HTMLImageButtonNode( "Reset All" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.reset();
                        }
                    } );
                }},
                //Button to add a single sodium ion
                new HTMLImageButtonNode( "Add Sodium Ion" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSodiumIon( randomX.apply(), model.beakerHeight );
                        }
                    } );
                }},
                //Button to add a chlorine icon
                new HTMLImageButtonNode( "Add Chlorine Ion" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addChlorineIon( randomX.apply(), model.beakerHeight );
                        }
                    } );
                }},
                //button to add a water
                new HTMLImageButtonNode( "Add Water" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addWater( randomX.apply(), randomY.apply(), 0 );
                        }
                    } );
                }},
                //button to add a water
                new HTMLImageButtonNode( "Add NaCl" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSalt( randomX.apply(), randomY.apply() );
                        }
                    } );
                }},

                //Developer controls for physics settings
                new PSwing( new JPanel() {{
                    add( new JLabel( "k" ) );
                    add( new PropertySlider( 0, 1000, model.k ) );
                }} ),
                new PSwing( new JPanel() {{
                    add( new JLabel( "pow" ) );
                    add( new PropertySlider( 0, 10, model.pow ) );
                }} ),
                new PSwing( new JPanel() {{
                    add( new JLabel( "rand" ) );
                    add( new PropertySlider( 0, 100, model.randomness ) );
                }} )
        ) ) );
        model.k.trace( "k" );
        model.pow.trace( "pow" );
        model.randomness.trace( "randomness" );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    //Graphic to show a barrier such as the beaker floor
    private class BarrierNode extends PNode {
        public BarrierNode( ModelViewTransform transform, Barrier floor ) {
            addChild( new PhetPPath( transform.modelToView( floor.shape.toRectangle2D() ), Color.lightGray ) );
        }
    }
}