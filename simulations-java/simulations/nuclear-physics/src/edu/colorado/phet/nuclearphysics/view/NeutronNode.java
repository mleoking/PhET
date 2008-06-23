/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.Nucleon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class displays a visual representation of the nucleus on the canvas.
 *
 * @author John Blanco
 */
public class NeutronNode extends PNode implements NucleonNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private final static double PARTICLE_DIAMETER = 1.6;  // Femto meters.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PPath _displayShape;
    private Nucleon _nucleon;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        
        /*
        // Set up the image for this particle.
        _displayShape = NuclearPhysicsResources.getImageNode("Neutron.png");
        
        _displayShape.scale( PARTICLE_DIAMETER/((_displayShape.getWidth() + _displayShape.getHeight()) / 2));
        addChild(_displayShape);
        */
        _displayShape = new PPath(new Ellipse2D.Double(0,0,PARTICLE_DIAMETER, PARTICLE_DIAMETER));
        _displayShape.setPaint( Color.LIGHT_GRAY );
        _displayShape.setStroke( new BasicStroke(0.1f) );
        addChild(_displayShape);
        
        nucleon.addListener(new Nucleon.Listener(){
            public void positionChanged()
            {
                update();
            }
            
        });
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Nucleon getNucleon(){
        return _nucleon;
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    private void update(){
        _displayShape.setOffset( _nucleon.getPositionReference().getX() - PARTICLE_DIAMETER/2,  
                _nucleon.getPositionReference().getY() - PARTICLE_DIAMETER/2);
    }
}
