// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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

        //Function which creates water nodes on initialization and when they are added to the model
        VoidFunction1<WaterMolecule> createWaterNode = new VoidFunction1<WaterMolecule>() {
            public void apply( WaterMolecule waterMolecule ) {

                //Create the node
                final WaterMoleculeNode node = new WaterMoleculeNode( transform, waterMolecule, new VoidFunction1<VoidFunction0>() {
                    public void apply( VoidFunction0 listener ) {
                        model.addFrameListener( listener );
                    }
                } );

                //Remove the node when it leaves the model
                waterMolecule.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        rootNode.removeChild( node );
                    }
                } );

                //Add the node to the scene graph
                rootNode.addChild( node );
            }
        };

        //Show the circles already in the model on startup
        for ( final WaterMolecule waterMolecule : model.getWaterList() ) {
            createWaterNode.apply( waterMolecule );
        }

        //Listen for subsequent additions of water molecules
        model.addWaterAddedListener( createWaterNode );

        addChild( new BarrierNode( transform, model.floor ) );
        addChild( new BarrierNode( transform, model.leftWall ) );
        addChild( new BarrierNode( transform, model.rightWall ) );

        //Add a reset all button that resets this tab
        addChild( new ButtonNode( "Reset All" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.reset();
                }
            } );
        }} );
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