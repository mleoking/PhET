package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * This class represents the "toolbox" for gene network elements, which is
 * where the user can grab things and add them to the model.
 * 
 * @author John Blanco
 */
public class ToolboxNode extends PNode {
	
	// Defines the width of the tool box as a proportion of the parent window's
	// width.
	static final double WIDTH_PROPORTION = 0.6;
	
	// Aspect ratio, width divided by height, from which the height will be
	// calculated.
	static final double ASPECT_RATIO = 6; 
	
	// Offset from the bottom of the window.
	static final double OFFSET_FROM_BOTTOM = 10;
	

	PhetPPath boxNode;
	
	public ToolboxNode(final JComponent parent) {
		parent.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	double width = parent.getWidth() * WIDTH_PROPORTION;
		    	boxNode.setPathTo(new RoundRectangle2D.Double(0, 0, width, width / ASPECT_RATIO, 15, 15));
		    	setOffset(((double)parent.getWidth() - width) / 2,
		    			parent.getHeight() - boxNode.getHeight() - OFFSET_FROM_BOTTOM);
		    }
		    
		});
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, parent.getWidth(), 100, 15, 15), Color.WHITE, new BasicStroke(2f), Color.BLACK);
		addChild(boxNode);
	}
}
