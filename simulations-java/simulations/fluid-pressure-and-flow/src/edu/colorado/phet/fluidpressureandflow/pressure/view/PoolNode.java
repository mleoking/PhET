// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;
import java.awt.GradientPaint;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor.*;

/**
 * Graphics for showing the swimming pool.
 *
 * @author Sam Reid
 */
public class PoolNode extends PNode {

    //Density of the fluid
    private final Property<Double> fluidDensity;

    public PoolNode( final ModelViewTransform transform2D, final IPool pool, Property<Double> fluidDensity ) {
        this.fluidDensity = fluidDensity;
        final PhetPPath path = new PhetPPath( transform2D.modelToView( pool.getWaterShape() ), createPaint( transform2D, pool ) ) {{
            waterColor.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( createPaint( transform2D, pool ) );
                }
            } );
        }};
        addChild( path );
        setPickable( false );
        setChildrenPickable( false );
        fluidDensity.addObserver( new SimpleObserver() {
            public void update() {
                path.setPaint( createPaint( transform2D, pool ) );
            }
        } );
    }

    //Create the GradientPaint for the water--must be transparent so objects can submerge
    private GradientPaint createPaint( ModelViewTransform transform2D, IPool pool ) {
        Color topColor = getTopColor( fluidDensity.get() );
        Color bottomColor = getBottomColor( fluidDensity.get() );
        double yBottom = transform2D.modelToViewY( -pool.getHeight() );//fade color halfway down
        double yTop = transform2D.modelToViewY( 0 );

        //Make pool gradient darker so it doesn't blend in with the air too much
        return new GradientPaint( 0, (float) yTop, darker( topColor ), 0, (float) yBottom, darker( bottomColor ) );
    }

    //Darken a color but keep the alpha value instead of discarding it (as in java.awt.Color.darker)
    private static Color darker( Color color ) {
        return ColorUtils.darkerColor( color, 0.2 );
    }
}