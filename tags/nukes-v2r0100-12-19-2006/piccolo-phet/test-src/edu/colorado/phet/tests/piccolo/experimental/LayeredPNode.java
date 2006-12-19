/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.colorado.phet.common.util.MultiMap;
import edu.umd.cs.piccolo.PNode;

import java.util.Iterator;

/**
 * It is up to clients of this class to never set the 0th child of this class to be anything different than what it is normally.
 * This means they cannot insert another child there, or delete the existing child there.
 */
public class LayeredPNode extends PNode {
    private MultiMap layerMap = new MultiMap();
    private PNode layerChild = new PNode();

    public LayeredPNode() {
        addChild( layerChild );
    }

    public void addLayerChild( PNode child, double layer ) {
        layerMap.put( new Double( layer ), child );
        updateLayers();
    }

    private void updateLayers() {
        Iterator it = layerMap.iterator();
        layerChild.removeAllChildren();
        while( it.hasNext() ) {
            PNode node = (PNode)it.next();
            layerChild.addChild( node );
        }
    }

    public void removeLayerChild( PNode child ) {
        layerMap.removeValue( child );
        updateLayers();
    }

    public void addLayerChild( PNode child ) {
        addLayerChild( child, 0 );
    }

    public void removeAllLayeredChildren() {
        layerMap.clear();
        updateLayers();
    }

}