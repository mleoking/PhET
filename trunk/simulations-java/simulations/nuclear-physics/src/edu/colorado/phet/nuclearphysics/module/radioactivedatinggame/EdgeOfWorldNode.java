package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
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
	
	private static final double EDGE_ANGLE = Math.PI * 0.23;  // In radians.
	private static final float STROKE_WIDTH = 2;
	private static final Stroke STROKE = new BasicStroke(STROKE_WIDTH);
	
	private RadioactiveDatingGameModel _model;
	private ModelViewTransform2D _mvt;
	private PhetPPath _background;
	private ArrayList<EdgeStratumNode> _edgeStratumNodes = new ArrayList<EdgeStratumNode>();

	/**
	 * Constructor.
	 * 
	 * @param model
	 * @param canvas
	 * @param mvt
	 */
	public EdgeOfWorldNode( RadioactiveDatingGameModel model, ModelViewTransform2D mvt ) {
		
		_model = model;
		_mvt = mvt;
		
		// Create the background that will sit behind the stratum edges.
		_background = new PhetPPath(Color.DARK_GRAY, new BasicStroke(2), Color.DARK_GRAY);
		addChild( _background );
		
		// 
		Iterable<Stratum> strata = _model.getStratumIterable();
		int stratumCount = 0;
		for (Stratum stratum : strata){
			Color stratmColor = NuclearPhysicsConstants.strataColors.get(stratumCount % NuclearPhysicsConstants.strataColors.size());
			stratmColor = ColorUtils.darkerColor(stratmColor,0.5);
			EdgeStratumNode edgeStratumNode = new EdgeStratumNode(stratum, stratmColor); 
			_edgeStratumNodes.add(edgeStratumNode);
			addChild(edgeStratumNode);
			stratumCount++;
		}
	}
	
	/**
	 * Update the appearance of the edge of the world.  The edge will be drawn
	 * from the corner X position to the edge of the canvas.
	 * 
	 * IMPORTANT NOTE: This method assumes that the edge of the world is being
	 * shown on the left side of the view port, and this would need to be
	 * changed or generalized to make it appear on the right instead.
	 *
	 * @param worldEdgeStartX - X position of the corner in intermediate coords.
	 * @param canvasEdgeX - X position of the edge of the canvas in intermediate coords.
	 */
	public void updateEdgeShape(double worldEdgeStartX, double canvasEdgeX){
		
		// Update the nodes that represent the edges of each of the individual
		// strata.
		for (EdgeStratumNode edgeStratumNode : _edgeStratumNodes){
			edgeStratumNode.updateEdgeShape(worldEdgeStartX, canvasEdgeX);
		}
		
		// Update the background.  This is here to prevent the stratum nodes
		// that are under the edge node from showing through.  Again, this
		// assumes that the edges are being shown on the left side.
		_background.setPathToRectangle(
				(float)canvasEdgeX,
				(float)_mvt.modelToViewY((_model.getBottomOfStrata() - _model.getTopOfStrata()) / 2), 
				(float)(worldEdgeStartX - canvasEdgeX - STROKE_WIDTH),
				(float)_mvt.modelToViewDifferentialY(((_model.getBottomOfStrata() - _model.getTopOfStrata()) / 2)));
	}
	
	/**
	 * This class represents the edge of a stratum, which means that it is
	 * angled in order to convey a 3D sort of effect that makes it look like
	 * the edge of
	 */
	private class EdgeStratumNode extends PhetPPath {

		Stratum _stratum;
		
		public EdgeStratumNode(Stratum stratum, Color color){
			super(color, STROKE, Color.BLACK);
			_stratum = stratum;
		}
		
		/**
		 * Update the appearance of this edge (i.e. angled) stratum.  The edge
		 * will be drawn from the corner X position to the edge of the canvas.
		 * 
		 * IMPORTANT NOTE: This method assumes that the edge of the world is being
		 * shown on the left side of the view port, and this would need to be
		 * changed or generalized to make it appear on the right instead.
		 *
		 * @param worldEdgeStartX - X position of the corner in intermediate coords.
		 * @param canvasEdgeX - X position of the edge of the canvas in intermediate coords.
		 */
		public void updateEdgeShape(double worldEdgeStartX, double canvasEdgeX){
			
			// Calculate the amount of distance in the Y direction that will
			// used to create the angled appearance.
			double deltaY = -(Math.abs(canvasEdgeX - worldEdgeStartX) * Math.tan(EDGE_ANGLE));
			
			// Create the shape that will define the stratum edge.
			
			GeneralPath edgeShapeIntermediateCoords = new GeneralPath();

			double topOfStratumY = _stratum.getTopYGivenX(_mvt.viewToModelX(worldEdgeStartX));
			double bottomOfStratumY = _stratum.getBottomYGivenX(_mvt.viewToModelX(worldEdgeStartX));
			edgeShapeIntermediateCoords.moveTo((float)worldEdgeStartX, (float)_mvt.modelToViewYDouble(topOfStratumY));
			edgeShapeIntermediateCoords.lineTo((float)worldEdgeStartX, (float)_mvt.modelToViewYDouble(bottomOfStratumY));
			edgeShapeIntermediateCoords.lineTo((float)canvasEdgeX, (float)(_mvt.modelToViewYDouble(bottomOfStratumY) + deltaY));
			edgeShapeIntermediateCoords.lineTo((float)canvasEdgeX, (float)(_mvt.modelToViewYDouble(topOfStratumY) + deltaY));
			edgeShapeIntermediateCoords.closePath();
			
			// Set the path to the new shape.
			setPathTo(edgeShapeIntermediateCoords);
		}
	}
}
