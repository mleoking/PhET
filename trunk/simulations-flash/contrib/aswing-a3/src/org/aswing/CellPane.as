/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * Cell Pane is just a container, it do not layout children, 
 * do not invalidate parent.
 * @author iiley
 */
public class CellPane extends Container{
	
	public function CellPane(){
		super();
	}
	
	override public function revalidate():void{
		valid = true;
	}
	
	override public function invalidate():void{
		valid = true;
	}
	
	override protected function invalidateTree():void{
		valid = true;
	}
	
	override public function validate():void {
		valid = true;
	}
}
}