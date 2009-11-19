//View and Controller of ball in TableView
package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	
	public class BallImage extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		var myBall:Ball;
		var indx:int;			//index labels ball 1, 2, 3,  
		var pixelsPerMeter:int;
		var canvas:Sprite;
		var pixelsPerMeter:Number;
		var tFormat:TextFormat;
		
		public function BallImage(myModel:Model, indx:int, myTableView){
			this.myModel = myModel;
			this.myTableView = myTableView;
			this.indx = indx;
			this.pixelsPerMeter = this.myTableView.pixelsPerMeter;
			this.pixelsPerMeter = pixPerM;
			this.myBall = this.myModel.ball_arr(this.index);
			this.drawImage();
		}//end of constructor
		
		public function drawImage():void{
			
		}
		
	}//end of class
}//end of package