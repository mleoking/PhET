/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;


/**
 * This node is intended for use on gene segments that need to have a DNA
 * strand in the background, but where there is no actual corresponding DNA
 * strand in the model.
 * 
 * @author John Blanco
 *
 */
public class DnaBackgroundNode extends PNode {
	
	private Stroke STRAND_STROKE = new BasicStroke(1f);
	private Color STRAND1_COLOR = Color.BLACK;
	private Color STRAND2_COLOR = Color.BLACK;
	private double POINT_SPACING = 0.1;
	
	public DnaBackgroundNode(Dimension2D size, double cycles, double interStrandOffset) {
		
		DoubleGeneralPath strand1Shape = new DoubleGeneralPath();
		DoubleGeneralPath strand2Shape = new DoubleGeneralPath();
		
		double startPosX = -size.getWidth()/2;
		double startPosY = 0;
		strand1Shape.moveTo(startPosX, startPosY);
		strand2Shape.moveTo(startPosX + interStrandOffset, startPosY);
		
		double angle = 0;
		double angleIncrement = Math.PI * 2 * POINT_SPACING * cycles / size.getWidth();
		for (double xPos = startPosX; xPos + interStrandOffset < size.getWidth() / 2; xPos += POINT_SPACING){
			strand1Shape.lineTo( (float)xPos, (float)(-Math.sin(angle) * size.getHeight() / 2));
			strand2Shape.lineTo( (float)xPos + interStrandOffset,
					(float)(-Math.sin(angle) * size.getHeight() / 2));
			angle += angleIncrement;
		}
		
		PhetPPath strand1 = new PhetPPath(strand1Shape.getGeneralPath(), STRAND_STROKE, STRAND1_COLOR);
		addChild(strand1);
		PhetPPath strand2 = new PhetPPath(strand2Shape.getGeneralPath(), STRAND_STROKE, STRAND2_COLOR);
		addChild(strand2);
	}
}
