// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.view.SpinnerArrowNode.SpinnerArrowOrientation;

/**
 * A set of objects that corresponds to the states of the spinner.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SpinnerStateIndicator<T> {

    public final T inactive, highlighted, pressed, disabled;

    public SpinnerStateIndicator( T inactive, T highlighted, T pressed, T disabled ) {
        this.inactive = inactive;
        this.highlighted = highlighted;
        this.pressed = pressed;
        this.disabled = disabled;
    }

    // Default images for the various states of the "up" button.
    public static class UpButtonImages extends SpinnerStateIndicator<Image> {
        public UpButtonImages( SpinnerStateIndicator<Color> colors ) {
            super( new SpinnerArrowNode( colors.inactive, SpinnerArrowOrientation.UP ).toImage(),
                   new SpinnerArrowNode( colors.highlighted, SpinnerArrowOrientation.UP ).toImage(),
                   new SpinnerArrowNode( colors.pressed, SpinnerArrowOrientation.UP ).toImage(),
                   new SpinnerArrowNode( colors.disabled, SpinnerArrowOrientation.UP, false ).toImage() );
        }
    }

    // Default images for the various states of the "down" button.
    public static class DownButtonImages extends SpinnerStateIndicator<Image> {
        public DownButtonImages( SpinnerStateIndicator<Color> colors ) {
            super( new SpinnerArrowNode( colors.inactive, SpinnerArrowOrientation.DOWN ).toImage(),
                   new SpinnerArrowNode( colors.highlighted, SpinnerArrowOrientation.DOWN ).toImage(),
                   new SpinnerArrowNode( colors.pressed, SpinnerArrowOrientation.DOWN ).toImage(),
                   new SpinnerArrowNode( colors.disabled, SpinnerArrowOrientation.DOWN, false ).toImage() );
        }
    }

    // Colors used for the states of a slope spinner's up/down buttons.
    public static class SlopeColors extends SpinnerStateIndicator<Color> {
        public SlopeColors() {
            super( LGColors.SLOPE, LGColors.SLOPE, LGColors.SLOPE.darker(), LGColors.SPINNER_BUTTON_DISABLED );
        }
    }

    // Colors used for the states of an intercept spinner's up/down buttons.
    public static class InterceptColors extends SpinnerStateIndicator<Color> {
        public InterceptColors() {
            super( LGColors.INTERCEPT, LGColors.INTERCEPT, LGColors.INTERCEPT.darker(), LGColors.SPINNER_BUTTON_DISABLED );
        }
    }

    // Colors used for the states of a point (x1,y1) spinner's up/down buttons.
    public static class PointColors extends SpinnerStateIndicator<Color> {
        public PointColors() {
            super( LGColors.POINT_X1_Y1, LGColors.POINT_X1_Y1, LGColors.POINT_X1_Y1.darker(), LGColors.SPINNER_BUTTON_DISABLED );
        }
    }

    // Colors used for the states of the background behind the number in a spinner.
    public static class BackgroundColors extends SpinnerStateIndicator<Color> {
        public BackgroundColors( Color highlighted, Color pressed ) {
            super( LGColors.SPINNER_BACKGROUND_DISABLED, highlighted, pressed, LGColors.SPINNER_BACKGROUND_DISABLED );
        }
    }
}
