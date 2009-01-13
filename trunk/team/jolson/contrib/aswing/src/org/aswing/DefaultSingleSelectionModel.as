/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;
import org.aswing.EventDispatcher;
import org.aswing.SingleSelectionModel;

/**
 * A generic implementation of SingleSelectionModel.
 * @author iiley
 */
class org.aswing.DefaultSingleSelectionModel extends EventDispatcher implements SingleSelectionModel {
	
	private var index:Number;
	
	public function DefaultSingleSelectionModel(){
		index = -1;
	}
	
	public function getSelectedIndex() : Number {
		return index;
	}

	public function setSelectedIndex(index : Number) : Void {
		if(this.index != index){
			this.index = index;
			fireChangeEvent();
		}
	}

	public function clearSelection() : Void {
		setSelectedIndex(-1);
	}

	public function isSelected() : Boolean {
		return getSelectedIndex() != -1;
	}
	
	/**
	 * listens to <code>Component.ON_STATE_CHANGED</code>
	 */
	public function addChangeListener(func : Function, obj : Object) : Object {
		return addEventListener(Component.ON_STATE_CHANGED, func, obj);
	}
	
	private function fireChangeEvent():Void{
		dispatchEvent(createEventObj(Component.ON_STATE_CHANGED));
	}
}