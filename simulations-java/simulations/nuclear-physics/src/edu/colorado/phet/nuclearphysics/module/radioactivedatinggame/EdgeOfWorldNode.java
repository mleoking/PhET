package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * This class represents a Piccolo node that is intended to create a 3D-like
 * view of the edge of the world.
 * 
 * @author John Blanco
 */
public class EdgeOfWorldNode extends PNode {
	
	private RadioactiveDatingGameModel _model;
	private ModelViewTransform2D _mvt;
	private PhetPPath _testShape;
	private PhetPPath _background;

	/**
	 * Constructor.
	 * 
	 * @param model
	 * @param canvas
	 * @param mvt
	 */
	public EdgeOfWorldNode( RadioactiveDatingGameModel model, ModelViewTransform2D mvt){
		
		_model = model;
		_mvt = mvt;
		_background = new PhetPPath(NuclearPhysicsConstants.CANVAS_BACKGROUND, new BasicStroke(2),
				NuclearPhysicsConstants.CANVAS_BACKGROUND);
		addChild( _background );
		_testShape = new PhetPPath(Color.gray, new BasicStroke(2), Color.gray);
		addChild( _testShape );
		_model.addObserver(new SimpleObserver(){
			public void update() {
				updateEdgeShape();
			}
		});
	}
	
	/**
	 * Draw the 3D-ish edge of the world based on information obtained from
	 * the model.
	 * 
	 * IMPORTANT NOTE: This assumes that the edge of the world is being shown
	 * on the left side of the view port, and this would need to be changed or
	 * generalized to make it appear on the right instead.
	 */
	private void updateEdgeShape(){
		
		// Set the shape of the edge.
		GeneralPath edgeShapeModelCoords = new GeneralPath();
		Rectangle2D edgeRect = _model.getEdgeOfWorldRect();
		edgeShapeModelCoords.moveTo((float)(edgeRect.getX() + edgeRect.getWidth()), (float)edgeRect.getY());
		edgeShapeModelCoords.lineTo((float)(edgeRect.getX() + edgeRect.getWidth()), 
				(float)(edgeRect.getY() + -(_model.getBottomOfStrata())));
		edgeShapeModelCoords.lineTo((float)(edgeRect.getX()), (float)(edgeRect.getY() + edgeRect.getHeight()));
		edgeShapeModelCoords.lineTo((float)(edgeRect.getX()), 
				(float)(edgeRect.getY() + edgeRect.getHeight() + _model.getBottomOfStrata()));

		_testShape.setPathTo(_mvt.createTransformedShape(edgeShapeModelCoords));
		
		Iterable<Stratum> strata = _model.getStratumIterable();
		
		
		// Set the shape of the background.  The background is used to prevent
		// nodes behind the edge node from being visible and thus ruining the
		// 3D look.
		Rectangle2D backgroundRect = new Rectangle2D.Double(edgeRect.getX(), edgeRect.getY(),
				edgeRect.getWidth(), edgeRect.getHeight() / 2);
		_background.setPathTo(_mvt.createTransformedShape(backgroundRect));
	}
}
