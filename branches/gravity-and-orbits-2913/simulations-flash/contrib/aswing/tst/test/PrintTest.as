/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;

/**
 * @author iiley
 */
class test.PrintTest extends JFrame {
	
	private var printButton:JButton;
	
	public function PrintTest() {
		super("PrintTest");
		printButton = new JButton("Print");
		printButton.addActionListener(__print, this);
		getContentPane().append(printButton, BorderLayout.SOUTH);
	}
	
	private function __print():Void{
		var my_pj:PrintJob = new PrintJob();
		if(my_pj.start()) {
			//print two page, one whole Frame, one just the button
		    if(print(my_pj) && printButton.print(my_pj)){
		    	my_pj.send();
		    }else{
		    	trace("Cant print!");
		    }
		}
	}
	
	public static function main():Void{
		var t:PrintTest = new PrintTest();
		t.setBounds(10, 10, 400, 400);
		t.show();
	}	
}