/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.model.Cloud;
import edu.umd.cs.piccolo.PNode;

/**
 * PNode that represents a cloud in the view.
 * 
 * @author John Blanco
 */
public class CloudNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    static final Random RAND = new Random();
    
    // Constants that affect appearance of clouds.
    static final int NUM_OVALS = 8;
    private static Paint CLOUD_PAINT = new Color( 230, 230, 230 );
    
    // For debug and placement.
    private static final boolean SHOW_BOUNDS = false;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private Cloud cloud;
    private ModelViewTransform2D mvt;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public CloudNode( Cloud cloud, ModelViewTransform2D mvt, long seed ) {
    	
        this.cloud = cloud;
        this.mvt = mvt;
        
        addChild(createRepresentation(seed));
        
        if (SHOW_BOUNDS){
        	addChild(new PhetPPath(mvt.createTransformedShape(cloud.getBounds()), new BasicStroke(2f), Color.pink));
        }
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private PNode createRepresentation(long seed) {
    	RAND.setSeed(seed);
    	PNode representationRoot = new PNode();
        Rectangle2D baseOval = mvt.createTransformedShape(cloud.getBounds()).getBounds2D();
        for ( int i = 0; i < NUM_OVALS; i++ ) {
            double height = Math.max( RAND.nextDouble() * baseOval.getHeight(), baseOval.getHeight() / 4 );
            double width = Math.max(RAND.nextDouble(), 0.33) * baseOval.getWidth();
            double dx = RAND.nextDouble() * ( baseOval.getWidth() / 2 - width / 2 ) * ( RAND.nextBoolean() ? 1 : -1 );
            double dy = RAND.nextDouble() * ( baseOval.getHeight() / 2 - height / 2 ) * ( RAND.nextBoolean() ? 1 : -1 );
            double x = baseOval.getCenterX() - width / 2 + dx;
            double y = baseOval.getCenterY() - height / 2 + dy;
            
            representationRoot.addChild(new PhetPPath(new Ellipse2D.Double(x, y, width, height), CLOUD_PAINT));
        }
        return representationRoot;
    }
}
