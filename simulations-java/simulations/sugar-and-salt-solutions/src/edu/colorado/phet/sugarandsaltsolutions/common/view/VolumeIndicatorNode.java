// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Solution;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.INSET;

/**
 * Displays the exact volume of the solution, as a text label inside the solution.
 *
 * @author Sam Reid
 */
public class VolumeIndicatorNode extends PNode {
    public VolumeIndicatorNode( final ModelViewTransform transform, final Solution solution, ObservableProperty<Boolean> visible, final ObservableProperty<Double> solidVolume ) {
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
        addChild( new PText() {{
            solution.volume.addObserver( new VoidFunction1<Double>() {
                public void apply( Double volumeInSI ) {
                    //Read out one more degree of precision than the tick marks on the side
                    DecimalFormat decimalFormat = new DecimalFormat( "0.00" );

                    //Convert to liters for the display
                    double liters = volumeInSI * 1000;

                    //Update the readout
                    setText( decimalFormat.format( liters ) + "L solution" );
                }
            } );

            //Use a large font so it will be easy to read inside the water
            setFont( CONTROL_FONT );
        }} );

        //Update the location so it remains in the top left of the fluid
        solution.shape.addObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                Rectangle2D waterViewBounds = transform.modelToView( shape ).getBounds2D();//TODO: rewrite to observe the shape itself instead of its dependencies
                setOffset( waterViewBounds.getX() + INSET, waterViewBounds.getY() + INSET );
            }
        } );
    }
}
