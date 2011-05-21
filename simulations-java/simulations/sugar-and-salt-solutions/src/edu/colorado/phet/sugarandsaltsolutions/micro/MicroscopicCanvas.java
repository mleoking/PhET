// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroscopicModel.Barrier;
import edu.umd.cs.piccolo.PNode;

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
        final ModelViewTransform transform = createRectangleInvertedYMapping( new Rectangle2D.Double( -model.beakerWidth / 2, -1E-10, model.beakerWidth, model.beakerWidth ), new Rectangle2D.Double( 0, 0, canvasSize.getHeight(), canvasSize.getHeight() ) );

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
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, Element.N );//TODO: no sodium element yet, so it will have to be added
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
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, Element.Cl );
            }
        }, model.getChlorineIonList(), new VoidFunction1<VoidFunction1<DefaultParticle>>() {
            public void apply( VoidFunction1<DefaultParticle> createNode ) {
                model.addChlorineIonAddedListener( createNode );
            }
        }
        );

        addChild( new BarrierNode( transform, model.floor ) );
        addChild( new BarrierNode( transform, model.leftWall ) );
        addChild( new BarrierNode( transform, model.rightWall ) );

        final Function0<Float> getX = new Function0<Float>() {
            public Float apply() {
                return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * model.beakerWidth - model.beakerWidth / 2 );
            }
        };

        //Control panel
        addChild( new ControlPanelNode( new VBox(
                //Add a reset all button that resets this tab
                new ButtonNode( "Reset All" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.reset();
                        }
                    } );
                }},
                //Button to add a single sodium ion
                new ButtonNode( "Add Sodium Ion" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSodiumIon( getX.apply(), model.beakerHeight, 0 );
                        }
                    } );
                }},
                //Button to add a chlorine icon
                new ButtonNode( "Add Chlorine Ion" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addChlorineIon( getX.apply(), model.beakerHeight, 0 );
                        }
                    } );
                }},
                //button to add a water
                new ButtonNode( "Add Water" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addWater( getX.apply(), model.beakerHeight, 0 );
                        }
                    } );
                }},
                //button to add a water
                new ButtonNode( "Add NaCl" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSalt( getX.apply(), model.beakerHeight );
                        }
                    } );
                }}
        ) ) );
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