// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
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
    public final Option<PNode> icon;

    public BarItem( ObservableProperty<Double> concentration, ObservableProperty<Color> color, String caption, Option<PNode> icon ) {
        this.concentration = concentration;
        this.color = color;
        this.caption = caption;
        this.icon = icon;
    }
}
