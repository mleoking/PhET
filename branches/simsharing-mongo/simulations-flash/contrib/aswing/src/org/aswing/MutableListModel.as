import org.aswing.ListModel;

/**
 * MutableListMode is a MVC pattern's mode for List UI, different List UI can connected to 
 * a same mode to view the mode's data. When the mode's data changed the mode should
 * fire a event to all its ListDataListeners.
 * @author bill
 */
interface org.aswing.MutableListModel extends ListModel {
	/**
	 * Inserts a element at specified position.
	 */
	public function insertElementAt(item:Object, index:Number):Void;
	
	/**
	 * Removes a element from a specified position.
	 */
	public function removeElementAt (index:Number):Void;
}