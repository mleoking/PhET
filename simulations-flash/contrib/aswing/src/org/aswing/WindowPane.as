import org.aswing.BorderLayout;
import org.aswing.Container;
import org.aswing.LayoutManager;

/**
 * @author Igor Sadovskiy
 */
class org.aswing.WindowPane extends Container {
	
	function WindowPane(layout:LayoutManager) {
		super();
		
		if (layout == undefined) layout = new BorderLayout();
		setLayout(layout);
		//setBorder(new LineBorder(null, ASColor.BLUE));
		setClipMasked(false);
	}

}