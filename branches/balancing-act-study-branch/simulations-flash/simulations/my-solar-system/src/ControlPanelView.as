class ControlPanelView{
	private var model:Model;
	//private var controller:Object;
	private var stageW:Number;
	private var stageH:Number;
	private var controlPanel_mc:MovieClip;
	private var trajectoryView:TrajectoryView;
	private var initialView:InitialView;
	private var intID:Number; //intervalID
	
	function ControlPanelView(model:Model, trajectoryView:TrajectoryView, initialView:InitialView){
		this.model = model;
		this.trajectoryView = trajectoryView;
		this.initialView = initialView;
		//this.controller = controller;
		//this.model.registerView(this);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.initialize();
	}//end of constructor
	
	function initialize():Void{
		//_root.createEmptyMovieClip("panelHolder_mc",_root.getNextHighestDepth());
		_root.attachMovie("controlPanel", "controlPanel_mc",Util.getNextDepth());
		this.controlPanel_mc = _root.controlPanel_mc;
		var panelW:Number = this.controlPanel_mc._width;
		var panelH:Number = this.controlPanel_mc._height;
		
		Util.setXYPosition(this.controlPanel_mc, stageW - 1.15*panelW, stageH - 1.15*panelH);
		_root.attachMovie("infoPage","infoPage_mc",Util.getNextDepth());
		Util.setXYPosition(_root.infoPage_mc, this.stageW*0.5, this.stageH*0.5);
		_root.infoPage_mc._visible = false;
		var myView:ControlPanelView = this;
		
		this.controlPanel_mc.startButton_mc.onPress = function(){
			//trace("start pushed");
			//myView.model.startSuperIntegration();
			myView.model.startIntegration();
			myView.initialView.setTextAndLabelAlpha(60);
		}
		this.controlPanel_mc.stopButton_mc.onPress = function(){
			//trace("stop pushed");
			myView.model.stopIntegration();
		}
		this.controlPanel_mc.resetButton_mc.onPress = function(){
			//trace("reset pushed");
			myView.model.reset();
			myView.initialView.setTextAndLabelAlpha(100);
			//myView.trajectoryView.resetOrbits();
		}
		
		this.controlPanel_mc.helpButton_mc.onPress = function(){
			_root.infoPage_mc._visible = true;
		}
		//this.controlPanel_mc.aboutButton_mc.onReleaseOutside = this.controlPanel_mc.aboutButton_mc.onRelease;
		
		//this.controlPanel_mc.aboutButton_mc.onRelease = function(){
			//_root.infoPage_mc._visible = false;
		//}
		_root.infoPage_mc.backButton_mc.onPress = function(){
			_root.infoPage_mc._visible = false;
		}
		
		this.controlPanel_mc.trackCM_cb.model = this.model;
		this.controlPanel_mc.slider_mc.model = this.model;
		this.controlPanel_mc.showTraces_cb.trajectoryView = this.trajectoryView;
		this.controlPanel_mc.showGrid_cb.trajectoryView = this.trajectoryView;
		this.controlPanel_mc.tapeMeasure_cb.initialView = this.initialView;
		
		this.intID = setInterval(this,"waitForComponent", 10);
		
		//this.controlPanel_mc.moreSpeed_cb.trajectoryView = this.trajectoryView;
		//this.controlPanel_mc.trackCM_cb.toggle();  //this doesn't work: usual problem with movieClip initialization
	}//end of initialize()
		
	function waitForComponent(){
		//trace("waiting for component...");
		if(_root.controlPanel_mc.myComboBox.length != undefined){
			//trace("myComboBox length is " + _root.controlPanel_mc.myComboBox.length);
			//trace("Components loaded.");
			this.initializeComponents();
			clearInterval(this.intID);
		}
	}//end of waitForComponent
	
	function initializeComponents(){
		var myComboBoxListener:Object = new Object();
		var myModel = this.model;
		var thisControlPanel = this;
		myComboBoxListener.change = function(evt:Object){
			//trace("selected label is " + evt.target.selectedIndex);
			//myModel.setInitialBodies(evt.target.selectedIndex);
			var selectedNbr:Number = evt.target.selectedIndex;
			if(selectedNbr != 0){
				myModel.setInitialConfig(evt.target.selectedIndex);
				_root.radioGroup_mc.selectButton(myModel.getN());
			}
		}
		_root.controlPanel_mc.myComboBox.addEventListener("change", myComboBoxListener);
		//and the very last thing..
		var internationlizer = new Internationalizer(_root.simStrings);
		//force redraw of comboBox to show translated strings
		_root.controlPanel_mc.myComboBox.selectedIndex = 0;
	}//end of initializeComponents

	
	function update():Void{
		
	}
	
	}//end of class