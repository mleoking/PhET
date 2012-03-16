import org.aswing.tree.Identifiable;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * @author iiley
 */
class test.tree.Item implements Identifiable{
	
	private var value:String;
	private static var __id_generator:Number = 0;
	private var id:String;
	
	public function Item(value:String){
		this.value = value;
		id = (__id_generator++) + "";
	}
	
	public function getIdCode() : String {
		return id;
	}
	
	public function toString():String{
		return value;
	}
}