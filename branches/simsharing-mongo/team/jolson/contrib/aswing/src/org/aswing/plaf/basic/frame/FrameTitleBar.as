/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Container;
import org.aswing.JFrame;
import org.aswing.LayoutManager;
import org.aswing.plaf.basic.frame.TitleBarLayout;
import org.aswing.plaf.ComponentUI;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.frame.FrameTitleBar extends Container {
	
	
	private var frame:JFrame;
	
	public function FrameTitleBar(frame:JFrame) {
		super();
		setName("FrameTitleBar");
		this.frame = frame;
		//updateUI is called in FrameUI to make it be controlled by FrameUI
		//updateUI();
	}
	
	public function setLayout(l:LayoutManager):Void{
		if(l instanceof TitleBarLayout){
			super.setLayout(l);
		}else{
			trace("FrameTitleBar just can accept FrameTitleBar!");
			throw new Error("FrameTitleBar just can accept FrameTitleBar!");
		}
	}
		
	public function getFrame():JFrame{
		return frame;
	}
	
    public function updateUI():Void{
    	//trace("FrameTitleBar is a instanceof Container? : " + (this instanceof Container));
    	setUI(UIManager.getUI(this));
    }
    
    public function setUI(newUI:ComponentUI):Void{
    	//trace("FrameTitleBar setUI : " + this);
    	super.setUI(newUI);
    }
	
	public function getUIClassID():String{
		return "Frame.titleBarUI";
	}
}
