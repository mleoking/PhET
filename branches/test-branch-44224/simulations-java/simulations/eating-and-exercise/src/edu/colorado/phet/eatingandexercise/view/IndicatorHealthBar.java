package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;

/**
 * Created by: Sam
 * Jun 27, 2008 at 12:16:16 PM
 */
public class IndicatorHealthBar extends HealthBar {
    private HealthLevel healthLevel;

    public IndicatorHealthBar( String name, double min, double max, double minOptimal, double maxOptimal, double viewHeight, Color minColor, Color maxColor ) {
        super( name, min, max, minOptimal, maxOptimal, viewHeight, minColor, maxColor );
        healthLevel = new HealthLevel( this );
        addChild( healthLevel );
    }

    public void setValue( double value ) {
        healthLevel.setValue( value );
    }
}
