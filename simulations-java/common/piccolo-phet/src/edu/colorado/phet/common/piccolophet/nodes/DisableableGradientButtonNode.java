package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class defines a gradient button node such that the button can be set to
 * a disabled state where it appears grayed out and pressed (i.e. no drop
 * drop shadow visible).
 * 
 * @author John Blanco
 */
public class DisableableGradientButtonNode extends PNode {

	// The normal and grayed-out buttons.
	private GradientButtonNode normalButtonNode;
	private DisabledGradientButtonNode grayedOutButtonNode;
	
	private boolean enabled = true;
	
	/**
	 * Constructor.
	 */
	public DisableableGradientButtonNode(String label, int fontSize, Color buttonColor) {
		
		// Create the buttons.
		normalButtonNode = new GradientButtonNode(label, fontSize, buttonColor);
		addChild(normalButtonNode);
		grayedOutButtonNode = new DisabledGradientButtonNode(label, fontSize);
		grayedOutButtonNode.setVisible(false);
		grayedOutButtonNode.setPickable(false);
		grayedOutButtonNode.setChildrenPickable(false);
		addChild(grayedOutButtonNode);
	}

	public void setEnabled(boolean enabled){
		if (this.enabled != enabled){
			this.enabled = enabled;
			normalButtonNode.setVisible(enabled);
			grayedOutButtonNode.setVisible(!enabled);
		}
	}
	
    public void addActionListener( ActionListener listener ) {
    	normalButtonNode.addActionListener(listener);
    }

    public void removeActionListener( ActionListener listener ) {
    	normalButtonNode.removeActionListener(listener);
    }
    
    /**
     * This class defines a permanently disabled gradient button node.
     */
    private static class DisabledGradientButtonNode extends GradientButtonNode {

		public DisabledGradientButtonNode(String label, int fontSize) {
			super(label, fontSize, Color.GRAY, Color.LIGHT_GRAY);
			setPickable(false);
			setChildrenPickable(false);
			getButton().setOffset(SHADOW_OFFSET, SHADOW_OFFSET);
		}
    }
}
