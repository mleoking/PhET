//view of table of numbers for displaying and setting initial conditions
package{
	import flash.display.*;
	import flash.events.*;
	//import fl.events.*;
	import flash.text.*;
	
	public class DataTable extends Sprite{
		var myModel:Model;
		var myMainView:MainView;
		var nbrBalls:int;
		var maxNbrBalls:int;
		var canvas:Sprite;
		
		var text_arr:Array;		//2D array of textFields
		var nbrColumns:int;		//nbr of columns in data table
		var tFormat:TextFormat;
		var testField:TextField;		//for testing purposing
		
		public function DataTable(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.maxNbrBalls = this.myModel.maxNbrBalls;
			this.nbrBalls = this.myModel.nbrBalls;
			this.nbrColumns = 9;
			this.text_arr = new Array(this.maxNbrBalls);	//9 rows
			this.tFormat = new TextFormat();
			for(var i:int = 0; i < this.maxNbrBalls; i++){
				this.text_arr[i] = new Array(this.nbrColumns);
				for(var j:int = 0; j < this.nbrColumns; j++){
					this.text_arr[i][j] = new TextField();
				}//for(j)
			}//for(i)
			this.initialize();
			
		}//end of constructor
		
		private function initialize():void{
			var colWidth = 60;
			var colHeight = 20;
			this.canvas = new Sprite;
			this.addChild(this.canvas);
			this.myMainView.addChild(this);
			this.drawBorder(this.nbrBalls);  //nbr of rows 
			for(var i:int = 0; i < this.maxNbrBalls; i++){ //this.maxNbrBalls
				for(var j:int = 0; j < this.nbrColumns; j++){//this.nbrColumns
					this.canvas.addChild(this.text_arr[i][j]);
					this.text_arr[i][j].x = j*colWidth;
					this.text_arr[i][j].y = i*colHeight;
					this.text_arr[i][j].text = "row"+i;
					this.text_arr[i][j].width = 60;
					this.text_arr[i][j].height = 20;
					this.text_arr[i][j].border = true;
					if(i == 0 || j == 0){
						this.text_arr[i][j].type = TextFieldType.DYNAMIC;
						this.text_arr[i][j].selectable = false;
					}else{
						this.text_arr[i][j].type = TextFieldType.INPUT;
						this.text_arr[i][j].restrict = "0-9 .";
					}
				}//for(j)
			}//for(i)
			this.makeHeaderRow();
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
		}//end drawBorder()
		
		//header row is 
		//ball	mass	radius	x	y	vx	vy	px	py
		private function makeHeaderRow():void{
			this.text_arr[0][0].text = "ball";
			this.text_arr[0][1].text = "mass";
			this.text_arr[0][2].text = "radius";
			this.text_arr[0][3].text = "x";
			this.text_arr[0][4].text = "y";
			this.text_arr[0][5].text = "vx";
			this.text_arr[0][6].text = "vy";
			this.text_arr[0][7].text = "px";
			this.text_arr[0][8].text = "py";
			for(var i:int = 1; i < this.maxNbrBalls; i++){
				this.text_arr[i][0].text = i;
			}
		}//end makeHeaderRow
		
	}//end of class
}//end of package