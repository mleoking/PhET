/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Shape;
import java.awt.geom.Line2D;

/**
 * This class represents a "stratum" in the model, which means a layer of rock
 * and/or soil with internally consistent characteristics that distinguishes 
 * it from contiguous layers (definition obtained from wikipedia).
 */
public class Stratum {
    private double bottomOfStratumY;
    private double height;
    private Shape shape;
    private Shape _topLine;
    private Shape _bottomLine;

    public Stratum( double bottomOfStratumY, double height, double width, boolean squiggle){

    	this.bottomOfStratumY = bottomOfStratumY;
        this.height = height;
    	
        if (!squiggle){
            _topLine = new Line2D.Double(-width / 2, bottomOfStratumY + height, width / 2, bottomOfStratumY + height);
            _bottomLine = new Line2D.Double(-width / 2, bottomOfStratumY, width / 2, bottomOfStratumY);
    	}
        else{
        	
        }
    }
    
    public Stratum( double bottomOfStratumY, double height ) {
        this.bottomOfStratumY = bottomOfStratumY;
        this.height = height;
        _topLine = new Line2D.Double(-1000,bottomOfStratumY+height,1000,bottomOfStratumY+height);
        _bottomLine = new Line2D.Double(-1000,bottomOfStratumY,1000,bottomOfStratumY);
    }
    
    public Stratum( Shape topLine, Shape bottomLine){
    	
    	_topLine = topLine;
    	_bottomLine = bottomLine;
    	bottomOfStratumY = _bottomLine.getBounds2D().getMaxY();  // TODO: Not sure if this is correct.
    }

    public double getBottomOfStratumY() {
        return bottomOfStratumY;
    }

    public double getHeight() {
        return height;
    }

    public Shape getTopLine() {
        return _topLine;
    }

    public Shape getBottomLine() {
        return _bottomLine;
    }
}
