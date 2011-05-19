// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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
    public PrecipitateNode( final ModelViewTransform transform, final ObservableProperty<Double> precipitateVolume, final Beaker beaker ) {

        //Show as white, but it renders between the water layers so it looks like it is in the water (unless it passes the top of the water)
        addChild( new PhetPPath( Color.white ) {{
            precipitateVolume.addObserver( new VoidFunction1<Double>() {
                public void apply( Double precipitateVolume ) {
                    //Scale up the precipitate volume to convert from meters cubed to stage coordinates, manually tuned
                    //We tried showing as a wide and short ellipse (a clump centered in the beaker), but that creates complications when it comes to showing the water level
                    //Note, this assumes that the beaker (and precipitate) are rectangular
                    setPathTo( transform.modelToView( new Rectangle2D.Double( beaker.getX(), beaker.getY(), beaker.getWidth(), beaker.getHeightForVolume( precipitateVolume ) ) ) );
                }
            } );
        }} );

        //Make it not intercept mouse events so the user can still retrieve a probe that is buried in the precipitate
        setPickable( false );
        setChildrenPickable( false );
    }
}