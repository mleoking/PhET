// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.view;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.PNode;

/**
 * If you're not part of the solution, you're part of the precipitate.  This node draws the clump of crystals that has come out of solution (because of passing the saturation point)
 *
 * @author Sam Reid
 */
public class PrecipitateNode extends PNode {
    public PrecipitateNode( final ModelViewTransform transform, final ObservableProperty<Double> molesOfPrecipitate, final Beaker beaker ) {

        //Show as white, but it renders between the water layers so it looks like it is in the water (unless it passes the top of the water)
        addChild( new PhetPPath( Color.white ) {{
            molesOfPrecipitate.addObserver( new VoidFunction1<Double>() {
                public void apply( Double moles ) {

                    //Make it a wide and short ellipse
                    double width = moles * 20;
                    double height = moles * 5;
                    double centerX = transform.modelToViewX( beaker.getCenterX() );
                    double y = transform.modelToViewY( beaker.getY() ) - height / 2.5;//Just show the top part of the ellipse

                    //Crop off any parts that would go outside of the beaker (but okay to go up past the beaker)
                    setPathTo( new Area( new Ellipse2D.Double( centerX - width / 2, y, width, height ) ) {{

                        //Find the clipping region, where the precipitate is allowed to be.  To compute this, just find the shape the water would take if there were 10000 m^3 of water
                        Shape allowedArea = beaker.getFluidShape( 10000 );
                        intersect( new Area( transform.modelToView( allowedArea ) ) );
                    }} );
                }
            } );
        }} );

        //Make it not intercept mouse events so the user can still retrieve a probe that is buried in the precipitate
        setPickable( false );
        setChildrenPickable( false );
    }
}