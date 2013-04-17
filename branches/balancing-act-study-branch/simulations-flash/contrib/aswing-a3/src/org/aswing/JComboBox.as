/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

import org.aswing.event.*;
import org.aswing.plaf.*;
import flash.events.Event;
import flash.display.InteractiveObject;
import org.aswing.plaf.basic.BasicComboBoxUI;

/**
 * Dispatched when the combobox act, when value set or selection changed.
 * @eventType org.aswing.event.AWEvent.ACT
 * @see org.aswing.JComboBox#addActionListener()
 */
[Event(name="act", type="org.aswing.event.AWEvent")]
		
/**
 *  Dispatched when the combobox's selection changed.
 * 
 *  @eventType org.aswing.event.InteractiveEvent.SELECTION_CHANGED
 */
[Event(name="selectionChanged", type="org.aswing.event.InteractiveEvent")]

/**
 * A component that combines a button or editable field and a drop-down list.
 * The user can select a value from the drop-down list, which appears at the 
 * user's request. If you make the combo box editable, then the combo box
 * includes an editable field into which the user can type a value.
 * <p>
 * <code>JComboBox</code> use a <code>JList</code> to be the drop-down list, 
 * so of course you can operate list to do some thing.
 * </p>
 * <p>
 * By default <code>JComboBox</code> can't count its preffered width accurately 
 * like default JList, you have to set its preffered size if you want. 
 * Or you make a not shared cell factory to it. see <code>ListCellFactory</code> 
 * and <code>JList</code> for details.
 * </p>
 * @see JList
 * @see ComboBoxEditor
 * @see DefaultComboBoxEditor
 * @author iiley
 */
public class JComboBox extends Component implements EditableComponent{
	
	private var editable:Boolean;
	private var maximumRowCount:int;
	protected var editor:ComboBoxEditor;
	protected var popupList:JList;
	
	/**
	 * Create a combobox with specified data.
	 */
	public function JComboBox(listData:*=null) {
		super();
		
		setName("JComboBox");
		maximumRowCount = 7;
		editable = false;
		setEditor(new DefaultComboBoxEditor());
		if(listData != null){
			if(listData is ListModel){
				setModel(ListModel(listData));
			}else if(listData is Array){
				setListData(listData as Array);
			}else{
				setListData(null); //create new
			}
		}
		
		updateUI();
	}
	
	/**
	 * Sets the ui.
	 * <p>
	 * JComboBox ui should implemented <code>ComboBoxUI</code> interface!
	 * </p>
	 * @param newUI the newUI
	 * @throws ArgumentError when the newUI is not an <code>ComboBoxUI</code> instance.
	 */
	override public function setUI(newUI:ComponentUI):void{
		if(newUI is ComboBoxUI){
			super.setUI(newUI);
			getEditor().getEditorComponent().setFont(getFont());
			getEditor().getEditorComponent().setForeground(getForeground());
		}else{
			throw new ArgumentError("JComboBox ui should implemented ComboBoxUI interface!");
		}
	}
	
	override public function updateUI():void{
		getPopupList().updateUI();
		editor.getEditorComponent().updateUI();
		setUI(UIManager.getUI(this));
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicComboBoxUI;
    }
	
	override public function getUIClassID():String{
		return "ComboBoxUI";
	}
	
    /**
     * Returns the ui for this combobox with <code>ComboBoxUI</code> instance
     * @return the combobox ui.
     */
    public function getComboBoxUI():ComboBoxUI{
    	return getUI() as ComboBoxUI;
    }
    
	/**
	 * The ActionListener will receive an ActionEvent when a selection has been made. 
	 * If the combo box is editable, then an ActionEvent will be fired when editing has stopped.
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.AWEvent#ACT
	 */
	public function addActionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(AWEvent.ACT, listener, false, priority, useWeakReference);
	}
	
	/**
	 * Removes a action listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.AWEvent#ACT
	 */
	public function removeActionListener(listener:Function):void{
		removeEventListener(AWEvent.ACT, listener, false);
	}
	
    	
	/**
	 * Add a listener to listen the combobox's selection change event.
	 * When the combobox's selection changed or a different value inputed or set programmiclly.
	 * @param listener the listener
	 * @param priority the priority
	 * @param useWeakReference Determines whether the reference to the listener is strong or weak.
	 * @see org.aswing.event.InteractiveEvent#SELECTION_CHANGED
	 */	
	public function addSelectionListener(listener:Function, priority:int=0, useWeakReference:Boolean=false):void{
		addEventListener(InteractiveEvent.SELECTION_CHANGED, listener, false, priority);
	}

	/**
	 * Removes a selection listener.
	 * @param listener the listener to be removed.
	 * @see org.aswing.event.InteractiveEvent#SELECTION_CHANGED
	 */
	public function removeSelectionListener(listener:Function):void{
		removeEventListener(InteractiveEvent.SELECTION_CHANGED, listener);
	}
	
	/**
	 * Returns the popup list that display the items.
	 */
	public function getPopupList():JList{
		if(popupList == null){
			popupList = new JList(null, new DefaultComboBoxListCellFactory());
			popupList.setSelectionMode(JList.SINGLE_SELECTION);		
			popupList.addSelectionListener(__listSelectionChanged);
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
	public function setMaximumRowCount(count:int):void{
		maximumRowCount = count;
	}
	
	/**
     * Returns the maximum number of items the combo box can display 
     * without a scrollbar
     * @return an integer specifying the maximum number of items that are 
     *         displayed in the list before using a scrollbar
     */
	public function getMaximumRowCount():int{
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
	public function setListCellFactory(newFactory:ListCellFactory):void{
		getPopupList().setCellFactory(newFactory);
	}
	
	/**
     * Sets the editor used to paint and edit the selected item in the 
     * <code>JComboBox</code> field.  The editor is used both if the
     * receiving <code>JComboBox</code> is editable and not editable.
     * @param anEditor  the <code>ComboBoxEditor</code> that
     *			displays the selected item
     */
	public function setEditor(anEditor:ComboBoxEditor):void{
		if(anEditor == null) return;
		
		var oldEditor:ComboBoxEditor = editor;
		if (oldEditor != null){
			oldEditor.removeActionListener(__editorActed);
			oldEditor.getEditorComponent().removeFromContainer();
		}
		editor = anEditor;
		editor.setEditable(isEditable());
		addChild(editor.getEditorComponent());
		if(ui != null){//means ui installed
			editor.getEditorComponent().setFont(getFont());
			editor.getEditorComponent().setForeground(getForeground());
			editor.getEditorComponent().setBackground(getBackground());
		}
		editor.addActionListener(__editorActed);
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
	 * Returns the editor component internal focus object.
	 */
	override public function getInternalFocusObject():InteractiveObject{
		if(isEditable()){
			return getEditor().getEditorComponent().getInternalFocusObject();
		}else{
			return this;
		}
	}
	
	/**
	 * Apply a new font to combobox and its editor and its popup list.
	 */
	override public function setFont(newFont:ASFont):void{
		super.setFont(newFont);
		getPopupList().setFont(newFont);
		getEditor().getEditorComponent().setFont(newFont);
	}
	
	/**
	 * Apply a new foreground to combobox and its editor and its popup list.
	 */
	override public function setForeground(c:ASColor):void{
		super.setForeground(c);
		getPopupList().setForeground(c);
		getEditor().getEditorComponent().setForeground(c);
	}
	
	/**
	 * Apply a new background to combobox and its editor and its popup list.
	 */
	override public function setBackground(c:ASColor):void{
		super.setBackground(c);
		getPopupList().setBackground(c);
		getEditor().getEditorComponent().setBackground(c);
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
     * @param b a boolean value, where true indicates that the
     *			field is editable
     */
	public function setEditable(b:Boolean):void{
		if(editable != b){
			editable = b;
			getEditor().setEditable(b);
			//editable changed, internal focus object will change too, so change the focus
			if(isFocusable() && isFocusOwner() && stage != null){
				if(stage.focus != getInternalFocusObject()){
					stage.focus = getInternalFocusObject();
				}
			}
		}
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
	override public function setEnabled(b:Boolean):void{
		super.setEnabled(b);
		if(!b && isPopupVisible()){
			setPopupVisible(false);
		}
		getEditor().setEditable(b && isEditable());
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
	public function setListData(ld:Array):void{
		getPopupList().setListData(ld);
	}
	
	/**
	 * Set the list mode to provide the data to JList.
	 * @see org.aswing.ListModel
	 */
	public function setModel(m:ListModel):void{
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
	public function showPopup():void{
		setPopupVisible(true);
	}
	/** 
     * Causes the combo box to close its popup window.
     * @see #setPopupVisible()
     */
	public function hidePopup():void{
		setPopupVisible(false);
	}
	/**
     * Sets the visibility of the popup, open or close.
     */
	public function setPopupVisible(v:Boolean):void{
		getComboBoxUI().setPopupVisible(this, v);
	}
	
	/** 
     * Determines the visibility of the popup.
     *
     * @return true if the popup is visible, otherwise returns false
     */
	public function isPopupVisible():Boolean{
		return getComboBoxUI().isPopupVisible(this);
	}
	
	 /** 
     * Sets the selected item in the combo box display area to the object in 
     * the argument.
     * If <code>item</code> is in the list, the display area shows 
     * <code>item</code> selected.
     * <p>
     * If <code>item</code> is <i>not</i> in the list and the combo box is
     * uneditable, it will not change the current selection. For editable 
     * combo boxes, the selection will change to <code>item</code>.
     * </p>
     * <code>AWEvent.ACT</code> (<code>addActionListener()</code>)events added to the combo box will be notified
     * when this method is called.
     * <br>
     * <code>InteractiveEvent.SELECTION_CHANGED</code> (<code>addSelectionListener()</code>)events added to the combo box will be notified
     * when this method is called only when the item is different from current selected item, 
     * it means that only when the selected item changed.
     *
     * @param item  the list item to select; use <code>null</code> to
     *              clear the selection.
     * @param programmatic indicate if this is a programmatic change.
     */
	public function setSelectedItem(item:*, programmatic:Boolean=true):void{
		var fireChanged:Boolean = false;
		if(item !== getSelectedItem()){
			fireChanged = true;
		}
		getEditor().setValue(item);
		var index:int = indexInModel(item);
		if(index >= 0){
			if(getPopupList().getSelectedIndex() != index){
				getPopupList().setSelectedIndex(index, programmatic);
				fireChanged = false;
			}
			getPopupList().ensureIndexIsVisible(index);
		}
		if(isFocusOwner()){
			getEditor().selectAll();
		}
		dispatchEvent(new AWEvent(AWEvent.ACT));
		if(fireChanged){
			dispatchEvent(new InteractiveEvent(InteractiveEvent.SELECTION_CHANGED, programmatic));
		}
	}
	
	/**
     * Returns the current selected item.
     * <p>
     * If the combo box is editable, then this value may not have been in 
     * the list model.
     * @return the current selected item
     * @see #setSelectedItem()
     */
	public function getSelectedItem():*{
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
     *			 indicates empty selection.
     * @param programmatic indicate if this is a programmatic change.
     */
	public function setSelectedIndex(anIndex:int, programmatic:Boolean=true):void{
		var size:int = getModel().getSize();
		if(anIndex < 0 || anIndex >= size){
			if(getSelectedItem() != null){
				getEditor().setValue(null);
				getPopupList().clearSelection();
			}
		}else{
			getEditor().setValue(getModel().getElementAt(anIndex));
			getPopupList().setSelectedIndex(anIndex, programmatic);
			getPopupList().ensureIndexIsVisible(anIndex);
		}
		dispatchEvent(new AWEvent(AWEvent.ACT));
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
	public function getSelectedIndex():int{
		return indexInModel(getEditor().getValue());
	}
	
	/**
     * Returns the number of items in the list.
     * @return an integer equal to the number of items in the list
     */
	public function getItemCount():int{
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
	public function getItemAt(index:int):*{
		return getModel().getElementAt(index);
	}
	
	//----------------------------------------------------------
	protected function __editorActed(e:Event):void{
		if(!isPopupVisible()){
			setSelectedItem(getEditor().getValue());
		}
	}
	
	protected function __listSelectionChanged(e:SelectionEvent):void{
		dispatchEvent(new InteractiveEvent(InteractiveEvent.SELECTION_CHANGED, e.isProgrammatic()));
	}
	
	protected function indexInModel(value:*):int{
		var model:ListModel = getModel();
		var n:int = model.getSize();
		for(var i:int=0; i<n; i++){
			if(model.getElementAt(i) === value){
				return i;
			}
		}
		return -1;
	}
	
}
}