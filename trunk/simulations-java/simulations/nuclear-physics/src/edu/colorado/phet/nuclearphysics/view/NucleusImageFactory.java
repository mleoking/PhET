
package edu.colorado.phet.nuclearphysics.view;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This singleton class generates a nucleus image from the constituent proton
 * and neutron nodes based on the size of the nucleus.  Since it would be too
 * computationally intensive to do this every time a new nucleus is needed,
 * images are cached and returned once several have been created for a given
 * atomic weight.
 * 
 * This is being done instead of using static images for the following reasons:
 * 
 * 1. To provide images of nuclei that are proportionate in size to their
 *    atomic weight.
 * 2. To make it so that changing the way the nucleons look will also change
 *    the nuclei images (rather than maintaining separate images.
 * 3. To prevent all the nuclei from all looking exactly the same.
 * 
 * @author John Blanco
 */
public class NucleusImageFactory {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    public static final int NUM_IMAGES_TO_CACHE = 4;
    private static final double DEEFAULT_PIXELS_PER_FM = 20;

    private static NucleusImageFactory _instance = null;
    private static Random _rand = new Random();

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // This is part of a two-level mapping structure that is used to cache
    // images according to the number of protons and neutrons in the
    // corresponding nucleus.
    private HashMap<Integer, HashMap<Integer, ImageList>> mapNumProtonsToNeutronMap = 
    	new HashMap<Integer, HashMap<Integer, ImageList>>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructor for this singleton class, should never be invoked directly
     * by another object.
     */
    private NucleusImageFactory() {
        // During construction we fill the cache for several of the more
        // common nuclei.  This saves us from doing it during run time, which
        // was found to create pauses in the simulation.
        for ( int i = 0; i < NUM_IMAGES_TO_CACHE; i++ ) {
            // U235
            getNucleusImage( 92, 143, DEEFAULT_PIXELS_PER_FM );
            // U236
            getNucleusImage( 92, 144, DEEFAULT_PIXELS_PER_FM );
            // U238
            getNucleusImage( 92, 146, DEEFAULT_PIXELS_PER_FM );
            // U239
            getNucleusImage( 92, 147, DEEFAULT_PIXELS_PER_FM );
            // K92
            getNucleusImage( 36, 56, DEEFAULT_PIXELS_PER_FM );
            // Br141
            getNucleusImage( 56, 85, DEEFAULT_PIXELS_PER_FM );
        }
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    /**
     * Get the one and only instance of this class.
     */
    public static NucleusImageFactory getInstance() {
        if ( _instance == null ) {
            _instance = new NucleusImageFactory();
        }
        return _instance;
    }

    /**
     * Generates an image of an atomic nucleus out of images of protons,
     * neutrons, and alpha particles that are available as resources for this
     * simulation.
     * 
     * @param numProtons
     * @param numNeutrons
     * @param pixelsPerFm - The desired pixels per femtometer of the supplied image.
     * @return
     */
    public PImage getNucleusImage( int numProtons, int numNeutrons, double pixelsPerFm ) {

        Image nucleusImage;

        // If we have enough cached images at this atomic weight, use one of
        // them.
        nucleusImage = getCachedImage2( numProtons, numNeutrons );
        if ( nucleusImage != null ) {
            return new PImage( nucleusImage );
        }
        else{
        	return generateNucleusImage(numProtons, numNeutrons, pixelsPerFm);
        }
    }
    
    /**
     * Generate images and cache them for the specified nucleus.  This is
     * generally done during the initialization of the sims to save time
     * later.
     * 
     * @param numProtons - number of protons in the nucleus.
     * @param numNeutrons - Number of neutrons in the nucleus.
     * @param pixelsPerFm - Pixels per femtometer
     */
    public void preGenerateNucleusImages( int numProtons, int numNeutrons, double pixelsPerFm ){
    	int numCachedImages = getNumCachedImages2(numProtons, numNeutrons);
    	if (numCachedImages < NUM_IMAGES_TO_CACHE){
    		// The cache isn't full for this atomic weight, so fill it.
    		for (int i = 0; i < NUM_IMAGES_TO_CACHE - numCachedImages; i++){
    			generateNucleusImage(numProtons, numNeutrons, pixelsPerFm);
    		}
    	}
    }

    /**
     * Generate a new nucleus image.
     */
    private PImage generateNucleusImage( int numProtons, int numNeutrons, double pixelsPerFm ){

        Image nucleusImage;
        PNode nucleus = new PNode();

        // Determine the size of the nucleus in femtometers.
        double nucleusRadius = getDiameter( numProtons + numNeutrons ) / 2.0;
        
        // Add images of individual nucleons together in order to create the
        // overall image of the nucleus.  There are some special cases here,
        // and more may be needed as more nuclei are included in the sim.
        
        if (numProtons + numNeutrons == 3){
        	// This special case was added to handle H3 and He3.  It places
        	// the nucleons such that there is no overlap between them.  It
        	// may also be possible to generalize it somewhat to handle small
        	// numbers of nuclei.
        	
        	int protonsToAdd = numProtons;
        	int neutronsToAdd = numNeutrons;
        	double rotationOffset = Math.PI;  // In radians, arbitrary and just for looks.
        	double distanceFromCenter = NuclearPhysicsConstants.NUCLEON_DIAMETER / 2 / Math.cos(Math.PI / 6);
        	for (int i = 0; i < 3; i++){
        		PNode nucleonNode = null;
        		if (neutronsToAdd > 0){
        			nucleonNode = new NeutronNode();
        			neutronsToAdd--;
        		}
        		else{
        			nucleonNode = new ProtonNode();
        			protonsToAdd--;
        		}
        		nucleonNode.scale( pixelsPerFm );
        		double angle = ( Math.PI * 2 / 3 ) * i + rotationOffset;
                double xPos = Math.sin( angle ) * distanceFromCenter * pixelsPerFm;
                double yPos = Math.cos( angle ) * distanceFromCenter * pixelsPerFm;
        		nucleonNode.setOffset( xPos, yPos );
        		nucleus.addChild( nucleonNode );
        	}
        }
        else { 
	        // Decide on the proportion of free protons and neutrons versus those
	        // tied up in alpha particles.
	        int numAlphas = ( ( numProtons + numNeutrons ) / 2 ) / 4; // Assume half of all particles are tied up in alphas.
	        int numFreeProtons = numProtons - ( numAlphas * 2 );
	        int numFreeNeutrons = numNeutrons - ( numAlphas * 2 );
	
	        // For the following loop to work, it is assumed that the number of
	        // neutrons equals or exceeds the number of protons, which is always
	        // true (I think) in the real world.  However, just in case the caller
	        // has not done this, we adjust the values here if necessary.
	        if ( numFreeProtons > numFreeNeutrons ) {
	            numFreeNeutrons = numFreeProtons;
	        }
	
	        // Add the individual images.  We add the most abundant images
	        // (usually neutrons) first, since otherwise they end up dominating
	        // the image.
	        int maxParticleType = Math.max( numAlphas, numFreeProtons );
	        maxParticleType = Math.max( numFreeNeutrons, maxParticleType );
	
	        for ( int i = 0; i < maxParticleType; i++ ) {
	            if ( numAlphas == numFreeNeutrons ) {
	                // Add an alpha particle.
	                addAlpha( nucleus, nucleusRadius, pixelsPerFm );
	                numAlphas--;
	            }
	            if ( numFreeProtons == numFreeNeutrons ) {
	                addProton( nucleus, nucleusRadius, pixelsPerFm );
	                numFreeProtons--;
	            }
	
	            addNeutron( nucleus, nucleusRadius, pixelsPerFm );
	            numFreeNeutrons--;
	        }
        }

        // Convert the collection of nucleon nodes to an image.
        nucleusImage = nucleus.toImage();
        
        // Cache the newly created image.
        addCachedImage2( numProtons, numNeutrons, nucleusImage );

        // Return the image.
        return new PImage( nucleus.toImage() );
    }

    private void addAlpha( PNode nucleus, double radius, double pixelsPerFm ) {

        PNode alphaParticle = new AlphaParticleNode();
        alphaParticle.scale( pixelsPerFm );
        PImage alphaParticleImage = new PImage( alphaParticle.toImage() );
        setParticlePosition( radius, alphaParticleImage, pixelsPerFm );
        nucleus.addChild( alphaParticleImage );

    }

    private void addNeutron( PNode nucleus, double radius, double pixelsPerFm ) {

        PNode neutron = new NeutronNode();
        neutron.scale( pixelsPerFm );
        PImage neutronImage = new PImage( neutron.toImage() );
        setParticlePosition( radius, neutronImage, pixelsPerFm );
        nucleus.addChild( neutronImage );

    }

    private void addProton( PNode nucleus, double radius, double pixelsPerFm ) {

        PNode proton = new ProtonNode();
        proton.scale( pixelsPerFm );
        PImage protonImage = new PImage( proton.toImage() );
        setParticlePosition( radius, protonImage, pixelsPerFm );
        nucleus.addChild( protonImage );
    }

    private void setParticlePosition( double nucleusRadius, PNode particle, double pixelsPerFm ) {
        double angle = ( _rand.nextDouble() * Math.PI * 2 );
        double multiplier = _rand.nextDouble();
        if ( multiplier > 0.8 ) {
            // Cause the distribution to tail off in the outer regions
            // of the nucleus.  This makes the center of the nucleus
            // look more concentrated, which is what we want.
            multiplier = multiplier * _rand.nextDouble() / 2;
        }
        double radius = nucleusRadius * multiplier * pixelsPerFm;
        double xPos = Math.sin( angle ) * radius;
        double yPos = Math.cos( angle ) * radius;
        particle.setOffset( xPos, yPos );
    }

    private double getDiameter( int atomicWeight ) {
        // This calculation is based on an empirically derived function that
        // seems to give pretty reasonable values.
        return ( 1.6 * Math.pow( (double) atomicWeight, 0.362 ) );
    }

    /**
     * Search for a cached image and return one if there are enough already
     * generated.
     * 
     * @param atomicWeight
     * @return - Image if the cache is full, null if not.
     */
    private Image getCachedImage2( int numProtons, int numNeutrons ) {

    	Integer numProtonsInteger = new Integer(numProtons);
    	Integer numNeutronsInteger = new Integer(numNeutrons);
    	HashMap<Integer, ImageList> numNeutronsToImageListMap;
    	
    	numNeutronsToImageListMap = mapNumProtonsToNeutronMap.get(numProtonsInteger);
    	
    	if (numNeutronsToImageListMap == null){
    		// No images for this element.
    		return null;
    	}
    	
    	ImageList imageList = numNeutronsToImageListMap.get(numNeutronsInteger);
    	
    	if (imageList == null || !imageList.isFullyPopulated()){
    		// Not enough cached images for this particular isotope of this element.
    		return null;
    	}
    	
    	// We have the needed number of cached images, so return one.
    	return imageList.getRandomImage();
    }
    
    /**
     * Get a number representing the number of cached images for the specified
     * atomic weight.
     */
    private int getNumCachedImages2( int numProtons, int numNeutrons ){
    	
    	Integer numProtonsInteger = new Integer(numProtons);
    	Integer numNeutronsInteger = new Integer(numNeutrons);
    	HashMap<Integer, ImageList> numNeutronsToImageListMap;
    	
    	numNeutronsToImageListMap = mapNumProtonsToNeutronMap.get(numProtonsInteger);
    	
    	if (numNeutronsToImageListMap == null){
    		// No images for this element.
    		return 0;
    	}
    	
    	ImageList imageList = numNeutronsToImageListMap.get(numNeutronsInteger);
    	
    	if (imageList == null){
    		// No images for this particular isotope of this element.
    		return 0;
    	}
    	
    	// We have the needed number of cached images, so return one.
    	return imageList.getNumImages();
    }

    /**
     * Add a new image to the image cache.
     */
    private void addCachedImage2( int numProtons, int numNeutrons, Image newImage ) {

    	Integer numProtonsInteger = new Integer(numProtons);
    	Integer numNeutronsInteger = new Integer(numNeutrons);
    	HashMap<Integer, ImageList> mapNumNeutonsToImageList;
    	
    	mapNumNeutonsToImageList = mapNumProtonsToNeutronMap.get(numProtonsInteger);
    	
    	if (mapNumNeutonsToImageList == null){
    		// First image for this element, so add a num-neutrons-to-image-list map.
    		mapNumNeutonsToImageList = new HashMap<Integer, ImageList>();
    		mapNumProtonsToNeutronMap.put(numProtonsInteger, mapNumNeutonsToImageList);
    	}
    	
    	ImageList imageList = mapNumNeutonsToImageList.get(numNeutronsInteger);
    	
    	if (imageList == null){
    		// No image list yet for this particular isotope, so add one.
    		imageList = new ImageList();
    		mapNumNeutonsToImageList.put(numNeutronsInteger, imageList);
    	}
    	
    	imageList.addImage(newImage);
    }
    		
    private static class ImageList {
    	ArrayList<Image> imageList = new ArrayList<Image>();
    	
    	/**
    	 * Add a new image to the list.
    	 */
    	public void addImage(Image image){
    		if (imageList.size() < NUM_IMAGES_TO_CACHE){
    			imageList.add(image);
    		}
    		else{
    			System.err.println(getClass().getName() + " - Warning: Attempt to add image to full list.");
    		}
    	}
    	
    	/**
    	 * Pick one of the images randomly from the list and return it.  Note
    	 * that this can return null if the list is not fully populated.
    	 * 
    	 * @return
    	 */
    	public Image getRandomImage(){
    		return imageList.get(_rand.nextInt( imageList.size() ));
    	}

    	/**
    	 * Returns true if the list is full, false if not.
    	 */
    	public boolean isFullyPopulated(){
    		return imageList.size() == NUM_IMAGES_TO_CACHE;
    	}
    	
    	public int getNumImages(){
    		return imageList.size();
    	}
    }
}
