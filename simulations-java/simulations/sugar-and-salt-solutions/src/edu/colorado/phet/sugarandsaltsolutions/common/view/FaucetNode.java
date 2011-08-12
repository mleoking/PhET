// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

// Copyright 2002-2011, University of Colorado

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Images.FAUCET_FRONT;

/**
 * Faucet node for showing and controlling water flowing into and out of the beaker.
 * Parts copied from water tower module in fluid pressure and flow.
 *
 * @author Sam Reid
 */
public class FaucetNode extends PNode {

    //Listeners for the shape of the water that flows out of the faucet
    private ArrayList<VoidFunction1<Rectangle2D>> listeners = new ArrayList<VoidFunction1<Rectangle2D>>();

    public FaucetNode(

            //Value between 0 and 1 inclusive to indicate the rate of flow
            final Property<Double> flowRate,

            //if some, the point at which water should stop flowing (for the input faucet, water should stop at the beaker base
            final Option<Double> flowPoint,

            //true if this faucet is allowed to add water.  The top faucet is allowed to add water if the beaker isn't full, and the bottom one can turn on if the beaker isn't empty.
            final ObservableProperty<Boolean> allowed ) {

        //Create the image and slider node used to display and control the faucet
        PImage imageNode = new PImage( FAUCET_FRONT ) {{

            //Scale up the faucet since it looks good larger
            setScale( 1.2 );
            setOffset( 0, 0 );

            //Create the slider
            final PSwing sliderNode = new PSwing( new JSlider( 0, 100 ) {{
                setBackground( new Color( 0, 0, 0, 0 ) );

                //Make the slider thumb wider on Windows 7
                setPaintTicks( true );

                //Fix the size so it will fit into the specified image dimensions
                setPreferredSize( new Dimension( 95, getPreferredSize().height ) );

                //Wire up 2-way communication with the Property
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        //Only change the flow rate if the beaker can accommodate
                        if ( allowed.get() ) {
                            flowRate.set( getValue() / 100.0 );
                        }
                    }
                } );
                flowRate.addObserver( new VoidFunction1<Double>() {
                    public void apply( Double value ) {
                        setValue( (int) ( value * 100 ) );
                    }
                } );

                //Set the flow back to zero when the user lets go, the user has to hold the slider to keep the faucet on
                addMouseListener( new MouseAdapter() {
                    @Override public void mouseReleased( MouseEvent e ) {

                        //Turn off the flow level
                        flowRate.set( 0.0 );

                        //To make sure the slider goes back to zero, it is essential to set the value to something other than zero first
                        //Just calling setValue(0) here or waiting for the callback from the model doesn't work if the user was dragging the knob
                        setValue( 1 );
                        setValue( 0 );
                    }
                } );
            }} ) {{

                //Mac sliders render lower than windows slider, so have to compensate
                translate( 0, -2 + ( PhetUtilities.isMacintosh() ? -8 : 0 ) );

                //TODO: Faucet slider should be invisible when in "auto" mode
            }};
            addChild( sliderNode );
        }};
        final double imageWidth = imageNode.getFullBounds().getMaxX();
        final double imageHeight = imageNode.getFullBounds().getMaxY();

        //Show the water flowing out of the faucet
        addChild( new PhetPPath( SugarAndSaltSolutionsApplication.WATER_COLOR ) {{
            flowRate.addObserver( new VoidFunction1<Double>() {
                public void apply( Double flow ) {
                    double width = flow * 100 * 0.5;
                    double pipeWidth = 56;
                    double bottomY = flowPoint.getOrElse( 1000.0 );//Compute the bottom of the water (e.g. if it collides with the beaker)
                    double height = bottomY - imageHeight - getOffset().getY();
                    final Rectangle2D.Double waterShape = new Rectangle2D.Double( imageWidth - width / 2 - pipeWidth / 2, imageHeight, width, height );
                    notifyWaterShapeChanged( waterShape );
                    setPathTo( waterShape );
                }
            } );
        }} );
        addChild( imageNode );
    }

    //Add a listener that will be notified about the shape of the water
    public void addListener( VoidFunction1<Rectangle2D> listener ) {
        listeners.add( listener );
    }

    //Notify listeners that the shape of the output water changed
    private void notifyWaterShapeChanged( Rectangle2D.Double waterShape ) {
        for ( VoidFunction1<Rectangle2D> listener : listeners ) {
            listener.apply( waterShape );
        }
    }
}