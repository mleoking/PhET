/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.JScrollBar;
import org.aswing.JScrollPane;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ScrollPaneUI;
import org.aswing.Viewportable;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.BasicScrollPaneUI extends ScrollPaneUI{
	
	private var scrollPane:JScrollPane;
	private var adjusterListener:Object;
	private var scrollPaneListener:Object;
	private var viewportStateChangeListener:Object;
	private var viewportChangedListener:Object;
	private var lastViewport:Viewportable;
	
	public function BasicScrollPaneUI(){
	}
    	
    public function installUI(c:Component):Void{
		scrollPane = JScrollPane(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		scrollPane = JScrollPane(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():Void{
		LookAndFeel.installBasicProperties(scrollPane, "ScrollPane.border");
        LookAndFeel.installBorder(scrollPane, "ScrollPane.border");
	}
    
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(scrollPane);
    }
    
	private function installComponents():Void{
    }
	private function uninstallComponents():Void{
    }
	
	private function installListeners():Void{
		scrollPaneListener = scrollPane.addEventListener(JScrollPane.ON_COM_ADDED, __comAdded, this);
		adjusterListener = scrollPane.addAdjustmentListener(__adjustChanged, this);
		viewportChangedListener = scrollPane.addEventListener(JScrollPane.ON_VIEWPORT_CHANGED, __viewportChanged, this);
		__viewportChanged();
	}
    
    private function uninstallListeners():Void{
    	scrollPane.removeEventListener(scrollPaneListener);
    	scrollPane.removeEventListener(adjusterListener);
    	scrollPane.removeEventListener(viewportChangedListener);
    	scrollPane.getViewport().removeEventListener(viewportStateChangeListener);
    }
    
	//-------------------------listeners--------------------------
    private function syncScrollPaneWithViewport():Void{
		var viewport:Viewportable = scrollPane.getViewport();
		var vsb:JScrollBar = scrollPane.getVerticalScrollBar();
		var hsb:JScrollBar = scrollPane.getHorizontalScrollBar();
		if (viewport != null) {
		    var extentSize:Dimension = viewport.getExtentSize();
		    if(extentSize.width <=0 || extentSize.height <= 0){
		    	//trace("/w/zero extent size");
		    	return;
		    }
		    var viewSize:Dimension = viewport.getViewSize();
		    var viewPosition:Point = viewport.getViewPosition();
			
		    if (vsb != null) {
				var extent:Number = extentSize.height;
				var max:Number = viewSize.height;
				var value:Number = Math.max(0, Math.min(viewPosition.y, max - extent));
				vsb.setValues(value, extent, 0, max);
				vsb.setUnitIncrement(viewport.getVerticalUnitIncrement());
				vsb.setBlockIncrement(viewport.getVerticalBlockIncrement());
	    	}

		    if (hsb != null) {
				var extent:Number = extentSize.width;
				var max:Number = viewSize.width;
				var value:Number = Math.max(0, Math.min(viewPosition.x, max - extent));
				hsb.setValues(value, extent, 0, max);
				hsb.setUnitIncrement(viewport.getHorizontalUnitIncrement());
				hsb.setBlockIncrement(viewport.getHorizontalBlockIncrement());
	    	}
		}
    }	
	
	private function __viewportChanged():Void{
		lastViewport.removeEventListener(viewportStateChangeListener);
		lastViewport = scrollPane.getViewport();
		viewportStateChangeListener = lastViewport.addChangeListener(__viewportStateChanged, this);
	}
	
	private function __viewportStateChanged():Void{
		syncScrollPaneWithViewport();
	}
	
	private function __comAdded(source:Container, child:Component):Void{
		var viewportCom:Component = scrollPane.getViewport().getViewportPane();
		if(child == viewportCom){
			//TODO check why need this??
			viewportCom.removeEventListener(viewportStateChangeListener);
			viewportCom.addEventListener(viewportStateChangeListener);
			syncScrollPaneWithViewport();
		}
	}
    private function __adjustChanged():Void{
    	var viewport:Viewportable = scrollPane.getViewport();
    	viewport.scrollRectToVisible(scrollPane.getVisibleRect());
    }
}
