/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.geom.Dimension;
import org.aswing.JTextComponent;
import org.aswing.plaf.TextUI;
import org.aswing.UIManager;

/**
 * JTextField is a component that allows the editing of a single line of text. 
 * @author Tomato, iiley
 */
class org.aswing.JTextField extends JTextComponent {
	
	private var columns:Number;
	
	/**
	 * JTextField(text:String, columns:Number)<br>
	 * JTextField(text:String) columns default to 0<br>
	 * JTextField() text default to null and columns default to 0<br>
	 * <p>
	 * Constructs a new TextField initialized with the specified text and columns.
	 * 
	 * @param text the text to be displayed, if it is null or undefined, it will be set to "";
	 * @param columns the number of columns to use to calculate the preferred width;
	 * if columns is set to zero or min than zero, the preferred width will be matched just to view all of the text.
	 * default value is zero if missed this param.
	 * @see #setColumns()
	 */
	public function JTextField(text:String, columns:Number){
		super();
		setName("JTextField");
		this.text = (text == undefined ? "" : text);
		setColumns(columns);
		updateUI();
		addEventListener(ON_KEY_DOWN, __fireActionIfEnterKeyDown, this);
	}
		
    public function updateUI():Void{
    	setUI(TextUI(UIManager.getUI(this)));
    }	
	
	public function getUIClassID():String{
		return "TextFieldUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicTextFieldUI;
    }
	
	/**
	 * Sets the number of columns in this JTextField, if it changed then call parent to do layout. 
	 * @param columns the number of columns to use to calculate the preferred width;
	 * if columns is set to zero or min than zero, the preferred width will be matched just to view all of the text.
	 * default value is zero if missed this param.
	 */
	public function setColumns(columns:Number):Void{
		if(columns == undefined) columns = 0;
		if(columns < 0) columns = 0;
		if(this.columns != columns){
			this.columns = columns;
			if(displayable){
				revalidate();
			}
		}
	}
	
	/**
	 * @see #setColumns
	 */
	public function getColumns():Number{
		return columns;
	}
	
	private function isAutoSize():Boolean{
		return columns == 0;
	}	
	
    /**
     * addActionListener(fuc:Function, obj:Object)<br>
     * addActionListener(fuc:Function)<br>
     * Adds a action listener to this <code>JTextField</code>. <code>JTextField</code> 
     * fire a action event when user pressed <code>Key.ENTER</code> on it.
     * @param fuc the listener function.
     * @param obj which context to run in by the func.
     * @return the listener just added.
     * @see EventDispatcher#ON_ACT
     * @see Component#ON_RELEASE
     */
    public function addActionListener(fuc:Function, obj:Object):Object{
    	return addEventListener(ON_ACT, fuc, obj);
    }	
	
	/**
	 * JTextComponent need count preferred size itself.
	 */
	private function countPreferredSize():Dimension{
		if(columns > 0){
			var columnWidth:Number = getColumnWidth();
			var width:Number = columnWidth * columns + getWidthMargin();
			var height:Number = getRowHeight() + getHeightMargin();
			var size:Dimension = new Dimension(width, height);
			return getInsets().getOutsideSize(size);
		}else{
			return getInsets().getOutsideSize(getTextFieldAutoSizedSize());
		}
	}
	
	//--------------------------------------------------------------
//	private function __onFocusGainedExtraFix():Void{
//		if(!isTextFieldFocused()){
//			Selection.setFocus(getTextField());
//		}
//	}
	private function __fireActionIfEnterKeyDown():Void{
		if(Key.getCode() == Key.ENTER){
			fireActionEvent();
		}
	}
}
