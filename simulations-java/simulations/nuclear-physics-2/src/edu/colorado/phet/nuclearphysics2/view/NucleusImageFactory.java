package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * This singleton class generates a nucleus image from the constituent proton
 * and neutron images based on the size of the nucleus.  Since it would be too
 * computationally intensive to do this every time a new nucleus is needed,
 * images are cached and returned after once several have been created for a
 * given atomic weight.
 * 
 * 1. To provide images of nuclei that are proportionate in size to their
 *    atomic weight.
 * 2. To make it so that changing the way the nucleons look will also change
 *    the nuclei images (rather than maintaining separate images.
 * 3. To prevent all the nuclei from looking exactly the same.
 * 
 * @author John Blanco
 */
public class NucleusImageFactory {
 
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
 
    private static final int IMAGES_TO_CACHE = 4;
    
    private static final double NUCLEON_DIAMETER = 1.6; // Femtometers.
    private static final double ALPHA_PARTICLE_DIAMTER = 3.2; // Femtometers.
    private static NucleusImageFactory _instance = null;
    private static Random _rand = new Random();
    
    private static final int MAX_ATOMIC_WEIGHT = 250;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private ArrayList [] imageMap = new ArrayList[MAX_ATOMIC_WEIGHT];
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    /**
     * Constructor for this singleton class, exists to defeat direct instantiation.
     */
    private NucleusImageFactory(){
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Get the one and only instance of this class.
     */
    public static NucleusImageFactory getInstance(){
        if (_instance == null){
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
    public PImage generateNucleusImage(int numProtons, int numNeutrons, double pixelsPerFm){
        
        Image nucleusImage;
        
        // If we have enough cached images at this atomic weight, use one
        // of them.
        nucleusImage = getCachedImage( numProtons + numNeutrons );
        if (nucleusImage != null){
            return new PImage(nucleusImage);
        }
        
        // Start creating a new image.
        PNode nucleus = new PNode();
        
        // Decide on the proportion of free protons and neutrons versus those
        // tied up in alpha particles.
        int numAlphas    = ((numProtons + numNeutrons) / 2) / 4;  // Assume half of all particles are tied up in alphas.
        int numFreeProtons   = numProtons - (numAlphas * 2);
        int numFreeNeutrons  = numNeutrons - (numAlphas * 2);
        
        // Determine the size of the nucleus in femtometers.
        double nucleusRadius = getDiameter(numProtons + numNeutrons) / 2.0;

        // For the following loop to work, it is assumed that the number of
        // neutrons equals or exceeds the number of protons, which is always
        // (I think) true in the real world.  However, just in case the caller
        // has not done this, we adjust the values here if it is not true.
        if (numFreeProtons > numFreeNeutrons){
            numFreeNeutrons = numFreeProtons;
        }
        
        // Scale and add the individual images.  We add the most abundant
        // images (usually neutrons) first, since otherwise they end up
        // dominating the image.
        int maxParticleType = Math.max( numAlphas, numFreeProtons );
        maxParticleType = Math.max( numFreeNeutrons, maxParticleType );
        
        for ( int i = 0; i < maxParticleType; i++ ) {
            if (numAlphas == numFreeNeutrons){
                // Add an alpha particle.
                addAlpha(nucleus, nucleusRadius, pixelsPerFm);
                numAlphas--;
            }
            if (numFreeProtons == numFreeNeutrons){
                addProton(nucleus, nucleusRadius, pixelsPerFm);
                numFreeProtons--;
            }
            
            addNeutron(nucleus, nucleusRadius, pixelsPerFm);
            numFreeNeutrons--;
        }
        
        // Cache the newly created image.
        nucleusImage = nucleus.toImage();
        addCachedImage( numNeutrons + numProtons, nucleusImage );
        
        // Return the image;
        return new PImage(nucleus.toImage());
    }
    
    private void addAlpha(PNode nucleus, double radius, double pixelsPerFm){

        // Randomly select from one of the available images.
        PImage alphaImage;
        if (_rand.nextBoolean()){
            alphaImage = NuclearPhysics2Resources.getImageNode("Alpha Particle 001.png");
        }
        else{
            alphaImage = NuclearPhysics2Resources.getImageNode("Alpha Particle 002.png");            
        }
        
        // Scale and position the image.
        alphaImage.scale( pixelsPerFm * ALPHA_PARTICLE_DIAMTER / ((alphaImage.getWidth() + alphaImage.getHeight()) / 2) );
        setParticlePosition(radius, alphaImage, pixelsPerFm);
        
        // Add the image to the main node.
        nucleus.addChild( alphaImage );
    }
    
    private void addNeutron(PNode nucleus, double radius, double pixelsPerFm){
        
        // Load the image.
        PImage neutronImage = NuclearPhysics2Resources.getImageNode("Neutron.png");
        
        // Scale and position the image.
        neutronImage.scale( pixelsPerFm * NUCLEON_DIAMETER / neutronImage.getWidth());
        setParticlePosition(radius, neutronImage, pixelsPerFm);
        
        // Add the image to the main node.
        nucleus.addChild( neutronImage );
    }   

    private void addProton(PNode nucleus, double radius, double pixelsPerFm){
        
        // Load the image.
        PImage protonImage = NuclearPhysics2Resources.getImageNode("Proton.png");
        
        // Scale and position the image.
        protonImage.scale( pixelsPerFm * NUCLEON_DIAMETER / protonImage.getWidth());
        setParticlePosition(radius, protonImage, pixelsPerFm);
        
        // Add the image to the main node.
        nucleus.addChild( protonImage );
    }   
    
    private void setParticlePosition(double nucleusRadius, PNode particle, double pixelsPerFm){
        double angle = (_rand.nextDouble() * Math.PI * 2);
        double multiplier = _rand.nextDouble();
        if (multiplier > 0.8){
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
    
    private double getDiameter(int atomicWeight){
        // This calculation is based on an empirically derived function that
        // seems to give pretty reasonable values.
        return (1.6 * Math.pow( (double)atomicWeight, 0.362));
    }
    
    /**
     * Search for a cached image and return one if there are enough already
     * generated.
     * 
     * @param atomicWeight
     * @return - Image if the cache is full, null if not.
     */
    private Image getCachedImage(int atomicWeight){
        
        if (atomicWeight > MAX_ATOMIC_WEIGHT){
            System.err.println("Warning: Requested image larger than max atomic weight allowed.");
            return null;
        }
        
        ArrayList imageList = imageMap[atomicWeight];
        
        if (imageList == null){
            // No cached images exist for this atomic weight.
            return null;
        }
        
        if (imageList.size() < IMAGES_TO_CACHE){
            // Not enough cached images exist for this atomic weight.
            return null;
        }
        
        // We have the needed number of cached images, so return one.
        return (Image)imageList.get( _rand.nextInt( IMAGES_TO_CACHE ) );
    }
    
    private void addCachedImage(int atomicWeight, Image newImage){

        if (atomicWeight > MAX_ATOMIC_WEIGHT){
            System.err.println("Warning: New image has atomic weight above allowed maximum.");
            return;
        }
        
        ArrayList imageList = imageMap[atomicWeight];
        
        if (imageList == null){
            // This is the first image for this particular atomic weight, so
            // we need to create an ArrayList to hold the images.
            imageList = new ArrayList(IMAGES_TO_CACHE);
            imageMap[atomicWeight] = imageList;
        }
        
        // Add the image to the list.
        imageList.add( newImage );
    }
    
    public static void main( String[] args ) {
        
        PImage testImage = getInstance().generateNucleusImage(120, 100, 20);
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testImage );
        canvas.setWorldScale( 100 );
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setVisible( true );
    }
}
