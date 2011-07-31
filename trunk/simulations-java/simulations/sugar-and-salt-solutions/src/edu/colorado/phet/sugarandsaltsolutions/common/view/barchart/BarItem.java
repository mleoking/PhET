// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view.barchart;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.PNode;

/**
 * Item that can be shown in the bar chart, along with concentration, color, caption and icon
 *
 * @author Sam Reid
 */
public class BarItem {
    public final ObservableProperty<Double> concentration;
    public final ObservableProperty<Color> color;
    public final String caption;

    //Icons to be shown beneath the bar.  Functions are used to create new icons for each kit since giving the same PNode multiple parents caused layout problems
    public final Function0<Option<PNode>> icon;

    public BarItem( ObservableProperty<Double> concentration, ObservableProperty<Color> color, String caption, Function0<Option<PNode>> icon ) {
        this.concentration = concentration;
        this.color = color;
        this.caption = caption;
        this.icon = icon;
    }
}