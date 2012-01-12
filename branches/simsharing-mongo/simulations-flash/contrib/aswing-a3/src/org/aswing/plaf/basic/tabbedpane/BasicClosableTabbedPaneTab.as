package org.aswing.plaf.basic.tabbedpane{

import org.aswing.*;
import org.aswing.border.EmptyBorder;

/**
 * The basic imp for ClosableTab
 * @author iiley
 */
public class BasicClosableTabbedPaneTab implements ClosableTab{
	
	protected var panel:Container;
	protected var label:JLabel;
	protected var button:AbstractButton;
	protected var margin:Insets;
	protected var owner:Component;
	
	public function BasicClosableTabbedPaneTab(){
		super();
	}
	
	public function initTab(owner:Component):void{
		this.owner = owner;
		panel = new Container();
		panel.setLayout(new BorderLayout());
		label = new JLabel();
		panel.append(label, BorderLayout.CENTER);
		button = createCloseButton();
		var bc:Container = new Container();
		bc.setLayout(new CenterLayout());
		bc.append(button);
		panel.append(bc, BorderLayout.EAST);
		label.setFocusable(false);
		button.setFocusable(false);
		margin = new Insets(0,0,0,0);
	}
	
	protected function createCloseButton():AbstractButton{
		var button:AbstractButton = new JButton();
		button.setMargin(new Insets());
		button.setOpaque(false);
		button.setBackgroundDecorator(null);
		button.setIcon(new CloseIcon());
		return button;
	}
	
	public function setFont(font:ASFont):void{
		label.setFont(font);
	}
	
	public function setForeground(color:ASColor):void{
		label.setForeground(color);
	}
	
	public function setMargin(m:Insets):void{
		if(!margin.equals(m)){
			panel.setBorder(new EmptyBorder(null, m));
			margin = m.clone();
		}
	}
	
	public function setEnabled(b:Boolean):void{
		label.setEnabled(b);
		button.setEnabled(b);
	}
	
	public function getCloseButton():Component{
		return button;
	}
	
	public function setVerticalAlignment(alignment:int):void{
		label.setVerticalAlignment(alignment);
	}
	
	public function getTabComponent():Component{
		return panel;
	}
	
	public function setHorizontalTextPosition(textPosition:int):void{
		label.setHorizontalTextPosition(textPosition);
	}
	
	public function setTextAndIcon(text:String, icon:Icon):void{
		label.setText(text);
		label.setIcon(icon);
	}
	
	public function setIconTextGap(iconTextGap:int):void{
		label.setIconTextGap(iconTextGap);
	}
	
	public function setSelected(b:Boolean):void{
		//do nothing
	}
	
	public function setVerticalTextPosition(textPosition:int):void{
		label.setVerticalTextPosition(textPosition);
	}
	
	public function setHorizontalAlignment(alignment:int):void{
		label.setHorizontalAlignment(alignment);
	}
	
}
}