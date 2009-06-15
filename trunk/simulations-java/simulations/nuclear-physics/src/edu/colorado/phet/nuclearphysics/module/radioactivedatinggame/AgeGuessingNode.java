package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * This class presents a dialog to the user and allows them to guess the age
 * of a datable item.
 * 
 * @author John Blanco
 */
public class AgeGuessingNode extends PNode {
	
	private static double WIDTH = 100;
	private static double HEIGHT = WIDTH / 2;
	
	public PhetPPath _boundingRect = new PhetPPath(Color.PINK);

	public AgeGuessingNode() {
		_boundingRect.setPathTo(new Rectangle2D.Double(0, 0, WIDTH, HEIGHT));
		addChild(_boundingRect);
	}
}
