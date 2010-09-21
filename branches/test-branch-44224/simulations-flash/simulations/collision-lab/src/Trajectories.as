//Draws paths(trajectories) of balls in TableView
//Version 1, no good handling of multiple events in same timeStep
package{
	import flash.display.*;
	//import flash.events.*;
	//import flash.text.*;
	//import flash.geom.*;
	
	public class Trajectories extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		var pixelsPerMeter:int;
		var borderHeight:Number;//height of Table in meters
		var maxNbrPaths:int;	//maximum nbr of paths shown = maximum nbr of balls
		var nbrPaths:int;		//current nbr of paths shown = current nbr of balls
		var path_arr:Array;		//array of Sprites showing paths
		var darkColor:uint;		//dark and light colors for dotted line
		var lightColor:uint;
		var currentColor:uint;
		var currentAlpha:Number;
		
		public function Trajectories(myModel:Model, myTableView:TableView){
			this.myModel = myModel;
			this.myTableView = myTableView;
			this.pixelsPerMeter = this.myTableView.pixelsPerMeter;
			this.maxNbrPaths = this.myModel.maxNbrBalls;
			this.borderHeight = this.myModel.borderHeight;
			this.nbrPaths = this.myModel.nbrBalls;
			this.path_arr = new Array(this.maxNbrPaths);
			this.initialize();
		}//end of constructor
		
		private function initialize():void{
			this.darkColor = 0x0000ff;
			this.lightColor = 0xffffff;
			this.currentColor = this.darkColor;
			this.currentAlpha = 1;
			for (var i:int = 0; i < this.maxNbrPaths; i++){
				this.path_arr[i] = new Sprite();
				this.addChild(this.path_arr[i]);
			}//end for()
		}//end of initialize
		
		public function updateNbrPaths():void{
			this.nbrPaths = this.myModel.nbrBalls;
			//erase high-index paths that should be invisible
			for (var i:int = this.nbrPaths; i < this.maxNbrPaths; i++){
				this.path_arr[i].graphics.clear();
			}
			//this.resetPaths();
		}
		
		public function erasePaths():void{
			//trace("Trajectories.erasePaths() called.");
			//this.myTableView.showingPaths = false;
			for (var i:int = 0; i < this.maxNbrPaths; i++){
				this.path_arr[i].graphics.clear();
			}
			this.resetPaths();
		}
		
		public function pathsOn():void{
			//trace("Trajectories.pathsOn()");
			this.myTableView.showingPaths = true;
			this.resetPaths();
		}
		
		public function pathsOff():void{
			//trace("Trajectories.pathsOff()");
			this.myTableView.showingPaths = false;
			this.erasePaths();
		}
		
		public function drawPaths():void{
			//trace("Trajectories.drawPaths() called.");
			this.myTableView.showingPaths = true;
			this.resetPaths();
			//trace("Trajectories.drawPaths() called.");
			
		}
		
		public function resetPaths():void{
			for (var i:int = 0; i < this.nbrPaths; i++){
				var g:Graphics = this.path_arr[i].graphics;
				g.lineStyle(1, 0x000000);
				var screenX = this.pixelsPerMeter*myModel.ball_arr[i].position.getX();
				var screenY = this.pixelsPerMeter*(this.borderHeight/2 - myModel.ball_arr[i].position.getY());
				g.moveTo(screenX, screenY);
			}
		}
		
		//called from TableView.update();
		public function drawStep():void{
			//trace("Trajectories.drawStep() called.");
			this.currentColor = this.darkColor;
			//this.currentAlpha == 1
			if(this.currentAlpha == 1){
				this.currentAlpha = 0;
			}else{
				this.currentAlpha = 1;
			}
			for (var i:int = 0; i < this.nbrPaths; i++){
				var g:Graphics = this.path_arr[i].graphics;
				g.lineStyle(2, this.currentColor, this.currentAlpha);
				//trace("Trajectories.drawStep() called.");
				var screenX = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getX();
				var screenY = this.pixelsPerMeter*(this.borderHeight/2 - this.myModel.ball_arr[i].position.getY());
				//trace("Trajectories.pixelsPerMeter: "+pixelsPerMeter);
				//trace("i: "+i+"   scrnX: "+screenX+"   scrnY: "+screenY);
				g.lineTo(screenX, screenY);
			}
		}//end drawStep()
		
	}//end of class
}//end of package