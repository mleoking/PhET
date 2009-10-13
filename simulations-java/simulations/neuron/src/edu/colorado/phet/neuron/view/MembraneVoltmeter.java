/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a voltmeter in the view that is capable of measuring
 * the potential between various points in and out of the cell membrane.
 * 
 * @author John Blanco
 */
public class MembraneVoltmeter extends PNode {

	private static final Dimension2D METER_BODY_DIMENSIONS = new PDimension(150, 230);  // In screen coords, which are essentially pixels.
	private static final Color METER_BODY_COLOR = Color.GRAY;
	
	private PPath meterBody;

	public MembraneVoltmeter() {
		meterBody =	new PPath(new RoundRectangle2D.Double(0, 0, METER_BODY_DIMENSIONS.getWidth(),
				METER_BODY_DIMENSIONS.getHeight(), METER_BODY_DIMENSIONS.getWidth()/5, METER_BODY_DIMENSIONS.getWidth()/5));
		meterBody.setPaint(METER_BODY_COLOR);
		addChild(meterBody);
	}
}
