package{
	import flash.display.*;
	//import flash.events.*;
	//import flash.text.*;
	//import flash.geom.*;
	
	public class Trajectories extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		var pixelsPerMeter:int;
		var maxNbrPaths:int;	//maximum nbr of paths shown = maximum nbr of balls
		var nbrPaths:int;		//current nbr of paths shown = current nbr of balls
		var path_arr:Array;
		
		public function Trajectories(myModel:Model, myTableView:TableView){
			this.myModel = myModel;
			this.myTableView = myTableView;
			this.pixelsPerMeter = this.myTableView.pixelsPerMeter;
			this.maxNbrPaths = this.myModel.maxNbrBalls;
			this.nbrPaths = this.myModel.nbrBalls;
			this.path_arr = new Array(this.maxNbrPaths);
			this.initialize();
		}//end of constructor
		
		private function initialize():void{
			for (var i:int = 0; i < this.maxNbrPaths; i++){
				this.path_arr[i] = new Sprite();
				this.addChild(this.path_arr[i]);
			}//end for()
		}//end of initialize
		
		public function erasePaths():void{
			//trace("Trajectories.erasePaths() called.");
			for (var i:int = 0; i < this.maxNbrPaths; i++){
				this.path_arr[i].graphics.clear();
			}
		}
		
		public function drawPaths():void{
			//trace("Trajectories.drawPaths() called.");
		}
		
		public function drawStep():void{
			for (var i:int = 0; i < this.nbrPaths; i++){
				var g:Graphics = this.path_arr[i].graphics;
				g.lineStyle(1, 0xffff00);
				trace("Trajectories.drawStep() called.");
				//g.moveTo.
				//g.lineTo.
			}
		}//end drawStep()
		
	}//end of class
}//end of package