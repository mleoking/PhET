//Create reduced Chi-square display

class ChiDisplay{
	var model:Object;
	var stageH:Number;
	var stageW:Number; 
	var chiDisplay_mc:MovieClip;
	var chiScale_mc:MovieClip;
	var barColor:Color;
	var lowerLimit:Number; //chi value of red zone
	var upperLimit:Number; //chi value of green zone
	var myTextFormat:TextFormat;
	
	function ChiDisplay(model:Object, target:MovieClip){
		this.model = model;
		this.myTextFormat = new TextFormat("_sans", 14, 0x000000);
		this.stageH = Util.STAGEH;
		this.stageW = Util.STAGEW;
		//var clip = target.attachMovie("chiBar", "chiBar_mc", Util.getNextDepth());
		this.chiDisplay_mc = target.createEmptyMovieClip("chiDisplay_mc", Util.getNextDepth());
		this.chiDisplay_mc.attachMovie("invisible", "grabMe_mc", this.chiDisplay_mc.getNextHighestDepth());
		Util.setXYPosition(this.chiDisplay_mc, -15, 0);
		var grabMe:MovieClip = this.chiDisplay_mc.grabMe_mc;
		Util.setXYPosition(grabMe, 50, 0.9*this.stageH);
		grabMe._alpha = 0;
		this.chiDisplay_mc.attachMovie("helpChiSquare","help_mc", this.chiDisplay_mc.getNextHighestDepth());
		Util.setXYPosition(this.chiDisplay_mc.help_mc, 100, 0.7*this.stageH);
		Util.makeParentClipDraggable(grabMe, undefined, -0.3*stageW, stageW, -0.2*stageH, 0.3*stageH);
		this.chiDisplay_mc.attachMovie("chiBar", "bar_mc", this.chiDisplay_mc.getNextHighestDepth());
		var bar_mc:MovieClip = this.chiDisplay_mc.bar_mc;
		barColor = new Color(bar_mc);
		this.upperLimit = 2;
		this.lowerLimit = 0.5;
		barColor.setRGB(0xFF0000);
		Util.setXYPosition(bar_mc, 50+0.5*bar_mc._width, 0.9*this.stageH);
		this.chiScale_mc = this.chiDisplay_mc.createEmptyMovieClip("chiScale_mc", this.chiDisplay_mc.getNextHighestDepth());
		this.drawAxis();
	}
	
	function update():Void{
		var bar:MovieClip = this.chiDisplay_mc.bar_mc;
		var chiValue:Number = this.model.getChiSquare()
		if (chiValue <= 1){
			bar._yscale = 100*chiValue;
			if(chiValue > lowerLimit){
				var green:Number = 255*(chiValue - lowerLimit)/(1 - lowerLimit);
				var red:Number = 255 - green;
				var blue:Number = 70
				var RGB = (red<<16)|(green<<8)|(blue);
				barColor.setRGB(RGB);
			}else{
				barColor.setRGB(0xFF0000);
			}
		}else{
			bar._yscale = 100*(1+Math.log(chiValue));
			if(chiValue < upperLimit){
				var green:Number = 255*(upperLimit - chiValue)/(upperLimit - 1);
				var red:Number = 255 - green;
				var blue:Number = 70
				var RGB = (red<<16)|(green<<8)|(blue);
				barColor.setRGB(RGB);
			}else{
				barColor.setRGB(0xFF0000);
			}
			//trace("bar height in pixels: "+this.chiScale.chiBar_mc._height);
		}
		this.chiDisplay_mc.chi_txt.text = 0.01*Math.round(100*chiValue);
		var value:Number = 0.01*Math.round(100*chiValue);
		if(value%1 == 0){
			this.chiDisplay_mc.chi_txt.text = value + ".00";
		}else if ((10*value)%1 == 0){
			this.chiDisplay_mc.chi_txt.text = value + "0";
		}
			
		//this.chiScale.chiValue_txt.text = String(Math.round(100*chiValue)/100);
	}
	
	function drawAxis():Void{
		var maxW = this.stageW;
		var maxH = this.stageH;
		var unit = 80;  //unit height is 80 pixels
		var leftEdge = 50;
		var ticLength = 7;
		var leftEdgeTic = leftEdge + 2; 
		var bottomEdge = 0.9*maxH;
		var topEdge = 0.1*maxH;
		//draw scale
		with(this.chiScale_mc){
			clear();
			var lineWidth = 3;
			lineStyle(lineWidth, 0x000000, 100);
			moveTo(leftEdge,bottomEdge);
			lineTo(leftEdge,topEdge);
			lineStyle(lineWidth, 0x000000, 100, false, "normal", "square");
		}//end of with()
		
		var scaleCanvas:MovieClip = this.chiScale_mc;
		scaleCanvas.lineStyle(lineWidth, 0x000000, 100, false, "normal", "square");
		
		drawTicMark(0);
		drawTicMark(0.5);
		drawTicMark(1);
		drawTicMark(1 + Math.log(2));
		drawTicMark(1 + Math.log(3));
		drawTicMark(1 + Math.log(10));
		drawTicMark(1 + Math.log(30));
		drawTicMark(1 + Math.log(100));
		function drawTicMark(lnNbr:Number):Void{
		 	scaleCanvas.moveTo(leftEdgeTic, bottomEdge - lnNbr*unit);
		 	scaleCanvas.lineTo(leftEdgeTic + ticLength, bottomEdge - lnNbr*unit);
	 	}
		//number tic marks
		this.myTextFormat.align = "right";
		var canvas = this.chiDisplay_mc;
		var txtH = 25;
		this.drawTicNumber("zero_txt", 0, txtH/2);  //textField name, number, hieght above bottomEdge
		this.drawTicNumber("half_txt", 0.5, 0.5*unit + txtH/2);
		this.drawTicNumber("one_txt", 1, 1.0*unit + txtH/2);
		this.drawTicNumber("two_txt", 2, (1 + Math.log(2))*unit + txtH/2);
		this.drawTicNumber("three_txt", 3, (1 + Math.log(3))*unit + txtH/2);
		this.drawTicNumber("ten_txt", 10, (1 + Math.log(10))*unit + txtH/2);
		this.drawTicNumber("thirty_txt", 30, (1 + Math.log(30))*unit + txtH/2);
		this.drawTicNumber("hundred_txt", 100, (1 + Math.log(100))*unit + txtH/2);
		
		//name, depth, x, y, width, height
		canvas.attachMovie("chiSqLabel","chiLabel_mc", this.chiDisplay_mc.getNextHighestDepth());
		Util.setXYPosition(canvas.chiLabel_mc, leftEdge-33, bottomEdge);
		canvas.createTextField("chi_txt", canvas.getNextHighestDepth(), leftEdge, bottomEdge + 4, 60, txtH);
		this.myTextFormat.size = 18;
		this.myTextFormat.align = "left";
		canvas.chi_txt.setNewTextFormat(this.myTextFormat);
		//canvas.chi_txt.border = true;
		canvas.chi_txt.text = "300.5";
	
	}//end of drawAxis()
	
	
	function drawTicNumber(txtFieldName:String, nbr:Number, yLocation:Number){
		var canvas = this.chiDisplay_mc;
		var maxH = this.stageH;
		var txtH:Number = 20;
		var bottomEdge = 0.9*maxH;
		//name, depth, x, y, width, height
		canvas.createTextField(txtFieldName, canvas.getNextHighestDepth(), 0, bottomEdge - yLocation, 45, txtH);
		canvas[txtFieldName].setNewTextFormat(this.myTextFormat);
		canvas[txtFieldName].antiAliasType = "advanced";
		canvas[txtFieldName].text = String(nbr);
	}
}//end of class