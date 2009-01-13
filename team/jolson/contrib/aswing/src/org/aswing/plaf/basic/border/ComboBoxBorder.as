import org.aswing.border.BevelBorder;
import org.aswing.border.Border;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.JComboBox;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.border.ComboBoxBorder extends BevelBorder implements UIResource{
	
	private static var instance:Border;
	/**
	 * this make shared instance and construct when use.
	 */	
	public static function createInstance():Border{
		if(instance == null){
			instance = new ComboBoxBorder();
		}
		return instance;
	}
	
	public function ComboBoxBorder() {
		super(null,
			LOWERED,
			UIManager.getColor("ComboBox.light"), 
            UIManager.getColor("ComboBox.highlight"), 
            UIManager.getColor("ComboBox.darkShadow"), 
            UIManager.getColor("ComboBox.shadow"));
	}
	
    public function paintBorderImp(c:Component, g:Graphics, b:Rectangle):Void{
    	var box:JComboBox = JComboBox(c);
    	if(box.isEditable()){
    		bevelType = LOWERED;
    	}else{
    		bevelType = RAISED;
    	}
       	super.paintBorderImp(c, g, b);
    }
}