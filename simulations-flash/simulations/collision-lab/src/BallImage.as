//View and Controller of ball in TableView
package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	
	public class BallImage() extends Sprite{
		var myModel:Model;
		var indx:int;			//index labels ball 1, 2, 3,  
		var canvas:Sprite;
		var tFormat:TextFormat;
		
		public class BallImage(myModel:Model, indx:int){
			this.myModel = myModel;
			this.indx = indx;
		}//end of constructor
		
	}//end of class
}//end of package