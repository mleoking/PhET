import org.aswing.ASFont;
import org.aswing.border.BevelBorder;
import org.aswing.BorderLayout;
import org.aswing.JFrame;
import org.aswing.JProgressBar;
import org.aswing.JScrollBar;
/**
 * @author iiley
 */
class test.ProgressBarTest extends JFrame{
		
	private var progBar:JProgressBar;
	private var indeterminateBar:JProgressBar;	
	private var scrollBar:JScrollBar;
	public function ProgressBarTest(){
		super("ProgressBarTest");
		
		progBar = new JProgressBar(JProgressBar.HORIZONTAL);
		progBar.setPreferredSize(100, 12);
		indeterminateBar = new JProgressBar(JProgressBar.VERTICAL);
		indeterminateBar.setIndeterminate(true);
		scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 100);
		scrollBar.addAdjustmentListener(__scrolled, this);
		getContentPane().append(scrollBar, BorderLayout.NORTH);
		
		initAsHorizontal();
		//initAsVertical();
	}
	
	private function initAsHorizontal():Void{
		getContentPane().append(progBar, BorderLayout.SOUTH);
		progBar.setBorder(new BevelBorder(null, BevelBorder.RAISED));
		getContentPane().append(indeterminateBar, BorderLayout.EAST);
		indeterminateBar.setBorder(new BevelBorder());
	}
	
	private function initAsVertical():Void{
		//should embed this font in input swf first
		progBar.setFont(new ASFont("Arial", 9, false, false, false, true));
		progBar.setOrientation(JProgressBar.VERTICAL);
		getContentPane().append(progBar, BorderLayout.WEST);
	}
		
	private function __scrolled():Void{
		progBar.setValue(scrollBar.getValue());
		progBar.setString(progBar.getPercentComplete() * 100 + "%");
	}
		
	public static function main():Void{
		Stage.scaleMode = "noScale";
		try{
			trace("try ProgressBarTest");
			
			var p:ProgressBarTest = new ProgressBarTest();
			p.setBounds(100, 100, 400, 300);
			p.show();
			trace("done ProgressBarTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}