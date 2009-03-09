//The View.  A GUI interface

class GUI {
private var stageW:Number = Stage.width;
private var stageH:Number = Stage.height;
private var nbrChargesCreated:Number = 0;  //used to name charge clip with unique ID number
private var nbrSensorsCreated:Number = 0;
private var model:ChargeGroup;
private var latticeW:Number;	//separation between adjacent E-field indicators (vanes) in pixels
private var latticeVW:Number	//width of square tiles in voltage mosaic
public var paneColor:Color;		//color object of voltage Sensor
private var colorVoltageIntervalID:Number; 
private var tileCounter:Number;
private var delI:Number;	//counter for hiRezVoltageDrawing
private var delJ:Number;	//counter for hiRezVoltageDrawing
private var x0:Number;		//x-coord of voltage tile which starts concentric update
private var y0:Number;		//y-coord of voltage tile which starts concentric update
private var tileLast:Number;	//total number of voltage tiles colored in concentric update
private var posLast:Number;		//index of last rArray element checked
private var rArray:Array;   //sorted array of [x,y,R] positions, used to draw voltage tiles from center outwards
private var nbrVoltageContours:Number; //used to count voltage contours, manage voltage TextFields
//all strings must be internationalizable:
var voltageUnit_str:String = " V";		//used in equipotential line label
var eFieldUnit_str:String = " V/m";		//used in E-field sensor label
var angleUnit_str:String = " deg";		//used in E-field sensor label

//constructor
function GUI(model:ChargeGroup) { 
	
	this.model = model;
	var thisGUI = this;
	
	_root.createEmptyMovieClip("voltageMosaic_mc",_root.getNextHighestDepth());
	_root.createEmptyMovieClip("voltageHiRezMosaic_mc",_root.getNextHighestDepth());
	_root.createEmptyMovieClip("backgroundGrid_mc",_root.getNextHighestDepth());
	_root.createEmptyMovieClip("vaneGrid_mc",_root.getNextHighestDepth());
	
	//create charge Bags
	_root.attachMovie("plusChargeBag","plusChargeBag_mc",_root.getNextHighestDepth());
	_root.attachMovie("minusChargeBag","minusChargeBag_mc",_root.getNextHighestDepth());
	_root.attachMovie("sensorBag","sensorBag_mc",_root.getNextHighestDepth());
	_root.attachMovie("controlPanel","controlPanel_mc",_root.getNextHighestDepth());
	
	var bagWidth:Number = _root.plusChargeBag_mc._width;
	this.positionClip(_root.plusChargeBag_mc, stageW - bagWidth,0.1*bagWidth);
	this.positionClip(_root.minusChargeBag_mc, stageW - bagWidth,1.2*bagWidth);
	this.positionClip(_root.sensorBag_mc, stageW - bagWidth,2.3*bagWidth);
	_root.plusChargeBag_mc.chargeBag_txt._visible = false;
	_root.minusChargeBag_mc.chargeBag_txt._visible = false;
	
	var panelWidth:Number = _root.controlPanel_mc._width;
	var panelHeight:Number = _root.controlPanel_mc._height;
	this.positionClip(_root.controlPanel_mc, stageW - panelWidth, stageH - 1.1*panelHeight);
	_root.controlPanel_mc.theView = thisGUI;
	
	this.makeChargeBagMousey(_root.minusChargeBag_mc);
	this.makeChargeBagMousey(_root.plusChargeBag_mc);
	this.makeSensorBagMousey(_root.sensorBag_mc);
	
	//create E-field sensor grid
	this.latticeW = stageW/15;	//width of lattice tile
	var nbrVanesX = Math.floor(stageW/latticeW);	//nbr of vanes in x-direction
	var nbrVanesY = Math.floor(stageH/latticeW);	//nbr of vanes in y-direction
	var startXPos = 0.5*(stageW - (nbrVanesX+1)*latticeW);
	var startYPos = 0.5*(stageH - (nbrVanesY+1)*latticeW);
	for(var i = 1; i <= nbrVanesY; i++){
		for(var j = 1; j <= nbrVanesX; j++){
			_root.vaneGrid_mc.attachMovie("vane","vaneY"+i+"X"+j,_root.vaneGrid_mc.getNextHighestDepth());
			_root.vaneGrid_mc["vaneY"+i+"X"+j]._x = startXPos + j*latticeW;
			_root.vaneGrid_mc["vaneY"+i+"X"+j]._y = startYPos + i*latticeW;
		}
	}
	//Draw background grid 
	this.drawBackgroundGrid();

	//create voltage mosaic  tile width of stageW/20 is best speed/rez compromise 
	this.loadBlankVoltageMosaic(_root.voltageMosaic_mc,stageW/20);
	this.loadBlankVoltageMosaic(_root.voltageHiRezMosaic_mc,stageW/64);
	
	//create sorted position array for coloring voltage tiles in correct order (from center outward)
	rArray = makeRArray();
	
	//initialize View
	_root.backgroundGrid_mc._visible = false;
	this.nbrVoltageContours = 0;
	
	//_root.voltageHiRezMosaic_mc._visible = false;
	this.tileCounter = 0;
	this.voltageTileOff();
	this.colorVoltageIntervalID = 0;
	this.stopHiResVoltageDrawing();
	//this.gridOn();
	this.gridOff();
	ObserverVaneClip.showDirAndMag = true;
	
	//create Voltage Sensor
	_root.attachMovie("voltageSensor","voltageSensor_mc",_root.getNextHighestDepth());
	_root.attachMovie("tapeMeasureHolder","tapeMeasure_mc",_root.getNextHighestDepth());
	_root.tapeMeasrure_mc.distanceUnit_str = " m";
	_root.tapeMeasure_mc._visible = false;
	_root.tapeMeasure_mc._x = 0.4*stageW;
	_root.tapeMeasure_mc._y = 0.9*stageH;
	var vClip_mc = _root.voltageSensor_mc;
	
	var traceVButton:ControlButton = new ControlButton(vClip_mc.traceVButton_mc, "plot", activateTrace);
	var clearTraceVButton:ControlButton = new ControlButton(vClip_mc.clearTraceV_mc, "clear", thisGUI.clearVoltageDrawing);
	
	function activateTrace():Void{
		thisGUI.traceV();
	}
	//vClip_mc.traceVButton_mc.onRelease = function(){
		//thisGUI.traceV();
	//}

	//vClip_mc.clearTraceV_mc.onRelease = function(){
		//thisGUI.clearVoltageDrawing();
	//}
	
	this.positionClip(vClip_mc, 1.1*vClip_mc._width, stageH-1.5*vClip_mc._height);			
	this.model.addObserver(vClip_mc);
	_root.voltageSensor_mc.selfRef_mc = vClip_mc;
	this.makeVoltageSensorDraggable(vClip_mc);
	//make voltage sensor respond to keyboard
	this.createKeyListener();
	
	_root.createEmptyMovieClip("voltageLineDrawing_mc",_root.getNextHighestDepth());
	//makeVoltageLineDrawingObserver();
	
}//end of constructor

//utility functions

//make position array for computing voltage tiles from center outward
function makeRArray():Array{
	var xHalfLength:Number = 64; //64;		//must be even
	var yHalfLength:Number = 48; //48;		//must be even
	var xMid:Number = 0;
	var yMid:Number = 0;
	var posList:Array = new Array(2*xHalfLength+1,2*yHalfLength+1);
	var posCntr:Number = 0;  //positon list counter

	for(var i=-xHalfLength; i<=xHalfLength; i++){
		for(var j=-yHalfLength; j<=yHalfLength; j++){
			var R:Number = (xMid - i)*(xMid - i)+(yMid - j)*(yMid - j);
			var position:Array = [i,j,R];
			posList[posCntr] = position;
			posCntr++;
		}//end of j-loop
	}//end of i-loop
	
	//sort the array
	function sortOnR(element1,element2){
		return (element1[2] - element2[2]);
	}
	posList.sort(sortOnR);
	return posList;
}//end of makeRArray()



function gridOn(){
	var grid_mc = _root.vaneGrid_mc;
	grid_mc._visible = true;
	for(var clip in grid_mc){
		if(typeof grid_mc[clip] == "movieclip"){
			this.model.addObserver(grid_mc[clip]);
		}
	}
	this.model.setChanged();
	this.model.notifyObservers();
}

function gridOff(){
	var grid_mc = _root.vaneGrid_mc;
	grid_mc._visible = false;
	for(var clip in grid_mc){
		if(typeof grid_mc[clip] == "movieclip"){
			this.model.removeObserver(grid_mc[clip]);
		}
	}
}

function voltageTileOn(){
	var mosaic_mc = _root.voltageMosaic_mc;
	mosaic_mc._visible = true;
	for(var clip in mosaic_mc){
		this.model.addObserver(mosaic_mc[clip]);
	}
	this.model.setChanged();
	this.model.notifyObservers();
}

function voltageTileOff(){
	var mosaic_mc = _root.voltageMosaic_mc;
	mosaic_mc._visible = false
	for(var clip in mosaic_mc){
		this.model.removeObserver(mosaic_mc[clip]);
	}
}


function clearAll(){		//clear stage of clips, clear model of charges,sensors,observers
	//this.gridOff(); 		//removes E-field vane observers
	//this.voltageTileOff(); 	//removes voltage tile observers
	
	this.clearVoltageDrawing();
	//_root.voltageLineDrawing_mc.clear();
	if(_root.controlPanel_mc.hiResVoltage_ch.checkState){
		//trace("stopping HiResVoltageDraw");
		this.stopHiResVoltageDrawing();
	}
	var maxNbrQClips = this.nbrChargesCreated;
	var maxNbrSClips = this.nbrSensorsCreated;
	for(var i = 1; i<= maxNbrQClips; i++){
		if(typeof _root["Q"+i] == "movieclip"){
			var qClip:MovieClip = _root["Q"+i];
			this.model.removeCharge(qClip.chargeObject);
			delete qClip.chargeObject;
			qClip.unloadMovie();
		}
	}
	for(var j = 1; j <= maxNbrSClips; j++){
		if(typeof _root["S"+j] == "movieclip"){
			var sClip = _root["S"+j];  
			this.model.removeObserver(sClip.arrow_mc);
			sClip.unloadMovie();
		}
	}
	_root.voltageSensor_mc.update(model);
}//end of clearAll()

function traceV():Void{
	if(!this.model.hasCharges()) {
		// if there are no charges, don't plot this
		return;
	}
	
	//var myModel = this.model;
	var delSA = 5;	 //step length along equipotential in pixels
	var delSB = 5;
	var VFAC = 1.917E-3; //voltage conversion factor
	//var delSASq = delSA*delSA;
	//var delSBSq = delSB*delSB;
	var tic = 0;
	var initX:Number = _root.voltageSensor_mc._x;
	var initY:Number = _root.voltageSensor_mc._y;
	var initV:Number = this.model.getV(initX,initY)[0];
	//create voltage textField
	this.nbrVoltageContours++;
	var vText_string = String(this.nbrVoltageContours);							
	_root.voltageLineDrawing_mc.createTextField(vText_string,this.nbrVoltageContours,initX-20,initY-10,40,20);
	_root.voltageLineDrawing_mc[vText_string].text = String(0.1*Math.round(10*VFAC*initV))+voltageUnit_str;
	_root.voltageLineDrawing_mc[vText_string].autoSize = "center";
	var textWidth = _root.voltageLineDrawing_mc[vText_string]._width;
	var textHeight = _root.voltageLineDrawing_mc[vText_string]._height;
	_root.voltageLineDrawing_mc[vText_string]._x = initX - 0.5*textWidth;
	_root.voltageLineDrawing_mc[vText_string]._y = initY - 0.5*textHeight;
	_root.voltageLineDrawing_mc[vText_string].backgroundColor = 0xFFFFCC;
	_root.voltageLineDrawing_mc[vText_string].background = true;
	_root.voltageLineDrawing_mc[vText_string]._visible = _root.controlPanel_mc.showNumbers_ch.checkState;

	
	var xNowA:Number = initX;		//A path is clockwise movement along equipotential
	var yNowA:Number = initY;
	var xNowB:Number = initX;		//B path is counterclockwise movement along equipot.
	var yNowB:Number = initY;
	var distSq:Number = 0;
	var pos_arrayA:Array;
	var pos_arrayB:Array;
	var nextXA:Number;
	var nextYA:Number;
	var nextXB:Number;
	var nextYB:Number;
	var readyToBreak:Boolean = false;
	_root.voltageLineDrawing_mc._alpha = 100;
	_root.voltageLineDrawing_mc.lineStyle(1.5);

	
	while(tic<500 && Math.abs(xNowA)< 1000 && Math.abs(yNowA)<1000 && Math.abs(xNowB)<1000 && Math.abs(yNowB)<1000){
		pos_arrayA = this.model.getMoveToSameVPos(initV,delSA,xNowA,yNowA);
		pos_arrayB = this.model.getMoveToSameVPos(initV,-delSB,xNowB,yNowB);
		//trace("delEAngA: "+pos_arrayA[2]);
		//var delEAngA = pos_arrayA[2];
		//var delEAngB = pos_arrayB[2];
		//var delFactor = 1.2;
		//if(delEAngA > delFactor*45/delSA){delSA = delSA/delFactor;}
		//if(delEAngA < 45/(delSA*delFactor)){delSA = delFactor*delSA;}
		//if(delEAngB > delFactor*45/delSB){delSB = delSB/delFactor;}
		//if(delEAngB < 45/(delSB*delFactor)){delSB = delFactor*delSB;}
		//trace("delSA:  "+delSA+"   delSB: "+delSB)
		nextXA = pos_arrayA[0]; nextYA = pos_arrayA[1];
		nextXB = pos_arrayB[0]; nextYB = pos_arrayB[1];
		with(_root.voltageLineDrawing_mc){
			moveTo(xNowA,yNowA);
			lineTo(nextXA,nextYA);
			moveTo(xNowB,yNowB);
			lineTo(nextXB,nextYB);
		}
		if(readyToBreak){break;}
		distSq = (nextXA-nextXB)*(nextXA-nextXB)+(nextYA-nextYB)*(nextYA-nextYB);
		
		if(distSq < (delSA*delSA + delSB*delSB)/3){ readyToBreak = true;}	//if A and B lines meet up, make one more pass thru drawing loop to close up curve
			
		xNowA = nextXA; yNowA = nextYA;
		xNowB = nextXB; yNowB = nextYB;
		tic++;
	}//end of while()
	

}//end of traceV()


function clearVoltageDrawing():Void{
	_root.voltageLineDrawing_mc.clear();
	for(var clip in _root.voltageLineDrawing_mc){
		//trace("_root.voltageLineDrawing_mc[clip]"+_root.voltageLineDrawing_mc[clip]);
		_root.voltageLineDrawing_mc[clip].removeTextField();
	}
	this.nbrVoltageContours = 0;
}
function showOrHideVoltageTextFields():Void{
	for(var clip in _root.voltageLineDrawing_mc){
		//trace("_root.voltageLineDrawing_mc[clip]"+_root.voltageLineDrawing_mc[clip]);
		_root.voltageLineDrawing_mc[clip]._visible = _root.controlPanel_mc.showNumbers_ch.checkState;
	}
}

function drawBackgroundGrid():Void{
	var canvas = _root.backgroundGrid_mc;
	var nbrYLines = 12; //grid set for 640x480 window
	var nbrXLines = 9;
	var majorSpace = stageW/nbrYLines;
	var minorSpace = majorSpace/5;
	canvas.lineStyle(2.5,0xFFCC33);
	for (var y = 0; y <= nbrXLines; y++){
		canvas.moveTo(0,y*majorSpace);
		canvas.lineTo(stageW,y*majorSpace);
	}
	for (var x = 0; x <= nbrYLines; x++){
		canvas.moveTo(x*majorSpace,0);
		canvas.lineTo(x*majorSpace,stageH);
	}
	canvas.lineStyle(1,0xFFCC33);
	for (var y = 0; y <= 5*nbrXLines; y++){
		canvas.moveTo(0,y*minorSpace);
		canvas.lineTo(stageW,y*minorSpace);
	}
	for (var x = 0; x <= 5*nbrYLines; x++){
		canvas.moveTo(x*minorSpace,0);
		canvas.lineTo(x*minorSpace,stageH);
	}
	canvas.attachMovie("scaleRuler","scaleRuler_mc",_root.getNextHighestDepth());
	this.positionClip(canvas.scaleRuler_mc,(1/6)*stageW,(17/18)*stageH);
	canvas.scaleRuler_mc._visible = false;
}//end of drawBackgroundGrid()

/*
function makeVoltageLineDrawingObserver():Void{
		//Set up equipotential canvas as Observer to gray when any charge moved
	var backgroundGridObserver = new Object();
	backgroundGridObserver.update = function(myModel:ChargeGroup){
		_root.voltageLineDrawing_mc.duplicateMovieClip("voltageLineCopy_mc",1917);
		
		//for (var prop in _root){
			//trace(_root[prop]);
		//}
		_root.voltageLineCopy_mc._alpha = 35;
		_root.voltageLineDrawing_mc.clear();
	}

	this.model.addObserver(backgroundGridObserver);
}
*/

function loadBlankVoltageMosaic(mosaic_mc:MovieClip, latticeVW:Number){
	var nbrTilesX = Math.floor(stageW/latticeVW);
	var nbrTilesY = Math.floor(stageH/latticeVW);
	var startVXPos = latticeVW/2;//0.5*(stageW - (nbrTilesX+1)*latticeVW);
	var startVYPos = latticeVW/2;//0.5*(stageH - (nbrTilesY+1)*latticeVW);
	for(var i = 0; i <= nbrTilesX-1; i++){
		for(var j = 0; j <= nbrTilesY-1; j++){
			mosaic_mc.attachMovie("voltageTile","tileX"+i+"tileY"+j,mosaic_mc.getNextHighestDepth());
			var thisTile:MovieClip = mosaic_mc["tileX"+i+"tileY"+j];
			thisTile.selfRef_mc = thisTile;
			thisTile._width = latticeVW;
			thisTile._height = latticeVW;
			thisTile._x = startVXPos+i*latticeVW;
			thisTile._y = startVYPos+j*latticeVW;
		}
	}	
}//end of loadBlankVoltageMosaic

function startHiResVoltageDrawing():Void{
	_root.voltageHiRezMosaic_mc._visible = true;
	this.delI = 0;
	this.delJ = 0
	clearInterval(this.colorVoltageIntervalID);
	this.colorVoltageIntervalID = setInterval(this,"colorAnotherTile",1);
}

function startHiResVoltageDrawingConcentric(x0:Number, y0:Number):Void{
	clearInterval(this.colorVoltageIntervalID);
	this.colorVoltageIntervalID = setInterval(this,"colorTilesOutward",1);
}

function stopHiResVoltageDrawing():Void{
	clearInterval(this.colorVoltageIntervalID);
	this.delI = 0;
	this.delJ = 0;
	//reset all tiles to white
	for(var i = 0; i < 64; i++){
		for(var j = 0; j < 48; j++){
			_root.voltageHiRezMosaic_mc["tileX"+i+"tileY"+j].paneColor.setRGB(0xFFFFFF)
		}
	}
	_root.voltageHiRezMosaic_mc._visible = false;
}

//stop voltage tile updating but do not erase
function pauseHiResVoltageDrawing():Void{
	clearInterval(this.colorVoltageIntervalID);
}
function colorAnotherTile():Void{
	var iA:Number = 0;
	var jA:Number = 0;
	
	for (var i=0; i<8; i++){
		for (var j=0; j<6; j++){
			iA=i*8 + delI;
			jA=j*8 + delJ;
			_root.voltageHiRezMosaic_mc["tileX"+iA+"tileY"+jA].update(this.model);//_visible = false;
		}
	}
	this.delI = (this.delI + 1)%8;
	if(this.delI == 0){this.delJ = (this.delJ + 1)%8;}
	//stop drawing when stage refreshed, to make other controls speedy
	if(this.delI == 0 && this.delJ == 0){this.pauseHiResVoltageDrawing();}
}

function colorTilesOutward():Void{
	//trace("x0:"+this.x0+"      y0:"+this.y0);
	var localRArray:Array = this.rArray;
	var posArray:Array;
	var localx0 = this.x0;
	var localy0 = this.y0;
	var xT:Number;	//T for tile
	var yT:Number;
	var localTileLast = this.tileLast;
	var localPosLast = this.posLast;
	for (var n = 0; n < 48; n++){
		posArray = localRArray[localPosLast+n];
		xT = posArray[0] + localx0;
		yT = posArray[1] + localy0;
		if(xT >=0 && xT < 64 && yT >=0 && yT < 48){ 
			_root.voltageHiRezMosaic_mc["tileX"+xT+"tileY"+yT].update(this.model);
			this.tileLast++;
		}
	}//end of n-loop
	this.posLast = this.posLast+48;
	if(this.tileLast > 64*48){
		this.pauseHiResVoltageDrawing();
		this.tileLast = 0;
	}
}//end of colorTilesOutward()

//Set up keyListener
function createKeyListener(){
	var thisGUI = this;
	var keyListener = new Object();
	var vSensorClip_mc = _root.voltageSensor_mc;
	var myModel = this.model;
	keyListener.onKeyDown = function(){
		if(Key.getCode()== Key.SPACE){
			//vSensorClip_mc.traceVButton_mc.onRelease()
			thisGUI.traceV();
			//trace("space");
		}else if(Key.getCode() == Key.RIGHT){
			vSensorClip_mc._x++;
			vSensorClip_mc.update(myModel);
		}else if(Key.getCode() == Key.LEFT){
			vSensorClip_mc._x--;
			vSensorClip_mc.update(myModel);
		}else if(Key.getCode() == Key.UP){
			vSensorClip_mc._y--;
			vSensorClip_mc.update(myModel);
		}else if(Key.getCode() == Key.DOWN){
			vSensorClip_mc._y++;
			vSensorClip_mc.update(myModel);
		}
	}
	Key.addListener(keyListener);
}//end of createKeyListener()

//set up functionality of charge bags
function makeChargeBagMousey(chargeBagClip:MovieClip){
	var thisGUI = this;
	var myModel = thisGUI.model;
	//create new charge
	chargeBagClip.onPress = function(){
		var signOfCharge = this.sign;		//sign of charge bag is +1  or -1
		if(signOfCharge == +1){
			var chargeClipName:String = "plusCharge_mc";
		}else{
			var chargeClipName:String = "minusCharge_mc";
		}

		thisGUI.nbrChargesCreated++;
		var chargeClipNbr_string:String = "Q" + String(thisGUI.nbrChargesCreated);
		_root.attachMovie(chargeClipName,chargeClipNbr_string,_root.getNextHighestDepth());
		var chargeClip_mc:MovieClip = _root[chargeClipNbr_string];
		chargeClip_mc.swapDepths("_root.tapeMeasure_mc");
		thisGUI.positionClip(chargeClip_mc,_root._xmouse,_root._ymouse);
		chargeClip_mc.startDrag(false,0,0,thisGUI.stageW,thisGUI.stageH);
		this.onMouseMove = function(){
			//chargeClip_mc.chargeObject.setPosition(_root._xmouse,_root._ymouse);
			updateAfterEvent();
		}
	}
		
	chargeBagClip.onReleaseOutside = function(){
		var chargeClip_mc = _root["Q"+String(thisGUI.nbrChargesCreated)];
		chargeClip_mc.stopDrag(false,0,0,thisGUI.stageW,thisGUI.stageH);
		thisGUI.makeChargeDraggable(chargeClip_mc);
		this.onMouseMove = undefined;
		//create new Charge instance and attach to clip
		var myChargeValue = chargeClip_mc.chargeValue;
		
		var mX = _root._xmouse;
		var mY = _root._ymouse;
		
		if(mX < 0) { mX = 0; }
		if(mX > Stage.width) { mX = Stage.width; }
		if(mY < 0) { mY = 0; }
		if(mY > Stage.height) { mY = Stage.height; }
		
		var presentCharge = new Charge(myChargeValue,myModel, mX, mY);
		chargeClip_mc.chargeObject = presentCharge;
		myModel.addCharge(presentCharge);
		//if(_root.controlPanel_mc.hiResVoltage_ch.checkState){
				//thisGUI.startHiResVoltageDrawing();
			//} 
		if(_root.controlPanel_mc.hiResVoltage_ch.checkState){
				thisGUI.x0 = Math.floor(_root._xmouse/10);
				thisGUI.y0 = Math.floor(_root._ymouse/10);
				thisGUI.tileLast = 0;
				thisGUI.posLast = 0;
				thisGUI.startHiResVoltageDrawingConcentric();
			}
		thisGUI.clearVoltageDrawing();
		//_root.voltageLineDrawing_mc.clear();
	}
	//if released inside chargeBag, then delete charge
	chargeBagClip.onRelease = function(){
		var chargeClip_mc:MovieClip = _root["Q"+String(thisGUI.nbrChargesCreated)];
		//trace(chargeClip_mc + " released");
		chargeClip_mc.unloadMovie();
		this.onMouseMove = undefined;
	}
		
}//end of makeChargeBagMousey()

function makeSensorBagMousey(sensorBagClip:MovieClip){
	var thisGUI = this;
	var myModel = thisGUI.model;
	
	sensorBagClip.onPress = function(){
		thisGUI.nbrSensorsCreated++;
		var sensorClipNbr_string:String = "S"+String(thisGUI.nbrSensorsCreated);
		_root.attachMovie("sensor",sensorClipNbr_string,_root.getNextHighestDepth());
		var sensorClip_mc:MovieClip = _root[sensorClipNbr_string];
		sensorClip_mc.eFieldUnit_str = thisGUI.eFieldUnit_str;
		sensorClip_mc.angleUnit_str = thisGUI.angleUnit_str;
		thisGUI.positionClip(sensorClip_mc,_root._xmouse,_root._ymouse);
		sensorClip_mc.startDrag(false,0,0,thisGUI.stageW,thisGUI.stageH);
		//Following line of code does not work.  Why not?
		//sensorClip_mc.setLength(0);  //set E-field arror = 0 until released onto Stage
		this.onMouseMove = function(){
			//chargeClip_mc.chargeObject.setPosition(_root._xmouse,_root._ymouse);
			updateAfterEvent();
		}
		
	}
	//if released inside sensor bag, then delete sensor
	sensorBagClip.onRelease = function(){
		var sensorClip_mc:MovieClip = _root["S"+String(thisGUI.nbrSensorsCreated)];
		sensorClip_mc.unloadMovie();
		this.onMouseMove = undefined;
	}
	sensorBagClip.onReleaseOutside = function(){
		var sensorClip_mc = _root["S"+String(thisGUI.nbrSensorsCreated)];
		myModel.addObserver(sensorClip_mc.arrow_mc);
		sensorClip_mc.stopDrag(false,0,0,thisGUI.stageW,thisGUI.stageH);
		var E:Array = myModel.getE(sensorClip_mc._x,sensorClip_mc._y);
		sensorClip_mc.setLength(E[0]);
		sensorClip_mc.setAngle(E[1]);
		thisGUI.makeSensorDraggable(sensorClip_mc);
		this.onMouseMove = undefined;
	}
}//end of makeSensorBagMousey()

function positionClip(clip:MovieClip,x:Number,y:Number):Void{
	clip._x = x;
	clip._y = y;
}

//makes a charge movieClip instance draggable
	function makeChargeDraggable(myClip_mc:MovieClip):Void{
		var thisGUI = this;
		var myModel = this.model;
		myClip_mc.onPress = function(){
			this.startDrag(false,0,0,thisGUI.stageW,thisGUI.stageH);
			this.onMouseMove = function(){
				this.chargeObject.setPosition(this._x,this._y);
				//trace("x: "+this.chargeObject.x);
				updateAfterEvent();
			}
		}
		myClip_mc.onRelease = function(){
			this.chargeObject.setPosition(this._x,this._y);
			//trace("x:"+this.chargeObject.x);
			if(this.hitTest(_root.plusChargeBag_mc)||this.hitTest(_root.minusChargeBag_mc)){
				myModel.removeCharge(this.chargeObject);
				//trace("clip's ChargeObject's chargeValue: "+this.chargeObject.q);
				delete this.chargeObject;
				this.unloadMovie();
			}
			this.stopDrag();
			//trace("Clip stats::  chargeNbr: " + chargeNbr + "   x: " + this._x + "   y:  " + this._y);
			this.onMouseMove = undefined;
			//trace("hiResVoltage state ="+_root.controlPanel_mc.hiResVoltage_ch.checkState);
			if(_root.controlPanel_mc.hiResVoltage_ch.checkState){
				thisGUI.x0 = Math.floor(this._x/10);
				thisGUI.y0 = Math.floor(this._y/10);
				thisGUI.tileLast = 0;
				thisGUI.posLast = 0;
				thisGUI.startHiResVoltageDrawingConcentric();
			} 
			thisGUI.clearVoltageDrawing();
			//_root.voltageLineDrawing_mc.clear();
		}
		myClip_mc.onReleaseOutside = myClip_mc.onRelease;
	}//end of makeChargeDraggable()


	//makes a sensor(test charge) movieClip instance draggable
	function makeSensorDraggable(myClip_mc:MovieClip):Void{
		var thisGUI = this;
		var myModel = this.model;
		myClip_mc.onPress = function(){
			this.startDrag(false,0,0,thisGUI.stageW,thisGUI.stageH);
			var E:Array = myModel.getE(this._x,this._y);
			this.setLength(E[0]);
			this.setAngle(E[1]);
			this.onMouseMove = function(){
				//var V = myModel.getV(this._x,this._y);
				var E:Array = myModel.getE(this._x,this._y);
				this.setLength(E[0]);
				this.setAngle(E[1]);
				updateAfterEvent();
			}
		}
	
		myClip_mc.onRelease = function(){
			if(this.sensorBody_mc.hitTest(_root.sensorBag_mc)){
				myModel.removeObserver(this.arrow_mc);
				this.unloadMovie();
			}
			this.stopDrag();
			this.onMouseMove = undefined;
		}
		myClip_mc.onReleaseOutside = myClip_mc.onRelease;
	}//end of makeTestDraggable()

	//makes the Voltage Sensor movieClip instance draggable
	function makeVoltageSensorDraggable(myClip_mc:MovieClip):Void{
		var thisGUI = this;
		var myModel = this.model;
		myClip_mc.hitArea_mc.onPress = function(){
			this._parent.startDrag(false);
			this._parent.update(myModel);
			this.onMouseMove = function(){
				this._parent.update(myModel);
				//thisGUI.traceV();
				updateAfterEvent();
			}
		}
	
		myClip_mc.hitArea_mc.onRelease = function(){
			this._parent.update(myModel);
			this._parent.stopDrag();
			this.onMouseMove = undefined;
		}

		myClip_mc.hitArea_mc.onReleaseOutside = myClip_mc.hitArea_mc.onRelease;
	}//end of makeTestDraggable()
	
}//end of GUI class