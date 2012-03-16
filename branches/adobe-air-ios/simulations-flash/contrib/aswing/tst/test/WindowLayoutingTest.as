/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JWindow;
import org.aswing.MCPanel;
import org.aswing.JFrame;
import org.aswing.WindowManager;
import org.aswing.WindowPane;
import org.aswing.CenterLayout;
import org.aswing.BoxLayout;
import org.aswing.LayoutManager;

/**
 *
 * @author iiley
 */
class test.WindowLayoutingTest {
	
	private var f1:JFrame;
	private var f2:JFrame;
	private var f3:JFrame;
	private var f4:JFrame;
	private var f5:JFrame;
	
	public function WindowLayoutingTest(){
		
		var button:JButton = new JButton("Hide");
		button.addActionListener(onHideClick, this);
		
		f1 = new JFrame("Frame 1");
		f1.setPreferredSize(200, 200);
		f1.getContentPane().append(button);
		f1.show(); 
		
		f2 = new JFrame("Frame 2");
		f2.setPreferredSize(200, 200);
		f2.show(); 

		f3 = new JFrame("Frame 3");
		f3.setPreferredSize(200, 200);
		f3.show(); 

		f4 = new JFrame("Frame 4");
		f4.setPreferredSize(200, 200);
		f4.show(); 

		f5 = new JFrame("Frame 5");
		f5.setPreferredSize(200, 200);
		f5.show(); 
		
		var p1:WindowPane = new WindowPane(new CenterLayout());
		p1.append(f1);

		var p2:WindowPane = new WindowPane(new BoxLayout(BoxLayout.Y_AXIS));
		p2.append(f3);
		p2.append(f4);
		
		var stage:WindowManager = WindowManager.getInstance();
		stage.append(p1, BorderLayout.CENTER);
		stage.append(f2, BorderLayout.EAST);
		stage.append(p2, BorderLayout.WEST);
		stage.append(f5, BorderLayout.SOUTH);
	}
	
	private function onHideClick(Void):Void
	{
		f3.hide();
		WindowManager.getInstance().revalidate();
	}
	
	public static function main():Void{
		try{
			Stage.align = "TL";
			Stage.scaleMode = "noScale";
			
			trace("try WindowLayoutingTest");
			var p:WindowLayoutingTest = new WindowLayoutingTest();
			trace("done WindowLayoutingTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}
