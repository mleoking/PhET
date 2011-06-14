/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.ext{

import org.aswing.BorderLayout;
import org.aswing.CenterLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.Insets;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JSeparator;
import org.aswing.JSpacer;
import org.aswing.LayoutManager;
import org.aswing.geom.IntDimension;

/**
 * Form is a vertical list of <code>FormRow</code>s.
 * Form will are tring to manage to layout <code>FormRow</code>s, if you append non-FormRow 
 * component to be child of Form, it will layouted as a <code>SoftBoxLayout</code> layouted.
 * @author iiley
 * @see FormRow
 */
public class Form extends JPanel implements LayoutManager{
	
	private var hGap:int;
	private var vGap:int;
	
	public function Form(){
		super();
		layout = this;
		hGap = vGap = 2;
	}
	
	public function setVGap(gap:int):void{
		if(this.vGap != gap){
			this.vGap = gap;
			revalidate();
		}
	}
	
	public function getVGap():int{
		return vGap;
	}	
	
	public function setHGap(gap:int):void{
		if(this.hGap != gap){
			this.hGap = gap;
			revalidate();
		}
	}
	
	public function getHGap():int{
		return hGap;
	}	
	
	override public function setLayout(layout:LayoutManager):void{
		if(!(layout is Form)){
			throw new ArgumentError("layout must be Form instance!");
			return;
		}
		super.setLayout(layout);
	}
	
	/**
	 * Adds a FormRow with columns children. Each child sit a row, null child make a column blank, 
	 * also a child can sit a continuous serveral columns.<br/>
	 * For the 3 case, they are:
	 * <p>
	 * <ul>
	 * 
	 * <li>
	 * [ --child1-- ][ --child2-- ][ --child3-- ]<br/>
	 * 3 children sit 3 columns, one by one: <br/>
	 * <code>addRow(child1, child2, child3);</code>
	 * </li>
	 * 
	 * <li>
	 * [ ---------- ][ --child1-- ][ --child2-- ]<br/>
	 * First blank, and then 2 children sit 2 columns: <br/>
	 * <code>addRow(null, child1, child2);</code>
	 * </li>
	 * 
	 * <li>
	 * [ ----------child1-------- ][ --child2-- ]<br/>
	 * child1 sit first two column2, child2 sit last column: <br/>
	 * <code>addRow(child1, child1, child2);</code>
	 * </li>
	 * 
	 * </ul>
	 * </p>
	 * <p>
	 * @return the form row.
	 */
	public function addRow(...columns):FormRow{
		var row:FormRow = createRow(columns);
		append(row);
		return row;
	}
	
	/**
	 * Appends a <code>JSeparator</code> and return it.
	 * @return the separator.
	 */
	public function addSeparator():JSeparator{
		var sp:JSeparator = new JSeparator(JSeparator.HORIZONTAL);
		append(sp);
		return sp;
	}
	
	public function addSpacer(height:int = 4):JSpacer{
		var sp:JSpacer = JSpacer.createVerticalSpacer(height);
		append(sp);
		return sp;
	}
	
	/**
	 * @see #addRow()
	 */	
	public function createRow(columns:Array):FormRow{
		var row:FormRow = new FormRow();
		row.setColumnChildren(columns);
		row.setGap(getHGap());
		return row;
	}
	
	/**
	 * @see #addRow()
	 */
	public function insertRow(index:int, ...columns):FormRow{
		var row:FormRow = createRow(columns);
		insert(index, row);
		return row;
	}
	
	public function createLeftLabel(text:String):JLabel{
		return new JLabel(text, null, JLabel.LEFT);
	}
	
	public function createRightLabel(text:String):JLabel{
		return new JLabel(text, null, JLabel.RIGHT);
	}
	
	public function createCenterLabel(text:String):JLabel{
		return new JLabel(text, null, JLabel.CENTER);
	}
	
	public function centerHold(comp:Component):Container{
		var p:JPanel = new JPanel(new CenterLayout());
		p.append(comp);
		return p;
	}
	
	public function leftHold(comp:Component):Container{
		var p:JPanel = new JPanel(new BorderLayout());
		p.append(comp, BorderLayout.WEST);
		return p;
	}
	
	public function rightHold(comp:Component):Container{
		var p:JPanel = new JPanel(new BorderLayout());
		p.append(comp, BorderLayout.EAST);
		return p;
	}
	
	public function flowLeftHold(gap:int, ...comps):Container{
		var p:JPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, gap, 0, false));
		for each(var i:Component in comps){
			p.append(i);
		}
		return p;
	}
	
	public function flowCenterHold(gap:int, ...comps):Container{
		var p:JPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, gap, 0, false));
		for each(var i:Component in comps){
			p.append(i);
		}
		return p;
	}
	
	public function flowRightHold(gap:int, ...comps):Container{
		var p:JPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, gap, 0, false));
		for each(var i:Component in comps){
			p.append(i);
		}
		return p;
	}
	
	/**
	 * Returns the FormRows list in the children list.
	 */
	public function getRows():Array{
		var rows:Array = new Array();
		var n:int = getComponentCount();
		for(var i:int=0; i<n; i++){
			var c:Component = this.getComponent(i);
			if(c is FormRow){
				rows.push(c);
			}
		}
		return rows;
	}
	
	/**
	 * Returns the children that are not Form Row.
	 */
	public function getOtherChildren():Array{
		var others:Array = new Array();
		var n:int = getComponentCount();
		for(var i:int=0; i<n; i++){
			var c:Component = this.getComponent(i);
			if(!(c is FormRow)){
				others.push(c);
			}
		}
		return others;
	}
	
	public function getColumnCount():int{
		var rows:Array = getRows();
		var count:int = 0;
		for(var i:int=0; i<rows.length; i++){
			var row:FormRow = rows[i];
			if(row.isVisible()){
				count = Math.max(count, row.getColumnCount());
			}
		}
		return count;
	}
	//___________________________layout manager_________________________________
	
	/**
	 * Returns the preferred size of specified column.
	 */
	protected function getColumnPreferredWidth(column:int, rows:Array):int{
		var wid:int = 0;
		for(var i:int=0; i<rows.length; i++){
			var row:FormRow = rows[i];
			if(row.isVisible()){
				wid = Math.max(wid, row.getColumnPreferredWidth(column));
			}
		}
		return wid;
	}	
	
	public function addLayoutComponent(comp:Component, constraints:Object):void{
	}
	
	public function invalidateLayout(target:Container):void{
	}
	
	public function minimumLayoutSize(target:Container):IntDimension{
		return getInsets().getOutsideSize(new IntDimension(0, 0));;
	}
	
	public function preferredLayoutSize(target:Container):IntDimension{
		var rows:Array = getRows();
    	var count:int;
    	var insets:Insets = target.getInsets();
    	var height:int = 0;
    	var width:int = 0;
    	var c:Component;
    	var i:int = 0;
    	count = getComponentCount();
    	for(i=0; i<count; i++){
    		c = getComponent(i);
    		if(c.isVisible()){
	    		var g:int = i > 0 ? getVGap() : 0;
	    		height += (c.getPreferredHeight() + g);
    		}
    	}
    	count = getColumnCount();
    	for(i=0; i<count; i++){
    		width += getColumnPreferredWidth(i, rows);
    		if(i > 0){
    			width += getHGap();
    		}
    	}
    	var others:Array = getOtherChildren();
    	count = others.length;
    	for(i=0; i<count; i++){
    		c = others[i];
    		if(c.isVisible()){
    			width = Math.max(width, c.getPreferredWidth());
    		}
    	}
    	
    	var dim:IntDimension = new IntDimension(width, height);
    	return insets.getOutsideSize(dim);
	}
	
	public function maximumLayoutSize(target:Container):IntDimension{
		return IntDimension.createBigDimension();
	}
	
	public function layoutContainer(target:Container):void{
		var rows:Array = getRows();
    	var columnWids:Array = new Array();
    	var n:int;
    	var i:int;
    	n = getColumnCount();
    	for(i=0; i<n; i++){
    		columnWids.push(getColumnPreferredWidth(i, rows));
    	}
    	
    	var insets:Insets = getInsets();
		var sx:int = insets.left;
		var sy:int = insets.top;
		var w:int = getWidth() - insets.getMarginWidth();
    	n = getComponentCount();
    	for(i=0; i<n; i++){
    		var c:Component = getComponent(i);
    		if(c.isVisible()){
	    		var row:FormRow = c as FormRow;
	    		var ph:int = c.getPreferredHeight();
	    		if(row){
	    			row.setColumnWidthes(columnWids);
	    			row.setGap(getHGap());
	    		}
	    		c.setComBoundsXYWH(sx, sy, w, ph);
	    		sy += (ph + getVGap());
    		}
    	}
	}
	
	public function removeLayoutComponent(comp:Component):void{
	}
	
	public function getLayoutAlignmentY(target:Container):Number{
		return getAlignmentY();
	}
	
	public function getLayoutAlignmentX(target:Container):Number{
		return getAlignmentX();
	}
	
}
}