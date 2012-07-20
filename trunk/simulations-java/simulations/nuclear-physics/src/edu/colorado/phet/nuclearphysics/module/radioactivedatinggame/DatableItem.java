// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium238Nucleus;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a physical object that can be dated using radiometric
 * measurements, such as a skull or a fossil or a tree.
 */
public class DatableItem implements AnimatedModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // A static datable object that represents air, which is useful in a
    // couple of places in the sim.
    public static final DatableItem DATABLE_AIR = new DatableItem( "Datable Air", (ArrayList<String>) null,
                                                                   new Point2D.Double( 0, 0 ), 0, 0, 0, true );

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private double width;
    private double height;
    private final double age;
    private final String name;
    private double rotationAngle; // In radians.
    private Point2D center;
    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private ArrayList<ModelAnimationListener> animationListeners = new ArrayList<ModelAnimationListener>();
    private int primaryImageIndex;
    private int secondaryImageIndex;
    private double fadeFactor;
    private final boolean isOrganic;

    /**
     * Constructor.
     *
     * @param name
     * @param resourceImageNames
     * @param center
     * @param width
     * @param rotationAngle
     * @param age
     * @param isOrganic
     */
    public DatableItem( String name, List<String> resourceImageNames, Point2D center, double width,
                        double rotationAngle, double age, boolean isOrganic ) {
        super();

        this.name = name;
        this.center = new Point2D.Double( center.getX(), center.getY() );
        this.width = width;
        this.age = age;
        this.rotationAngle = rotationAngle;
        this.isOrganic = isOrganic;

        if ( resourceImageNames != null && resourceImageNames.size() != 0 ) {
            // Load the initial primary image, which is the first one on the list.
            // Note that the primary image can be changed later if desired.
            BufferedImage primaryImage = NuclearPhysicsResources.getImage( resourceImageNames.get( 0 ) );
            images.add( primaryImage );

            // Calculate the height, which is defined by a combination of the
            // prescribed width and the aspect ratio of the primary image.
            this.height = (double) images.get( primaryImageIndex ).getHeight()
                          / (double) images.get( primaryImageIndex ).getWidth() * width;

            // Load up any subsequent images.  Note that they must be scaled to
            // the same size as the first (primary) image.
            for ( int i = 1; i < resourceImageNames.size(); i++ ) {
                BufferedImage image = NuclearPhysicsResources.getImage( resourceImageNames.get( i ) );
                if ( image.getWidth() != primaryImage.getWidth() || image.getHeight() != primaryImage.getHeight() ) {
                    // Scale as needed.
                    image = BufferedImageUtils.rescaleFractional( image,
                                                                  (double) primaryImage.getWidth() / (double) image.getWidth(),
                                                                  (double) primaryImage.getHeight() / (double) image.getHeight() );
                }
                images.add( image );
            }
        }
        else {
            // It is allowable to have a datable item with no image, so this
            // is okay.  Set the height to equal the width.
            height = width;
        }

        // Set up initial indicies.
        primaryImageIndex = secondaryImageIndex = 0;
        if ( images.size() >= 2 ) {
            secondaryImageIndex = 1;
        }
    }

    /**
     * Constructor with only one image name.
     *
     * @param name
     * @param resourceImageName
     * @param center
     * @param width
     * @param rotationAngle
     * @param age
     * @param isOrganic
     */
    public DatableItem( String name, String resourceImageName, Point2D center, double width,
                        double rotationAngle, double age, boolean isOrganic ) {
        this( name, Arrays.asList( resourceImageName ), center, width, rotationAngle, age, isOrganic );
    }


    public Point2D getPosition() {
        return new Point2D.Double( center.getX(), center.getY() );
    }

    public void setPosition( Point2D centerPoint ) {
        setPosition( centerPoint.getX(), centerPoint.getY() );
    }

    public void setPosition( double x, double y ) {
        if ( center.getX() != x || center.getY() != y ) {
            center = new Point2D.Double( x, y );
            notifyPositionChanged();
        }
    }

    public void addAnimationListener( ModelAnimationListener listener ) {
        if ( !animationListeners.contains( listener ) ) {
            animationListeners.add( listener );
        }
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isOrganic() {
        return isOrganic;
    }

    /**
     * Get the radiometric age of the item in milliseconds. This is the age
     * since the "closure" occurred, which for an organic item means when it
     * died and for a mineral, since it stopped chemically interacting with
     * its environment.
     */
    public double getRadiometricAge() {
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
    public static double getPercentageCustomNucleusRemaining( DatableItem item, double customNucleusHalfLife ) {
        return calculatePercentageRemaining( item.getRadiometricAge(), customNucleusHalfLife );
    }

    public static double getPercentageCarbon14Remaining( DatableItem item ) {
        return calculatePercentageRemaining( item.getRadiometricAge(), Carbon14Nucleus.HALF_LIFE );
    }

    public static double getPercentageUranium238Remaining( DatableItem item ) {
        return calculatePercentageRemaining( item.getRadiometricAge(), Uranium238Nucleus.HALF_LIFE );
    }

    private static double calculatePercentageRemaining( double age, double halfLife ) {
        if ( age <= 0 ) {
            return 100;
        }
        else {
            return 100 * Math.exp( -0.693 * age / halfLife );
        }
    }

    public BufferedImage getImage() {
        if ( images.size() > 1 ) {
            return fadeImages( images.get( primaryImageIndex ), images.get( secondaryImageIndex ), fadeFactor );
        }
        else {
            return images.get( primaryImageIndex );
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + name;
    }

    public boolean contains( Point2D pt ) {

        // TODO: Dec 1 2009 - Need to work with Noah to decide if it is better
        // to require touching by the probe tip, or just getting in the ballpark.

        // return getBoundingRect().contains(pt);

        // Determine if one of the non-transparent pixels in the image is being touched.
        ModelViewTransform2D derotatingTransform = new ModelViewTransform2D( getBoundingRect(),
                                                                             new Rectangle2D.Double( 0, 0, getImage().getWidth(), getImage().getHeight() ) );
        Point unrotatedPoint = derotatingTransform.modelToView( pt );

        Point2D imageCenter = new Point2D.Double( getImage().getWidth() / 2, getImage().getHeight() / 2 );
        MutableVector2D unrotatedVector = new MutableVector2D( imageCenter, unrotatedPoint );
        Vector2D rotatedVector = unrotatedVector.getRotatedInstance( -rotationAngle );
        Point2D rotatedPoint = rotatedVector.getDestination( imageCenter );
        Point pixel = new Point( (int) rotatedPoint.getX(), (int) rotatedPoint.getY() );

        if ( pixel.x >= 0 && pixel.y >= 0 && pixel.x < getImage().getWidth() && pixel.y < getImage().getHeight() ) {
            return isPixelOpaque( pixel );
        }
        else {
            return false;
        }
    }

    private boolean isPixelOpaque( Point pixelpt ) {
        if ( getImage().getType() == BufferedImage.TYPE_INT_ARGB ) {
            int[] pixel = getImage().getData().getPixel( pixelpt.x, pixelpt.y, (int[]) null );
            return pixel[3] > 128;
        }
        else {
            return true;
        }
    }

    public Rectangle2D getBoundingRect() {
        return new Rectangle2D.Double( center.getX() - width / 2, center.getY() - height / 2, width, height );
    }

    public double getRotationalAngle() {
        return rotationAngle;
    }

    public Dimension2D getSize() {
        return new PDimension( width, height );
    }

    public void removeAllAnimationListeners() {
        animationListeners.clear();
    }

    public boolean removeAnimationListener( ModelAnimationListener listener ) {
        return animationListeners.remove( listener );
    }

    public void setFadeFactor( double fadeFactor ) {
        if ( this.fadeFactor != fadeFactor ) {
            this.fadeFactor = fadeFactor;
            notifyImageChanged();
        }
    }

    public void setRotationalAngle( double rotationalAngle ) {
        if ( this.rotationAngle != rotationalAngle ) {
            this.rotationAngle = rotationalAngle;
            notifyRotationalAngleChanged();
        }
    }

    public int getNumberImages() {
        return images.size();
    }

    public int getPrimaryImageIndex() {
        return primaryImageIndex;
    }

    public int getSecondaryImageIndex() {
        return secondaryImageIndex;
    }

    public void setPrimaryImageIndex( int imageIndex ) {
        assert imageIndex < images.size();
        if ( this.primaryImageIndex != imageIndex ) {
            primaryImageIndex = imageIndex;
            notifyImageChanged();
        }
    }

    public void setSecondaryImageIndex( int imageIndex ) {
        assert imageIndex < images.size();
        if ( this.secondaryImageIndex != imageIndex ) {
            secondaryImageIndex = imageIndex;
            notifyImageChanged();
        }
    }

    public double getFadeFactor() {
        return fadeFactor;
    }

    public void setSize( Dimension2D size ) {
        if ( width != size.getWidth() || height != size.getHeight() ) {
            width = size.getWidth();
            height = size.getHeight();
            notifySizeChanged();
        }
    }

    private void notifySizeChanged() {
        for ( ModelAnimationListener listener : animationListeners ) {
            listener.sizeChanged();
        }
    }

    private void notifyPositionChanged() {
        for ( ModelAnimationListener listener : animationListeners ) {
            listener.positionChanged();
        }
    }

    private void notifyRotationalAngleChanged() {
        for ( ModelAnimationListener listener : animationListeners ) {
            listener.rotationalAngleChanged();
        }
    }

    private void notifyImageChanged() {
        for ( ModelAnimationListener listener : animationListeners ) {
            listener.imageChanged();
        }
    }

    /**
     * Merge the two images based on the fade factor.
     *
     * @param primaryImage
     * @param secondaryImage
     * @param fadeFactor     - 0 for all primary image, 1 for all secondary image,
     *                       in between for merged.
     * @return
     */
    private BufferedImage fadeImages( BufferedImage primaryImage, BufferedImage secondaryImage, double fadeFactor ) {

        if ( fadeFactor < 0 || fadeFactor > 1 ) {
            System.err.println( "Error: invalid fade factor = " + fadeFactor );
            assert false;
            fadeFactor = 0;
        }

        // Use piccolo as a utility to merge the images.
        PNode parent = new PNode();
        PImage backgroundImage = new PImage( primaryImage );
        PImage foregroundImage = new PImage( secondaryImage );

        // Set the transparency of each image based on a function that uses an
        // exponent.  Linear values don't work because the composite image
        // ends up looking transparent overall in the middle range (I tried
        // it).
        backgroundImage.setTransparency( (float) ( -Math.pow( fadeFactor, 4 ) + 1 ) );
        foregroundImage.setTransparency( (float) ( -Math.pow( ( fadeFactor - 1 ), 4 ) + 1 ) );
        parent.addChild( backgroundImage );
        parent.addChild( foregroundImage );

        return BufferedImageUtils.toBufferedImage( parent.toImage() );
    }
}
