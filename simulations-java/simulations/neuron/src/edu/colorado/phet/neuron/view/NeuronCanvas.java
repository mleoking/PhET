/* Copyright 2007, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas on which the neuron simulation is depicted.
 *
 * @author John Blanco
 */
public class NeuronCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Initial size of the reference coordinates that are used when setting up
	// the canvas transform strategy.  These were empirically determined to
	// roughly match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model
    private AxonModel model;
    
    // Model to view transform.
    ModelViewTransform2D mvt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public NeuronCanvas( AxonModel model ) {

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT /2 )),
        		9,
        		true);
        
        
        this.model = model;

        System.out.println( "transform.modelToView = " + mvt.modelToView(0,0) );
        
        setBackground( NeuronConstants.CANVAS_BACKGROUND );
        
        AxonMembraneNode axonMembraneNode = new AxonMembraneNode(model.getAxonMembrane(), mvt);
        addWorldChild(axonMembraneNode);
        
        SphericalNode redSphere1 = new SphericalNode(25, new Color(200,0,0), null, null, false);
        redSphere1.setOffset(100, 100);
        addWorldChild(redSphere1);
        SphericalNode redSphere2 = new SphericalNode(25, new Color(200,0,0), null, null, false);
        redSphere2.setOffset(65, 120);
        addWorldChild(redSphere2);
        SphericalNode redSphere3 = new SphericalNode(25, new Color(200,0,0), null, null, false);
        redSphere3.setOffset(500, 90);
        addWorldChild(redSphere3);
        SphericalNode greenSphere1 = new SphericalNode(25, new Color(0,200,0), null, null, false);
        greenSphere1.setOffset(300, 300);
        addWorldChild(greenSphere1);
        SphericalNode greenSphere2 = new SphericalNode(25, new Color(0,200,0), null, null, false);
        greenSphere2.setOffset(450, 250);
        addWorldChild(greenSphere2);
        SphericalNode greenSphere3 = new SphericalNode(25, new Color(0,200,0), null, null, false);
        greenSphere3.setOffset(500, 360);
        addWorldChild(greenSphere3);
        
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( NeuronConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "getSize() = " + getSize() );
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
