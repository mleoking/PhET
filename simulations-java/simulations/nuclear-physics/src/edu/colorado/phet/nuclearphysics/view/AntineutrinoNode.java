/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.Antineutrino;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Basic representation of an antineutrino in the view.
 * 
 * @author John Blanco
 */
public class AntineutrinoNode extends SubatomicParticleNode{

	PPath _representation;
	
    public AntineutrinoNode( ) {
    	super();
    	createRepresentation();
    }
    
    public AntineutrinoNode( Antineutrino antineutrino ) {
        super( antineutrino );
    	createRepresentation();
    }
    
    private void createRepresentation(){
    	if (_representation == null){
    		float diameter = (float)NuclearPhysicsConstants.ANTINEUTRINO_DIAMETER;
    		_representation = new PhetPPath(NuclearPhysicsConstants.ANTINEUTRINO_COLOR, new BasicStroke(diameter/10), 
    				ColorUtils.darkerColor(NuclearPhysicsConstants.ANTINEUTRINO_COLOR, 0.5));
    		_representation.setPathTo(new Ellipse2D.Double(0, 0, diameter, diameter));
    		_representation.setOffset(-diameter/ 2, -diameter * 0.35);
    		addChild(_representation);
    	}
    }
    
    /*
     * Below is the initial version of the antineutrino's representation, used
     * prior to 8/27/2009.  Some changes have been requested, so this is being
     * commented out for now.  This should be permanently removed once there
     * is acceptance of the new representation.
    private void createRepresentation(){
    	if (_representation == null){
    		float diameter = (float)NuclearPhysicsConstants.ANTINEUTRINO_DIAMETER;
    		_representation = new PPath();
    		_representation.setPaint(NuclearPhysicsConstants.ANTINEUTRINO_COLOR);
    		_representation.setStroke(new BasicStroke(diameter/10));
    		GeneralPath shape = new GeneralPath();
    		shape.moveTo(0, 0);
    		shape.lineTo(diameter, 0);
    		shape.lineTo(diameter / 2, 0.7f * diameter);
    		shape.lineTo(0, 0);
    		shape.closePath();
    		_representation.setPathTo(shape);
    		_representation.setOffset(-diameter/ 2, -diameter * 0.35);
    		addChild(_representation);
    	}
    }
     */
}
