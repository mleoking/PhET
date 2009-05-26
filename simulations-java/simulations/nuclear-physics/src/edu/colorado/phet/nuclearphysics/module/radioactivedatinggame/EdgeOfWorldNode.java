package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
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
		_testShape = new PhetPPath(Color.red,new BasicStroke(2), Color.red);
		addChild( _testShape );
		_model.addObserver(new SimpleObserver(){
			public void update() {
				_testShape.setPathTo(_mvt.createTransformedShape(_model.getEdgeOfWorldRect()));
			}
		});
	}
}
