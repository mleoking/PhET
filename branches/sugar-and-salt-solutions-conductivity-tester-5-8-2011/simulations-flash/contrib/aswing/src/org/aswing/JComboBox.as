/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASFont;
import org.aswing.ComboBoxEditor;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultComboBoxEditor;
import org.aswing.overflow.JList;
import org.aswing.ListCellFactory;
import org.aswing.ListModel;
import org.aswing.plaf.ComboBoxUI;
import org.aswing.UIManager;

/**
 * A component that combines a button or editable field and a drop-down list.
 * The user can select a value from the drop-down list, which appears at the 
 * user's request. If you make the combo box editable, then the combo box
 * includes an editable field into which the user can type a value.
 * 
 * <p>
 * <code>JComboBox</code> use a <code>JList</code> to be the drop-down list, so of course you can operate 
 * list to do some thing.
 * <p>
 * By default <code>JComboBox</code> can't count its preffered width accurately 
 * like default JList, you have to set its preffered size if you want. 
 * Or you make a not shared cell factory to it. see <code>ListCellFactory</code> and <code>JList</code> for details.
 * @author iiley
 * @see JList
 * @see ComboBoxEditor
 * @see DefaultComboBoxEditor
 */
class org.aswing.JComboBox extends Container {
	
	private var editor:ComboBoxEditor;
	private var editable:Boolean;
	private var popupList:JList;
	private var editorLisenter:Object;
	private var maximumRowCount:Number;
	
	/**
	 * JComboBox(listData:Array)<br>
	 * JComboBox(model:ListModel)<br>
	 * JComboBox()
	 * <p>
	 */
	public function JComboBox(listData:Object) {
		super();
		
		setName("JComboBox");
		maximumRowCount = 7;
		editable = false;
		setEditor(new DefaultComboBoxEditor());
		if(listData != undefined){
			if(listData instanceof ListModel){
				setModel(ListModel(listData));
			}else{
				var o = listData;//avoid Array casting
				if(o instanceof Array){
					setListData(o);
				}else{
					setListData(null); //create new
				}
			}
		}
		
		updateUI();
	}
	
	public function setUI(ui:ComboBoxUI):Void{
		super.setUI(ui);
	}
	
	public function updateUI():Void{
		getPopupList().updateUI();
		editor.getEditorComponent().updateUI();
		setUI(ComboBoxUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "ComboBoxUI";
	}
	
	public function getUI():ComboBoxUI{
		return ComboBoxUI(ui);
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicComboBoxUI;
    }
	
	/**
	 * addChangeListener(func:Function)<br>
	 * addChangeListener(func:Function, obj:Object)
	 * <p>
	 * The ActionListener will receive an ActionEvent when a selection has been made. 
	 * If the combo box is editable, then an ActionEvent will be fired when editing has stopped.
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
	 * @return the listener added.
	 * @see #ON_ACT
	 * @see #addEventListener()
	 * @see #removeEventListener()
	 */
	public function addActionListener(func:Function, obj:Object):Object{
		return addEventListener(ON_ACT, func, obj);
	}
	
	/**
	 * Returns the popup list that display the items.
	 */
	public function getPopupList():JList{
		if(popupList == null){
			popupList = new JList();
			popupList.setFont(getFont());
		}
		return popupList;
	}
	/**
     * Sets the maximum number of rows the <code>JComboBox</code> displays.
     * If the number of objects in the model is greater than count,
     * the combo box uses a scrollbar.
     * @param count an integer specifying the maximum number of items to
     *              display in the list before using a scrollbar
     */
	public function setMaximumRowCount(count:Number):Void{
		maximumRowCount = count;
	}
	
	/**
     * Returns the maximum number of items the combo box can display 
     * without a scrollbar
     * @return an integer specifying the maximum number of items that are 
     *         displayed in the list before using a scrollbar
     */
	public function getMaximumRowCount():Number{
		return maximumRowCount;
	}
	
	/**
	 * @return the cellFactory for the popup List
	 */
	public function getListCellFactory():ListCellFactory{
		return getPopupList().getCellFactory();
	}
	
	/**
	 * This will cause all cells recreating by new factory.
	 * @param newFactory the new cell factory for the popup List
	 */
	public function setListCellFactory(newFactory:ListCellFactory):Void{
		getPopupList().setCellFactory(newFactory);
	}
	
	/**
     * Sets the editor used to paint and edit the selected item in the 
     * <code>JComboBox</code> field.  The editor is used both if the
     * receiving <code>JComboBox</code> is editable and not editable.
     * @param anEditor  the <code>ComboBoxEditor</code> that
     *			displays the selected item
     */
	public function setEditor(anEditor:ComboBoxEditor):Void{
		if(anEditor == null) return;
		
		var oldEditor:ComboBoxEditor = editor;
		if (oldEditor != null)
		{
			oldEditor.removeEventListener(editorLisenter);
			remove(oldEditor.getEditorComponent());
		}
		editor = anEditor;
		editor.setEditable(isEditable());
		append(editor.getEditorComponent());
		editor.getEditorComponent().setFont(getFont());
		editorLisenter = editor.addActionListener(__editorActed, this);
		revalidate();
	}
	
	/**
     * Returns the editor used to paint and edit the selected item in the 
     * <code>JComboBox</code> field.
     * @return the <code>ComboBoxEditor</code> that displays the selected item
     */
	public function getEditor():ComboBoxEditor{
		return editor;
	}
	
	/**
	 * Apply a new font to combobox and its editor and its popup list.
	 */
	public function setFont(newFont:ASFont):Void{
		super.setFont(newFont);
		getPopupList().setFont(newFont);
		getEditor().getEditorComponent().setFont(newFont);
	}
	
	/**
     * Determines whether the <code>JComboBox</code> field is editable.
     * An editable <code>JComboBox</code> allows the user to type into the
     * field or selected an item from the list to initialize the field,
     * after which it can be edited. (The editing affects only the field,
     * the list item remains intact.) A non editable <code>JComboBox</code> 
     * displays the selected item in the field,
     * but the selection cannot be modified.
     * 
     * @param aFlag a boolean value, where true indicates that the
     *			field is editable
     */
	public function setEditable(aFlag:Boolean):Void{
		editable = aFlag;
		getEditor().setEditable(aFlag);
	}
	/**
     * Returns true if the <code>JComboBox</code> is editable.
     * By default, a combo box is not editable.
     * @return true if the <code>JComboBox</code> is editable, else false
     */
	public function isEditable():Boolean{
		return editable;
	}
	
	/**
     * Enables the combo box so that items can be selected. When the
     * combo box is disabled, items cannot be selected and values
     * cannot be typed into its field (if it is editable).
     *
     * @param b a boolean value, where true enables the component and
     *          false disables it
     */
	public function setEnabled(b:Boolean):Void{
		if(b != isEnabled()){
			repaint();
		}
		super.setEnabled(b);
		for(var i:Number=0; i<this.getComponentCount(); i++){
			var com:Component = getComponent(i);
			if(com == getEditor().getEditorComponent()){
				getEditor().setEditable(b && isEditable());
			}else{
				com.setEnabled(b);
			}
		}
	}
	
	/**
	 * set a array to be the list data, but array is not a List Mode.
	 * So when the array content was changed, you should call updateListView
	 * to update the JList(the list for combo box).But this is not a good way, its slow.
	 * So suggest you to create a ListMode eg. VectorListMode,
	 * When you modify ListMode, it will automatic update JList.
	 * @see #setMode()
	 * @see org.aswing.ListModel
	 */
	public function setListData(ld:Array):Void{
		getPopupList().setListData(ld);
	}
	
	/**
	 * Set the list mode to provide the data to JList.
	 * @see org.aswing.ListModel
	 */
	public function setModel(m:ListModel):Void{
		getPopupList().setModel(m);
	}
	
	/**
	 * @return the model of this List
	 */
	public function getModel():ListModel{
		return getPopupList().getModel();
	}	
	/** 
     * Causes the combo box to display its popup window.
     * @see #setPopupVisible()
     */
	public function showPopup():Void{
		setPopupVisible(true);
	}
	/** 
     * Causes the combo box to close its popup window.
     * @see #setPopupVisible()
     */
	public function hidePopup():Void{
		setPopupVisible(false);
	}
	/**
     * Sets the visibility of the popup, open or close.
     */
	public function setPopupVisible(v:Boolean):Void{
		getUI().setPopupVisible(this, v);
	}
	/** 
     * Determines the visibility of the popup.
     *
     * @return true if the popup is visible, otherwise returns false
     */
	public function isPopupVisible():Boolean{
		return getUI().isPopupVisible(this);
	}
	
	 /** 
     * Sets the selected item in the combo box display area to the object in 
     * the argument.
     * If <code>anObject</code> is in the list, the display area shows 
     * <code>anObject</code> selected.
     * <p>
     * If <code>anObject</code> is <i>not</i> in the list and the combo box is
     * uneditable, it will not change the current selection. For editable 
     * combo boxes, the selection will change to <code>anObject</code>.
     * <p>
     * <code>ON_ACT</code> (<code>addActionListener()</code>)events added to the combo box will be notified
     * when this method is called.
     *
     * @param anObject  the list object to select; use <code>null</code> to
                        clear the selection
     */
	public function setSelectedItem(anObject:Object):Void{
		getEditor().setValue(anObject);
		var index:Number = indexInModel(anObject);
		if(index >= 0){
			getPopupList().setSelectedIndex(index);
			getPopupList().ensureIndexIsVisible(index);
		}
		getEditor().selectAll();
		fireActionEvent();
	}
	
	/**
     * Returns the current selected item.
     * <p>
     * If the combo box is editable, then this value may not have been in 
     * the list model.
     * @return the current selected Object
     * @see #setSelectedItem()
     */
	public function getSelectedItem():Object{
		return getEditor().getValue();
	}
	/**
     * Selects the item at index <code>anIndex</code>.
     * <p>
     * <code>ON_ACT</code> (<code>addActionListener()</code>)events added to the combo box will be notified
     * when this method is called.
     *
     * @param anIndex an integer specifying the list item to select,
     *			where 0 specifies the first item in the list and -1 or greater than max index
     *			 indicates empty selection
     */
	public function setSelectedIndex(anIndex:Number):Void{
		var size:Number = getModel().getSize();
		if(anIndex < 0 || anIndex >= size){
			getEditor().setValue(null);
		}else{
			getEditor().setValue(getModel().getElementAt(anIndex));
			getPopupList().setSelectedIndex(anIndex);
			getPopupList().ensureIndexIsVisible(anIndex);
		}
		fireActionEvent();
	}
	
	/**
     * Returns the first item in the list that matches the given item.
     * The result is not always defined if the <code>JComboBox</code>
     * allows selected items that are not in the list. 
     * Returns -1 if there is no selected item or if the user specified
     * an item which is not in the list.
     * @return an integer specifying the currently selected list item,
     *			where 0 specifies
     *                	the first item in the list;
     *			or -1 if no item is selected or if
     *                	the currently selected item is not in the list
     */
	public function getSelectedIndex():Number{
		return indexInModel(getEditor().getValue());
	}
	
	/**
     * Returns the number of items in the list.
     * @return an integer equal to the number of items in the list
     */
	public function getItemCount():Number{
		return getModel().getSize();
	}
	
	/**
     * Returns the list item at the specified index.  If <code>index</code>
     * is out of range (less than zero or greater than or equal to size)
     * it will return <code>undefined</code>.
     *
     * @param index  an integer indicating the list position, where the first
     *               item starts at zero
     * @return the <code>Object</code> at that list position; or
     *			<code>undefined</code> if out of range
     */
	public function getItemAt(index:Number):Object{
		return getModel().getElementAt(index);
	}
	
	//----------------------------------------------------------
	private function __editorActed():Void{
		setSelectedItem(getEditor().getValue());
	}
	
	private function indexInModel(value:Object):Number{
		var model:ListModel = getModel();
		var n:Number = model.getSize();
		for(var i:Number=0; i<n; i++){
			if(model.getElementAt(i) == value){
				return i;
			}
		}
		return -1;
	}
}
