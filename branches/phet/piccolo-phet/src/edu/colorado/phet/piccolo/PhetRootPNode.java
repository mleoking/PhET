/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Arranges graphics in terms of Layers.  Each Layer contains a world node and a screen node (on top).
 * This should allow the user to interleave world and screen graphics arbitrarily.
 */

public class PhetRootPNode extends PNode {
    private Layer defaultLayer;
    private ArrayList layers = new ArrayList();

    public void translateWorld( double dx, double dy ) {
        for( int i = 0; i < layers.size(); i++ ) {
            Layer layer = (Layer)layers.get( i );
            layer.getWorldNode().translate( dx, dy );
        }
    }

    public void setWorldOffset( double dx, double dy ) {
        for( int i = 0; i < layers.size(); i++ ) {
            Layer layer = (Layer)layers.get( i );
            layer.setOffset( dx, dy );
        }

    }

    public void scaleWorldAboutPoint( double scale, Point2D point ) {
        for( int i = 0; i < layers.size(); i++ ) {
            Layer layer = (Layer)layers.get( i );
            layer.getWorldNode().scaleAboutPoint( scale, point );
        }
    }

    public static class Layer extends PNode {
        private PNode worldNode;
        private PNode screenNode;

        public Layer() {
            this.worldNode = new PNode();
            this.screenNode = new PNode();
            correctChildren();
        }

        private void correctChildren() {
            removeAllChildren();

            addChild( worldNode );
            addChild( screenNode );
        }

        public PNode getWorldNode() {
            return worldNode;
        }

        public void setWorldNode( PNode worldNode ) {
            removeChild( this.worldNode );
            this.worldNode = worldNode;
            correctChildren();
        }

        public PNode getScreenNode() {
            return screenNode;
        }

        public void setScreenNode( PNode screenNode ) {
            removeChild( this.screenNode );
            this.screenNode = screenNode;
            correctChildren();
        }

        public void setWorldScale( double scale ) {
            worldNode.scale( scale / worldNode.getGlobalScale() );
        }

        public void setContainsScreenNode( boolean b ) {

            if( b && !containsScreenNode() ) {
                addChild( screenNode );
            }
            else if( !b ) {
                removeChild( screenNode );
            }
        }

        public boolean containsScreenNode() {
            return getChildrenReference().contains( screenNode );
        }
    }

    public PhetRootPNode() {
        defaultLayer = new Layer();
        addLayer( defaultLayer );
    }

    public Layer layerAt( int i ) {
        return (Layer)layers.get( i );
    }

    public Layer addLayer() {
        Layer layer = new Layer();
        addLayer( layer );
        return layer;
    }

    public void addLayer( Layer layer ) {
        addChild( layer );
        layers.add( layer );
    }

    public void addLayer( Layer layer, int index ) {
        addChild( index, layer );
        layers.add( index, layer );
    }

    public PNode getWorldNode() {
        return defaultLayer.getWorldNode();
    }

    public PNode getScreenNode() {
        return defaultLayer.getScreenNode();
    }

    public void setWorldScale( double scale ) {
        for( int i = 0; i < layers.size(); i++ ) {
            Layer layer = (Layer)layers.get( i );
            layer.setWorldScale( scale );
        }
    }

    public void addWorldChild( int layer, PNode graphic ) {
        defaultLayer.getWorldNode().addChild( layer, graphic );
    }

    public void addWorldChild( PNode graphic ) {
        defaultLayer.getWorldNode().addChild( graphic );
    }

    public void removeWorldChild( PNode graphic ) {
        defaultLayer.getWorldNode().removeChild( graphic );
    }

    public void addScreenChild( PNode node ) {
        defaultLayer.getScreenNode().addChild( node );
    }

    public void removeScreenChild( PNode node ) {
        defaultLayer.getScreenNode().removeChild( node );
    }

    public void setContainsScreenNode( boolean b ) {
        defaultLayer.setContainsScreenNode( b );
    }

    public boolean containsScreenNode() {
        return defaultLayer.containsScreenNode();
    }

    public void setScreenNode( PNode screenNode ) {
        defaultLayer.setScreenNode( screenNode );
    }
}
