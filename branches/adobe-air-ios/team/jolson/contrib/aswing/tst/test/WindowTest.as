/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JWindow;

/**
 *
 * @author iiley
 */
class test.WindowTest extends JWindow {
	
	private var button:JButton;
	
	public function WindowTest(){
		super(_root, false);
		
		button = new JButton("Button");
		
		getContentPane().append(button, BorderLayout.CENTER);
		
	}
	public static function main():Void{
		try{
			trace("try WindowTest");
			var p:WindowTest = new WindowTest();
			p.setLocation(10, 10);
			p.setSize(200, 200);
			p.show();
			trace("done WindowTest pane = " + p.getContentPane());
			trace("done WindowTest button = " + p.button);
		}catch(e){
			trace("error : " + e);
		}
	}
}
