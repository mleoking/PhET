/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Basic representation of an antineutrino in the view.
 * 
 * @author John Blanco
 */
public class AntineutrinoNode extends PNode{
	
	public AntineutrinoNode() {
		float diameter = (float)NuclearPhysicsConstants.ANTINEUTRINO_DIAMETER;
		PPath antineutrino = new PPath();
		antineutrino.setPaint(NuclearPhysicsConstants.ANTINEUTRINO_COLOR);
		antineutrino.setStroke(new BasicStroke(diameter/10));
		GeneralPath shape = new GeneralPath();
		shape.moveTo(0, 0);
		shape.lineTo(diameter, 0);
		shape.lineTo(diameter / 2, 0.7f * diameter);
		shape.lineTo(0, 0);
		shape.closePath();
		antineutrino.setPathTo(shape);
		antineutrino.setOffset(-diameter/ 2, -diameter * 0.35);
		addChild(antineutrino);
	}
}
