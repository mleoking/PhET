import org.aswing.JCheckBox;
import org.aswing.JFrame;
import org.aswing.SoftBox;

/**
 * @author iiley
 */
class test.CheckBoxTest extends JFrame {
	
	public function CheckBoxTest() {
		super(_root, "CheckBoxTest", false);
		
		var pane1:SoftBox = SoftBox.createVerticalBox();
		pane1.append(new JCheckBox("check1"));
		pane1.append(new JCheckBox("check2"));
		setContentPane(pane1);
	}


	public static function main():Void{
		try{
			trace("try CheckBoxTest");
			
			var p:CheckBoxTest = new CheckBoxTest();
			p.setClosable(false);
			
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			
			trace("done CheckBoxTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}