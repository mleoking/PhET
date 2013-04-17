import org.aswing.util.Reflection;
import org.aswing.geom.Point;
import org.aswing.ASColor;
/**
 * @author Igor Sadovskiy
 */
class test.ReflectionTest {
	
	public static function main() : Void {
		var t = new ReflectionTest();
	}
	
	public function ReflectionTest() {
		var a:Date = Reflection.createInstance(ASColor, [0xFFFFFF, 50]);
		trace("a = " + a.toString());	
	}
}