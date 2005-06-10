/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2;

import edu.colorado.phet.common.util.MultiMap;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:21:53 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class GraphicLayerNode extends GraphicNode {
    private MultiMap multiMap = new MultiMap();

    public void addGraphic( AbstractGraphic graphic ) {
        addGraphic( graphic, 0 );
    }

    public void addGraphic( AbstractGraphic graphic, double layer ) {
        multiMap.put( new Double( layer ), graphic );
    }

    public AbstractGraphic[] getChildren() {
        Iterator it = multiMap.iterator();
        ArrayList list = new ArrayList();
        while( it.hasNext() ) {
            list.add( it.next() );
        }
        return (AbstractGraphic[])list.toArray( new AbstractGraphic[0] );
    }


}
