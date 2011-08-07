// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.DefaultParticle;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * This is a framed "window"-like node that shows the zoomed in water and solute particles.
 * Allows us to model periodic boundary conditions (where the particle leaves one side and comes in the other, without flickering.
 *
 * @author Sam Reid
 */
public class ParticleWindowNode extends PNode {
    private final PNode particleLayer = new PNode();

    //Color to show around the particle window as its border.  Also used for the zoom in box in ZoomIndicatorNode
    public static final Color FRAME_COLOR = Color.orange;

    public ParticleWindowNode( final WaterModel waterModel, final ModelViewTransform transform ) {

        //Adapter method for wiring up components to listen to when the model updates
        final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 listener ) {
                waterModel.addFrameListener( listener );
                listener.apply();
            }
        };

        //Provide graphics for WaterMolecules
        new GraphicAdapter<WaterMolecule>( particleLayer, new Function1<WaterMolecule, PNode>() {
            public PNode apply( WaterMolecule waterMolecule ) {
                return new WaterMoleculeNode( transform, waterMolecule, addFrameListener );
            }
        }, waterModel.getWaterList(), new VoidFunction1<VoidFunction1<WaterMolecule>>() {
            public void apply( VoidFunction1<WaterMolecule> createNode ) {
                waterModel.addWaterAddedListener( createNode );
            }
        }
        );

        //Provide graphics for SodiumIons
        new GraphicAdapter<DefaultParticle>( particleLayer, new Function1<DefaultParticle, PNode>() {
            public PNode apply( DefaultParticle sodiumIon ) {
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, Element.NA_ION );
            }
        }, waterModel.getSodiumIonList(), new VoidFunction1<VoidFunction1<DefaultParticle>>() {
            public void apply( VoidFunction1<DefaultParticle> createNode ) {
                waterModel.addSodiumIonAddedListener( createNode );
            }
        }
        );

        //Provide graphics for Chlorine Ions
        new GraphicAdapter<DefaultParticle>( particleLayer, new Function1<DefaultParticle, PNode>() {
            public PNode apply( DefaultParticle sodiumIon ) {
                return new DefaultParticleNode( transform, sodiumIon, addFrameListener, Element.CL_ION );
            }
        }, waterModel.getChlorineIonList(), new VoidFunction1<VoidFunction1<DefaultParticle>>() {
            public void apply( VoidFunction1<DefaultParticle> createNode ) {
                waterModel.addChlorineIonAddedListener( createNode );
            }
        }
        );


        //Provide graphics for Sugar molecules
        new GraphicAdapter<Sucrose>( particleLayer, new Function1<Sucrose, PNode>() {
            public PNode apply( Sucrose sodiumIon ) {
                return new MultiSucroseNode( transform, sodiumIon, addFrameListener, waterModel.showSugarAtoms );
            }
        },
                                     //TODO: use the pre-existing list when it is made
                                     new ArrayList<Sucrose>(),
                                     new VoidFunction1<VoidFunction1<Sucrose>>() {
                                         public void apply( VoidFunction1<Sucrose> createNode ) {
                                             waterModel.addSugarAddedListener( createNode );
                                         }
                                     }
        );


        PClip clip = new PClip() {{

            //Show a frame around the particles so they
            double inset = 40;
            setPathTo( new Rectangle2D.Double( inset, inset, WaterCanvas.canvasSize.getWidth() - inset * 2, WaterCanvas.canvasSize.getHeight() - inset * 2 ) );
            setStroke( new BasicStroke( 2 ) );
            setStrokePaint( FRAME_COLOR );
            addChild( particleLayer );
        }};
        addChild( clip );

        scale( 0.75 );
    }
}