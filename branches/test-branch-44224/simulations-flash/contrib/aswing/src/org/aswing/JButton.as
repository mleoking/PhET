/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.AbstractButton;
import org.aswing.DefaultButtonModel;
import org.aswing.Icon;
import org.aswing.plaf.ButtonUI;
import org.aswing.UIManager;
 
/**
 * An implementation of a "push" button.
 * @author iiley
 */
class org.aswing.JButton extends AbstractButton{
	
	/**
     * JButton(text:String, icon:Icon)<br>
     * JButton(text:String)<br>
     * JButton(icon:Icon)
     * <p>
	 */
	public function JButton(text, icon:Icon){
		super(text, icon);
		setName("JButton");
    	setModel(new DefaultButtonModel());
		updateUI();
	}
	
    public function updateUI():Void{
    	setUI(ButtonUI(UIManager.getUI(this)));
    }
	
	public function getUIClassID():String{
		return "ButtonUI";
	}
	
    public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingButtonUI;
    }
}
