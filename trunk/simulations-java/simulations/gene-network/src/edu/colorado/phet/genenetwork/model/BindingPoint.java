package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;

/**
 * This class defines the point at which a particular model element type
 * can be bound to another.  Each binding point is defined by the type
 * of element bonded and the offset with respect to this element's
 * position.
 * 
 * @author John Blanco
 */
public class BindingPoint{
	private final ModelElementType elementType;
	private final Dimension2D offset;
	
	public BindingPoint(ModelElementType elementType, Dimension2D offset) {
		super();
		this.elementType = elementType;
		this.offset = offset;
	}

	public ModelElementType getElementType() {
		return elementType;
	}

	public Dimension2D getOffset() {
		return offset;
	}
}