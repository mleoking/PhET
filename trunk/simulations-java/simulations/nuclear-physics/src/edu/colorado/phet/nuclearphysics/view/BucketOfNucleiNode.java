/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

public class BucketOfNucleiNode extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	public static final Stroke LINE_STROKE = new BasicStroke( 0.5f );
	public static final Color OUTER_COLOR_DARK = new Color (0xAA7700);
	public static final Color OUTER_COLOR_LIGHT = new Color (0xFF9933);
	public static final Color INNER_COLOR_DARK = new Color (0xAA7700);
	public static final Color INNER_COLOR_LIGHT = new Color (0xCC9933);
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	/**
	 * Constructor - This takes a width and height in world coordinates.
	 */
	public BucketOfNucleiNode(double width, double height){
		
		// Create the gradient paints that will be used to paint the bucket.
		GradientPaint outerPaint = new GradientPaint(0, (float)height/2, OUTER_COLOR_DARK, 
        		(float)width, (float)height/2, OUTER_COLOR_LIGHT);
		
		GradientPaint innerPaint = new GradientPaint(0, (float)height/2, INNER_COLOR_LIGHT, 
        		(float)width, (float)height/2, INNER_COLOR_DARK);
		
		// TODO: JPB TBD - A basic rect for guidance, remove when this node is done.
		PhetPPath outerRect = new PhetPPath(new Rectangle2D.Double( 0, 0, width, height ));
		outerRect.setStroke( new BasicStroke( 0.25f ) );
		outerRect.setStrokePaint(Color.red);
		addChild(outerRect);
		
		// Create a layering effect using PNodes so that we can create the
		// illusion of three dimensions.
		PNode backLayer = new PNode();
		addChild( backLayer );
		PNode middleLayer = new PNode();
		addChild( middleLayer );
		PNode frontLayer = new PNode();
		addChild( frontLayer );
		
		double ellipseVerticalSpan = height * 0.4;
		
		// Draw the inside of the bucket.
		PhetPPath ellipseInBackOfBucket = new PhetPPath(new Ellipse2D.Double( 0, 0, width, ellipseVerticalSpan ));
		ellipseInBackOfBucket.setStroke( LINE_STROKE );
		ellipseInBackOfBucket.setPaint(innerPaint);
		backLayer.addChild(ellipseInBackOfBucket);

		// Draw the outside of the bucket.
		GeneralPath bucketBodyPath = new GeneralPath();
		bucketBodyPath.moveTo(0, (float)(ellipseVerticalSpan / 2));
		bucketBodyPath.lineTo((float)(width * 0.1), (float)(height * 0.8));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height * 1.1), (float)(width * 0.9), (float)(height * 0.8));
		bucketBodyPath.lineTo((float)(width), (float)(ellipseVerticalSpan / 2));
		bucketBodyPath.quadTo((float)(width / 2), (float)(height * 0.6), 0, (float)(ellipseVerticalSpan / 2));
		bucketBodyPath.closePath();
		PhetPPath bucketBody = new PhetPPath( bucketBodyPath );
		bucketBody.setStroke( LINE_STROKE );
		bucketBody.setPaint(outerPaint);
		frontLayer.addChild(bucketBody);
		
		// Draw the handle.
		PhetPPath bucketHandle = new PhetPPath( new QuadCurve2D.Double(width/2, ellipseVerticalSpan, width * 1.6, 
				ellipseVerticalSpan * 2, width, ellipseVerticalSpan / 2) );
		bucketHandle.setStroke( LINE_STROKE );
		frontLayer.addChild(bucketHandle);
	}
}
