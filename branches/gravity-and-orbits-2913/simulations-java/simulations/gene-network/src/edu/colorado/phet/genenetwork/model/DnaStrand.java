// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
	
	// Distance value used when trying to determine whether a given point is
	// just above a location on the DNA strand.
	protected static final double RANGE_FOR_PROXIMITRY_TEST = 3;  // In nanometers.
	
	// Error range for comparing floating point values.
	protected static final double ERROR_RANGE = 0.01;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	protected IGeneNetworkModelControl model;
	private DoubleGeneralPath strand1Shape = new DoubleGeneralPath();
	private DoubleGeneralPath strand2Shape = new DoubleGeneralPath();
	private final Dimension2D size;
	private Point2D position = new Point2D.Double();
	
	// The "spaces" or shapes where specific pieces of the strand, such as a
	// gene, can reside.
	private DnaSegmentSpace lacIPromoterSpace;
	private DnaSegmentSpace lacIGeneSpace;
	private DnaSegmentSpace lacPromoterSpace;
	private DnaSegmentSpace lacOperatorSpace;
	private DnaSegmentSpace lacZGeneSpace;

	protected ArrayList<DnaSegmentSpace> shapeList = new ArrayList<DnaSegmentSpace>();
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public DnaStrand(IGeneNetworkModelControl model, Dimension2D size, Point2D initialPosition){
		
		this.model = model;
		this.size = new PDimension(size);
		setPosition(initialPosition);
		
		updateStrandShapes();
		
		// Set the offsets for the various DNA segments.
		lacIPromoterSpace = new DnaSegmentSpace(this, new LacIPromoter(null).getShape(), new Point2D.Double(-52, 0));
		lacIGeneSpace = new DnaSegmentSpace(this, new LacIGene(null).getShape(), new Point2D.Double(-35, 0));
		lacPromoterSpace = new DnaSegmentSpace(this, new LacPromoter(null).getShape(), new Point2D.Double(5, 0));
		lacOperatorSpace = new DnaSegmentSpace(this, new LacOperator(null).getShape(), new Point2D.Double(15, 0));
		lacZGeneSpace = new DnaSegmentSpace(this, new LacZGene(null).getShape(), new Point2D.Double(30, 0));

		shapeList.add(lacIPromoterSpace);
		shapeList.add(lacIGeneSpace);
		shapeList.add(lacPromoterSpace);
		shapeList.add(lacOperatorSpace);
		shapeList.add(lacZGeneSpace);

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
	 * Get a boolean value indicating whether the provided point is just above
	 * the LacZ gene location on the DNA strand.  This is generally used when
	 * traversing the DNA strand in order to know what should be transcribed.
	 * 
	 * @param pt
	 * @return
	 */
	public boolean isOnLacZGeneSpace(Point2D pt){
		Rectangle2D uncompensatedBounds = lacZGeneSpace.getBounds2D();
		Rectangle2D compensatedBounds = new Rectangle2D.Double(
			uncompensatedBounds.getX() + lacZGeneSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX(),
			uncompensatedBounds.getY() + lacZGeneSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY(),
			uncompensatedBounds.getWidth(),
			uncompensatedBounds.getHeight() + RANGE_FOR_PROXIMITRY_TEST);
		return compensatedBounds.contains(pt);
	}
	
	/**
	 * Get a boolean value indicating whether the provided point is just above
	 * the LacY gene location on the DNA strand.  This is generally used when
	 * traversing the DNA strand in order to know what should be transcribed.
	 * 
	 * NOTE: LacY is not present in the base class, so this is always false.
	 * This should be overridden in subclasses that add LacY.
	 * 
	 * @param pt
	 * @return
	 */
	public boolean isOnLacYGeneSpace(Point2D pt){
		return false;
	}
	
	/**
	 * Returns true if the LacZ gene is in place on the DNA strand and false
	 * if not.
	 * 
	 * @return
	 */
	public boolean isLacZGeneInPlace(){
		LacZGene lacZGene = model.getLacZGene();
		boolean lacZGeneInPlace = false;
		if (lacZGene != null){
			double xPos = lacZGeneSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX();
			double yPos = lacZGeneSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY();
			// Use a range for this test to account for floating point errors.
			if ( lacZGene.getPositionRef().getX() + ERROR_RANGE > xPos &&
				 lacZGene.getPositionRef().getX() - ERROR_RANGE <  xPos &&
				 lacZGene.getPositionRef().getY() + ERROR_RANGE > yPos &&
				 lacZGene.getPositionRef().getY() - ERROR_RANGE <  yPos ){
				
				lacZGeneInPlace = true;
			}
		}
		
		return lacZGeneInPlace;
	}
	
	/**
	 * Returns true if the LacY gene is in place on the DNA strand and false
	 * if not.
	 * 
	 * NOTE: LacY doesn't exist in the base class, so this must be overridden
	 * in classes that include LacY.
	 * 
	 * @return
	 */
	public boolean isLacYGeneInPlace(){
		return false;
	}
	
	/**
	 * Get a boolean value indicating whether the provided point is just above
	 * the LacI gene location on the DNA strand.  This is generally used when
	 * traversing the DNA strand in order to know what should be transcribed.
	 * 
	 * @param pt
	 * @return
	 */
	public boolean isOnLacIGeneSpace(Point2D pt){
		Rectangle2D uncompensatedBounds = lacIGeneSpace.getBounds2D();
		Rectangle2D compensatedBounds = new Rectangle2D.Double(
			uncompensatedBounds.getX() + lacIGeneSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX(),
			uncompensatedBounds.getY() + lacIGeneSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY(),
			uncompensatedBounds.getWidth(),
			uncompensatedBounds.getHeight() + RANGE_FOR_PROXIMITRY_TEST);
		return compensatedBounds.contains(pt);
	}
	
	/**
	 * Returns true if the LacZ gene is in place on the DNA strand and false
	 * if not.
	 * 
	 * @return
	 */
	public boolean isLacIGeneInPlace(){
		LacIGene lacIGene = model.getLacIGene();
		boolean lacIGeneInPlace = false;
		if (lacIGene != null){
			double xPos = lacIGeneSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX();
			double yPos = lacIGeneSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY();
			// Use a range for this test to account for floating point errors.
			if ( lacIGene.getPositionRef().getX() + ERROR_RANGE > xPos &&
				 lacIGene.getPositionRef().getX() - ERROR_RANGE <  xPos &&
				 lacIGene.getPositionRef().getY() + ERROR_RANGE > yPos &&
				 lacIGene.getPositionRef().getY() - ERROR_RANGE <  yPos ){
				
				lacIGeneInPlace = true;
			}
		}
		
		return lacIGeneInPlace;
	}
	
	/**
	 * Get a boolean value indicating whether the provided point is just above
	 * the lac operator (a.k.a. the lac I binding location) on the DNA strand.
	 * This is generally used when traversing the DNA strand in order to know
	 * if the traversal is blocked.
	 * 
	 * @param pt
	 * @return
	 */
	public boolean isOnLacOperatorSpace(Point2D pt){
		Rectangle2D uncompensatedBounds = lacOperatorSpace.getBounds2D();
		Rectangle2D compensatedBounds = new Rectangle2D.Double(
			uncompensatedBounds.getX() + lacOperatorSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX(),
			uncompensatedBounds.getY() + lacOperatorSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY(),
			uncompensatedBounds.getWidth(),
			uncompensatedBounds.getHeight() + RANGE_FOR_PROXIMITRY_TEST);
		return compensatedBounds.contains(pt);
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
	 * strand's position) of the LacYGene space on the DNA strand.
	 * 
	 * NOTE: LacY doesn't exist in this class, but there needs to be an
	 * interface for getting it.  This should be overridden in any subclasses
	 * that add LacY.
	 * 
	 * @return
	 */
	public Point2D getLacYGeneLocation(){
		return null;
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
	 * strand's position) of the lac I promoter space on the DNA strand.
	 * 
	 * @return
	 */
	public Point2D getLacIPromoterLocation(){
		return new Point2D.Double(getPositionRef().getX() + lacIPromoterSpace.getOffsetFromDnaStrandPosRef().getX(),
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
		ArrayList<DnaSegmentSpace> shapeListCopy = new ArrayList<DnaSegmentSpace>(shapeList);
		return shapeListCopy;
	}
	
	protected void handleModelElementAdded(SimpleModelElement modelElement){
		// Set the "eye catching" mode for any model elements that are added,
		// and register to clear it if the element is removed.
		if (modelElement instanceof LacIPromoter){
			lacIPromoterSpace.setEyeCatching(true);
			lacIPromoterSpace.setModelElement(modelElement);
		}
		else if (modelElement instanceof LacIGene){
			lacIGeneSpace.setEyeCatching(true);
			lacIGeneSpace.setModelElement(modelElement);
		}
		else if (modelElement instanceof LacZGene){
			lacZGeneSpace.setEyeCatching(true);
			lacZGeneSpace.setModelElement(modelElement);
		}
		else if (modelElement instanceof LacPromoter){
			lacPromoterSpace.setEyeCatching(true);
			lacPromoterSpace.setModelElement(modelElement);
		}
		else if (modelElement instanceof LacOperator){
			lacOperatorSpace.setEyeCatching(true);
			lacOperatorSpace.setModelElement(modelElement);
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
		
		// Distance from the model element at which this space is considered
		// occupied.
		private static final double OCCUPIED_DISTANCE = 1; // In nanometers.
		
		// DNA strand on which this space exists.
		private final DnaStrand dnaStrand;

		// Offset in space from the DNA strand where this space exists.
		private final Point2D offsetFromDnaStrandPos = new Point2D.Double();
		
		// Model element that is associated with this space and that could
		// end up occupying the space.
		private SimpleModelElement modelElement = null;
		
		// State variable to says whether this space should be presented to
		// the user in a normal or "eye catching" manner.
		private boolean eyeCatching = false;
		
		// State variable that tracks whether or not this space is occupied,
		// meaining that it has a model element in it.
		private boolean occupied = false;
		
		// Listener for relevant events.
		private ModelElementListenerAdapter modelElementListener = new ModelElementListenerAdapter(){
			public void positionChanged(){
				updateOccupancyState();
			};
			public void removedFromModel() {
				setEyeCatching(false);
				modelElement = null;
				updateOccupancyState();
			};
		};

	    protected ArrayList<DnaSegmentSpace.Listener> listeners = new ArrayList<DnaSegmentSpace.Listener>();
		
		public DnaSegmentSpace(DnaStrand dnaStrand, Shape s, Point2D offsetFromDnaStrandPos) {
			super(s);
			this.dnaStrand = dnaStrand;
			this.offsetFromDnaStrandPos.setLocation(offsetFromDnaStrandPos);
		}
		
		public void setModelElement(SimpleModelElement modelElement){
			this.modelElement = modelElement;
			modelElement.addListener(modelElementListener);
			updateOccupancyState();
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
		
		/**
		 * Get the position in absolute model space.
		 * 
		 * @return
		 */
		private Point2D getAbsolutePosition(){
			return new Point2D.Double(dnaStrand.getPositionRef().getX() + offsetFromDnaStrandPos.getX(),
					dnaStrand.getPositionRef().getY() + offsetFromDnaStrandPos.getY());
		}
		
		public boolean isOccupied(){
			return occupied;
		}
		
		private void updateOccupancyState(){
			if (occupied == false){
				if (modelElement != null && modelElement.getPositionRef().distance(getAbsolutePosition()) < OCCUPIED_DISTANCE){
					occupied = true;
					notifyOccupiedStateChanged();
				}
			}
			else if (occupied == true){
				if (modelElement == null || modelElement.getPositionRef().distance(getAbsolutePosition()) > OCCUPIED_DISTANCE){
					// The model element has been removed from the model or
					// moved far away from the strand, so this space should be
					// considered unoccupied.
					occupied = false;
					notifyOccupiedStateChanged();
				}
			}
		}
		
		private void notifyEyeCatchingStateChanged(){
			// Notify all listeners of the change to the eye catching state.
			for (Listener listener : listeners)
			{
				listener.eyeCatchingStateChange();
			}
		}
		
		private void notifyOccupiedStateChanged(){
			// Notify all listeners of the change to the occupied state.
			for (Listener listener : listeners)
			{
				listener.occupiedStateChange();
			}
		}
		
		public interface Listener {
			void eyeCatchingStateChange();
			void occupiedStateChange();
		}
	}
}
