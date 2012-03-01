// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import fj.Function;

import java.awt.Color;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.HorizontalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.model.pieset.SliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory;

/**
 * Manages factories for all representations for one tab.
 *
 * @author Sam Reid
 */
public class FactorySet {
    public final SliceFactory circularSliceFactory;
    public final SliceFactory horizontalSliceFactory;
    public final SliceFactory verticalSliceFactory;
    public final SliceFactory waterGlassSetFactory;
    public final SliceFactory cakeSliceFactory;

    public FactorySet( Vector2D bucketPosition, int numPerRow, double pieDiameter, double pieX, double pieY, F<Site, Site> siteMap, Color sliceColor ) {
        this( new CircularSliceFactory( numPerRow, bucketPosition, pieDiameter, pieX, pieY, siteMap, sliceColor ),
              new HorizontalSliceFactory( bucketPosition, sliceColor ),
              new VerticalSliceFactory( 125, 225, false, bucketPosition, sliceColor ),
              new VerticalSliceFactory( 100, 200, true, bucketPosition, sliceColor ),
              new CakeSliceFactory( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ) ) );
    }

    public FactorySet( CircularSliceFactory circularSliceFactory, HorizontalSliceFactory horizontalSliceFactory, VerticalSliceFactory verticalSliceFactory, VerticalSliceFactory waterGlassSetFactory, CakeSliceFactory cakeSliceFactory ) {
        this.circularSliceFactory = circularSliceFactory;
        this.horizontalSliceFactory = horizontalSliceFactory;
        this.verticalSliceFactory = verticalSliceFactory;
        this.waterGlassSetFactory = waterGlassSetFactory;
        this.cakeSliceFactory = cakeSliceFactory;
    }

    public static FactorySet IntroTab = new FactorySet( new Vector2D( SliceFactory.stageSize.width / 2, -SliceFactory.stageSize.height + 200 ), 6, 155, 0, 250, Function.<Site>identity(), AbstractFractionsCanvas.LightGreen );
}