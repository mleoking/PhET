//View and Controller of ball in TableView
//BallImage has 4 sprite layers,
// 1: colored ball and textField label, on bottom, not grabbable
// 2: velocity arrow shaft, not grabbable
// 3: transparent disk for dragging ball, to set position
// 4: velocity arrow head, for dragging velocity arrow, to set velocity, on top

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
		//var canvas:Sprite;
		var ballAndLabel:Sprite;
		var arrowShaft:Sprite;
		var ballHandle:Sprite;
		var arrowHead:Sprite;
		var tFormat:TextFormat;
		var tField:TextField;
		
		public function BallImage(myModel:Model, indx:int, myTableView:TableView){
			this.myModel = myModel;
			this.myTableView = myTableView;
			this.indx = indx;
			this.pixelsPerMeter = this.myTableView.pixelsPerMeter;
			this.myBall = this.myModel.ball_arr[this.indx];
			this.ballAndLabel = new Sprite();
			this.arrowShaft = new Sprite();
			this.ballHandle = new Sprite();
			this.arrowHead = new Sprite();
			
			//this.canvas = new Sprite();
			//this.addChild(this.canvas);
			
			this.tField = new TextField();
			var ballNbr:String = String(1 + this.indx);
			this.tField.text = ballNbr;
			this.tFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.color = 0xffffff;
			tFormat.size = 20;
			this.tField.setTextFormat(tFormat);
			this.setLayerDepths();
			this.drawLayer1();
		}//end of constructor
		
		private function setLayerDepths():void{
			this.myTableView.canvas.addChild(this);
			this.addChild(this.ballAndLabel);
			this.ballAndLabel.addChild(this.tField);
		}
		
		public function drawLayer1():void{
			var g:Graphics = this.ballAndLabel.graphics;
			var currentColor:uint = this.myTableView.ballColor_arr[this.indx];
			var r:Number = this.myBall.radius;
			g.clear();
			g.lineStyle(1,0x000000);
			g.beginFill(currentColor);
			g.drawCircle(0,0,r*pixelsPerMeter);
			g.endFill();
			
			this.tField.autoSize = TextFieldAutoSize.CENTER;
			this.tField.x = -this.tField.width/2;
			this.tField.y = -this.tField.height/2;
		}//end of drawLayer1()
		
	}//end of class
}//end of package