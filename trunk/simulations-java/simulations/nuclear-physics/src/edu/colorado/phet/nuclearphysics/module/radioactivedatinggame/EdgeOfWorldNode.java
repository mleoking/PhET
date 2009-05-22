package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

public class EdgeOfWorldNode extends PNode {
	
	private RadioactiveDatingGameModel _model;
	private ModelViewTransform2D _mvt;
	private PhetPPath _testShape;

	public EdgeOfWorldNode( RadioactiveDatingGameModel model, ModelViewTransform2D mvt){
		_model = model;
		_mvt = mvt;
		_testShape = new PhetPPath(Color.red,new BasicStroke(2), Color.red);
		_testShape.setPathTo(_mvt.createTransformedShape(_model.getEdgeOfWorld()));
		addChild( _testShape );
		_model.addObserver(new SimpleObserver(){
			public void update() {
				_testShape.setPathTo(_mvt.createTransformedShape(_model.getEdgeOfWorld()));
			}
		});
	}
}
