package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a stratum (i.e. a rock and sediment layer) in
 * the view.
 */
public class StratumNode extends PNode {

	private ModelViewTransform2D _mvt;
	private Stratum _stratum;
	private PhetPPath _markingLine = new PhetPPath(Color.red);
	
    public StratumNode( Stratum stratum, Color color, ModelViewTransform2D mvt ) {

    	_mvt = mvt;
    	_stratum = stratum;
    	
        PhetPPath path = new PhetPPath( mvt.createTransformedShape(stratum.getShape()), color, new BasicStroke( 2 ), 
        		Color.black );
        addChild( path );
        
        // Add the marking line.  TODO: This can be commented out or removed
        // once the corner drawing is worked out.
//        _markingLine.setPathToRectangle(0, 0, 50, 50);
//        _markingLine.setPaint(Color.RED);
//        _markingLine.setStroke(new BasicStroke(2));
//        _markingLine.setStrokePaint(Color.red);
//        addChild(_markingLine);
    }

    static Random random = new Random();

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PhetPCanvas contentPane = new PhetPCanvas();

        Stratum stratum;
        ArrayList<Stratum> strata = new ArrayList<Stratum>();
        ArrayList<Color> colors = new ArrayList<Color>();
        stratum = new Stratum(new Stratum.LayerLine(15), new Stratum.LayerLine(20));
        strata.add( stratum );
        colors.add(Color.red);
        stratum = new Stratum(new Stratum.LayerLine(10), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.green);
        stratum = new Stratum(new Stratum.LayerLine(5), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.blue);
        stratum = new Stratum(new Stratum.LayerLine(0), stratum.getTopLine());
        strata.add( stratum );
        colors.add(Color.cyan);
        
        for ( int i = 0; i < strata.size(); i++ ){
        	StratumNode stratumNode = new StratumNode(strata.get(i), colors.get(i), 
        			new ModelViewTransform2D(new Rectangle2D.Double(0, 0, 1, 1), 
        			new Rectangle2D.Double(0, 0, 10, 10)));
        	contentPane.addWorldChild( stratumNode );
        }

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 1200, 600 );
        frame.setVisible( true );
    }
    
    /**
     * Update the layout of this node based on size of the canvas.  This is
     * needed in order to show the corner of the world, and thus make it 
     * clearer to the user that they are seeing strata under the ground.  To
     * keep the corner in the right place at the side of the canvas, we need
     * to know how large the canvas is.
     */
    // TODO - JPB TBD: I'm thinking that this WON'T be used, and will be supplanted
    // instead by a EdgeOfWorld node.  Delete this if I'm correct here.
    public void updateLayout(double width, double height){
    	Line2D verticalLine = new Line2D.Double(0, 0, 0, -_mvt.modelToViewDifferentialY(_stratum.getTopLine().getMinDepth() - _stratum.getBottomOfStratumY()));
//    	Point2D topOfVerticalLine = new Point2D.Double(width * 0.8, 
//    			_mvt.modelToViewYDouble(_stratum.getTopLine().getMinDepth()));
//    	Point2D bottomOfVerticalLine = new Point2D.Double(width * 0.8, 
//    			_mvt.modelToViewYDouble(_stratum.getTopLine().getMaxDepth()));
//    	Point2D topOfVerticalLine = new Point2D.Double(10, 10);
//    	Point2D bottomOfVerticalLine = new Point2D.Double(10, 20);
//    	
    	_markingLine.setPathTo(verticalLine);
//    	_markingLine.setOffset(786/2, 10);  // Center of the world (horizontally)
    	_markingLine.setOffset(width, 10);  // Starts above house, but moves as canvas resized.
    	_markingLine.setOffset(1000 - width, 10);  // Moves slightly the wrong way
    	_markingLine.setOffset(500 - width * 0.666, 10);  // Moves slightly the wrong way
//    	_markingLine.setOffset(786/2 - width/2 + 10, 10);
    }
}
