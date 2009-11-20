package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

public class ToolboxNode extends PNode {

	PhetPPath boxNode;
	
	public ToolboxNode(final JComponent parent) {
		parent.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	boxNode.setPathTo(new RoundRectangle2D.Double(0, 0, parent.getWidth(), 100, 15, 15));
		    	setOffset(0, parent.getHeight() - 105);
		    }
		    
		});
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, parent.getWidth(), 100, 15, 15), Color.WHITE, new BasicStroke(2f), Color.BLACK);
		addChild(boxNode);
	}
}
