class Protractor{
	var clip:MovieClip;
	var pointerClip1:MovieClip;		//two movieClips for the pendula initial angles
	var pointerClip2:MovieClip;		
	var radius:Number;				//radius of protractor in pixels
	var majorTickLength:Number;		//in pixels
	var minorTickLength:Number;
	var pendulumInitAngle:Number;	//initial angle in degrees, 2 pendula so 2 elements in array

	
	function Protractor(){
		this.pointerClip1 = _root.createEmptyMovieClip("pointer1", _root.getNextHighestDepth());
		this.pointerClip2 = _root.createEmptyMovieClip("pointer2", _root.getNextHighestDepth());
		
		this.clip = _root.createEmptyMovieClip("clip_mc", _root.getNextHighestDepth());
		
		Util.setXYPosition(this.clip, Util.ORIGINX, Util.ORIGINY);
		Util.setXYPosition(this.pointerClip1, Util.ORIGINX, Util.ORIGINY);
		Util.setXYPosition(this.pointerClip2, Util.ORIGINX, Util.ORIGINY);
		this.radius = 150;
		this.majorTickLength = 7;
		this.minorTickLength = 3;
		//this.pendulumInitAngle = new Array(2);
		this.drawProtractor();
	}//end of constructor
	
	function drawProtractor():Void{
		var lineWidth:Number = 1.5;
		var R:Number = this.radius
		var tS:Number = this.minorTickLength;  	//tick Small
		var tL:Number = this.majorTickLength;	//tick Large
		var tLL:Number = 10;
		var dToR:Number = Math.PI/180;		//degrees to radians
		this.clip.lineStyle(lineWidth, 0x000000, 100);
		for(var i:Number = 0; i <= 180; i++){
			clip.moveTo(R*Math.cos(i*dToR), R*Math.sin(i*dToR));
			clip.lineTo((R+tS)*Math.cos(i*dToR), (R+tS)*Math.sin(i*dToR));
			if(i%5 == 0){
				clip.lineTo((R+tL)*Math.cos(i*dToR), (R+tL)*Math.sin(i*dToR));
			}
			if(i%10 == 0){
				clip.lineTo((R+tLL)*Math.cos(i*dToR), (R+tLL)*Math.sin(i*dToR));
			}
		}
	}//drawProtractor
	
	function setVisibility(tOrF:Boolean){
		this.clip._visible = tOrF;
	}
	
	//following function is obsolete
	function setInitAngle(pendulumNbr:Number, initAngle:Number):Void{
		var R:Number = this.radius
		var dR:Number = 10
		//var idx:Number = pendulumNbr - 1;
		this.pendulumInitAngle = initAngle;  //initial angle in degree
		var lineWidth:Number = 6;
		var colorNbr:Number;
		var dToR:Number = Math.PI/180;	
		var pointerClip:MovieClip;
		if(pendulumNbr == 1){
			colorNbr = 0x0000FF;
			pointerClip = this.pointerClip1;
		} else if(pendulumNbr ==2){
			colorNbr = 0xFF0000;
			pointerClip = this.pointerClip2;
		} else{
			trace("Error in call to Protractor.  Index is not 1 or 2");
		}

		//trace("pendNbr: " + pendulumNbr + "   colorNbr: "+ colorNbr + "   initAngle:"+ initAngle);

		with(pointerClip){
			clear();
			lineStyle(lineWidth, colorNbr, 100);
			moveTo(R*Math.cos(initAngle*dToR), R*Math.sin(initAngle*dToR));
			lineTo((R+dR)*Math.cos(initAngle*dToR), (R+dR)*Math.sin(initAngle*dToR));
			//trace(initAngle);
			//moveTo(R*Math.cos(initAngle*dToR), R*Math.sin(initAngle*dToR));
			//lineTo((R+dR)*Math.cos(initAngle*dToR), (R+dR)*Math.sin(initAngle*dToR));
		}

	}//setInitAngle
}//end of class