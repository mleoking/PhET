// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

import java.awt.Dimension;
import java.awt.TexturePaint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.piccolophet.PiccoloPhetApplication.RESOURCES;

/**
 * Faucet node for showing and controlling water flowing into and out of the beaker.
 * Parts copied from water tower module in fluid pressure and flow.
 * <p>
 * Origin is at the upper-left corner of the bounds of the main faucet image.
 * Additional pipe is added to the left of the origin.
 * </p>
 *
 * @author Sam Reid
 */
public class FaucetNode extends PNode {

    private static final BufferedImage FAUCET_FRONT = RESOURCES.getImage( "faucet_front.png" );
    private static final BufferedImage FAUCET_PIPE = RESOURCES.getImage( "faucet_pipe.png" );

    //TODO #3199, change image-specific constants when the faucet images are revised
    // Locations in the image file where the input pipe connects to the faucet.
    private static final double INPUT_PIPE_X = 0;
    private static final double INPUT_PIPE_Y1 = 32;
    private static final double INPUT_PIPE_Y2 = 78;

    // Locations in the image file where the fluid comes out of the faucet.
    private static final double OUTPUT_PIPE_X1 = 57;
    private static final double OUTPUT_PIPE_X2 = 109;
    private static final double OUTPUT_PIPE_Y = 133;

    // Width of the faucet handle in the image file.
    private static final double HANDLE_WIDTH = 85;

    // sim-sharing strings
    public static final String SIM_SHARING_OBJECT_FAUCET_IMAGE = "faucetImage";

    public final PImage faucetImageNode;
    public final FaucetSliderNode faucetSliderNode;

    /**
     * Constructor that adapts flow rate to a percentage of max flow rate, see #3193
     *
     * @param maxFlowRate            the maximum flow rate, in model units
     * @param flowRate               the flow rate, in model units
     * @param enabled                determines whether the slider is enabled
     * @param faucetLength           length of the input pipe
     * @param snapToZeroWhenReleased does the knob snap back to zero when the user releases it?
     */
    public FaucetNode( final double maxFlowRate, final Property<Double> flowRate, ObservableProperty<Boolean> enabled, double faucetLength, boolean snapToZeroWhenReleased ) {
        this( new Property<Double>( flowRate.get() / maxFlowRate ) {{
            // set the flow rate when the percentage changes
            addObserver( new VoidFunction1<Double>() {
                public void apply( Double newFlowRatePercentage ) {
                    flowRate.set( newFlowRatePercentage * maxFlowRate );
                }
            } );
            // set the percentage when the flow rate changes
            flowRate.addObserver( new VoidFunction1<Double>() {
                public void apply( Double newFlowRate ) {
                    set( newFlowRate / maxFlowRate );
                }
            } );
        }}, enabled, faucetLength, snapToZeroWhenReleased );
    }

    /**
     * Constructor
     *
     * @param flowRatePercentage     the percentage of max flow rate, a value between 0 and 1 inclusive
     * @param enabled                determines whether the slider is enabled
     * @param faucetLength           length of the input pipe
     * @param snapToZeroWhenReleased does the knob snap back to zero when the user releases it?
     */
    public FaucetNode( final Property<Double> flowRatePercentage, final ObservableProperty<Boolean> enabled, final double faucetLength, boolean snapToZeroWhenReleased ) {

        //Create the faucet slider node here so that it can be final, even though it is attached as a child of the faucetImageNode
        faucetSliderNode = new FaucetSliderNode( enabled, flowRatePercentage, snapToZeroWhenReleased ) {{
            setOffset( 4, 2.5 ); //TODO #3199, change offsets when the faucet images are revised, make these constants
            scale( HANDLE_WIDTH / getFullBounds().getWidth() ); //scale to fit into the handle portion of the faucet image
        }};

        //Create the image and slider node used to display and control the faucet
        faucetImageNode = new PImage( FAUCET_FRONT ) {{

            //Scale up the faucet since it looks better at a larger size
            setScale( 1.2 ); //TODO #3199, make the image file larger instead of scaling up

            //Add the slider as a child of the image so it will receive the same scaling so it will stay in corresponding with the image area for the slider
            addChild( faucetSliderNode );

            //Show the pipe to the left of the faucet with a tiled image
            final Rectangle2D.Double rect = new Rectangle2D.Double( -faucetLength, INPUT_PIPE_Y1, faucetLength, INPUT_PIPE_Y2 - INPUT_PIPE_Y1 );
            addChild( new PhetPPath( rect, new TexturePaint( FAUCET_PIPE, new Rectangle2D.Double( 0, rect.getY(), FAUCET_PIPE.getWidth(), FAUCET_PIPE.getHeight() ) ) ) );

            //sim-sharing
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    SimSharingManager.sendNotInteractiveEvent( SIM_SHARING_OBJECT_FAUCET_IMAGE, Actions.PRESSED );
                }
            } );
        }};

        addChild( faucetImageNode );
    }

    // Gets the drag handler, for adding sim-sharing feature.
    public SimSharingDragSequenceEventHandler getDragHandler() {
        return faucetSliderNode.getDragHandler();
    }

    //Gets the location of the input part of the pipe in global coordinates for coordination with the model coordinates.
    public Point2D getInputGlobalViewPoint() {
        return faucetImageNode.localToGlobal( new Point2D.Double( INPUT_PIPE_X, ( INPUT_PIPE_Y1 + INPUT_PIPE_Y2 ) / 2 ) );
    }

    public Point2D getOutputGlobalViewPoint() {
        return faucetImageNode.localToGlobal( new Point2D.Double( ( OUTPUT_PIPE_X2 + OUTPUT_PIPE_X1 ) / 2, OUTPUT_PIPE_Y ) );
    }

    public Dimension2D getGlobalFaucetWidthDimension() {
        return faucetImageNode.localToGlobal( new Dimension2DDouble( OUTPUT_PIPE_X2 - OUTPUT_PIPE_X1, 0 ) );
    }

    public static void main( String[] args ) {
        double maxFlowRate = 20; // L/sec
        Property<Double> flowRate = new Property<Double>( 0d ) {{
            addObserver( new VoidFunction1<Double>() {
                public void apply( Double flowRate ) {
                    System.out.println( "flowRate = " + flowRate + " L/sec" );
                }
            } );
        }};
        final FaucetNode faucetNode = new FaucetNode( maxFlowRate, flowRate, new Property<Boolean>( true ), 50, true ) {{
            setOffset( 100, 100 );
        }};
        final PhetPCanvas canvas = new PhetPCanvas() {{
            getLayer().addChild( faucetNode );
            setPreferredSize( new Dimension( 500, 500 ) );
        }};
        new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
            setVisible( true );
        }};
    }
}