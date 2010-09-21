package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricMeasurementDefaults;
import edu.umd.cs.piccolo.PNode;

/**
 * Standalone application for testing various aspects of animation.
 * 
 * @author John Blanco
 *
 */
public class AnimationTestApp {
	
	public static int CANVAS_WIDTH = 1000;
	public static int CANVAS_HEIGHT = 600;
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;

	/**
	 * Main test routine.
	 * 
	 * @param args
	 */
    public static void main(String [] args){
    	
    	ConstantDtClock clock = new NuclearPhysicsClock( RadiometricMeasurementDefaults.CLOCK_FRAME_RATE, 
    			RadiometricMeasurementDefaults.CLOCK_DT );
    	
        ModelViewTransform2D mvt = new ModelViewTransform2D( 
        		new Point2D.Double(0, 0),
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, INITIAL_INTERMEDIATE_COORD_HEIGHT / 2),
        		1,
        		true);

        // Create a simple node for testing (non-animated).
        PNode nonMvtTestNode = new PhetPPath(new Rectangle2D.Double(0, 0, 100, 100), Color.yellow);
        
        // Create the animated item under test.
    	AnimationTestModelElement modelElement = new AnimationTestModelElement(clock,
    			new Point2D.Double(0, 0), CANVAS_WIDTH / 4);

    	// Create the node for the animated item.
    	DatableItemNode animatedModelElementTestNode = new DatableItemNode(modelElement, mvt);
    	
    	// Create a vertical line in the center of model space.
    	Point2D topOfVertLine = mvt.modelToViewDouble(new Point2D.Double(0, -200));
    	Point2D bottomOfVertLine = mvt.modelToViewDouble(new Point2D.Double(0, 200));
    	PNode vertAxisNode = new PhetPPath( new Line2D.Double(topOfVertLine, bottomOfVertLine), Color.red,
    			new BasicStroke(5), Color.red);
    	
    	// Create a horizontal line in the center of model space.
    	Point2D leftSideOfHozizLine = mvt.modelToViewDouble(new Point2D.Double(-200, 0));
    	Point2D rightSideOfHozizLine = mvt.modelToViewDouble(new Point2D.Double(200, 0));
    	PNode horizAxisNode = new PhetPPath( new Line2D.Double(leftSideOfHozizLine, rightSideOfHozizLine), Color.orange,
    			new BasicStroke(5), Color.orange);
    	
    	// Create the frame, put the canvas on it, and fill 'er up.
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addWorldChild(vertAxisNode);
        canvas.addWorldChild(horizAxisNode);
        canvas.addWorldChild(nonMvtTestNode);
        canvas.addWorldChild(animatedModelElementTestNode);
        frame.setContentPane( canvas );
        frame.setSize( CANVAS_WIDTH, CANVAS_HEIGHT );
        frame.setVisible( true );
        
        // Debug
        System.out.println("Full bounds before = " + animatedModelElementTestNode.getFullBounds());
        
        // Start the clock.
        clock.start();
        
        // Debug
        System.out.println("Full bounds after = " + animatedModelElementTestNode.getFullBounds());
        
    }
}
