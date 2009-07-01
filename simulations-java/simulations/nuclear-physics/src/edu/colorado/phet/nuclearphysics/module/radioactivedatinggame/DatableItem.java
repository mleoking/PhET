/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a physical object that can be dated using radiometric
 * measurements, such as a skull or a fossil or a tree.
 * 
 */
public class DatableItem implements AnimatedModelElement {

	private double width;
	private double height;
	private final double age;
	private final String name;
	private final String resourceImageName;
	private double rotationAngle; // In radians.
	private Point2D center;
	private BufferedImage image;
	private ArrayList<ModelAnimationListener> animationListeners = new ArrayList<ModelAnimationListener>();


	
	public DatableItem(String name, String resourceImageName, Point2D center, double width, 
			double rotationAngle, double age) {
		super();
		this.name = name;
		this.center = new Point2D.Double(center.getX(), center.getY());
		this.width = width;
		this.age = age;
		this.resourceImageName = resourceImageName;
		this.rotationAngle = rotationAngle;
		
		image = NuclearPhysicsResources.getImage(resourceImageName);
		if (rotationAngle != 0){
			image = BufferedImageUtils.getRotatedImage(image, rotationAngle);
		}
		
		// The height is defined by a combination of the width of the artifact
		// and the aspect ratio of the image.
		this.height = (double)image.getHeight() / (double)image.getWidth() * width;
	}

	public Point2D getPosition() {
		return new Point2D.Double(center.getX(), center.getY());
	}
	
	public void setPosition(Point2D centerPoint) {
		center = new Point2D.Double(centerPoint.getX(), centerPoint.getY());
		notifyPositionChanged();
	}
	
	public void addAnimationListener(ModelAnimationListener listener) {
		if (!animationListeners.contains(listener)){
			animationListeners.add(listener);
		}
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
	
	/**
	 * Get the amount of a substance that would be left based on the age of an
	 * item and the half life of the nucleus of the radiometric material being
	 * tested.
	 * 
	 * @param item
	 * @param customNucleusHalfLife
	 * @return
	 */
	public static double getPercentageCustomNucleusRemaining( DatableItem item, double customNucleusHalfLife ){
		return calculatePercentageRemaining(item.getAge(), customNucleusHalfLife);
	}
	
	public static double getPercentageCarbon14Remaining( DatableItem item ){
		return calculatePercentageRemaining(item.getAge(), Carbon14Nucleus.HALF_LIFE);
	}
	
	public static double getPercentageUranium238Remaining( DatableItem item ){
		return calculatePercentageRemaining(item.getAge(), Uranium238Nucleus.HALF_LIFE);
	}
	
	private static double calculatePercentageRemaining( double age, double halfLife ){
		if ( age <= 0 ){
			return 100;
		}
		else{
			return 100 * Math.exp( -0.693 * age / halfLife );
		}
	}
	
	public String getResourceImageName() {
		return resourceImageName;
	}

	public BufferedImage getImage() {
		return image;
	}
	
	protected void setImage( BufferedImage newImage ){
		image = newImage;
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

	public double getRotationalAngle() {
		return rotationAngle;
	}

	public Dimension2D getSize() {
		return new PDimension(width, height);
	}

	public void removeAllAnimationListeners() {
		animationListeners.clear();
	}

	public boolean removeAnimationListener(ModelAnimationListener listener) {
		return animationListeners.remove(listener);
	}

	public void setFadeFactor(double fadeFactor) {
		// TODO Auto-generated method stub
		
	}

	public void setRotationalAngle(double rotationalAngle) {
		this.rotationAngle = rotationalAngle;
		notifyRotationalAngleChanged();
	}

	public int getNumberImages() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPrimaryImageIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSecondaryImageIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPrimaryImageIndex(int imageIndex) {
		// TODO Auto-generated method stub
		
	}

	public void setSecondaryImageIndex(int imageIndex) {
		// TODO Auto-generated method stub
		
	}

	public double getFadeFactor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSize(Dimension2D size) {
		width = size.getWidth();
		height = size.getHeight();
		notifySizeChanged();
	}
	
	private void notifySizeChanged(){
		for (ModelAnimationListener listener : animationListeners){
			listener.sizeChanged();
		}
	}
	
	private void notifyPositionChanged(){
		for (ModelAnimationListener listener : animationListeners){
			listener.positionChanged();
		}
	}
	
	private void notifyRotationalAngleChanged(){
		for (ModelAnimationListener listener : animationListeners){
			listener.rotationalAngleChanged();
		}
	}
	
	private void notifyImageChanged(){
		for (ModelAnimationListener listener : animationListeners){
			listener.imageChanged();
		}
	}
}
