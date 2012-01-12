// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.genenetwork.GeneNetworkStrings;

public class LacYGene extends SimpleModelElement {

	private static final double WIDTH = 20;  // Nanometers. 
	private static final double HEIGHT = 4;  // Nanometers.
	
	public LacYGene(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 1, 1),
				initialPosition, new Color(138, 198, 118), false, Double.POSITIVE_INFINITY);
	}
	
	public LacYGene(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public LacYGene(){
		this(null, new Point2D.Double());
	}
	
	@Override
	public String getHtmlLabel() {
		return GeneNetworkStrings.LAC_Y_GENE_LABEL;
	}
	
	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}
	
	@Override
	protected boolean isInAllowableLocation() {
		// Find out if we are within range of our location on the DNA strand.
		return getPositionRef().distance(getModel().getDnaStrand().getLacYGeneLocation()) < LOCK_TO_DNA_DISTANCE;
	}
	
	@Override
	protected Point2D getDefaultLocation() {
		return getModel().getDnaStrand().getLacYGeneLocation();
	}
}
