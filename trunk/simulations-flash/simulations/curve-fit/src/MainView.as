
class MainView{
	private var model:Object;
	private var myGraphView:Object;
	private var panel:MovieClip; //controlPanel movieclip
	private var canvas:MovieClip; //blank canvas on which points are placed
	private var myChiDisplay:ChiDisplay;
	private var pointsBucket:MovieClip; //box of points 
	private var chiScale:MovieClip;	//chi-square display
	private var equationDisplay:MovieClip;
	private var nbrPointsCreated:Number; 
	private var fitDescription:String;  //"none" "best" or "adjustable"
	private var scale:Number; //scale factor: xGraph = scale*xPixels
	var stageH:Number; //height of stage in pixels
	var stageW:Number;
	var clearButton:ControlButton;
	var helpViewMaker:HelpViewInitializer;
	//var helpButton:ControlButton;
	
	function MainView(model:Object){
		//Stage.scaleMode = "showAll";
		//Stage.align = "";
		this.model = model;
		this.model.registerMainView(this);
		this.stageH = Util.STAGEH;
		this.stageW = Util.STAGEW;
		this.scale = 0.05; 
		this.myGraphView = new GraphView(this.model);
		this.panel = _root.attachMovie("controlPanel","controlPanel_mc", Util.getNextDepth());
		Util.setXYPosition(this.panel, 0.82*this.stageW, 0.08*this.stageH);
		//this.pointsBox = _root.attachMovie("dataPointSupplyBox", "pointsBox_mc", Util.getNextDepth());
		//Util.setXYPosition(this.pointsBox, 0.8*stageW, 0.05*stageH);
		this.pointsBucket = _root.attachMovie("dataPointBucket", "pointsBucket_mc", Util.getNextDepth());
		Util.setXYPosition(this.pointsBucket, 0.7*stageW, 0.05*stageH);
		this.pointsBucket.hitAreaPoints_mc._alpha = 0;
		this.pointsBucket.hitAreaBorder_mc._alpha = 0;
		_root.attachMovie("helpDataPoint", "helpDataPoint_mc", Util.getNextDepth());
		Util.setXYPosition(_root.helpDataPoint_mc, 0.6*stageW, 0.4*stageH);
		this.equationDisplay = _root.attachMovie("equation","equation_mc", Util.getNextDepth());
		Util.setXYPosition(this.equationDisplay, Util.ORIGINX + 0.06*stageW, 0.9*stageH);
		this.myChiDisplay = new ChiDisplay(this.model, _root);
		//this.chiScale = _root.attachMovie("chiScale","chiScale_mc",Util.getNextDepth());
		//Util.setXYPosition(this.chiScale, -0.05*stageW, 0.05*stageH);
		this.canvas = _root.createEmptyMovieClip("canvas_mc", Util.getNextDepth());
		this.nbrPointsCreated = 0;
		this.initializeDataPointSupplyBox();
		this.initializeControlPanel();
		//}
		
	}//end of constructor
	
	function initializeControlPanel():Void{
		this.makeDisplaysDraggable();
		//this.startButton = new ControlButton(this.panel.startButton_mc, "Start", this.model, this.startButtonAction);
		this.clearButton = new ControlButton(this.panel.clearButton_mc, "Clear All", this.model, this.clearButtonAction);
		this.helpViewMaker = new HelpViewInitializer(this.panel.helpButton_mc, this);
		this.helpViewMaker.showHelp(false);
		//this.helpButton = new ControlButton(this.panel.helpButton_mc, "Help", this.model, undefined);
		this.panel.fitTypeRadioGroup.controlPanelView = this;
		this.panel.fitOrNotRadioGroup.controlPanelView = this;
		this.setFitOrNot(1);
		this.setOrderOfFit(1);
		var aSlider:MovieClip = this.panel.sliders_mc.aSlider;
		var bSlider:MovieClip = this.panel.sliders_mc.bSlider;
		var cSlider:MovieClip = this.panel.sliders_mc.cSlider;
		var dSlider:MovieClip = this.panel.sliders_mc.dSlider;
		var eSlider:MovieClip = this.panel.sliders_mc.eSlider;
		aSlider.label_txt.text = "a";
		bSlider.label_txt.text = "b";
		cSlider.label_txt.text = "c";
		dSlider.label_txt.text = "d";
		eSlider.label_txt.text = "e";
		//arrange slider labels for visibility
		//bSlider.value_txt._y += 1.2*bSlider.value_txt._height;
		//dSlider.value_txt._y += 1.2*dSlider.value_txt._height;
		//cSlider.value_txt._width *= 1.2;
		dSlider.value_txt._width *= 1.1;
		eSlider.value_txt._width *= 1.1;
		aSlider.max = 0.9*Math.round(this.scale*(Util.ORIGINY)); 
		aSlider.min = -0.9*Math.round(this.scale*(this.stageH - Util.ORIGINY));
		aSlider.value_txt.text = "0";
		var bExtreme = 0.95*Math.PI/2;
		bSlider.max = bExtreme; bSlider.min = -bExtreme;
		var cExtreme = 0.90*Math.PI/2;// angle in radians for 
		cSlider.max = cExtreme; cSlider.min = -cExtreme;
		var dExtreme = 0.80*Math.PI/2;
		dSlider.max = dExtreme; dSlider.min = -dExtreme;
		var eExtreme = 0.80*Math.PI/2;
		eSlider.max = eExtreme; eSlider.min = -eExtreme;
		var modelRef = this.model;
		var scaleRef = this.scale;
		var stageWRef = this.stageW;
		aSlider.update = function(){
			var max = this.max;
			var min = this.min;
			var value = (1/10)*Math.round(10*((max+min)/2 - (max - min)*(this.puck_mc._y/200)));
			//this.value_txt.text = value; 
			modelRef.setA(value);
			//trace("update called. value is "+ value);
			//updateAfterEvent();
		}
		/*
		aSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var max = this._parent.max;
			var min = this._parent.min;
			var mid = (max + min)/2;
			if(!isNaN(value) && value <= max && value >= min){
				this._parent.puck_mc._y = (mid - value)*100/(max - min);
				modelRef.setA(value);
			}else if(!isNaN(value) && value > max){
				value = max;
				modelRef.setA(value);
				this._parent.value_txt.text = max;
				this._parent.puck_mc._y = (mid - value)*100/(max - min);
			}
		}//end of onChanged()
		*/
		
		bSlider.update = function(){
			var max = this.max;
			var min = this.min;
			var theta = (1/100)*Math.round(100*((max+min)/2 - (max - min)*(this.puck_mc._y/200)));
			var bParam = Math.tan(theta);
			//this.value_txt.text = 0.01*Math.round(100*bParam); 
			modelRef.setB(bParam);
			//updateAfterEvent();
		}
		/*
		bSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var sliderPos = Math.atan(value);
			var max = this._parent.max;
			var min = this._parent.min;
			var mid = (max + min)/2;
			if(!isNaN(sliderPos) && sliderPos <= max && sliderPos >= min){
				this._parent.puck_mc._y = (mid - sliderPos)*100/(max - min);
				modelRef.setB(sliderPos);
			}else if(!isNaN(sliderPos) && sliderPos > max){
				sliderPos = max;
				value = Math.tan(sliderPos);
				modelRef.setB(value);
				this._parent.value_txt.text = 0.01*Math.round(100*value);
				this._parent.puck_mc._y = (mid - sliderPos)*100/(max - min);
			}
		}//end of onChanged()
		*/
		
		cSlider.update = function(){
			var max = this.max;
			var min = this.min;
			//ensure uniform motion of parabola edge as c-slider is moved
			var radius = scaleRef*stageWRef/2;
			var theta = (max+min)/2 - (max - min)*(this.puck_mc._y/200);
			var cosTheta = Math.cos(theta);
			var value =Math.sin(theta)/(radius*cosTheta*cosTheta);
			//this.value_txt.text = 0.001*Math.round(1000*value); 
			modelRef.setC(value);
			//updateAfterEvent();
		}
		/*
		cSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var max = this._parent.max;
			var min = this._parent.min;
			var mid = (max + min)/2;
			if(!isNaN(value) && value <= max && value >= min){
				this._parent.puck_mc._y = (mid - value)*100/(max - min);
				modelRef.setC(value);
			}else if(!isNaN(value) && value > max){
				value = max;
				modelRef.setC(value);
				this._parent.value_txt.text = 0.001*Math.round(1000*value);
				this._parent.puck_mc._y = (mid - value)*100/(max - min);
			}
		}//end of onChanged()
		*/
		
		dSlider.update = function(){
			var max = this.max;
			var min = this.min;
			//ensure uniform motion of parabola edge as c-slider is moved
			var radius = scaleRef*stageWRef/2;
			var theta = (max+min)/2 - (max - min)*(this.puck_mc._y/200);
			var cosTheta = Math.cos(theta);
			var value =Math.sin(theta)/(Math.pow(radius*cosTheta, 2)*cosTheta);
			//this.value_txt.text = Util.expNotation(value);//0.00001*Math.round(100000*value); 
			modelRef.setD(value);
			//updateAfterEvent();
		}
		/*
		dSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var max = this._parent.max;
			var min = this._parent.min;
			var mid = (max + min)/2;
			if(!isNaN(value) && value <= max && value >= min){
				this._parent.puck_mc._y = (mid - value)*100/(max - min);
				modelRef.setD(value);
			}else if(!isNaN(value) && value > max){
				value = max;
				modelRef.setD(value);
				this._parent.value_txt.text = 0.0001*Math.round(1000*value);
				this._parent.puck_mc._y = (mid - value)*1000/(max - min);
			}
		}//end of onChanged()
		*/
		
		eSlider.update = function(){
			var max = this.max;
			var min = this.min;
			//ensure uniform motion of parabola edge as c-slider is moved
			var radius = scaleRef*stageWRef/2;
			var theta = (max+min)/2 - (max - min)*(this.puck_mc._y/200);
			var cosTheta = Math.cos(theta);
			var value =Math.sin(theta)/(Math.pow(radius*cosTheta, 3)*cosTheta);
			//this.value_txt.text = Util.expNotation(value);//0.000001*Math.round(1000000*value); 
			modelRef.setE(value);
			//updateAfterEvent();
		}
		/*
		eSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var max = this._parent.max;
			var min = this._parent.min;
			var mid = (max + min)/2;
			if(!isNaN(value) && value <= max && value >= min){
				this._parent.puck_mc._y = (mid - value)*100/(max - min);
				modelRef.setD(value);
			}else if(!isNaN(value) && value > max){
				value = max;
				modelRef.setE(value);
				this._parent.value_txt.text = 0.00001*Math.round(10000*value);
				this._parent.puck_mc._y = (mid - value)*1000/(max - min);
			}
		}//end of onChanged()
		*/
	}//end of initializeControls()
	
	
	function startButtonAction():Void{
		//this.model.makeLinearFit();
	}
	
	function clearButtonAction():Void{
		this.model.deleteAllPoints();
	}
	
	function setOrderOfFit(fitType:Number):Void{
		//trace("fit type is "+fitType);
		this.model.setOrderOfFit(fitType);
		if(fitType == 0){
			this.equationDisplay._visible = false;
		}else{
			this.equationDisplay._visible = true;
		}
		switch(fitType){
			case 0:
				//no fit
				this.panel.sliders_mc._visible = false;
				break;
			case 1:
				if(fitDescription == "adjustable"){this.panel.sliders_mc._visible = true;}
				this.panel.sliders_mc.aSlider._visible = true;
				this.panel.sliders_mc.bSlider._visible = true;
				this.panel.sliders_mc.cSlider._visible = false;
				this.panel.sliders_mc.dSlider._visible = false;
				this.panel.sliders_mc.eSlider._visible = false;
				this.setSlidersBackground(2);
				this.equationDisplay.equationDisplayTerms_mc.gotoAndStop(1);
				this.equationDisplay.aEquals_txt._visible = true;
				this.equationDisplay.aDisplay_txt._visible = true;
				this.equationDisplay.bEquals_txt._visible = true;
				this.equationDisplay.bDisplay_txt._visible = true;
				this.equationDisplay.cEquals_txt._visible = false;
				this.equationDisplay.cDisplay_txt._visible = false;
				this.equationDisplay.dEquals_txt._visible = false;
				this.equationDisplay.dDisplay_txt._visible = false;
				this.equationDisplay.eEquals_txt._visible = false;
				this.equationDisplay.eDisplay_txt._visible = false;
				break;
			case 2:
				if(fitDescription == "adjustable"){this.panel.sliders_mc._visible = true;}
				this.panel.sliders_mc.aSlider._visible = true;
				this.panel.sliders_mc.bSlider._visible = true;
				this.panel.sliders_mc.cSlider._visible = true;
				this.panel.sliders_mc.dSlider._visible = false;
				this.panel.sliders_mc.eSlider._visible = false;
				this.setSlidersBackground(3);
				this.equationDisplay.equationDisplayTerms_mc.gotoAndStop(2);
				this.equationDisplay.aEquals_txt._visible = true;
				this.equationDisplay.aDisplay_txt._visible = true;
				this.equationDisplay.bEquals_txt._visible = true;
				this.equationDisplay.bDisplay_txt._visible = true;
				this.equationDisplay.cEquals_txt._visible = true;
				this.equationDisplay.cDisplay_txt._visible = true;
				this.equationDisplay.dEquals_txt._visible = false;
				this.equationDisplay.dDisplay_txt._visible = false;
				this.equationDisplay.eEquals_txt._visible = false;
				this.equationDisplay.eDisplay_txt._visible = false;
				break;
			case 3:
				if(fitDescription == "adjustable"){this.panel.sliders_mc._visible = true;}
				this.panel.sliders_mc.aSlider._visible = true;
				this.panel.sliders_mc.bSlider._visible = true;
				this.panel.sliders_mc.cSlider._visible = true;
				this.panel.sliders_mc.dSlider._visible = true;
				this.panel.sliders_mc.eSlider._visible = false;
				this.setSlidersBackground(4);
				this.equationDisplay.equationDisplayTerms_mc.gotoAndStop(3);
				this.equationDisplay.aEquals_txt._visible = true;
				this.equationDisplay.aDisplay_txt._visible = true;
				this.equationDisplay.bEquals_txt._visible = true;
				this.equationDisplay.bDisplay_txt._visible = true;
				this.equationDisplay.cEquals_txt._visible = true;
				this.equationDisplay.cDisplay_txt._visible = true;
				this.equationDisplay.dEquals_txt._visible = true;
				this.equationDisplay.dDisplay_txt._visible = true;
				this.equationDisplay.eEquals_txt._visible = false;
				this.equationDisplay.eDisplay_txt._visible = false;
				break;
			case 4:
				if(fitDescription == "adjustable"){this.panel.sliders_mc._visible = true;}
				this.panel.sliders_mc.aSlider._visible = true;
				this.panel.sliders_mc.bSlider._visible = true;
				this.panel.sliders_mc.cSlider._visible = true;
				this.panel.sliders_mc.dSlider._visible = true;
				this.panel.sliders_mc.eSlider._visible = true;
				this.setSlidersBackground(5);
				this.equationDisplay.equationDisplayTerms_mc.gotoAndStop(4);
				this.equationDisplay.aEquals_txt._visible = true;
				this.equationDisplay.aDisplay_txt._visible = true;
				this.equationDisplay.bEquals_txt._visible = true;
				this.equationDisplay.bDisplay_txt._visible = true;
				this.equationDisplay.cEquals_txt._visible = true;
				this.equationDisplay.cDisplay_txt._visible = true;
				this.equationDisplay.dEquals_txt._visible = true;
				this.equationDisplay.dDisplay_txt._visible = true;
				this.equationDisplay.eEquals_txt._visible = true;
				this.equationDisplay.eDisplay_txt._visible = true;
				break;
		}//end of switch
	}//end of setOrderOfFit()
	
	function setSlidersBackground(nbrOfSliders:Number):Void{
		var n:Number = nbrOfSliders;
		var delWidth = 24;
		this.panel.sliders_mc.background_mc.center_mc._width = n*delWidth;
		this.panel.sliders_mc.background_mc.rightEdge_mc._x = 10 + n*delWidth;
		this.panel.sliders_mc.hitBorder_mc._width = this.panel.sliders_mc.background_mc._width + n; //20 + n*delWidth;
		//trace("this.panel.sliders_mc.background_mc._width: " + this.panel.sliders_mc.background_mc._width);
		//trace("hitAreaWidth"+this.panel.sliders_mc.background_mc.hitBorder_mc._width);
	}
	
	function setFitOrNot(fitOrNot:Number):Void{
		if(fitOrNot == 1){  //fitOrNot == 1 means best fit
			this.fitDescription = "best";
			this.equationDisplay.bestFit_txt._visible = true;
			this.equationDisplay.adjustableFit_txt._visible = false;
			this.panel.sliders_mc._visible = false;
			//this.panel.sliders_mc.aSlider._visible = false;
			//this.panel.sliders_mc.bSlider._visible = false;
			//this.panel.sliders_mc.cSlider._visible = false;
			this.model.setFitOn(true);
			this.model.makeFit();
		}else if(fitOrNot == 2){	//else adjustable fit
			this.fitDescription = "adjustable";
			this.panel.sliders_mc._visible = true;
			this.equationDisplay.bestFit_txt._visible = false;
			this.equationDisplay.adjustableFit_txt._visible = true;
			this.panel.sliders_mc.aSlider.update();
			this.panel.sliders_mc.bSlider.update();
			this.panel.sliders_mc.cSlider.update();
			this.panel.sliders_mc.dSlider.update();
			this.panel.sliders_mc.eSlider.update();
			this.model.setFitOn(false);
			/*
			if(this.model.orderOfFit == 1){
				this.panel.sliders_mc.cSlider._visible = false;
				//this.panel.sliders_mc.cSlider.init(); //set c = 0
				//this.panel.sliders_mc.cSlider.update();
			}else if(this.model.orderOfFit == 2){
				this.panel.sliders_mc.cSlider._visible = true;
				this.panel.sliders_mc.cSlider.update();
			}
			*/
		}
	}//end of setFitOrNot()
	
	function displayFit():Void{
		var a:Number = this.model.fitParameters[0];
		var b:Number = this.model.fitParameters[1];
		var c:Number = this.model.fitParameters[2];
		var d:Number = this.model.fitParameters[3];
		var e:Number = this.model.fitParameters[4];
		this.equationDisplay.aDisplay_txt.text = Math.round(10*a)/10;
		this.equationDisplay.bDisplay_txt.text = Math.round(100*b)/100;
		this.equationDisplay.cDisplay_txt.text = Math.round(1000*c)/1000;
		this.equationDisplay.dDisplay_txt.text = Util.expNotation(d);//Math.round(10000*d)/10000;
		this.equationDisplay.eDisplay_txt.text = Util.expNotation(e);//Math.round(100000*e)/100000;
		//if(isNaN(a)){this.equationDisplay.aDisplay_txt.text = "--";}
		//if(isNaN(b)){this.equationDisplay.bDisplay_txt.text = "--";}
	}
	
	function updateChiScaleDisplay():Void{
		this.myChiDisplay.update();
	}
	
	
	function makeDisplaysDraggable():Void{
		var border:MovieClip = this.panel.background_mc.border_mc;
		var sliderBorder:MovieClip = this.panel.sliders_mc.hitBorder_mc;
		sliderBorder._alpha = 0;
		var dataBucketBorder:MovieClip = this.pointsBucket.hitAreaBorder_mc;
		var panelRef = this.panel;
		var slidersRef = this.panel.sliders_mc;
		var dataBucketRef = this.pointsBucket;
		var xMin = -0.3*stageW; 
		var yMin = -0.3*stageH;
		var xMax = 1.3*stageW;
		var yMax = 1.3*stageH;
		border.onPress = function(){
			panelRef.startDrag(false, xMin, yMin, xMax, yMax); //left,top(yMin),right,bottom(yMax))
			this.onMouseMove = function(){
				updateAfterEvent();
			}//end of onMouseMove()
		}//end of onPress()
		border.onRelease = function(){
			panelRef.stopDrag();
			this.onMouseMove = undefined;
		}
		border.onReleaseOutside = border.onRelease;
		
		sliderBorder.onPress = function(){
			slidersRef.startDrag(false, xMin, yMin, xMax, yMax); //left,top(yMin),right,bottom(yMax))
			this.onMouseMove = function(){
				updateAfterEvent();
			}//end of onMouseMove()
		}//end of onPress()
		sliderBorder.onRelease = function(){
			slidersRef.stopDrag();
			this.onMouseMove = undefined;
		}
		sliderBorder.onReleaseOutside = sliderBorder.onRelease;
		
		dataBucketBorder.onPress = function(){
			dataBucketRef.startDrag(false, xMin, yMin, xMax, yMax); //left,top(yMin),right,bottom(yMax))
			this.onMouseMove = function(){
				updateAfterEvent();
			}//end of onMouseMove()
		}//end of onPress()
		dataBucketBorder.onRelease = function(){
			dataBucketRef.stopDrag();
			this.onMouseMove = undefined;
		}
		dataBucketBorder.onReleaseOutside = dataBucketBorder.onRelease;
	}
	
	function initializeDataPointSupplyBox():Void{
		var thisView = this;
		var localCanvas_mc = this.canvas;
		var scaleFactor = this.scale;
		this.pointsBucket.hitAreaPoints_mc.onPress = function(){
			thisView.nbrPointsCreated++;
			var dataPointName:String = "dataPointHolder"+String(thisView.nbrPointsCreated);
			var pointClip:MovieClip = localCanvas_mc.attachMovie("dataPointHolder",dataPointName,localCanvas_mc.getNextHighestDepth());
			Util.setXYPosition(pointClip,_root._xmouse, _root._ymouse);
			localCanvas_mc[dataPointName].startDrag(false, 0, 0, thisView.stageW, thisView.stageH);
			pointClip.displayDelY_mc._visible = false;
			this.onMouseMove = function(){
				var xNow = scaleFactor*(pointClip._x - Util.ORIGINX);
				var yNow = scaleFactor*(-pointClip._y + Util.ORIGINY);
				pointClip.display_mc.xDisplay_txt.text = 0.1*Math.round(10*xNow);
				pointClip.display_mc.yDisplay_txt.text = 0.1*Math.round(10*yNow);
				updateAfterEvent();
			}
		}
		this.pointsBucket.hitAreaPoints_mc.onReleaseOutside = function(){
			var dataPointName:String = "dataPointHolder"+String(thisView.nbrPointsCreated);
			//create Point instance and attach to clip
			var pointClip:MovieClip = localCanvas_mc[dataPointName];
			var currentPoint:Point;
			currentPoint = thisView.model.addPoint(scaleFactor*(_root._xmouse - Util.ORIGINX), scaleFactor*(-_root._ymouse + Util.ORIGINY), pointClip);
			//pointClip.pointObject = thisView.model.addPoint(_root._xmouse - Util.ORIGINX, -(_root._ymouse - Util.ORIGINY), pointClip);
			pointClip.stopDrag();
			pointClip.display_mc._visible = false;
			this.onMouseMove = undefined;
			//thisView.makePointDraggable(currentPoint); //localCanvas_mc[dataPointName]);
			this.model.makeFit();
		}
		this.pointsBucket.hitAreaPoints_mc.onRelease = function(){
			var dataPointName:String = "dataPointHolder"+String(thisView.nbrPointsCreated);
			localCanvas_mc[dataPointName].unloadMovie();
			this.onMouseMove = undefined;
		}
	}//end of initializeControls
	

}//end of class MainView