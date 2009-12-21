package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the legend that allows the user to see the names of
 * the various items floating around in the cell.
 * 
 * @author John Blanco
 */
public class Legend extends PNode {
	
	private static final Dimension2D SIZE = new PDimension(130, 300);
	private static final Stroke OUTLINE_STROKE = new BasicStroke(2f);
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	
	PPath background;
	
	public Legend(){
		background = new PhetPPath(BACKGROUND_COLOR, OUTLINE_STROKE, Color.BLACK);
		background.setPathTo(new RoundRectangle2D.Double(0, 0, SIZE.getWidth(), SIZE.getHeight(), 8, 8));
		addChild(background);
	}

}
