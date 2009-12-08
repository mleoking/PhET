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
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	// Length of one "cycle" of the DNA strand, in nanometers.
	private static final double DNA_WAVE_LENGTH = 3;
	
	// Spacing between each "sample" that defines the DNA strand shape.
	// Larger numbers mean finer resolution.  This is in nanometers.
	private static final double DNA_SAMPLE_SPACING = 0.5;
	
	// Offset in the x direction between the two DNA strands.
	private static final double DNA_INTER_STRAND_OFFSET = 0.75;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private DoubleGeneralPath strand1Shape = new DoubleGeneralPath();
	private DoubleGeneralPath strand2Shape = new DoubleGeneralPath();
	private final Dimension2D size;
	private Point2D position = new Point2D.Double();
	
	// The "spaces" or shapes where specific pieces of the strand, such as a
	// gene, can reside.
	private DnaSegmentSpace lacIGeneSpace;
	private DnaSegmentSpace lacPromoterSpace;
	private DnaSegmentSpace lacOperatorSpace;
	private DnaSegmentSpace lacZGeneSpace;
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public DnaStrand(IGeneNetworkModelControl model, Dimension2D size, Point2D initialPosition){
		
		this.size = new PDimension(size);
		setPosition(initialPosition);
		
		updateStrandShapes();
		
		// Set the offsets for the various DNA segments.
		lacIGeneSpace = new DnaSegmentSpace(new LacIGene(null).getShape(), new Point2D.Double(-40, 0));
		lacPromoterSpace = new DnaSegmentSpace(new LacPromoter(null).getShape(), new Point2D.Double(5, 0));
		lacOperatorSpace = new DnaSegmentSpace(new LacOperator(null).getShape(), new Point2D.Double(15, 0));
		lacZGeneSpace = new DnaSegmentSpace(new LacZGene(null).getShape(), new Point2D.Double(30, 0));
		
		// Register for model events that concern us.
		model.addListener(new GeneNetworkModelAdapter(){
			public void modelElementAdded(SimpleModelElement modelElement) {
				handleModelElementAdded(modelElement);
			}
		});
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
	 * Get the location in absolute model space (i.e. not relative to the DNA
	 * strand's position) of the LacZGene space on the DNA strand.
	 * 
	 * @return
	 */
	public Point2D getLacZGeneLocation(){
		return new Point2D.Double(getPositionRef().getX() + lacZGeneSpace.getOffsetFromDnaStrandPosRef().getX(),
				getPositionRef().getY() + lacZGeneSpace.getOffsetFromDnaStrandPosRef().getY());
	}
	
	/**
	 * Get the location in absolute model space (i.e. not relative to the DNA
	 * strand's position) of the LacIGene space on the DNA strand.
	 * 
	 * @return
	 */
	public Point2D getLacIGeneLocation(){
		return new Point2D.Double(getPositionRef().getX() + lacIGeneSpace.getOffsetFromDnaStrandPosRef().getX(),
				getPositionRef().getY() + lacIGeneSpace.getOffsetFromDnaStrandPosRef().getY());
	}
	
	/**
	 * Get the location in absolute model space (i.e. not relative to the DNA
	 * strand's position) of the lac promoter space on the DNA strand.
	 * 
	 * @return
	 */
	public Point2D getLacPromoterLocation(){
		return new Point2D.Double(getPositionRef().getX() + lacPromoterSpace.getOffsetFromDnaStrandPosRef().getX(),
				getPositionRef().getY() + lacPromoterSpace.getOffsetFromDnaStrandPosRef().getY());
	}
	
	/**
	 * Get the location in absolute model space (i.e. not relative to the DNA
	 * strand's position) of the lac operator space on the DNA strand.
	 * 
	 * @return
	 */
	public Point2D getLacOperatorLocation(){
		return new Point2D.Double(getPositionRef().getX() + lacOperatorSpace.getOffsetFromDnaStrandPosRef().getX(),
				getPositionRef().getY() + lacOperatorSpace.getOffsetFromDnaStrandPosRef().getY());
	}
	
	/**
	 * Get a list of the spaces in the DNA strand where genes and promoters
	 * and all that will eventually go.
	 */
	public ArrayList<DnaSegmentSpace> getDnaSegmentSpaces(){
		ArrayList<DnaSegmentSpace> shapeList = new ArrayList<DnaSegmentSpace>();
		shapeList.add(lacIGeneSpace);
		shapeList.add(lacPromoterSpace);
		shapeList.add(lacOperatorSpace);
		shapeList.add(lacZGeneSpace);
		return shapeList;
	}
	
	private void handleModelElementAdded(SimpleModelElement modelElement){
		// Set the "eye catching" mode for any model elements that are added,
		// and register to clear it if the element is removed.
		if (modelElement instanceof LacIGene){
			lacIGeneSpace.setEyeCatching(true);
			modelElement.addListener(new ModelElementListenerAdapter(){
				public void removedFromModel() {
					// Become non-eyecatching when element is removed.
					lacIGeneSpace.setEyeCatching(false);
				};
			});
		}
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
	
    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

	/**
	 * This class represents a space on the DNA segment where a gene or a
	 * promoter or whatever can reside.
	 */
	public static class DnaSegmentSpace extends Area {

		// Offset in space from the DNA strand where this space exists.
		private final Point2D offsetFromDnaStrandPos = new Point2D.Double();
		
		// State variable to says whether this space should be presented to
		// the user in a normal or "eye catching" manner.
		private boolean eyeCatching = false;

	    protected ArrayList<DnaSegmentSpace.Listener> listeners = new ArrayList<DnaSegmentSpace.Listener>();
		
		public DnaSegmentSpace(Shape s, Point2D offsetFromDnaStrandPos) {
			super(s);
			this.offsetFromDnaStrandPos.setLocation(offsetFromDnaStrandPos);
		}
		
		public void addListener(DnaSegmentSpace.Listener listener) {
			if (listeners.contains( listener ))
			{
				// Don't bother re-adding.
				System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
				assert false;
				return;
			}
			
			listeners.add( listener );
		}
		
		public void removeListener(DnaSegmentSpace.Listener listener){
			listeners.remove(listener);
		}
		
		public Point2D getOffsetFromDnaStrandPosRef(){
			return offsetFromDnaStrandPos;
		}
		
		public boolean isEyeCatching(){
			return eyeCatching;
		}
		
		public void setEyeCatching(boolean eyeCatching){
			if (this.eyeCatching != eyeCatching){
				this.eyeCatching = eyeCatching;
				notifyEyeCatchingStateChanged();
			}
		}
		
		private void notifyEyeCatchingStateChanged(){
			// Notify all listeners of the change to the eye catching state.
			for (Listener listener : listeners)
			{
				listener.eyeCatchingStateChange();
			}
		}
		
		public interface Listener {
			void eyeCatchingStateChange();
		}
	}
}
