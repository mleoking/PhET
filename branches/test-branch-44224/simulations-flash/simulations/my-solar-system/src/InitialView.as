class InitialView{
	private var model:Model;
	private var stageW:Number;
	private var stageH:Number;
	private var bodyClips:Array;
	private var maxNbrBodies:Number;
	private var myTextFormat:TextFormat;
	private var myFieldListener:Object;
	private var updateNeeded:Boolean;
	
	function InitialView(model:Model){
		this.model = model;
		this.model.registerView(this);
		this.model.registerInitialView(this);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.maxNbrBodies = 4;
		this.myFieldListener = new Object();
		this.myFieldListener.model = model;
		this.myFieldListener.onChanged = textChanged;
		this.setTextFormat();
		this.initialize();
		//this.test();
	}//end of constructor
	
	function initialize():Void{
		//trace("initialize called. Nbr bodies = " + this.model.getN());
		this.updateNeeded = true;
		_root.attachMovie("phetLogo","phetLogo_mc", Util.getNextDepth());
		//_root.phetLogo_mc._xscale = _root.phetLogo_mc._yscale = 25;
		var logoW:Number = _root.phetLogo_mc._width;
		var logoH:Number = _root.phetLogo_mc._height;
		Util.setXYPosition(_root.phetLogo_mc, this.stageW - 1.0*logoW , this.stageH - 0.5*logoH);	
		
		_root.createEmptyMovieClip("bodyHolder_mc",Util.getNextDepth());
		_root.createEmptyMovieClip("textHolder_mc",Util.getNextDepth());
		_root.createEmptyMovieClip("labelHolder_mc",Util.getNextDepth());
		_root.createEmptyMovieClip("timeHolder_mc",Util.getNextDepth());
		_root.attachMovie("tapeMeasureHolder","tapeMeasureHolder_mc", Util.getNextDepth());
		Util.setXYPosition(_root.tapeMeasureHolder_mc, this.stageW*0.2, this.stageH*0.5);
		_root.tapeMeasureHolder_mc._visible = false;
				
		var bodyHolder:MovieClip = _root.bodyHolder_mc;
		var maxN = this.maxNbrBodies;
		var N = this.model.getN();
		this.initializeBodies();
		
		//create textfields and their labels
		var tWidth:Number = 35;		//width of text box
		var lWidth:Number = 50;		//length of text box
		var tHeight:Number = 18;
		var leftMarginT:Number = 0.3*stageW; //T for text
		var leftMarginL:Number = 0.3*stageW;  //L for label
		var spacing:Number = tWidth + 5;
		var vertSpacing:Number = 1.25*tHeight;
		var topMargin:Number = this.stageH - 5*vertSpacing;//0.78*this.stageH;
		_root.attachMovie("numBodiesRadioGroup", "radioGroup_mc", Util.getNextDepth());
		Util.setXYPosition(_root.radioGroup_mc, leftMarginL - 1.7*spacing, topMargin + 2.35*vertSpacing);
		_root.radioGroup_mc.model = this.model;
		for(var j:Number = 1; j <= maxN; j++){
			with(_root.textHolder_mc){
				createTextField("mass"+j, 10*j, leftMarginT, topMargin + j*vertSpacing, tWidth, tHeight); //(name, depth, x, y, width, height)
				createTextField("x"+j, 10*j+1, leftMarginT + spacing, topMargin + j*vertSpacing, tWidth, tHeight);
		 		createTextField("y"+j, 10*j+2, leftMarginT + 2*spacing, topMargin + j*vertSpacing, tWidth, tHeight);
		 		createTextField("vx"+j, 10*j+3, leftMarginT + 3*spacing, topMargin + j*vertSpacing, tWidth, tHeight);
		 		createTextField("vy"+j, 10*j+4, leftMarginT + 4*spacing, topMargin + j*vertSpacing, tWidth, tHeight);
			}//end of with()
			with(_root.labelHolder_mc){
				createTextField("body "+j, 100*j, leftMarginL - 1.20*spacing, topMargin + j*vertSpacing, lWidth, tHeight); //(name, depth, x, y, width, height)
				//createTextField("x"+j, 100*j+1, leftMarginL +  spacing, topMargin + j*vertSpacing, tWidth, tHeight);
		 		//createTextField("y"+j, 100*j+2, leftMarginL + 2*spacing, topMargin + j*vertSpacing, tWidth, tHeight);
		 		//createTextField("vx"+j, 100*j+3, leftMarginL + 3*spacing, topMargin + j*vertSpacing, tWidth, tHeight);
		 		//createTextField("vy"+j, 100*j+4, leftMarginL + 4*spacing, topMargin + j*vertSpacing, tWidth, tHeight);
			}//end of with()
			
		}//end of for(var j = 1,
		
		with(_root.labelHolder_mc){
			createTextField("position", 100*maxN+1, leftMarginL +  1.2*spacing, topMargin - 0.35*vertSpacing, 1.4*lWidth, tHeight);
			createTextField("velocity", 100*maxN+2, leftMarginL +  3.2*spacing, topMargin - 0.35*vertSpacing, 1.4*lWidth, tHeight);
			createTextField("mass", 100*maxN+3, leftMarginL +  0.0*spacing, topMargin + 0.3*vertSpacing, tWidth, tHeight);
			createTextField("x", 100*maxN+4, leftMarginL +  1.0*spacing, topMargin + 0.3*vertSpacing, tWidth, tHeight);
			createTextField("y", 100*maxN+5, leftMarginL +  2.0*spacing, topMargin + 0.3*vertSpacing, tWidth, tHeight);
			createTextField("x ", 100*maxN+6, leftMarginL +  3.0*spacing, topMargin + 0.3*vertSpacing, tWidth, tHeight);
			createTextField("y ", 100*maxN+7, leftMarginL +  4.0*spacing, topMargin + 0.3*vertSpacing, tWidth, tHeight);
			createTextField("Initial Settings:", 100*maxN+8, leftMarginL - 3.5*spacing, topMargin - 0.3*vertSpacing, 3*lWidth, 1.5*tHeight);
			
		}
		
		_root.timeHolder_mc.createTextField("timeEquals", 10, leftMarginL + 5.6*spacing, topMargin + 1.0*vertSpacing, tWidth+1.0*spacing, tHeight+5);
		_root.timeHolder_mc.createTextField("elapsedTime", 20, leftMarginL + 7.5*spacing, topMargin + 1.0*vertSpacing, tWidth+5, tHeight+5);
				
		for(var txtField in _root.textHolder_mc){
			//txtField is a string
			this.formatField(_root.textHolder_mc[txtField]);
		}
		for(var txtField in _root.labelHolder_mc){
			//txtField is a string
			this.formatLabel(_root.labelHolder_mc[txtField]);
		}
		
		var boldFormat:TextFormat = new TextFormat("Arial", 15, 0xFFFF00);
		boldFormat.bold = true;
		_root.labelHolder_mc["Initial Settings:"].setTextFormat(boldFormat);
		
		this.formatTime();
		
		this.resetAllFields();
		
	}//end of initialize()
	
	function initializeBodies():Void{
		for(var clipName:String in _root.bodyHolder_mc){
			_root.bodyHolder_mc[clipName].removeMovieClip();
		}
		for(var i:Number = 0; i < this.bodyClips.length; i++){
			delete this.bodyClips[i];
		}
		
		var N = this.model.getN();
		this.bodyClips = new Array(N);
		for(var j:Number = 1; j <= N; j++){
			var i:Number = j - 1;
			//BodyClip(nLabel:Number, name:String, target:MovieClip, model:Model)
			this.bodyClips[i] = new BodyClip(j, "body"+i, _root.bodyHolder_mc, model);
			var bodyColor:Color = new Color(this.bodyClips[i].clip_mc.disk_mc.diskBody_mc.diskBodySolid_mc);
			bodyColor.setRGB(Util.BODYCOLORS[i]);
			this.bodyClips[i].update();
		}
	}//end of initializeBodies
	
	function resetAllFields():Void{
		var currentN:Number = this.model.getN()
		for(var j:Number = 1; j <= currentN; j++){
			var i:Number = j-1;
			_root.textHolder_mc["mass"+j].text = String(this.model.getMassOfBodyI(i));
			var initPos:Vector = this.model.getInitPosOfBodyI(i);
			var initVel:Vector = this.model.getInitVelOfBodyI(i);
			_root.textHolder_mc["x"+j].text = String(initPos.x);//replaceSel(String(initPos.x));//
			_root.textHolder_mc["y"+j].text = String(initPos.y);//replaceSel(String(initPos.y));//
			_root.textHolder_mc["vx"+j].text = String(initVel.x);//replaceSel(String(initVel.x));//
			_root.textHolder_mc["vy"+j].text = String(initVel.y);//replaceSel(String(initVel.y));//
		}//end of for loop
		
		for(var fieldName:String in _root.textHolder_mc){
			var field:TextField = _root.textHolder_mc[fieldName];
			var bodyIndex:String = field._name.charAt(field._name.length - 1);
			if(bodyIndex <= currentN){
				field._visible = true;
			}else{
				field._visible = false;
			}
		}//end of for loop
		
		for(var j:Number = j; j <= 4; j++){
			if(j <= this.model.getN()){
				_root.labelHolder_mc["body "+j]._visible = true;
			}else{
				_root.labelHolder_mc["body "+j]._visible = false;
			}
		}
		
		for(var fieldName:String in _root.labelHolder_mc){
			var field:TextField = _root.labelHolder_mc[fieldName];
			var bodyIndex:String = field._name.charAt(field._name.length - 1);
			if(bodyIndex <= currentN){
				field._visible = true;
			}else{
				field._visible = false;
			}
		}//end of for loop
		
		_root.timeHolder_mc.timeEquals.text = "time =";
		_root.timeHolder_mc.elapsedTime.text = "0";

	}//end of resetAllFields()
	
	function setTextFormat():Void{
		this.myTextFormat = new TextFormat("Arial", 12, 0x000000);
		myTextFormat.align = "right";
	}
	
	function setTextFieldType(currentType:String):Void{
		//currentType is "input" or "dynamic"
		for(var fieldName:String in _root.textHolder_mc){
			var field:TextField = _root.textHolder_mc[fieldName];
			field.type = currentType;
			if(currentType == "input"){
				field.selectable = true;
			}else{
				field.selectable = false;
			}
		}//end of for loop
	}//end of setTextFieldType()
	
	function setTextAndLabelAlpha(alpha:Number){
		//trace("setTextAndLabelAlpha called  alpha = " + alpha);
		for(var fieldName:String in _root.textHolder_mc){
			var field:TextField = _root.textHolder_mc[fieldName];
			field._alpha = alpha;
		}
		for(var fieldName:String in _root.labelHolder_mc){
			var field:TextField = _root.labelHolder_mc[fieldName];
			field._alpha = alpha;
		}
	}
	
	function formatField(field:TextField){
		field.setNewTextFormat(this.myTextFormat);
		var bodyIndex:String = field._name.charAt(field._name.length - 1); //1 thru 9 only
		var firstLetter:String = field._name.charAt(0);
		field.border = true;
		field.embedFonts = true;  //needed in order to set alpha value of textfield
		field.type = "input";
		//field.restrict = "0-9.\\-";
		if(firstLetter == "m"){
			field.restrict = "0-9.";  //do not allow negative masses
		}else{
			field.restrict = "0-9.\\-";
		}
		field.background = true;
		field.addListener(this.myFieldListener);
		if(bodyIndex == "1"){field.backgroundColor = Util.TRACECOLORS[0];} 
		if(bodyIndex == "2"){field.backgroundColor = Util.TRACECOLORS[1];} 
		if(bodyIndex == "3"){field.backgroundColor = Util.TRACECOLORS[2];} 
		if(bodyIndex == "4"){field.backgroundColor = Util.TRACECOLORS[3];} 
	}
	
	function formatLabel(field:TextField){
		var labelTextFormat:TextFormat = new TextFormat("Arial", 12, 0xCCCCCC);
		labelTextFormat.align = "center";
		field.setNewTextFormat(labelTextFormat);
		//field.border = true;
		field.text = field._name;
		//field.embedFonts = true;  //needed in order to set alpha value of textfield
		field.selectable = false;
	}
	
	
	function formatTime(){
		var timeLabelFormat:TextFormat = new TextFormat("Arial", 13, 0xCCCCCC);
		timeLabelFormat.align = "right";
		timeLabelFormat.bold = true;
		var timeLabel = _root.timeHolder_mc.timeEquals;
		timeLabel.setNewTextFormat(timeLabelFormat);
		timeLabelFormat.align = "right";
		timeLabel.text = timeLabel._name;
		//timeLabel.border = true;
		timeLabel.selectable = false;
		var timeFormat:TextFormat = new TextFormat("Arial", 14, 0xCCCCCC);
		timeFormat.align = "left";
		timeFormat.bold = true;
		var time_txt = _root.timeHolder_mc.elapsedTime;
		time_txt.setNewTextFormat(timeFormat);
		time_txt.selectable = false;
	}
	
	function setElapsedTime(time:Number):Void{
		var timeInTenths:Number = Math.round(time*10)/10;
		if(Math.round(time) != timeInTenths){
			_root.timeHolder_mc.elapsedTime.text = String(timeInTenths);
		}else{
			_root.timeHolder_mc.elapsedTime.text = String(timeInTenths) + ".0";
		}
	}//end of setElapsedTime
	
	function updateTextFields():Void{
		var textHolder:MovieClip = _root.textHolder_mc;
		var textFieldString:String;
		//var myTextFormat:TextFormat = new TextFormat("Arial", 12, 0x000000);
		for(var j:Number = 1; j <= this.model.getN(); j++){
			var pos:Vector = this.model.getInitPosOfBodyI(j - 1);
			var vel:Vector = this.model.getInitVelOfBodyI(j - 1);
			textHolder["x"+j].text = Math.round(pos.x); // replaceSel(String(pos.x));  // 
			textHolder["y"+j].text = Math.round(pos.y); //replaceSel(String(pos.y));  //
			textHolder["vx"+j].text = Math.round(vel.x); //replaceSel(String(vel.x)); //
			textHolder["vy"+j].text = Math.round(vel.y); //replaceSel(String(vel.y)); //
		}
	}
	
	function textChanged(tfObject:TextField):Void{
		//trace("fieldListener called. " + tfObject._name);
		//trace("textChanged");
		var name:String = tfObject._name;
		var j:Number = Number(name.charAt(name.length -1));  //number of body: 1 or 2 or ...
		if(tfObject.text != "-" && tfObject.text != ""){
			var type:String = name.charAt(0);		//m if mass, x if position, v if velocity
			//trace("type:"+type+"   body number:"+j)
			var mass:Number = Number(_root.textHolder_mc["mass"+j].text);
			var xInit:Number = Number(_root.textHolder_mc["x"+j].text);
			var yInit:Number = Number(_root.textHolder_mc["y"+j].text);
			var vXInit:Number = Number(_root.textHolder_mc["vx"+j].text);
			var vYInit:Number = Number(_root.textHolder_mc["vy"+j].text);
			var posInit:Vector = new Vector(xInit, yInit);
			var velInit:Vector = new Vector(vXInit, vYInit);
			var i:Number = j - 1;
			this.updateNeeded = false;
			if(type == "m"){
				this.model.setMassOfBodyI(i, mass);
				this.updateNeeded = true;
			}else if(type == "x" || type == "y"){
				this.model.setInitPosOfBodyI(i, posInit);
				this.updateNeeded = true;
			}else if(type == "v"){
				this.model.setInitVelOfBodyI(i, velInit);
				this.updateNeeded = true;
			}else{
				trace("ERROR:type of text field is unrecognized. type is " + type +".");
			}//end of if(type..
		}
	}//end of textChanged();
		
	function test():Void{
		trace("mass of body 2:"+this.model.getMassOfBodyI(1));
		//for(var prop in _root.textHolder_mc){
			//trace(prop);
		//}
	}
	
	function update():Void{
		//trace("InitialView Update called");
		if(this.model.resettingN){
			this.initializeBodies();
			this.resetAllFields();
		}
		if(!this.model.integrationOn && this.updateNeeded){
			this.updateTextFields();
			//trace("updating fields");
		}
	}//end of update()
}//end of class