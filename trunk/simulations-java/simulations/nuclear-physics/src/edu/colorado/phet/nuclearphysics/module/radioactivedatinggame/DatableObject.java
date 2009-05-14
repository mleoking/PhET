/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;

/**
 * This class represents a physical object that can be dated using radiometric
 * measurements, such as a skull or a fossil or a tree.
 * 
 */
public class DatableObject {

	private final Point2D center;
	private final double width;
	private final double height;
	private final double age;
	private final String name;
	private final String resourceImageName;
	private final BufferedImage image;
	
	public DatableObject(String name, String resourceImageName, Point2D center, double width, double age) {
		super();
		this.name = name;
		this.center = new Point2D.Double(center.getX(), center.getY());
		this.width = width;
		this.age = age;
		this.resourceImageName = resourceImageName;
		
		image = NuclearPhysicsResources.getImage(resourceImageName);
		
		// The height is defined by a combination of the width of the artifact
		// and the aspect ratio of the image.
		this.height = (double)image.getHeight() / (double)image.getWidth() * width;
	}

	public Point2D getCenter() {
		return new Point2D.Double(center.getX(), center.getY());
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
	
	/**
	 * Get the age of the item in milliseconds.
	 */
	public double getAge() {
		return age;
	}

	public String getResourceImageName() {
		return resourceImageName;
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + name;
	}
	
	public boolean contains(Point2D pt){
		
		return getBoundingRect().contains(pt);
		
	}
	
	public Rectangle2D getBoundingRect(){
		return new Rectangle2D.Double( center.getX() - width /2, center.getY() - height/2, width, height );
	}
}
