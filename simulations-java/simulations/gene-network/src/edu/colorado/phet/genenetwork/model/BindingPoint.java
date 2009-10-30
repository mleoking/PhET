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
	private final SimpleElementType elementType;
	private final Dimension2D offset;
	
	public BindingPoint(SimpleElementType elementType, Dimension2D offset) {
		super();
		this.elementType = elementType;
		this.offset = offset;
	}

	public SimpleElementType getElementType() {
		return elementType;
	}

	public Dimension2D getOffset() {
		return offset;
	}
}