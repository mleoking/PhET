class AxesView{
	var model:Object;
	var canvas:MovieClip;
	var yAxisLabel:MovieClip;		//needed for internationalization
	var maxNbrRows:Number;
	var nbrRows:Number;
	var delX:Number;
	var delY:Number;
	var textFormat:TextFormat;
	var labelFormat:TextFormat;
	var countryCode:String;			//needed for internationalization
	var fraction_str:String;
	var number_str:String;
	var stageW:Number;
	var stageH:Number;
	
	function AxesView(model:Object, countryCode:String){
		this.model = model;
		this.model.registerAxesView(this);
		this.countryCode = countryCode;
		this.fraction_str = "fraction";  //in case XML fails to load
		this.number_str = "number";  //in case XML fails to load
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.maxNbrRows = this.model.maxNbrRows;
		this.delX = this.model.bigView.horizSpace;	
		this.canvas = _root.createEmptyMovieClip("axesCanvas_mc", Util.getNextDepth());
		Util.setXYPosition(this.canvas, Util.ORIGINX, Util.BOTTOMY);
		this.yAxisLabel = this.canvas.attachMovie("yAxisLabel", "yAxisLabel_mc", this.canvas.getNextHighestDepth());
		trace("yAxisLabel"+this.yAxisLabel);
		this.canvas.attachMovie("trueMeanIndicator", "trueMeanIndicator_mc", this.canvas.getNextHighestDepth());
		Util.setXYPosition(this.canvas.trueMeanIndicator_mc, 0, 0);
		this.canvas.attachMovie("meanIndicator", "meanIndicator_mc", this.canvas.getNextHighestDepth());
		Util.setXYPosition(this.canvas.meanIndicator_mc, 0, 0);
		this.initializeText();
		//this.drawYAxis();
		this.drawAxes();
	}//end of constructor
	
	function initializeText():Void{
		this.textFormat = new TextFormat("Arial",12, 0x000000);
		this.textFormat.align = "left"
		var maxNbr = this.maxNbrRows;
		//create x-axis text
		for(var i = 0; i <= maxNbr; i++){
			this.canvas.createTextField(i,this.canvas.getNextHighestDepth(),300,10,15,35); 
			this.canvas[i].setNewTextFormat(this.textFormat);
			this.canvas[i].autoSize = "left";
			this.canvas[i].text = i;
		}
		//create yAxis text
		this.canvas.createTextField("topNumber_txt",this.canvas.getNextHighestDepth(),-294, -this.model.bigView.maxHistHeight - 4, 55, 25);
		this.canvas.createTextField("midNumber_txt",this.canvas.getNextHighestDepth(),-294, -this.model.bigView.maxHistHeight/2 - 4, 55, 25);
		//trace("_root.histogramCanvas_mc._width:"+_root.histogramCanvas_mc._width);
		this.canvas.topNumber_txt.setNewTextFormat(this.textFormat);
		this.canvas.midNumber_txt.setNewTextFormat(this.textFormat);
			
		this.canvas.createTextField("yAxisLabel_txt", this.canvas.getNextHighestDepth(),-100,-100,80,30);
		this.canvas.yAxisLabel_txt.embedFonts = true;
		this.labelFormat = new TextFormat();
		this.labelFormat.font = "ArialBold16pt";
		with(this.canvas){
			yAxisLabel_txt.text = fraction_str;
			//yAxisLabel_txt._x = xPos;
			//yAxisLabel_txt._y = yPos;
			yAxisLabel_txt.setTextFormat(this.labelFormat);
			yAxisLabel_txt._rotation = -90;
		}

	}//end of initializeText()
	

	function positionText():Void{
		var maxNbr = this.nbrRows;
		var xOff = maxNbr*delX;
		for(var i = 0; i <=  maxNbr; i++){
			this.canvas[i]._x = -xOff + i*2*delX - this.canvas[i]._width/2;
		}//end for loop
		if(maxNbr > 32){	//hide odd numbers if nbrRows too large
			for(var i = 1; i <  maxNbr; i += 2){
				this.canvas[i]._x = 10000;
			}//end for loop
		}
	}//end of positionText();
	
	
	function drawAxes():Void{
		this.setXYScale();
		this.updateIndicators();
		//trace("this.nbrRows"+this.nbrRows + "     delX" + delX);
		with(this.canvas){
			clear();
			var lineWidth:Number = 2;
			lineStyle(lineWidth, 0x0000FF, 100);
			//moveTo(0, 0);
			//lineTo(0, 10);
			
			for(var i:Number = -this.nbrRows; i <= this.nbrRows; i += 2){
				moveTo(i*this.delX, 0);
				lineTo(i*this.delX, 3);
			}
			//draw x-axis
			moveTo(-this.nbrRows*this.delX,0);
			lineTo(this.nbrRows*this.delX,0);
			this.drawYAxis();
		}
		this.positionText();
	}
	
	function drawYAxis():Void{
		with(this.canvas){
			//clear();
			var lineWidth:Number = 2;
			lineStyle(lineWidth, 0x0000FF, 100);
			moveTo(-this.nbrRows*this.delX,0);
			lineTo(-this.nbrRows*this.delX, -this.model.bigView.maxHistHeight);
			lineTo(-this.nbrRows*this.delX + 3, -this.model.bigView.maxHistHeight);
			moveTo(-this.nbrRows*this.delX, -this.model.bigView.maxHistHeight/2);
			lineTo(-this.nbrRows*this.delX + 3, -this.model.bigView.maxHistHeight/2);
		}
		this.labelYAxis();
	}
	
	function labelYAxis():Void{
		//trace("labelYAxis called:" + this.model.bigView.histDisplayState);
		var currentLabel:String
		if(this.model.bigView.histDisplayState == 1){
			this.writeYLabel(fraction_str);
			currentLabel = fraction_str;
		}else{
			this.writeYLabel(number_str);
			currentLabel = number_str;
		}

		this.writeYAxisNumbers();
		
		if(this.countryCode == "en" || this.countryCode == undefined){
			with(this.canvas){
				//yAxisLabel_txt.text = "fraction";
				yAxisLabel_txt.autoSize = true;
				yAxisLabel_txt._x = -this.nbrRows*this.delX - 30;
				yAxisLabel_txt._y = -0.5*this.model.bigView.maxHistHeight;// + 2*this.canvas.yAxisLabel_txt._width;
				yAxisLabel_txt.setTextFormat(this.labelFormat);
				yAxisLabel_txt._rotation = -90;
			}//end with()
		}else{
			this.canvas.yAxisLabel_txt.text = "";
			this.yAxisLabel._x = -this.nbrRows*this.delX - 30;
			this.yAxisLabel._y = -0.5*this.model.bigView.maxHistHeight;
			this.yAxisLabel.label_txt.text = currentLabel;
			this.stackString(this.yAxisLabel.label_txt);

		}//end else
	}//end labelYAxis
	
	function writeYLabel(yLabel:String):Void{
		this.canvas.yAxisLabel_txt.text = yLabel;
	}
	
	function writeYAxisNumbers():Void{
		var pixHeight:Number = this.model.bigView.maxHistHeight;
		var topNumber:Number = pixHeight/this.model.bigView.nbrToPixConversion;
		var midNumber:Number = topNumber/2;
		var topFraction:Number = Math.round(100*pixHeight/this.model.bigView.fracToPixConversion)/100;
		var midFraction:Number = topFraction/2;
		//trace("topNumber:"+topNumber + "  topFraction:"+topFraction)
		if(this.model.bigView.histDisplayState == 1){ //if fraction
			this.canvas.topNumber_txt.text = topFraction;
			this.canvas.midNumber_txt.text = midFraction;
		}else if (this.model.bigView.histDisplayState == 2){ //if number
			this.canvas.topNumber_txt.text = 20;
			this.canvas.midNumber_txt.text = 10;
		}else { //if autoscale
			this.canvas.topNumber_txt.text = topNumber;
			this.canvas.midNumber_txt.text = midNumber;
		}
	}//end of writeYAxisNumbers()
	
	function setXYScale():Void{
		this.delX = this.model.bigView.horizSpace;	
		//this.delY = this.model.bigView.vertSpace;
		this.nbrRows = this.model.nbrSteps;
		this.delX *= this.maxNbrRows/this.nbrRows;
		//set unused x-axis numbers to the side
		for(var i = this.nbrRows + 1; i <= this.maxNbrRows; i++){
			this.canvas[i]._x = 600;
		}//end for loop
	}
	
	function updateIndicators():Void{
		this.canvas.trueMeanIndicator_mc._x = (2*this.model.trueMean - this.nbrRows)*this.delX;
		var N:Number = this.model.trialNbr; //this.model.bigView.histN;
		if(N != 0){
			if(this.model.controlPanelView.showState == 1){
				this.canvas.meanIndicator_mc._x = (2*this.model.bigView.histMean - this.nbrRows)*this.delX;
			}else{
				this.canvas.meanIndicator_mc._x = (2*this.model.mean - this.nbrRows)*this.delX;
			}
		}else{
			this.canvas.meanIndicator_mc._x = 10000;  //put off stage
		}
	}//end of updateIndicators
	
	function stackString(field:TextField){
		//trace("key: "+key);
		var stringValue:String = field.text;
		var currentTextFormat:TextFormat = field.getTextFormat();
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			if(field.html){
				field.htmlText = stringValue;
			}else{
				//search for "newline" 
				var chars_arr:Array = stringValue.split("");
				if(chars_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < chars_arr.length; i++){
						newStringValue += chars_arr[i]+newline;
					}
					stringValue = newStringValue;
				}
				field.text = stringValue;
			}
			//field.setTextFormat(currentTextFormat);
			//this.resizeText(field, alignment);
			//trace("key: "+key+"   stringValue:"+stringValue);
		}
	}//end of stackString()
	
}//end of class