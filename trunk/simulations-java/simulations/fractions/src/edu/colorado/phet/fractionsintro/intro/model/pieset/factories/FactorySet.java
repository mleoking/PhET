// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import fj.Function;

import java.awt.Color;

import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;

/**
 * Manages factories for all representations for one set.
 *
 * @author Sam Reid
 */
public class FactorySet {
    public final SliceFactory circularSliceFactory;
    public final SliceFactory horizontalSliceFactory;
    public final SliceFactory verticalSliceFactory;
    public final SliceFactory waterGlassSetFactory;
    public final SliceFactory cakeSliceFactory;

    public FactorySet( SliceFactory circularSliceFactory, SliceFactory horizontalSliceFactory, SliceFactory verticalSliceFactory, SliceFactory waterGlassSetFactory, SliceFactory cakeSliceFactory ) {
        this.circularSliceFactory = circularSliceFactory;
        this.horizontalSliceFactory = horizontalSliceFactory;
        this.verticalSliceFactory = verticalSliceFactory;
        this.waterGlassSetFactory = waterGlassSetFactory;
        this.cakeSliceFactory = cakeSliceFactory;
    }

    public static FactorySet introTab() {
        final Vector2D bucketPosition = new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 + 20 );
        int numPerRow = 6;
        double pieDiameter = 155;
        double pieX = 0;
        double pieY = 250 + 20;

        final F<Site, Site> siteMap = Function.identity();
        final Color sliceColor = AbstractFractionsCanvas.LIGHT_GREEN;

        final double distanceBetweenBars = 20 * 1.3;
        Dimension2D bucketSize = new Dimension2D( 350, 135 );
        return new FactorySet( new CircularSliceFactory( numPerRow, bucketPosition, bucketSize, pieDiameter, pieX, pieY, siteMap, sliceColor ),
                               new HorizontalSliceFactory( bucketPosition, bucketSize, sliceColor ),
                               new VerticalSliceFactory( -35.5, 125, 200, false, bucketPosition, bucketSize, sliceColor, distanceBetweenBars ),
                               new VerticalSliceFactory( 60, 100, 200, true, bucketPosition, bucketSize, sliceColor, distanceBetweenBars ),
                               new CakeSliceFactory( bucketPosition, bucketSize ) );
    }
}