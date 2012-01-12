/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.CellEditor;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.event.CellEditorListener;
import org.aswing.geom.Rectangle;
import org.aswing.table.TableCellEditor;
import org.aswing.tree.TreeCellEditor;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.AbstractCellEditor implements CellEditor, TableCellEditor, TreeCellEditor{
	
	private var listeners:Array;
	private var clickCountToStart:Number;
	private var editorComponentListener:Object;
	
	public function AbstractCellEditor(){
		listeners = new Array();
		clickCountToStart = 0;
		editorComponentListener = new Object();
		editorComponentListener[Component.ON_FOCUS_LOST] = Delegate.create(this, __editorComponentFocusLost);
		editorComponentListener[Component.ON_ACT] = Delegate.create(this, __editorComponentAct);
	}
	
    /**
     * Specifies the number of clicks needed to start editing.
     * Default is 0.(mean start after pressed)
     * @param count  an int specifying the number of clicks needed to start editing
     * @see #getClickCountToStart()
     */
    public function setClickCountToStart(count:Number):Void {
		clickCountToStart = count;
    }

    /**
     * Returns the number of clicks needed to start editing.
     * @return the number of clicks needed to start editing
     */
    public function getClickCountToStart():Number {
		return clickCountToStart;
    }	
    
    /**
     * Calls the editor's component to update UI.
     */
    public function updateUI():Void{
    	getEditorComponent().updateUI();
    }    
    
    public function getEditorComponent():Component{
		trace("Error: Subclass should override this method!");
		throw new Error("Subclass should override this method!");
		return null;
    }
	
	public function getCellEditorValue() {		
		trace("Error: Subclass should override this method!");
		throw new Error("Subclass should override this method!");
	}
	
   /**
    * Sets the value of this cell. Subclass must override this method to 
    * make editor display this value.
    * @param value the new value of this cell
    */
	private function setCellEditorValue(value):Void{		
		trace("Error: Subclass should override this method!");
		throw new Error("Subclass should override this method!");
	}

	public function isCellEditable(clickCount : Number) : Boolean {
		return clickCount == clickCountToStart;
	}

	public function startCellEditing(owner : Container, value, bounds : Rectangle) : Void {
		var com:Component = getEditorComponent();
		com.removeEventListener(editorComponentListener);
		com.destroy();
		com.setBounds(bounds);
		com.addTo(owner);
		setCellEditorValue(value);
		com.requestFocus();
		//if com is a container and can't has focus, then focus its first sub child.
		if(com instanceof Container && !com.isFocusOwner()){
			var con:Container = Container(com);
			for(var i:Number=0; i<con.getComponentCount(); i++){
				var sub:Component = con.getComponent(i);
				sub.requestFocus();
				if(sub.isFocusOwner()){
					break;
				}
			}
		}
		com.addEventListener(editorComponentListener);
		com.validate();
	}
	
	private function __editorComponentFocusLost():Void{
		cancelCellEditing();
	}
	
	private function __editorComponentAct():Void{
		stopCellEditing();
	}

	public function stopCellEditing() : Boolean {
		removeEditorComponent();
		fireEditingStopped();
		return true;
	}

	public function cancelCellEditing() : Void {
		removeEditorComponent();
		fireEditingCanceled();
	}
	
	public function isCellEditing() : Boolean {
		var editorCom:Component = getEditorComponent();
		return editorCom != null && editorCom.isShowing();
	}

	public function addCellEditorListener(l : CellEditorListener) : Void {
		listeners.push(l);
	}

	public function removeCellEditorListener(l : CellEditorListener) : Void {
		ArrayUtils.removeFromArray(listeners, l);
	}
	
	private function fireEditingStopped():Void{
		for(var i:Number = listeners.length-1; i>=0; i--){
			var l:CellEditorListener = CellEditorListener(listeners[i]);
			l.editingStopped(this);
		}
	}
	private function fireEditingCanceled():Void{
		for(var i:Number = listeners.length-1; i>=0; i--){
			var l:CellEditorListener = CellEditorListener(listeners[i]);
			l.editingCanceled(this);
		}
	}
	
	private function removeEditorComponent():Void{
		getEditorComponent().destroy();
	}
}