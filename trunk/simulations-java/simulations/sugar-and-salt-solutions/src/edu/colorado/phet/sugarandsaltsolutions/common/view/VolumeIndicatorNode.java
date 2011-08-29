// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Solution;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.PATTERN__LITERS_SOLUTION;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.PATTERN__LITERS_WATER;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.INSET;

/**
 * Displays the exact volume of the solution, as a text label inside the solution.
 *
 * @author Sam Reid
 */
public class VolumeIndicatorNode extends PNode {
    public VolumeIndicatorNode( final ModelViewTransform transform, final Solution solution, ObservableProperty<Boolean> visible, final ObservableProperty<Boolean> anySolutes, final Function1<Double, String> formatter ) {
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
        addChild( new HTMLNode() {{
            new RichSimpleObserver() {
                @Override public void update() {

                    //Apply the context sensitive formatter (e.g., accounting for the module and whether on the side of beaker or continuous readout within the beaker)
                    String formatted = formatter.apply( solution.volume.get() );

                    //if there is no sugar or salt in the beaker, say 1.00L "water" instead of "solution"
                    setHTML( MessageFormat.format( anySolutes.get() ? PATTERN__LITERS_SOLUTION : PATTERN__LITERS_WATER, formatted ) );
                }
            }.observe( solution.volume, anySolutes );

            //Use a large font so it will be easy to read inside the water
            setFont( CONTROL_FONT );
        }} );

        //Update the location so it remains in the top left of the fluid
        solution.shape.addObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                Rectangle2D waterViewBounds = transform.modelToView( shape ).getBounds2D();
                setOffset( waterViewBounds.getX() + INSET, waterViewBounds.getY() + INSET );
            }
        } );
    }
}
