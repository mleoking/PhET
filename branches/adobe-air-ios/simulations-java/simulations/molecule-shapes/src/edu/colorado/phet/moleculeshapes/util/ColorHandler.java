// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.property.Property;

public class ColorHandler<ColorKey extends Enum> {
    private Map<ColorKey, Property<Color>> colorMap = new HashMap<ColorKey, Property<Color>>();

    public ColorHandler() {
    }

    public ColorHandler( ColorProfile<ColorKey> profile ) {
        setProfile( profile );
    }

    public void setProfile( ColorProfile<ColorKey> profile ) {
        profile.apply( this );
    }

    public Color get( ColorKey key ) {
        return getProperty( key ).get();
    }

    public void set( ColorKey key, Color color ) {
        Property<Color> property = getProperty( key );
        if ( property == null ) {
            property = new Property<Color>( color );
            colorMap.put( key, property );
        }
        else {
            property.set( color );
        }
    }

    public Property<Color> getProperty( ColorKey key ) {
        return colorMap.get( key );
    }
}
