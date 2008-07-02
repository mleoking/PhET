package edu.colorado.phet.eatingandexercise.view;

/**
 * Created by: Sam
 * Jun 27, 2008 at 12:16:16 PM
 */
public class IndicatorHealthBar extends HealthBar {
    private HealthLevel healthLevel;

    public IndicatorHealthBar( String name, double min, double max, double minOptimal, double maxOptimal,double viewHeight ) {
        super( name, min, max, minOptimal, maxOptimal, viewHeight );
        healthLevel = new HealthLevel( this );
        addChild( healthLevel );
    }

    public void setValue( double value ) {
        healthLevel.setValue( value );
    }
}
