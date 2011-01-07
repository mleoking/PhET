// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.genenetwork.GeneNetworkStrings;

public class LacIGene extends SimpleModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final double WIDTH = 20;  // Nanometers. 
	private static final double HEIGHT = 4;  // Nanometers.
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	public LacIGene(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, new RoundRectangle2D.Double(-WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT, 1, 1),
				new Point2D.Double(), new Color(167, 167, 167), false, Double.POSITIVE_INFINITY);
	}
	
	public LacIGene(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public LacIGene(){
		this(null, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public String getHtmlLabel() {
		return GeneNetworkStrings.LAC_I_GENE_LABEL;
	}
	
	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}

	@Override
	protected boolean isInAllowableLocation() {
		// Find out if we are within range of our location on the DNA strand.
		return getPositionRef().distance(getModel().getDnaStrand().getLacIGeneLocation()) < LOCK_TO_DNA_DISTANCE;
	}

	@Override
	protected Point2D getDefaultLocation() {
		return getModel().getDnaStrand().getLacIGeneLocation();
	}
}
