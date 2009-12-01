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
		var invisibleBorder:Sprite;	//draggable border
		var rowCanvas_arr:Array;	//array of Sprites, each holds one row of textFields
		var colWidth:int;			//width of column in pix
		var rowHeight:int;			//height of row in pix
		var text_arr:Array;			//2D array of textFields
		var nbrColumns:int;			//nbr of columns in data table
		var tFormat:TextFormat;
		var manualUpdating:Boolean;	//true if user is typing into textField, needed to prevent input-model-output loop
		//var currentBody:int;		//number of body associated with currently-selected textField
		//var currentProperty:int;	//property number of currently-selected textField, 1 = mass, 2 = x, etc
		//var testField:TextField;		//for testing purposing
		
		public function DataTable(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myModel.registerView(this);
			this.myMainView = myMainView;
			this.maxNbrBalls = this.myModel.maxNbrBalls;
			this.nbrBalls = this.myModel.nbrBalls;
			this.nbrColumns = 8;
			this.colWidth = 60;
			this.rowHeight = 27;
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
					this.text_arr[i][j].name = i;  //label textfield with ball number
				}//for(j)
			}//for(i)
			this.manualUpdating = false;
			this.initialize();
			
		}//end of constructor
		
		private function initialize():void{
			//var colWidth = 60;
			//var colHeight = 25;
			this.canvas = new Sprite;
			this.invisibleBorder = new Sprite();
			this.addChild(this.canvas);
			this.canvas.addChild(this.invisibleBorder);
			this.myMainView.addChild(this);
			
			//layout textFields
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){ 
				this.canvas.addChild(this.rowCanvas_arr[i]);
				for(var j:int = 0; j < this.nbrColumns; j++){
					this.rowCanvas_arr[i].addChild(this.text_arr[i][j]);
					this.rowCanvas_arr[i].y = i*this.rowHeight;
					this.text_arr[i][j].x = j*this.colWidth;
					this.text_arr[i][j].y = 0;
					this.text_arr[i][j].text = "row"+i;
					this.text_arr[i][j].width = this.colWidth - 5;
					this.text_arr[i][j].height = this.rowHeight - 5;
					this.text_arr[i][j].border = false;
					//Not user-settable: ballnbr, mass, px, py
					//0)ball  1)mass  2)x  3)y  4)vx  5)vy  6)px  7)py
					if(i == 0 || j == 0 || j == 6 || j == 7){
						this.text_arr[i][j].type = TextFieldType.DYNAMIC;
						this.text_arr[i][j].selectable = false;
					}else if(j > 0 && j < 4){
						this.dressInputTextField(i, j);
						this.text_arr[i][j].restrict = "0-9.";
					}else if(j == 4 || j == 5){
						this.dressInputTextField(i, j);
						this.text_arr[i][j].restrict = "0-9.\\-";  //velocities can be negative
					}else{
						trace("ERROR in DataTable.initialize. Invalid value of j");
					}
				}//for(j)
			}//for(i)
			this.drawBorder(this.nbrBalls);  //nbr of rows 
			this.makeHeaderRow();
			this.setNbrDisplayedRows();
			this.createTextChangeListeners();
			Util.makePanelDraggableWithBorder(this, this.invisibleBorder);
			this.update();
		}//end of initialize()
		
		public function dressInputTextField(i:int, j:int):void{
			this.text_arr[i][j].type = TextFieldType.INPUT;
			this.text_arr[i][j].border = true;
			this.text_arr[i][j].background = true;
			this.text_arr[i][j].backgroundColor = 0xffffff;
		}
		
		private function drawBorder(nbrBalls:int):void{
			var nbrRows:int = nbrBalls + 1;  //one header row + 1 row per ball
			var g:Graphics = this.canvas.graphics;
			//var rowHeight = 30;
			var rowWidth = this.nbrColumns*this.colWidth;//0.85*this.myMainView.myTableView.width;
			var bWidth = 5;   //borderWidth
			var del = bWidth/2;
			g.clear();
			g.lineStyle(bWidth,0x2222ff);
			g.beginFill(0xffff99);
			g.moveTo(0 - del,0 - del);
			g.lineTo(rowWidth + del, 0 - del);
			g.lineTo(rowWidth + del, nbrRows*this.rowHeight +del);
			g.lineTo(0 - del, nbrRows*this.rowHeight + del);
			g.lineTo(0 - del,0 - del);
			g.endFill();
			
			var gI:Graphics = this.invisibleBorder.graphics;
			gI.clear();
			gI.lineStyle(bWidth,0x000000, 0);
			gI.moveTo(0 - del,0 - del);
			gI.lineTo(rowWidth + del, 0 - del);
			gI.lineTo(rowWidth + del, nbrRows*this.rowHeight +del);
			gI.lineTo(0 - del, nbrRows*this.rowHeight + del);
			gI.lineTo(0 - del,0 - del);
			
		}//end drawBorder()
		
		//header row is 
		//ball	mass	x	y	vx	vy	px	py,   no radius for now	
		private function makeHeaderRow():void{
			this.text_arr[0][0].text = "ball";
			this.text_arr[0][1].text = "mass";
			//this.text_arr[0][2].text = "radius";
			this.text_arr[0][2].text = "x";
			this.text_arr[0][3].text = "y";
			this.text_arr[0][4].text = "vx";
			this.text_arr[0][5].text = "vy";
			this.text_arr[0][6].text = "px";
			this.text_arr[0][7].text = "py";
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
		
		//ball	mass	x	y	vx	vy	px	py
		public function createTextChangeListeners():void{
			for(var i:int = 1; i < this.maxNbrBalls + 1; i++){
				for(var j:int = 1; j < this.nbrColumns; j++){
					//this.currentBody = i;
					if(j == 1){
						this.text_arr[i][j].addEventListener(Event.CHANGE, changeMassListener);
					}else if(j == 2){
						this.text_arr[i][j].addEventListener(Event.CHANGE, changeXListener);
					}else if(j == 3){
						this.text_arr[i][j].addEventListener(Event.CHANGE, changeYListener);
					}else if(j == 4){
						this.text_arr[i][j].addEventListener(Event.CHANGE, changeVXListener);
					}else if(j == 5){
						this.text_arr[i][j].addEventListener(Event.CHANGE, changeVYListener);
					}else if(j ==6){
						//do nothing
					}else if(j == 7){
						//do nothing
					}
				}//end for j
			}//end for i
		}
		private function changeMassListener(evt:Event):void{
			this.manualUpdating = true;
			var mass = Number(evt.target.text);
			var ballNbr = Number(evt.target.name);  //first ball is ball 1, is Model.ball_arr[0]
			this.myModel.setMass(ballNbr - 1, mass);
			this.myMainView.myTableView.ball_arr[ballNbr - 1].drawLayer1();  //redraw ballImage for new diameter
			this.manualUpdating = false;
			//trace("DataTable.changeMassListener().mass:  "+mass);
			//trace("DataTable.curretBody:"+this.currentBody);
		}
		
		private function changeXListener(evt:Event):void{
			this.manualUpdating = true;
			var xPos = Number(evt.target.text);
			var ballNbr = Number(evt.target.name);  //first ball is ball 1, is Model.ball_arr[0]
			this.myModel.setX(ballNbr - 1, xPos);
			this.manualUpdating = false;
		}
		
		private function changeYListener(evt:Event):void{
			this.manualUpdating = true;
			var yPos = Number(evt.target.text);
			var ballNbr = Number(evt.target.name);  //first ball is ball 1, is Model.ball_arr[0]
			this.myModel.setY(ballNbr - 1, yPos);
			this.manualUpdating = false;
		}
		
		private function changeVXListener(evt:Event):void{
			this.manualUpdating = true;
			var xVel = Number(evt.target.text);
			var ballNbr = Number(evt.target.name);  //first ball is ball 1, is Model.ball_arr[0]
			this.myModel.setVX(ballNbr - 1, xVel);
			this.manualUpdating = false;
		}
		private function changeVYListener(evt:Event):void{
			this.manualUpdating = true;
			var yVel = Number(evt.target.text);
			var ballNbr = Number(evt.target.name);  //first ball is ball 1, is Model.ball_arr[0]
			this.myModel.setVY(ballNbr - 1, yVel);
			this.manualUpdating = false;
		}
		
		public function update():void{
			this.setNbrDisplayedRows();
			var i:int;
			var j:int
			var mass:Number;
			var nbrPlaces:int = 3  //number of places right of decimal pt displayed 
			if(!manualUpdating){   //do not update if user is manually filling textFields
			for(i = 1; i < this.maxNbrBalls + 1; i++){  //skip header row
				for(j = 0; j < this.nbrColumns; j++){
					if(j == 0){
						//do nothing
					}else if(j == 1){			//mass in kg
						mass = this.myModel.ball_arr[i-1].getMass();
						this.text_arr[i][j].text = this.round(mass, 1);
					//}else if(j == 2){	//radius in m
						//var radius:Number = this.myModel.ball_arr[i-1].getRadius();
						//this.text_arr[i][j].text = this.round(radius, 2);
					}else if(j == 2){	//x position in m
						var xPos:Number = this.myModel.ball_arr[i-1].position.getX();
						//trace("DataTable, xPos of ball "+ i +" = "+xPos);
						this.text_arr[i][j].text = this.round(xPos, nbrPlaces);
					}else if(j == 3){	//y position in m
						var yPos:Number = this.myModel.ball_arr[i-1].position.getY();
						this.text_arr[i][j].text = this.round(yPos, nbrPlaces);
					}else if(j == 4){	//v_x in m/s
						var xVel:Number = this.myModel.ball_arr[i-1].velocity.getX();
						this.text_arr[i][j].text = this.round(xVel, nbrPlaces);
					}else if(j == 5){	//v_y in m/s
						var yVel:Number = this.myModel.ball_arr[i-1].velocity.getY();
						this.text_arr[i][j].text = this.round(yVel, nbrPlaces);
					}else if(j == 6){	//p_x in kg*m/s
						//do nothing
					}else if(j == 7){	//p_y in kg*m/s
						//do nothing
					}else{
						trace("ERROR in DataTable. Incorrect column index. j = " + j);
					}
				}//end for j
			}//end for i
			}//end if(!manualUpdating)
			
			//update Momenta fields regardless of whether user is manually updating other fields
			for(i = 1; i < this.maxNbrBalls + 1; i++){  //skip header row
				mass = this.myModel.ball_arr[i-1].getMass();
				xVel = this.myModel.ball_arr[i-1].velocity.getX();
				yVel = this.myModel.ball_arr[i-1].velocity.getY();
				for(j = 0; j < this.nbrColumns; j++){
					if(j == 6){	//p_x in kg*m/s
						var xMom:Number = mass*xVel;
						this.text_arr[i][j].text = this.round(xMom, nbrPlaces);
					}else if(j == 7){	//p_y in kg*m/s
						var yMom:Number = mass*yVel;
						this.text_arr[i][j].text = this.round(yMom, nbrPlaces);
					}
				}//end for j
			}//end for i
		}//end update()
		
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
			return result;
		}//end padZeroes
		
	}//end of class
}//end of package