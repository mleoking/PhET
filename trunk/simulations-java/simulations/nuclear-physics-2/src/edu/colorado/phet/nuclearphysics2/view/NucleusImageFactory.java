package edu.colorado.phet.nuclearphysics2.view;

import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * This class generates a nucleus image from the constituent proton and
 * neutron images based on the size of the nucleus.  This is being done for
 * several reasons:
 * 1. To provide images of nuclei that are proportionate in size to their
 *    atomic weight.
 * 2. To make it so that changing the way the nucleons look will also change
 *    the nuclei images (rather than maintaining separate images.
 * 3. To prevent all the nuclei from looking exactly the same.
 * 
 * @author John Blanco
 */
public class NucleusImageFactory {
    
    private static final double NUCLEON_DIAMETER = 1.6; // Femtometers.
    private static final double ALPHA_PARTICLE_DIAMTER = 3.2; // Femtometers.
    
    private static Random _rand = new Random();
    
    // Not intended for instantiation.
    private NucleusImageFactory(){}
    
    /**
     * Generates an image of an atomic nucleus out of images of protons,
     * neutrons, and alpha particles that are available as resources for this
     * simulation.  Note that this does NOT generate a scaled image - the
     * responsibility for scaling rests with the caller.
     * 
     * @param numProtons
     * @param numNeutrons
     * @return
     */
    public static PImage generateNucleusImage(int numProtons, int numNeutrons){
        
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
                addAlpha(nucleus, nucleusRadius);
                numAlphas--;
            }
            if (numFreeProtons == numFreeNeutrons){
                addProton(nucleus, nucleusRadius);
                numFreeProtons--;
            }
            
            addNeutron(nucleus, nucleusRadius);
            numFreeNeutrons--;
        }
        
        return new PImage(nucleus.toImage());
    }
    
    private static void addAlpha(PNode nucleus, double radius){

        // Randomly select from one of the available images.
        PImage alphaImage;
        if (_rand.nextBoolean()){
            alphaImage = NuclearPhysics2Resources.getImageNode("Alpha Particle 001.png");
        }
        else{
            alphaImage = NuclearPhysics2Resources.getImageNode("Alpha Particle 002.png");            
        }
        double adjustedRadius = alphaImage.getWidth() * NUCLEON_DIAMETER;
        
        // Add the image to the node and position it.
        setParticlePosition(adjustedRadius, alphaImage);
        nucleus.addChild( alphaImage );
    }
    
    private static void addNeutron(PNode nucleus, double radius){
        
        // Load the image.
        PImage neutronImage = NuclearPhysics2Resources.getImageNode("Neutron.png");
        double adjustedRadius = neutronImage.getWidth() * NUCLEON_DIAMETER;
        
        // Add the image to the node and position it.
        setParticlePosition(adjustedRadius, neutronImage);
        nucleus.addChild( neutronImage );
    }   

    private static void addProton(PNode nucleus, double radius){
        
        // Load the image.
        PImage protonImage = NuclearPhysics2Resources.getImageNode("Proton.png");
        double adjustedRadius = protonImage.getWidth() * NUCLEON_DIAMETER;
        
        // Add the image to the node and position it.
        nucleus.addChild( protonImage );
        setParticlePosition(adjustedRadius, protonImage);
    }   
    
    private static void setParticlePosition(double nucleusRadius, PNode particle){
        double angle = (_rand.nextDouble() * Math.PI * 2);
        double multiplier = _rand.nextDouble();
        if (multiplier > 0.8){
            // Cause the distribution to tail off in the outer regions
            // of the nucleus.  This makes the center of the nucleus
            // look more concentrated, which is what we want.
            multiplier = multiplier * _rand.nextDouble();
        }
        double radius = nucleusRadius * multiplier;
        double xPos = Math.sin( angle ) * radius;
        double yPos = Math.cos( angle ) * radius;
        particle.setOffset( xPos, yPos );
    }
    
    private static double getDiameter(int atomicWeight){
        // This calculation is based on an empirically derived function that
        // seems to give pretty reasonable values.
        return (1.6 * Math.pow( (double)atomicWeight, 0.362));
    }

    public static void main( String[] args ) {
        
        PImage testImage = generateNucleusImage(120, 100);
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testImage );
        canvas.setWorldScale( 100 );
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setVisible( true );
    }
}
