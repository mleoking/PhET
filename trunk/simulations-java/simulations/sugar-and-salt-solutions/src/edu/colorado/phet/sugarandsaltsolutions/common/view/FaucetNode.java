// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

// Copyright 2002-2011, University of Colorado

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.RESOURCES;

/**
 * Faucet node for showing and controlling water flowing into and out of the beaker.
 * Parts copied from water tower module in fluid pressure and flow.
 *
 * @author Sam Reid
 */
public class FaucetNode extends PNode {

    public FaucetNode( ModelViewTransform transform,
                       final Property<Double> faucetFlowLevel,
                       final Option<Double> flowPoint,//if some, the point at which water should stop flowing (for the input faucet, water should stop at the beaker base
                       final ObservableProperty<Boolean> allowed,//true if this faucet is allowed to add water.  The top faucet is allowed to add water if the beaker isn't full, and the bottom one can turn on if the beaker isn't empty.
                       final Point2D offset//Offset to account for in ending the output fluid flow, so it doesn't go past the bottom of the beaker
    ) {
        setOffset( offset );
        PImage imageNode = new PImage( RESOURCES.getImage( "faucet.png" ) ) {{
            //Scale and offset so that the slider will fit into the tap control component
            setScale( 0.75 );
            setOffset( -27, 0 );

            //Create the slider
            final PSwing sliderNode = new PSwing( new JSlider( 0, 100 ) {{
                setBackground( new Color( 0, 0, 0, 0 ) );
                setPaintTicks( true );//to make the slider thumb wider on Windows 7

                //Fix the size so it will fit into the specified image dimensions
                setPreferredSize( new Dimension( 120, getPreferredSize().height ) );

                //Wire up 2-way communication with the Property
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        //Only change the flow rate if the beaker can accommodate
                        if ( allowed.get() ) {
                            faucetFlowLevel.set( getValue() / 100.0 );
                        }
                    }
                } );
                faucetFlowLevel.addObserver( new VoidFunction1<Double>() {
                    public void apply( Double value ) {
                        setValue( (int) ( value * 100 ) );
                    }
                } );
                //Set the flow back to zero when the user lets go, the user has to hold the slider to keep the faucet on
                addMouseListener( new MouseAdapter() {
                    @Override public void mouseReleased( MouseEvent e ) {
                        faucetFlowLevel.set( 0.0 );
                    }
                } );
            }} ) {{
                translate( 186, -2 + ( PhetUtilities.isMacintosh() ? -8 : 0 ) );//Mac sliders render lower than windows slider, so have to compensate
                //Faucet slider should be invisible when in "auto" mode
            }};
            addChild( sliderNode );
        }};
        final double imageWidth = imageNode.getFullBounds().getMaxX();
        final double imageHeight = imageNode.getFullBounds().getMaxY();

        //Show the water flowing out of the faucet
        addChild( new PhetPPath( SugarAndSaltSolutionsApplication.WATER_COLOR ) {{
            faucetFlowLevel.addObserver( new VoidFunction1<Double>() {
                public void apply( Double flow ) {
                    double width = flow * 100 * 0.5;
                    double pipeWidth = 56;
                    double bottomY = flowPoint.getOrElse( 1000.0 );//Compute the bottom of the water (e.g. if it collides with the beaker)
                    double topY = imageHeight;
                    double height = bottomY - topY - offset.getY();
                    setPathTo( new Rectangle2D.Double( imageWidth - width / 2 - pipeWidth / 2, imageHeight, width, height ) );
                }
            } );
        }} );
        addChild( imageNode );
//        setOffset( 0, 0 );
    }
}