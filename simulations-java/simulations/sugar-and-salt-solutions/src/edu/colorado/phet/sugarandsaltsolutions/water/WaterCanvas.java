// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.MacroCanvas;
import edu.colorado.phet.sugarandsaltsolutions.water.WaterModel.Barrier;
import edu.umd.cs.piccolo.PNode;

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
    private PNode particleLayer = new PNode();

    public WaterCanvas( final WaterModel model, final SugarAndSaltSolutionsColorScheme config ) {
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
        addWorldChild( rootNode );
        rootNode.addChild( particleLayer );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, canvasSize ) );

        //Adapter method for wiring up components to listen to when the model updates
        final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 listener ) {
                model.addFrameListener( listener );
            }
        };

        //Provide graphics for WaterMolecules
        new GraphicAdapter<WaterMolecule>( particleLayer, new Function1<WaterMolecule, PNode>() {
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
        new GraphicAdapter<DefaultParticle>( particleLayer, new Function1<DefaultParticle, PNode>() {
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
        new GraphicAdapter<DefaultParticle>( particleLayer, new Function1<DefaultParticle, PNode>() {
            public PNode apply( DefaultParticle sodiumIon ) {
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, S3Element.ClIon );
            }
        }, model.getChlorineIonList(), new VoidFunction1<VoidFunction1<DefaultParticle>>() {
            public void apply( VoidFunction1<DefaultParticle> createNode ) {
                model.addChlorineIonAddedListener( createNode );
            }
        }
        );


        //Provide graphics for Salt Crystals
        new GraphicAdapter<Molecule>( particleLayer, new Function1<Molecule, PNode>() {
            public PNode apply( Molecule sodiumIon ) {
                return new MoleculeNode( transform, sodiumIon, addFrameListener, S3Element.NaIon );
            }
        },
                                      //TODO: use the pre-existing list when it is made
                                      new ArrayList<Molecule>(),
                                      new VoidFunction1<VoidFunction1<Molecule>>() {
                                          public void apply( VoidFunction1<Molecule> createNode ) {
                                              model.addSugarAddedListener( createNode );
                                          }
                                      }
        );

//        addChild( new BarrierNode( transform, model.bottomWall ) );
//        addChild( new BarrierNode( transform, model.leftWall ) );
//        addChild( new BarrierNode( transform, model.rightWall ) );
        //Control panel
        addChild( new ControlPanelNode( new VBox(

                //button to add a salt
                new HTMLImageButtonNode( "Add Salt" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSalt();
                        }
                    } );

                    //disable the "add salt" button if there are already 2 salts
                    model.numSodiums.lessThan( 2 ).addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean lessThanTwoSodiums ) {
                            setEnabled( lessThanTwoSodiums );
                        }
                    } );
                }},

                //button to add a sugar
                new HTMLImageButtonNode( "Add Sugar" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.addSugar();
                        }
                    } );

                    //disable the "add sugar" button if there are already 2 sugars
                    model.numSugars.lessThan( 2 ).addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean lessThanTwoSugars ) {
                            setEnabled( lessThanTwoSugars );
                        }
                    } );
                }},

                //Add a reset all button that resets this tab
                new HTMLImageButtonNode( "Reset All" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            model.reset();
                        }
                    } );
                }}
        ) ) {{
            setOffset( canvasSize.getWidth() - getFullBounds().getWidth() - MacroCanvas.INSET, canvasSize.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }} );
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