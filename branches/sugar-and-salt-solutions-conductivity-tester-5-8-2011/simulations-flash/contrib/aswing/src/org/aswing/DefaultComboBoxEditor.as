import org.aswing.ComboBoxEditor;
import org.aswing.Component;
import org.aswing.EventDispatcher;
import org.aswing.JTextField;
import org.aswing.UIManager;

/**
 * Default to use a JTextField to be the editor. Just works with string values well.
 * @author iiley
 */
class org.aswing.DefaultComboBoxEditor extends EventDispatcher implements ComboBoxEditor {
    
    /**
     * When the editing entered.
     *<br>
     * onActionPerformed(source:DefaultComboBoxEditor)
     */ 
    public static var ON_ACT:String = EventDispatcher.ON_ACT;
    
    private var textField:JTextField;
    private var value:Object;
    
    public function DefaultComboBoxEditor(){
        value = null;
    }
    
    public function addActionListener(func:Function, obj:Object):Object{
        return addEventListener(ON_ACT, func, obj);
    }
    
    public function setEditable(b:Boolean):Void{
        getTextField().setEditable(b);
        getTextField().setEnabled(b);
        getTextField().setFocusable(b);
    }

    public function isEditable():Boolean{
        return getTextField().isEditable();
    }
    
    public function getEditorComponent() : Component {
        return getTextField();
    }

    public function setValue(v : Object) : Void {
        getTextField().setText(v.toString());
        value = v;
    }

    public function getValue() : Object {
        return value;
    }

    public function selectAll() : Void {
    	getTextField().selectAll();
        getTextField().requestFocus();
    }
    
    public function toString():String{
        return "DefaultComboBoxEditor[]";
    }
    
    //------------------------------------------------------

    
    private function getTextField():JTextField{
        if(textField == null){
            textField = new JTextField(null, 1); //set rows 1 to ensure the JTextField has a perfer height when empty
            textField.setBorder(null);
            textField.setForeground(UIManager.getColor("ComboBox.foreground"));
            textField.setTriggerEnabled(false);
            textField.setOpaque(false);
            initHandler();
        }
        return textField;
    }

    private function initHandler():Void{
        textField.addActionListener(__textFieldActed, this);
        textField.addEventListener(JTextField.ON_FOCUS_LOST, __grapValueFormText, this);
    }

    private function __grapValueFormText():Void{
        value = getTextField().getText();
    }

    private function __textFieldActed():Void{
        __grapValueFormText();
        dispatchEvent(createEventObj(ON_ACT));
    }   
}