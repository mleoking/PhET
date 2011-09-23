// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorProfile<ColorKey extends Enum> {
    private Map<ColorKey, Color> colorMap = new HashMap<ColorKey, Color>();
    private final String name;

    public ColorProfile( String name) {

        this.name = name;
    }

    public void apply( ColorHandler<ColorKey> handler ) {
        for ( ColorKey key : colorMap.keySet() ) {
            handler.set( key, colorMap.get( key ) );
        }
    }

    public void add( ColorKey key, Color color ) {
        colorMap.put( key, color );
    }

    public String getName() {
        return name;
    }
}
