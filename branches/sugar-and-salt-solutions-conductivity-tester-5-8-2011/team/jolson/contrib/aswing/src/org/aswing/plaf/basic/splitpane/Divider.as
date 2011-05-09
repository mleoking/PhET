﻿/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.Container;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JSplitPane;
import org.aswing.plaf.basic.icon.SolidArrowIcon;
import org.aswing.plaf.basic.splitpane.DividerIcon;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.splitpane.Divider extends Container {
	
	private var icon:Icon;
	private var leftButton:AbstractButton;
	private var rightButton:AbstractButton;
	private var leftIcon:SolidArrowIcon;
	private var rightIcon:SolidArrowIcon;
	private var sp:JSplitPane;
	
	public function Divider(sp:JSplitPane) {
		super();
		this.sp = sp;
		setOpaque(false);
		setFocusable(false);
		setBackground(sp.getBackground());
		leftButton = createCollapseLeftButton();
		rightButton = createCollapseRightButton();
		leftButton.setSize(leftButton.getPreferredSize());
		rightButton.setSize(rightButton.getPreferredSize());
		icon = new DividerIcon();
		append(leftButton);
		append(rightButton);
	}
	
	public function getOwner():JSplitPane{
		return sp;
	}
	
	private function paint(b:Rectangle):Void{
		super.paint(b);
		var g:Graphics = getGraphics();
		if(icon != null){
			icon.paintIcon(this, g, b.x, b.y);
		}
		if(sp.isOneTouchExpandable()){
			if(sp.getOrientation() == JSplitPane.VERTICAL_SPLIT){
				leftIcon.setArrow(-Math.PI/2);
				rightIcon.setArrow(Math.PI/2);
				leftButton.setLocation(4, 0);
				rightButton.setLocation(14, getHeight()-rightButton.getHeight());
			}else{
				leftIcon.setArrow(Math.PI);
				rightIcon.setArrow(0);
				leftButton.setLocation(0, 4);
				rightButton.setLocation(getWidth()-rightButton.getWidth(), 14);
			}
			leftButton.setVisible(true);
			rightButton.setVisible(true);
			leftButton.validate();
			rightButton.validate();
			leftButton.repaint();
			rightButton.repaint();
		}else{
			leftButton.setVisible(false);
			rightButton.setVisible(false);
		}
	}
	
	public function setIcon(icon:Icon):Void{
		if(this.icon != icon){
			uninstallIconWhenNextPaint(this.icon);
			this.icon = icon;
			repaint();
		}
	}
	
	public function getIcon():Icon{
		return icon;
	}
	
	private function createCollapseLeftButton():AbstractButton{
		leftIcon = new SolidArrowIcon(Math.PI, 8, sp.getForeground());
		return createButton(leftIcon);
	}
	private function createCollapseRightButton():AbstractButton{
		rightIcon = new SolidArrowIcon(0, 8, sp.getForeground());
		return createButton(rightIcon);
	}
	private function createButton(icon:Icon):AbstractButton{
		var btn:JButton = new JButton();
		btn.setOpaque(false);
		btn.setFocusable(false);
		btn.setBorder(null);
		btn.setMargin(new Insets());
		btn.setIcon(icon);
		return btn;
	}
	
	public function getCollapseLeftButton():AbstractButton{
		return leftButton;
	}
	
	public function getCollapseRightButton():AbstractButton{
		return rightButton;
	}
}