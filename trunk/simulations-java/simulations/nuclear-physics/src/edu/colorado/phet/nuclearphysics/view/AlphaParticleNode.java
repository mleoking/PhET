/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * AlphaParticleNode - This class is used to represent an alpha particle in
 * the view.
 *
 * @author John Blanco
 */
public class AlphaParticleNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private final static double PARTICLE_DIAMETER = 3.2d;  // Femto meters.
    private static final Color PROTON_COLOR = new Color(0xaa0000); // Red
    private static final Color PROTON_HILITE_COLOR = new Color(0xffaaaa); // Light red
    private static final Paint PROTON_ROUND_GRADIENT = new RoundGradientPaint( -PARTICLE_DIAMETER/12, 
            -PARTICLE_DIAMETER/12, PROTON_HILITE_COLOR, new Point2D.Double( PARTICLE_DIAMETER/8,
            PARTICLE_DIAMETER/8 ), PROTON_COLOR );
    private static final Color NEUTRON_COLOR = Color.GRAY;
    private static final Color NEUTRON_HILITE_COLOR = new Color(0xeeeeee);
    private static final Paint NEUTRON_ROUND_GRADIENT = new RoundGradientPaint( -PARTICLE_DIAMETER/12, 
            -PARTICLE_DIAMETER/12, NEUTRON_HILITE_COLOR, new Point2D.Double( PARTICLE_DIAMETER/8, 
            PARTICLE_DIAMETER/8 ), NEUTRON_COLOR );
    private static final Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PNode _displayNode;
    private AlphaParticle _alphaParticle;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaParticleNode(AlphaParticle alphaParticle)
    {
        _alphaParticle = alphaParticle;
        
        // Randomly choose an image for this particle.  This is done to give
        // the nucleus a more random and thus realistic look.
        if (_rand.nextDouble() > 0.5){
            _displayNode = createCompositeNode1();
        }
        else {
            _displayNode = createCompositeNode2();
        }
        
        addChild(_displayNode);
        alphaParticle.addListener(new AlphaParticle.Listener(){
            public void positionChanged(AlphaParticle alpha)
            {
                update();
            }
            
        });
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * This is a static factory method that can be used to obtain an image
     * node that depicts an alpha particle that will look just like the
     * images used in the play area.
     */
    static public PImage generateAlphaParticleImageNode(double diameter){
        
        return new PImage(generateAlphaParticleImage( diameter ));
    }
    
    /**
     * This is a static factory method that can be used to obtain an
     * image of an alpha particle that will look just like the images used in
     * the play area.
     */
    static public Image generateAlphaParticleImage(double diameter){
        
        double scaleFactor = diameter / PARTICLE_DIAMETER;

        PNode compositeNode;
        if (_rand.nextBoolean()){
            compositeNode = createCompositeNode1();
        }
        else{
            compositeNode = createCompositeNode2();
        }
        
        compositeNode.setScale( scaleFactor );
        
        return compositeNode.toImage();
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    private void update(){
        _displayNode.setOffset( _alphaParticle.getPosition().getX() - PARTICLE_DIAMETER/2,  
                _alphaParticle.getPosition().getY() - PARTICLE_DIAMETER/2);
    }
    
    private static PNode createCompositeNode1(){
        SphericalNode proton1 = new SphericalNode( PARTICLE_DIAMETER / 2, PROTON_ROUND_GRADIENT, false );
        SphericalNode proton2 = new SphericalNode( PARTICLE_DIAMETER / 2, PROTON_ROUND_GRADIENT, false );
        SphericalNode neutron1 = new SphericalNode( PARTICLE_DIAMETER / 2, NEUTRON_ROUND_GRADIENT, false );
        SphericalNode neutron2 = new SphericalNode( PARTICLE_DIAMETER / 2, NEUTRON_ROUND_GRADIENT, false );
        
        PNode alphaParticle = new PNode();
        proton1.setOffset( 0, PARTICLE_DIAMETER/4 );
        alphaParticle.addChild(proton1);
        neutron1.setOffset( PARTICLE_DIAMETER/4, 0 );
        alphaParticle.addChild(neutron1);
        neutron2.setOffset( PARTICLE_DIAMETER/4, PARTICLE_DIAMETER/2 );
        alphaParticle.addChild(neutron2);
        proton2.setOffset( PARTICLE_DIAMETER/2, PARTICLE_DIAMETER/4 );
        alphaParticle.addChild(proton2);
        
        return alphaParticle;
    }
    
    private static PNode createCompositeNode2(){
        SphericalNode proton1 = new SphericalNode( PARTICLE_DIAMETER / 2, PROTON_ROUND_GRADIENT, false );
        SphericalNode proton2 = new SphericalNode( PARTICLE_DIAMETER / 2, PROTON_ROUND_GRADIENT, false );
        SphericalNode neutron1 = new SphericalNode( PARTICLE_DIAMETER / 2, NEUTRON_ROUND_GRADIENT, false );
        SphericalNode neutron2 = new SphericalNode( PARTICLE_DIAMETER / 2, NEUTRON_ROUND_GRADIENT, false );
        
        PNode alphaParticle = new PNode();
        proton1.setOffset( PARTICLE_DIAMETER / 3, PARTICLE_DIAMETER / 4  );
        alphaParticle.addChild(proton1);
        neutron1.setOffset( 0, 0 );
        alphaParticle.addChild(neutron1);
        neutron2.setOffset( PARTICLE_DIAMETER / 3, PARTICLE_DIAMETER / 2 );
        alphaParticle.addChild(neutron2);
        proton2.setOffset( 0, PARTICLE_DIAMETER / 3 );
        alphaParticle.addChild(proton2);
        
        return alphaParticle;
    }
}
