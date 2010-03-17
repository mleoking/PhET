package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public class DnaStrandWithLacY extends DnaStrand {

	protected DnaSegmentSpace lacYGeneSpace;
	
	public DnaStrandWithLacY(IGeneNetworkModelControl model, Dimension2D size, Point2D initialPosition) {
		super(model, size, initialPosition);
		
		lacYGeneSpace = new DnaSegmentSpace(this, new LacYGene().getShape(), new Point2D.Double(40, 0));
		shapeList.add(lacYGeneSpace);
	}
}
