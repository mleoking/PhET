// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

// Copyright 2002-2011, University of Colorado

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property3.Property;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
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

    public FaucetNode( ModelViewTransform transform, final Property<Double> faucetFlowLevel ) {
        addChild( new PImage( RESOURCES.getImage( "faucet.png" ) ) {{
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
                        faucetFlowLevel.set( getValue() / 100.0 );
                    }
                } );
                faucetFlowLevel.addObserver( new VoidFunction1<Double>() {
                    public void apply( Double value ) {
                        setValue( (int) ( value * 100 ) );
                    }
                } );
            }} ) {{
                translate( 186, -2 + ( PhetUtilities.isMacintosh() ? -8 : 0 ) );//Mac sliders render lower than windows slider, so have to compensate
                //Faucet slider should be invisible when in "auto" mode
            }};
            addChild( sliderNode );
        }} );
        setOffset( 20, 10 );
    }
}