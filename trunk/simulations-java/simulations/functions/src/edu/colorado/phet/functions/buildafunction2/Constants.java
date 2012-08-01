package edu.colorado.phet.functions.buildafunction2;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;

/**
 * @author Sam Reid
 */
public class Constants {
    public static final Dimension2DDouble bodyDimension = new Dimension2DDouble( 150, 100 );
    public static final double ellipseWidth = 50;
    public static final int inset = 5;//space between adjacent plugged in functions
    public static final Color valueColor = Color.white;
    public static final Property<Color> functionColor = new Property<Color>( new Color( 252, 241, 217 ) );
}