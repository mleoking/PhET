// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import fj.Function;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.AbstractSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CakeSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.HorizontalSliceFactory;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory;

/**
 * Manages factories for all representations for one tab.
 *
 * @author Sam Reid
 */
public class FactorySet {
    public final CircularSliceFactory CircularSliceFactory;
    public final HorizontalSliceFactory HorizontalSliceFactory;
    public final VerticalSliceFactory VerticalSliceFactory;
    public final VerticalSliceFactory WaterGlassSetFactory;
    public final CakeSliceFactory CakeSliceFactory;

    public FactorySet( Vector2D bucketPosition, int numPerRow, double pieDiameter, double pieX, double pieY, F<Site, Site> siteMap ) {
        this( new edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory( numPerRow, bucketPosition, pieDiameter, pieX, pieY, siteMap ),
              new edu.colorado.phet.fractionsintro.intro.model.pieset.HorizontalSliceFactory( bucketPosition ),
              new edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory( 125, 225, false, bucketPosition ),
              new VerticalSliceFactory( 100, 200, true, bucketPosition ),
              new edu.colorado.phet.fractionsintro.intro.model.pieset.CakeSliceFactory( new Vector2D( AbstractSliceFactory.stageSize.width / 2, -AbstractSliceFactory.stageSize.height + 200 ) ) );
    }

    public FactorySet( CircularSliceFactory circularSliceFactory, HorizontalSliceFactory horizontalSliceFactory, VerticalSliceFactory verticalSliceFactory, VerticalSliceFactory waterGlassSetFactory, CakeSliceFactory cakeSliceFactory ) {
        CircularSliceFactory = circularSliceFactory;
        HorizontalSliceFactory = horizontalSliceFactory;
        VerticalSliceFactory = verticalSliceFactory;
        WaterGlassSetFactory = waterGlassSetFactory;
        CakeSliceFactory = cakeSliceFactory;
    }

    public static FactorySet IntroTab = new FactorySet( new Vector2D( AbstractSliceFactory.stageSize.width / 2, -AbstractSliceFactory.stageSize.height + 200 ), 6, 155, 0, 250, Function.<Site>identity() );
}