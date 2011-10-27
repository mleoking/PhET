// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.JMEUtils;

import com.jme3.math.ColorRGBA;

public class JMEColorHandler<ColorKey extends Enum> extends ColorHandler<ColorKey> {
    private Map<ColorKey, Property<ColorRGBA>> colorRGBAMap = new HashMap<ColorKey, Property<ColorRGBA>>();

    @Override public void set( final ColorKey key, Color color ) {
        super.set( key, color );

        if ( colorRGBAMap.get( key ) == null ) {
            // initialize it correctly
            final Property<ColorRGBA> property = new Property<ColorRGBA>( JMEUtils.convertColor( color ) );

            // make it accessible for the future
            colorRGBAMap.put( key, property );

            // keep it up-to-date
            getProperty( key ).addObserver( new SimpleObserver() {
                public void update() {
                    property.set( JMEUtils.convertColor( get( key ) ) );
                }
            } );
        }
    }

    public ColorRGBA getRGBA( ColorKey key ) {
        return getRGBAProperty( key ).get();
    }

    public Property<ColorRGBA> getRGBAProperty( ColorKey key ) {
        return colorRGBAMap.get( key );
    }
}
