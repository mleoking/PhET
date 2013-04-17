// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.control;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;

/**
 * SliderOnlyLayoutStrategy is a layout strategy for a value control that shows only the slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SliderOnlyLayoutStrategy implements ILayoutStrategy {

    public SliderOnlyLayoutStrategy() {}

    public void doLayout( AbstractValueControl valueControl ) {
        valueControl.add( valueControl.getSlider() );
    }
}
