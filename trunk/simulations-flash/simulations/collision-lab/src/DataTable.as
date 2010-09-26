﻿//view of table of numbers for displaying and setting initial conditions
//one of two data tables is displayed: full data table or partial data table
//each has header row + row for each ball
//partial data table has column for ball number, col for mass, and col for mass slider

package{
import edu.colorado.phet.flashcommon.SimStrings;

import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.display.*;
import flash.events.*;
import fl.events.*;
import fl.controls.*;
import flash.text.*;
	
	public class DataTable extends Sprite{
		var myModel:Model;
		var myMainView:MainView;
		var nbrBalls:int;
		var maxNbrBalls:int;
		var canvas:Sprite;			//canvas holds several rowCanvases, full data table
		var invisibleBorder:Sprite;	//draggable border
		var rowCanvas_arr:Array;	//array of Sprites, each holds one row of textFields
		var colWidth:int;			//width of column in pix
		var rowHeight:int;			//height of row in pix
		var rowWidth:int;			//width of row in pix, used to set borderwidth
		var text_arr:Array;			//row of textFields, one for each of 9 columns, text must be internationalized
		//var toggleButton:Button;	//button to toggle full or partial data display
		var addBallButton:NiceButton;		//button to add ball, originally on Control Panel
		var removeBallButton:NiceButton; 	//button to remove ball, originally on Control Panel
		var moreOrLessDataButton:NiceButton;//button to toggle full or partial data display
		var addBallButton_sp:Sprite;		//addBallButton sprite
		var removeBallButton_sp:Sprite;		//removeBallButton sprite
		var moreOrLessDataButton_sp:Sprite;	//moreOrLessDataButton Sprite
		var massSlider_arr:Array;	//array of mass sliders
		var nbrColumns:int;			//nbr of columns in full data table
		var tFormat:TextFormat;
		var manualUpdating:Boolean;	//true if user is typing into textField, needed to prevent input-model-output loop
		var sliderUpdating:Boolean; //true if use is using mass slider
		//var testField:TextField;		//for testing purposing
		
		//following strings must be internationalized, see function initializeStrings() below
		var addBall_str:String;
		var removeBall_str:String;
		var moreData_str:String;
		var lessData_str:String;
		var ball_str:String;
		var mass_str:String;
		var x_str:String;
		var y_str:String;
		var Vx_str:String;
		var Vy_str:String;
		var Px_str:String;
		var Py_str:String;
		
		
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
			this.text_arr = new Array(this.maxNbrBalls + 1);	//rows
			this.massSlider_arr = new Array(this.maxNbrBalls + 1); 
			this.tFormat = new TextFormat();
			this.tFormat.font = "Arial";
			this.tFormat.size = 14;
			this.tFormat.align = TextFormatAlign.CENTER;
			//create textfields for full data table and mass sliders for partial data table
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){  //header row + row for each ball
				this.rowCanvas_arr[i] = new Sprite();
				this.text_arr[i] = new Array(this.nbrColumns);
				if(i > 0){
					var k:int = i - 1;
					this.massSlider_arr[k] = new Slider();
					this.massSlider_arr[k].name = k; //label slider with ball number: 0, 1, ..
					this.setupSlider(this.massSlider_arr[k]);
				}
				for(var j:int = 0; j < this.nbrColumns; j++){
					this.text_arr[i][j] = new TextField();
					this.text_arr[i][j].defaultTextFormat = tFormat;
					this.text_arr[i][j].name = i;  //label textfield with ball number
				}//for(j)
			}//for(i)
			this.manualUpdating = false;
			this.initialize(); //initialize full data table
			this.displayPartialDataTable(true);
			//this.initialize2(); //initialize partial data table
			
		}//end of constructor
		
		//create full data table
		private function initialize():void{
			//var colWidth = 60;
			//var colHeight = 25;
			this.canvas = new Sprite;
			this.invisibleBorder = new Sprite();
			//this.toggleButton = new Button()
			//following are symbols in Flash Library
			this.addBallButton_sp = new DataTableButtonBody();
			this.removeBallButton_sp = new DataTableButtonBody();
			this.moreOrLessDataButton_sp = new DataTableButtonBody();
			this.addBallButton = new NiceButton(this.addBallButton_sp, 90, addBall);
			this.removeBallButton = new NiceButton(this.removeBallButton_sp, 90, removeBall);
			this.moreOrLessDataButton = new NiceButton(this.moreOrLessDataButton_sp, 90, toggleDataButton);
			this.initializeStrings();
			//don't put buttons on canvas, since want buttons stationary when canvas resizes
			this.addChild(this.addBallButton_sp);
			this.addChild(this.removeBallButton_sp);
			this.addChild(this.moreOrLessDataButton_sp);
			this.addChild(this.canvas);
			this.canvas.addChild(this.invisibleBorder);
			//this.canvas.addChild(this.toggleButton);
			this.myMainView.addChild(this);
			
			
			//this.x = 60;
			
			//layout textFields in full data table
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){ 
				this.canvas.addChild(this.rowCanvas_arr[i]);
				
				if(i > 0){
					var k:int = i - 1;
					this.rowCanvas_arr[i].addChild(this.massSlider_arr[k]);
					this.massSlider_arr[k].x = 2.2*this.colWidth;
					this.massSlider_arr[k].y = 0.2*this.rowHeight;
					this.massSlider_arr[k].visible = false;
				}
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
			//manually set rowWidth for 1st call to positionButtons()
			this.rowWidth = 4.5*this.colWidth;
			this.positionButtons();
			this.makeHeaderRow();
			//this.setNbrDisplayedRows();  //not necessary, called in update()
			this.createTextChangeListeners();
			Util.makePanelDraggableWithBorder(this, this.invisibleBorder);
			this.update();
		}//end of initialize1()
		
		
		//following function to be altered during internationalization
		public function initializeStrings():void{
			this.addBall_str = SimStrings.get("DataTable.addBall","Add Ball");
			this.removeBall_str = SimStrings.get("DataTable.removeBall","Remove Ball");
			this.addBallButton.setLabel(this.addBall_str);
			this.removeBallButton.setLabel(this.removeBall_str);
			this.moreData_str = SimStrings.get("DataTable.moreData","More Data");
			this.lessData_str = SimStrings.get("DataTable.lessData","Less Data");
			this.moreOrLessDataButton.setLabel(this.moreData_str);
			this.ball_str = SimStrings.get("DataTable.ball","ball");
			this.mass_str = SimStrings.get("DataTable.mass","mass");
			this.x_str = SimStrings.get("DataTable.x","x");
			this.y_str = SimStrings.get("DataTable.y","y");
			this.Vx_str = SimStrings.get("DataTable.vx","Vx");
			this.Vy_str = SimStrings.get("DataTable.vy","Vy");
			this.Px_str = SimStrings.get("DataTable.px","Px");
			this.Py_str = SimStrings.get("DataTable.py","Py");
			
			
		}//end of initializeString()
		
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
			//var rowWidth = this.rowWidth; //this.nbrColumns*this.colWidth;//0.85*this.myMainView.myTableView.width;
			var bWidth = 5;   //borderWidth
			var del = bWidth/2;
			g.clear();
			g.lineStyle(bWidth,0x2222ff);
			g.beginFill(0xffff99);
			g.drawRect(-del, -del, this.rowWidth + 2*del, nbrRows*this.rowHeight + 2*del);
			//g.moveTo(0 - del,0 - del);
			//g.lineTo(rowWidth + del, 0 - del);
			//g.lineTo(rowWidth + del, nbrRows*this.rowHeight +del);
			//g.lineTo(0 - del, nbrRows*this.rowHeight + del);
			//g.lineTo(0 - del,0 - del);
			g.endFill();
			
			var gI:Graphics = this.invisibleBorder.graphics;
			gI.clear();
			gI.lineStyle(bWidth,0x000000, 0);
			gI.drawRect(-del, -del, this.rowWidth + 2*del, nbrRows*this.rowHeight + 2*del);
			//gI.moveTo(0 - del,0 - del);
			//gI.lineTo(rowWidth + del, 0 - del);
			//gI.lineTo(rowWidth + del, nbrRows*this.rowHeight +del);
			//gI.lineTo(0 - del, nbrRows*this.rowHeight + del);
			//gI.lineTo(0 - del,0 - del);
		}//end drawBorder()
		
		//header row is 
		//ball	mass	x	y	vx	vy	px	py,   no radius for now	
		private function makeHeaderRow():void{
			this.text_arr[0][0].text = ball_str;
			this.text_arr[0][1].text = mass_str;
			//this.text_arr[0][2].text = "radius";
			this.text_arr[0][2].text = x_str;
			this.text_arr[0][3].text = y_str;
			this.text_arr[0][4].text = Vx_str;
			this.text_arr[0][5].text = Vy_str;
			this.text_arr[0][6].text = Px_str;
			this.text_arr[0][7].text = Py_str;
			this.tFormat.bold = true;
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){
				if(i != 0){this.text_arr[i][0].text = i;}
				for(var j:int = 0; j < this.nbrColumns; j++){
					if(i == 0 || j == 0){
						this.text_arr[i][j].setTextFormat(this.tFormat);
					}
				}//end for j
			}//end for i

            TextFieldUtils.resizeText(this.text_arr[0][0],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][1],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][2],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][3],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][4],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][5],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][6],TextFieldAutoSize.CENTER);
            TextFieldUtils.resizeText(this.text_arr[0][7],TextFieldAutoSize.CENTER);
		}//end makeHeaderRow
		
		
		public function positionButtons():void{
			//this.canvas.addChild(this.toggleButton);
			//this.toggleButton.buttonMode = true;
			//this.toggleButton.emphasized = true;
			//this.toggleButton.width = 90;//TODO: JO: How to set the width of this button properly?
			//this.toggleButton.label = this.moreData_str;
			//this.toggleButton.x = this.rowWidth + 0.2*this.toggleButton.width;
			//this.toggleButton.addEventListener(MouseEvent.CLICK, toggleButtonClick);
			//this.toggleButton.buttonMode = true;  //only works with Sprites
			
			this.addBallButton_sp.x = -0.6*this.addBallButton_sp.width-0.5*this.removeBallButton_sp.width;
			this.addBallButton_sp.y = -0.75*this.addBallButton_sp.height;
			this.removeBallButton_sp.x = 0;
			this.removeBallButton_sp.y = -0.75*this.addBallButton_sp.height;
			this.moreOrLessDataButton_sp.x = 0.5*this.removeBallButton_sp.width + 0.8*this.moreOrLessDataButton_sp.width;
			this.moreOrLessDataButton_sp.y = -0.75*this.addBallButton_sp.height;
		}
		
		
		
		public function setupSlider(mSlider:Slider):void{
			mSlider.minimum = 0.1;
			mSlider.maximum = 3.0;
			mSlider.snapInterval = 0.1;
			mSlider.value = 1;
			mSlider.width = 2.8*this.colWidth;
			mSlider.liveDragging = true;
			mSlider.addEventListener(Event.CHANGE, massSliderListener);
		}//end setupMassSlider()
		
		public function displayPartialDataTable(tOrF:Boolean):void{
			if(tOrF){
				this.rowWidth = 5.5*this.colWidth;
				this.canvas.x = -this.rowWidth/2;
			}else{
				this.rowWidth = this.nbrColumns*this.colWidth;
				this.canvas.x = -this.rowWidth/2;
			}
			//this.toggleButton.x = this.rowWidth + 0.2*this.toggleButton.width;
			this.drawBorder(this.nbrBalls);
			//hide all but 1st two columns for partial
			//this.drawBorder(this.nbrBalls, 4.5*this.colWidth)
			for(var i:int = 0; i < this.maxNbrBalls + 1; i++){
				if(i > 0){
					this.massSlider_arr[i - 1].visible = tOrF;
				}
				for(var j:int = 2; j < this.nbrColumns; j++){
					this.text_arr[i][j].visible = !tOrF;
				}//end for j
			}//end for i
		}//end displayPartialDataTable()
		
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
			this.massSlider_arr[ballNbr - 1].value = mass;
			this.myMainView.myTableView.ball_arr[ballNbr - 1].drawLayer1();  //redraw ballImage for new diameter
			this.myMainView.myTableView.ball_arr[ballNbr - 1].drawLayer4();  //redraw ballImage for new diameter
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
			var ballNbr:int = Number(evt.target.name);  //first ball is ball 1, is Model.ball_arr[0]
			this.myModel.setVY(ballNbr - 1, yVel);
			this.manualUpdating = false;
		}
		
		private function massSliderListener(evt:SliderEvent):void{
			this.sliderUpdating = true;
			var ballNbr:int = Number(evt.target.name); 
			var mass = Number(evt.target.value);
			this.myModel.setMass(ballNbr, mass);
			this.myMainView.myTableView.ball_arr[ballNbr].drawLayer1();  //redraw ballImage for new diameter
			this.myMainView.myTableView.ball_arr[ballNbr].drawLayer4();  //redraw ballImage for new diameter
			this.sliderUpdating = false;
			trace("ball "+ballNbr + "   value: "+evt.target.value);
		}
		
		//following function is obsolete
//		private function toggleButtonClick(evt:MouseEvent):void{
//			trace(evt.target.label);
//			if(evt.target.label == this.moreData_str){
//				evt.target.label = this.lessData_str;//TODO: JO: Also need to resize the button here?
//				this.displayPartialDataTable(false);
//			}else if(evt.target.label == this.lessData_str){
//				evt.target.label = this.moreData_str;
//				this.displayPartialDataTable(true);
//			}
//		}//toggleButtonClick
		
		private function toggleDataButton():void{
			if(this.moreOrLessDataButton.getLabel() == this.moreData_str){
				this.moreOrLessDataButton.setLabel(this.lessData_str);
				this.displayPartialDataTable(false);
			}else if(this.moreOrLessDataButton.getLabel() == this.lessData_str){
				this.moreOrLessDataButton.setLabel(this.moreData_str);
				this.displayPartialDataTable(true);
			}
		}//end toggleDataButton()
		
		
		private function addBall():void{
			this.myModel.addBall();
			this.checkBallNbrLimits();
		}
		
		private function removeBall():void{
			this.myModel.removeBall();
			this.checkBallNbrLimits();
		}//end removeBall()
		
		//if nbrBalls = max or min allowed, adjust display appropriately
		public function checkBallNbrLimits():void{
			if(this.nbrBalls == 1){
				this.removeBallButton_sp.visible = false;
				this.myMainView.myTableView.CM.visible = false;
			}else{
				this.removeBallButton_sp.visible = true;
				if(this.myMainView.controlPanel.showCMOn){
					this.myMainView.myTableView.CM.visible = true;
				}
			}
			if(this.nbrBalls == this.maxNbrBalls){
				this.addBallButton_sp.visible = false;
			}else{
				this.addBallButton_sp.visible = true;
			}
		}//end checkBallNbrLimits()
		
		private function testFunction():void{
			trace("button pushed");
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
			
			if(sliderUpdating){
				for(i = 0; i < this.maxNbrBalls; i++){
					mass = this.myModel.ball_arr[i].getMass();
					this.text_arr[i+1][1].text = this.round(mass, 1);
				}
			}
			
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