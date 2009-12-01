/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a strand of DNA in the model, which includes its
 * size, shape, location, and the locations of genes upon it.
 * 
 * @author John Blanco
 */
public class DnaStrand {
	
	// Length of one "cycle" of the DNA strand, in nanometers.
	private static final double DNA_WAVE_LENGTH = 3;
	
	// Spacing between each "sample" that defines the DNA strand shape.
	// Larger numbers mean finer resolution.  This is in nanometers.
	private static final double DNA_SAMPLE_SPACING = 0.5;
	
	// Offset in the x direction between the two DNA strands.
	private static final double DNA_INTER_STRAND_OFFSET = 0.75;

	private DoubleGeneralPath strand1Shape = new DoubleGeneralPath();
	private DoubleGeneralPath strand2Shape = new DoubleGeneralPath();
	private final Dimension2D size;
	private Point2D position = new Point2D.Double();
	private ArrayList<GeneSegmentShape> geneSectionShapeList = new ArrayList<GeneSegmentShape>();
	
	public DnaStrand(Dimension2D size, Point2D initialPosition){
		this.size = new PDimension(size);
		setPosition(initialPosition);
		
		updateStrandShapes();
		
		// Create the gene segments, which are the places where genes will
		// eventually be placed.
		Shape lacIGeneShape = new LacIGene(null).getShape();
		geneSectionShapeList.add(new GeneSegmentShape(lacIGeneShape, new Point2D.Double(-40, 0)));
		Shape lacPromoterShape = new LacPromoter(null).getShape();
		geneSectionShapeList.add(new GeneSegmentShape(lacPromoterShape, new Point2D.Double(5, 0)));
		Shape lacOperatorShape = new LacOperator(null).getShape();
		geneSectionShapeList.add(new GeneSegmentShape(lacOperatorShape, new Point2D.Double(15, 0)));
		Shape lacZGeneShape = new LacZGene(null).getShape();
		geneSectionShapeList.add(new GeneSegmentShape(lacZGeneShape, new Point2D.Double(30, 0)));
	}
	
	public Point2D getPositionRef() {
		return position;
	}

	public void setPosition(double x, double y) {
		position.setLocation(x, y);
	}
	
	public void setPosition(Point2D newPosition) {
		setPosition(newPosition.getX(), newPosition.getY());
	}

	public Dimension2D getSize() {
		return size;
	}
	
	public Shape getStrand1Shape(){
		return strand1Shape.getGeneralPath();
	}
	
	public Shape getStrand2Shape(){
		return strand2Shape.getGeneralPath();
	}
	
	/**
	 * Get a list of the shapes that represent the spaces on the DNA strand
	 * where the genes should go.
	 */
	public ArrayList<GeneSegmentShape> getGeneSegmentShapeList(){
		return new ArrayList<GeneSegmentShape>(geneSectionShapeList);
	}
	
	private void updateStrandShapes(){
		double startPosX = -size.getWidth() / 2;
		double startPosY = 0;
		strand1Shape.moveTo(startPosX, startPosY);
		strand2Shape.moveTo(startPosX + DNA_INTER_STRAND_OFFSET, startPosY);
		double angle = 0;
		double angleIncrement = Math.PI * 2 * DNA_SAMPLE_SPACING / DNA_WAVE_LENGTH;
		for (double xPos = startPosX; xPos - startPosX - DNA_INTER_STRAND_OFFSET < size.getWidth(); xPos += DNA_SAMPLE_SPACING){
			strand1Shape.lineTo( (float)xPos, (float)(-Math.sin(angle) * size.getHeight() / 2));
			strand2Shape.lineTo( (float)xPos + DNA_INTER_STRAND_OFFSET,
					(float)(-Math.sin(angle) * size.getHeight() / 2));
			angle += angleIncrement;
		}
	}
	
	public static class GeneSegmentShape extends Area {

		private final Point2D offsetFromDnaStrandPos = new Point2D.Double();
		
		public GeneSegmentShape(Shape s, Point2D offsetFromDnaStrandPos) {
			super(s);
			this.offsetFromDnaStrandPos.setLocation(offsetFromDnaStrandPos);
		}
		
		public Point2D getOffsetFromDnaStrandPosRef(){
			return offsetFromDnaStrandPos;
		}
	}
}
