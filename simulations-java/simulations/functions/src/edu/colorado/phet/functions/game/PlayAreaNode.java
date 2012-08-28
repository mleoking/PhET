// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.functions.intro.Scenes;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas.STAGE_SIZE;

/**
 * @author Sam Reid
 */
public class PlayAreaNode extends PNode {
    public PlayAreaNode() {
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height / 2 ), Color.white, new BasicStroke( 1 ), Color.black ) );
        PNode faceNode = Scenes.createFaceNode();
        faceNode.setOffset( STAGE_SIZE.width / 2 - faceNode.getFullBounds().getWidth() / 2, STAGE_SIZE.height / 4 - faceNode.getFullBounds().getHeight() / 2 );
        addChild( faceNode );
    }
}