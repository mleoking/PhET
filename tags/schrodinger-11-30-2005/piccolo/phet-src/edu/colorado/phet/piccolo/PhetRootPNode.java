/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 13, 2005
 * Time: 12:31:12 AM
 * Copyright (c) Sep 13, 2005 by Sam Reid
 */

public class PhetRootPNode extends PNode {
    private PNode worldNode;
    private PNode screenNode;

    public PhetRootPNode() {
        worldNode = new PNode();
        screenNode = new PNode();
        addChild( worldNode );
        addChild( screenNode );
    }

    public PNode getWorldNode() {
        return worldNode;
    }

    public PNode getScreenNode() {
        return screenNode;
    }

    public void setWorldScale( double scale ) {
        worldNode.scale( scale / worldNode.getGlobalScale() );
    }

    public void addWorldChild( int layer, PNode graphic ) {
        worldNode.addChild( layer, graphic );
    }

    public void addWorldChild( PNode graphic ) {
        worldNode.addChild( graphic );
    }

    public void removeWorldChild( PNode graphic ) {
        worldNode.removeChild( graphic );
    }

    public void addScreenChild( PNode node ) {
        screenNode.addChild( node );
    }

    public void removeScreenChild( PNode node ) {
        screenNode.removeChild( node );
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

    public void setScreenNode( PNode screenNode ) {
        removeChild( this.screenNode );
        this.screenNode = screenNode;
        addChild( screenNode );
    }
}
