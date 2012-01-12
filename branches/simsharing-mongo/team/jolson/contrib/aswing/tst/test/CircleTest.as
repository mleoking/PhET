import org.aswing.JOptionPane;
/**
 * @author iiley
 */
class test.CircleTest {
	
	public static function main():Void{
		JOptionPane.showMessageDialog("System", "Input your Name:", __entered);
	}
	
	public static function __entered(r:Number):Void{
		if(r == JOptionPane.CLOSE){
			JOptionPane.showMessageDialog("Result", "User canceled");
		}else{
			JOptionPane.showMessageDialog("Result", "it is: OK");
		}
	}
}