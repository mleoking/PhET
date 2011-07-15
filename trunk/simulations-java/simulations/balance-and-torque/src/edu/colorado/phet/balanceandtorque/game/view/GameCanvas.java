// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author John Blanco
 */
public class GameCanvas extends PhetPCanvas {
    public GameCanvas() {
        setBackground( Color.BLACK );
        addWorldChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ), Color.PINK ) );
        addWorldChild( new PImage( Images.LEVER_INTO_WHEELBARROW ) );
    }
}
