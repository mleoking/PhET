package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	
	public class TableView extends Sprite{
		var myModel:Model;
		var myMainView;			//mediator and container of views
		var canvas:Sprite;		//background on which everything is placed
		var border:Sprite;		//reflecting border
		var borderColor:uint;	//color of border 0xrrggbb
		var pixelsPerMeter:int;	//scale of view
		var ball_arr:Array;		//array of ball images
		var ballColor_arr:Array;	//array of uint for colors of balls
		
		public function TableView(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.canvas = new Sprite();
			this.myMainView.addChild(this);
			this.addChild(this.canvas);
			this.canvas.x = 20;
			this.canvas.y = 60;
			this.myModel.registerView(this);
			this.pixelsPerMeter = 200;
			this.drawBorder();
			this.createBallImages();
			this.myModel.startMotion();
		}//end of constructor
		
		public function drawBorder():void{
			var W:Number = this.myModel.borderWidth * this.pixelsPerMeter;
			var H:Number = this.myModel.borderHeight * this.pixelsPerMeter;
			var thickness:Number = 6;  //border thickness in pixels
			var del:Number = thickness/2;
			//trace("width: "+W+"    height: "+H);
			with(this.canvas.graphics){
				clear();
				lineStyle(thickness,0xFF0000);
				var x0:Number = 0;
				var y0:Number = 0;
				beginFill(0x00ff00);
				moveTo(-del, -del);
				lineTo(W+del, -del);
				lineTo(W+del, +H+del);
				lineTo(-del, +H);
				lineTo(-del, -del);
				endFill();
			}
		}//end of drawBorder();
		
		public function createBallImages():void{
			var nbrBalls:int = this.myModel.nbrBalls;
			this.ball_arr = new Array(nbrBalls);
			for(var i:int = 0; i < nbrBalls; i++){
				this.ball_arr[i] = new Sprite();
				var radius:Number = this.pixelsPerMeter*this.myModel.ball_arr[i].radius;
				with(this.ball_arr[i].graphics){
					clear();
					beginFill(0x0000ff);
					lineStyle(1, 0x000000);
					drawCircle(0,0,radius);
					endFill();
				}//with
				ball_arr[i].x = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getX();
				ball_arr[i].y = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getY();
				//trace("i: "+i+"  x: "+ball_arr[i].x+"  y: "+ball_arr[i].y);
				this.canvas.addChild(ball_arr[i]);
				
			}//for
			
		}//createBallImages()
		
		public function update():void{
			//trace("myTableView.update() called.");
			var nbrBalls:int = this.myModel.nbrBalls;
			var yMax:Number = this.myModel.borderHeight;
			for(var i:int = 0; i < nbrBalls; i++){
				ball_arr[i].x = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getX();
				ball_arr[i].y = this.pixelsPerMeter*(yMax - this.myModel.ball_arr[i].position.getY());
			}
		}
		
	}//end of class
}//end of package