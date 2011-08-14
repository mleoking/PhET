// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view.faucet;

import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Images.FAUCET_FRONT;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Images.FAUCET_PIPE;

/**
 * Faucet node for showing and controlling water flowing into and out of the beaker.
 * Parts copied from water tower module in fluid pressure and flow.
 *
 * @author Sam Reid
 */
public class FaucetNode extends PNode {

    //Locations where the left side of the faucet connects to the pipe, so that the pipe can be tiled beyond the faucet image
    private final double inputPipeY1 = 32;
    private final double inputPipeY2 = 77;
    private final double inputPipeX = 0;

    private final double outputPipeX1 = 57;
    private final double outputPipeX2 = 109;
    private final double outputPipeY = 133;

    private final PImage faucetImageNode;

    //Node that displays the water flowing out of the faucet
    public final WaterNode waterNode;

    public FaucetNode(

            //Value between 0 and 1 inclusive to indicate the rate of flow
            final Property<Double> flowRate,

            //if some, the point at which water should stop flowing (for the input faucet, water should stop at the beaker base
            final Option<Double> flowPoint,

            //true if this faucet is allowed to add water.  The top faucet is allowed to add water if the beaker isn't full, and the bottom one can turn on if the beaker isn't empty.
            final ObservableProperty<Boolean> allowed,

            //Length of the faucet input pipe in pixels
            final double faucetLength ) {

        //Create the image and slider node used to display and control the faucet
        faucetImageNode = new PImage( FAUCET_FRONT ) {{

            //Scale up the faucet since it looks better at a larger size
            setScale( 1.2 );

            //Add the slider as a child of the image so it will receive the same scaling so it will stay in corresponding with the image area for the slider
            addChild( new FaucetSliderNode( allowed, flowRate ) );

            //Show the pipe to the left of the faucet with a tiled image
            final Rectangle2D.Double rect = new Rectangle2D.Double( -faucetLength + 1, inputPipeY1 - 0.5, faucetLength, inputPipeY2 - inputPipeY1 + 1.5 );
            addChild( new PhetPPath( rect, new TexturePaint( FAUCET_PIPE, new Rectangle2D.Double( 0, rect.getY(), FAUCET_PIPE.getWidth(), FAUCET_PIPE.getHeight() ) ) ) );
        }};

        //Show the water flowing out of the faucet
        waterNode = new WaterNode( flowRate, flowPoint, outputPipeY, outputPipeX1, outputPipeX2 );
        addChild( waterNode );
        addChild( faucetImageNode );
    }

    //Gets the location of the input part of the pipe in global coordinates for coordination with the model coordinates.
    public Point2D getInputGlobalViewPoint() {
        return faucetImageNode.localToGlobal( new Point2D.Double( inputPipeX, ( inputPipeY1 + inputPipeY2 ) / 2 ) );
    }

    public Point2D getOutputGlobalViewPoint() {
        return faucetImageNode.localToGlobal( new Point2D.Double( ( outputPipeX2 + outputPipeX1 ) / 2, outputPipeY ) );
    }
}