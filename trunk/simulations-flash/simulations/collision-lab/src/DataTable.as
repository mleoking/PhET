//view of table of numbers for displaying and setting initial conditions
package{
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	
	public class DataTable extends Sprite{
		var myModel:Model;
		var myMainView:MainView;
		var nbrBalls:int;
		var maxNbrBalls:int;
		var canvas:Sprite;
		
		var text_arr:Array;		//2D array of textFields
		var testField:TextField;		//for testing purposing
		
		public function DataTable(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.maxNbrBalls = this.myModel.maxNbrBalls;
			this.nbrBalls = this.myModel.nbrBalls;
			this.initialize();
			
		}//end of constructor
		
		private function initialize():void{
			this.canvas = new Sprite;
			this.addChild(this.canvas);
			this.myMainView.addChild(this);
			this.drawBorder(this.nbrBalls);  //nbr of rows 
		}//end of initialize()
		
		private function drawBorder(nbrBalls:int):void{
			var nbrRows:int = nbrBalls + 1;  //one header row + 1 row per ball
			var g:Graphics = this.canvas.graphics;
			var rowHeight = 30;
			var rowWidth = 0.85*this.myMainView.myTableView.width;
			g.clear();
			g.lineStyle(5,0x2222ff);
			g.beginFill(0xffff99);
			g.moveTo(0,0);
			g.lineTo(rowWidth, 0);
			g.lineTo(rowWidth, nbrRows*rowHeight);
			g.lineTo(0, nbrRows*rowHeight);
			g.lineTo(0,0);
			g.endFill();
		}
		
	}//end of class
}//end of package