/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.tabbedpane{

import org.aswing.*;
import org.aswing.border.EmptyBorder;

/**
 * BasicTabbedPaneTab implemented with a JLabel 
 * @author iiley
 * @private
 */
public class BasicTabbedPaneTab implements Tab{
	
	protected var label:JLabel;
	protected var margin:Insets;
	protected var owner:Component;
	
	public function BasicTabbedPaneTab(){
	}
	
	public function initTab(owner:Component):void{
		this.owner = owner;
		label = new JLabel();
		margin = new Insets(0,0,0,0);
	}
	
	public function setFont(font:ASFont):void{
		label.setFont(font);
	}
	
	public function setForeground(color:ASColor):void{
		label.setForeground(color);
	}
	
	public function setMargin(m:Insets):void
	{
		if(!margin.equals(m)){
			label.setBorder(new EmptyBorder(null, m));
			margin = m.clone();
		}
	}
	
	public function setVerticalAlignment(alignment:int):void
	{
		label.setVerticalAlignment(alignment);
	}
	
	public function getTabComponent():Component
	{
		return label;
	}
	
	public function setHorizontalTextPosition(textPosition:int):void
	{
		label.setHorizontalTextPosition(textPosition);
	}
	
	public function setTextAndIcon(text:String, icon:Icon):void
	{
		label.setText(text);
		label.setIcon(icon);
	}
	
	public function setIconTextGap(iconTextGap:int):void
	{
		label.setIconTextGap(iconTextGap);
	}
	
	public function setSelected(b:Boolean):void
	{
		//do nothing
	}
	
	public function setVerticalTextPosition(textPosition:int):void
	{
		label.setVerticalTextPosition(textPosition);
	}
	
	public function setHorizontalAlignment(alignment:int):void
	{
		label.setHorizontalAlignment(alignment);
	}
	
}
}