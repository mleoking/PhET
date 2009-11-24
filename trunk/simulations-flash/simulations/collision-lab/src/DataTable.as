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
		var canvas:Sprite;			//canvas holds several rowCanvases
		var rowCanvas_arr:Array;	//array of Sprites, each holds one row of textFields
		var colWidth:int;			//width of column in pix
		var rowHeight:int;			//height of row in pix
		var text_arr:Array;			//2D array of textFields
		var nbrColumns:int;			//nbr of columns in data table
		var tFormat:TextFormat;
		//var testField:TextField;		//for testing purposing
		
		public function DataTable(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myModel.registerView(this);
			this.myMainView = myMainView;
			this.maxNbrBalls = this.myModel.maxNbrBalls;
			this.nbrBalls = this.myModel.nbrBalls;
			this.nbrColumns = 9;
			this.colWidth = 60;
			this.rowHeight = 25;
			this.rowCanvas_arr = new Array(this.maxNbrBalls + 1); //header row + row for each ball
			this.text_arr = new Array(this.maxNbrBalls);	//9 columns
			this.tFormat = new TextFormat();
			this.tFormat.font = "Arial";
			this.tFormat.size = 14;
			this.tFormat.align = TextFormatAlign.CENTER;
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){  //header row + row for each ball
				this.text_arr[i] = new Array(this.nbrColumns);
				this.rowCanvas_arr[i] = new Sprite();
				for(var j:int = 0; j < this.nbrColumns; j++){
					this.text_arr[i][j] = new TextField();
					this.text_arr[i][j].defaultTextFormat = tFormat;
				}//for(j)
			}//for(i)
			this.initialize();
			
		}//end of constructor
		
		private function initialize():void{
			//var colWidth = 60;
			//var colHeight = 25;
			this.canvas = new Sprite;
			this.addChild(this.canvas);
			this.myMainView.addChild(this);
			this.drawBorder(this.nbrBalls);  //nbr of rows 
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){ 
				this.canvas.addChild(this.rowCanvas_arr[i]);
				for(var j:int = 0; j < this.nbrColumns; j++){
					this.rowCanvas_arr[i].addChild(this.text_arr[i][j]);
					this.rowCanvas_arr[i].y = i*this.rowHeight;
					this.text_arr[i][j].x = j*this.colWidth;
					this.text_arr[i][j].y = 0;
					this.text_arr[i][j].text = "row"+i;
					this.text_arr[i][j].width = this.colWidth;
					this.text_arr[i][j].height = this.rowHeight;
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
			this.setNbrDisplayedRows();
			this.update();
		}//end of initialize()
		
		private function drawBorder(nbrBalls:int):void{
			var nbrRows:int = nbrBalls + 1;  //one header row + 1 row per ball
			var g:Graphics = this.canvas.graphics;
			//var rowHeight = 30;
			var rowWidth = this.nbrColumns*this.colWidth;//0.85*this.myMainView.myTableView.width;
			g.clear();
			g.lineStyle(5,0x2222ff);
			g.beginFill(0xffff99);
			g.moveTo(0,0);
			g.lineTo(rowWidth, 0);
			g.lineTo(rowWidth, nbrRows*this.rowHeight);
			g.lineTo(0, nbrRows*this.rowHeight);
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
			this.tFormat.bold = true;
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){
				if(i != 0){this.text_arr[i][0].text = i;}
				for(var j:int = 0; j < this.nbrColumns; j++){
					if(i == 0 || j == 0){
						this.text_arr[i][j].setTextFormat(this.tFormat);
					}
				}//end for j
			}//end for i
		}//end makeHeaderRow
		
		public function setNbrDisplayedRows():void{
			this.nbrBalls = this.myModel.nbrBalls;
			this.drawBorder(this.nbrBalls);
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){
				if(i < this.nbrBalls + 1){
					this.rowCanvas_arr[i].visible = true;
				}else{
					this.rowCanvas_arr[i].visible = false;
				}
			}//end for(i)
		}//end setNbrDisplayedRows
		
		public function update():void{
			this.setNbrDisplayedRows();
			if(this.myModel.atInitialConfig){
				//trace("Calling DataTable.update. Time is " + this.myModel.time);
			for(var i:int = 1; i < this.maxNbrBalls + 1; i++){  //skip header row
				for(var j:int = 0; j < this.nbrColumns; j++){
					if(j == 0){
						//do nothing
					}else if(j == 1){			//mass in kg
						var mass:Number = this.myModel.ball_arr[i-1].mass;
						this.text_arr[i][j].text = this.round(mass, 1);
					}else if(j == 2){	//radius in m
						var radius:Number = this.myModel.ball_arr[i-1].radius;
						this.text_arr[i][j].text = this.round(radius, 2);
					}else if(j == 3){	//x position in m
						var xPos:Number = this.myModel.ball_arr[i-1].position.getX();
						//trace("DataTable, xPos of ball "+ i +" = "+xPos);
						this.text_arr[i][j].text = this.round(xPos, 2);
					}else if(j == 4){	//y position in m
						var yPos:Number = this.myModel.ball_arr[i-1].position.getY();
						this.text_arr[i][j].text = this.round(yPos, 2);
					}else if(j == 5){	//v_x in m/s
						var xVel:Number = this.myModel.ball_arr[i-1].velocity.getX();
						this.text_arr[i][j].text = this.round(xVel, 2);
					}else if(j == 6){	//v_y in m/s
						var yVel:Number = this.myModel.ball_arr[i-1].velocity.getY();
						this.text_arr[i][j].text = this.round(yVel, 2);
					}else if(j == 7){	//p_x in kg*m/s
						var xMom:Number = mass*xVel;
						this.text_arr[i][j].text = this.round(xMom, 2);
					}else if(j == 8){	//p_y in kg*m/s
						var yMom:Number = mass*yVel;
						this.text_arr[i][j].text = this.round(yMom, 2);
						
					}else{
						trace("ERROR in DataTable. Incorrect column index. j = " + j);
					}
				}//end for j
			}//end for i
			}//end if(this.myModel.atInitialConfig)
		}
		
		//round decimal number to n places
		private function round(input:Number, nPlaces:int):Number{
			var result:Number;
			var factor:Number = Math.pow(10, nPlaces);
			result = Math.round(factor*input)/factor;
			return result
		}
		
		private function padZeroes(input:Number, nPlaces):String{
			var result:String = ""+input;
			if(input == Math.round(input)){
				result += "."; 
				for (var i:int = 1; i <= nPlaces; i++){
					result += "0";
				}//end for()
			}//end if()
			
		}//end padZeroes
		
	}//end of class
}//end of package