/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:21:53 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class GraphicListNode extends GraphicNode {
    private ArrayList children = new ArrayList();

    public void addGraphic( AbstractGraphic graphic ) {
        children.add( graphic );
    }

    public AbstractGraphic[] getChildren() {
        return (AbstractGraphic[])children.toArray( new AbstractGraphic[0] );
    }


}
