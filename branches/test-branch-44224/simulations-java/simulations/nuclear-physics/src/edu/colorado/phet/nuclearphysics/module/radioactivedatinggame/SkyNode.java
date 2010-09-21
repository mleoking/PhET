/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

public class SkyNode extends PNode {

	private final double _skyWidth;
	private final double _skyHeight;
	
	public SkyNode( double width, double height ) {
		
		_skyWidth = width;
		_skyHeight = height;
		
		PPath sky = new PPath( new Rectangle2D.Double( 0, 0, _skyWidth, _skyHeight));
		GradientPaint skyGradient = new GradientPaint(0, 0, new Color(0, 150, 200), 0,
				(float)_skyHeight, new Color(255, 255, 255), false);
		sky.setPaint(skyGradient);
		sky.setOffset(-_skyWidth / 2, -_skyHeight);
		addChild(sky);
	}
}
