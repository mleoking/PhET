class View{
	var pendulum:Object;
	//var pendulumNbr:Number;
	var clip:MovieClip;				//clip showing pendulum
	var arcClip:MovieClip;			//trajectory arc shown during period measurement
	var arcRadius:Number;			//radius of period sweeping arc
	var pendulumDirection:Number;	//used to draw period Arc: +1 (-1) if pendulum.omega is + (-)
	var initAnglePointer:MovieClip;
	var pendulumColor:Number;
	//var protractor:Object;
	var currentAngleInDeg:Number;	//angle of pendulum from vertical
	var angleUnit_str:String;		//must code strings for internationalization
	var scaleFactor:Number; 		//number of pixels per meter
	var massColor:Color;			//obsolete
	var velocityShown:Boolean;		//true if velocity vector is shown
	var accelerationShown:Boolean;
	
	function View(pendulum:Object){
		this.pendulum = pendulum;
		this.pendulum.registerView(this);
		//this.protractor = new Protractor();
		//this.protractor.setVisibility(false);
		
		//test
		//this.protractor.setInitAngle(this.pendulum.labelNbr, 35 + 10*this.pendulum.labelNbr);
		init();
	}//end of constructor
	
	function init():Void{
		var nbr:Number = this.pendulum.labelNbr;
		this.clip = _root.attachMovie("pendulumHolder","pendulum"+nbr, _root.getNextHighestDepth());
		this.arcClip = _root.createEmptyMovieClip("arcClip_mc", _root.getNextHighestDepth());
		Util.setXYPosition(this.clip, Util.ORIGINX, Util.ORIGINY);
		Util.setXYPosition(this.arcClip, Util.ORIGINX, Util.ORIGINY);
		
		this.initAnglePointer = _root.createEmptyMovieClip("pointer"+nbr, _root.getNextHighestDepth());
		this.angleUnit_str = " deg";
		Util.setXYPosition(this.initAnglePointer, Util.ORIGINX, Util.ORIGINY);
		this.setpendulumColor();
		
		//this.setPointerAngle(10+15*nbr);
		this.showVelocity(false);
		this.showAcceleration(false);
		var viewRef = this;
		var clipRef =this.clip;
		this.massColor = new Color(this.clip.pendulum_mc.mass_mc.body_mc);
		//this.pendulumColor = 0x000000;
		//set string length
		this.scaleFactor = 200;
		this.clip.pendulum_mc.string_mc._height = this.pendulum.length*this.scaleFactor;  
		this.clip.pendulum_mc.vectorHolder_mc._y = this.clip.pendulum_mc.string_mc._height;
		this.clip.pendulum_mc.mass_mc._y = this.clip.pendulum_mc.string_mc._height;
		this.clip.pendulum_mc.mass_mc.label_txt.text = viewRef.pendulum.getLabelNbr();
		
		//initialize period Arc
		//var lineWidth:Number = 5;
		//this.arcClip.lineStyle(lineWidth, this.pendulumColor, 100);
		//this.arcRadius = 0.8*this.pendulum.length*this.scaleFactor;
		//this.arcClip.clear();
		//this.arcClip.moveTo(Util.ORIGINX, Util.ORIGINY + this.arcRadius);
		
		
		this.clip.pendulum_mc.mass_mc.onPress = function(){
			viewRef.pendulum.stopMotion();
			clipRef.angle_txt._visible = true;
			//viewRef.protractor.setVisibility(true);
			clipRef.onMouseMove = function(){
				var xPos:Number = _root._xmouse - Util.ORIGINX;
				var yPos:Number = -(_root._ymouse - Util.ORIGINY);
				viewRef.currentAngleInDeg = Math.round(90 + Math.atan2(yPos, xPos)*180/Math.PI);
				viewRef.pendulum.setAngleInDeg(viewRef.currentAngleInDeg);
				clipRef.pendulum_mc._rotation = -viewRef.currentAngleInDeg;
				viewRef.setPointerAngle(viewRef.currentAngleInDeg);
				var angle_str:String = String(0.1*Math.round(10*viewRef.currentAngleInDeg));
				//if(angle_str.indexOf(".") == -1){
					//angle_str = angle_str + ".0";
				//}
				clipRef.angle_txt.text = angle_str + viewRef.angleUnit_str;
				updateAfterEvent();
				//trace("   x: "+ xPos+"    yPos: "+ yPos +"   angle(degrees): "+currentAngleInDeg);
			}
		}//end of onPress()
		
		this.clip.pendulum_mc.mass_mc.onRelease = function(){
			viewRef.pendulum.setAngleInDeg(viewRef.currentAngleInDeg);
			viewRef.pendulum.startMotion();
			clipRef.angle_txt._visible = false;
			//viewRef.protractor.setVisibility(false);
			clipRef.onMouseMove = undefined;
		}
		
		this.clip.pendulum_mc.mass_mc.onReleaseOutside = this.clip.pendulum_mc.mass_mc.onRelease;
		
		//_root.createEmptyMovieClip("canvas_mc", _root.getNextHighestDepth());
	}//end of init
	
	function setVisibility(tOrF:Boolean):Void{
		this.clip._visible = tOrF;
	}
	
	function setMassColor(nbr:Number):Void{
		this.clip.pendulum_mc.mass_mc.body_mc.gotoAndStop(nbr);
		//trace("this.pendulum.labelNbr: "+nbr);
	}
	
	//set color of little pointer on the protractor
	function setpendulumColor():Void{
		var pendulumNbr:Number = this.pendulum.labelNbr;
		if(pendulumNbr == 1){
			this.pendulumColor = 0x0000FF;
		} else if(pendulumNbr == 2){
			this.pendulumColor = 0xFF0000;
		} else{
			trace("Error in call to pendulum number.  Number is not 1 or 2");
		}
	}
	
	function setPointerAngle(initAngle:Number):Void{		//initial angle in degrees
		var R:Number = 139;
		var dR:Number = 10
		//var idx:Number = pendulumNbr - 1;
		//this.pendulumInitAngle = initAngle;  //initial angle in degree
		var lineWidth:Number = 3;
		var colorNbr:Number = this.pendulumColor;
		var dToR:Number = Math.PI/180;	
		var pointerClip:MovieClip = this.initAnglePointer;
		//trace("pendNbr: " + pendulumNbr + "   colorNbr: "+ colorNbr + "   initAngle:"+ initAngle);
		var angle:Number = 90 - initAngle;
		with(pointerClip){
			clear();
			lineStyle(lineWidth, colorNbr, 100, false, "normal", "none");
			moveTo(R*Math.cos(angle*dToR), R*Math.sin(angle*dToR));
			lineTo((R+dR)*Math.cos(angle*dToR), (R+dR)*Math.sin(angle*dToR));
		}
 
	}//setPointerAngle
	
	function resetLength():Void{
		this.clip.pendulum_mc.string_mc._height = this.pendulum.length*this.scaleFactor;  
		this.clip.pendulum_mc.mass_mc._y = this.clip.pendulum_mc.string_mc._height;
		this.clip.pendulum_mc.vectorHolder_mc._y = this.clip.pendulum_mc.mass_mc._y;
		this.arcRadius = 0.8*this.pendulum.length*this.scaleFactor;
		this.arcClip.moveTo(Util.ORIGINX, Util.ORIGINY + this.arcRadius);
	}
	
	function resetMass():Void{
		var massInKg = this.pendulum.mass;
		var edgeLength = Math.pow(massInKg, 1/3);
		this.clip.pendulum_mc.mass_mc._xscale = this.clip.pendulum_mc.mass_mc._yscale = edgeLength*100;
	}
	
	function showVelocity(tOrF:Boolean):Void{
		this.velocityShown = tOrF;
		this.clip.pendulum_mc.vectorHolder_mc.velArrow_mc._visible = tOrF;
	}
	
	function showAcceleration(tOrF:Boolean):Void{
		this.accelerationShown = tOrF;
		this.clip.pendulum_mc.vectorHolder_mc.accArrow_mc._visible = tOrF;
	}
	
	function startPeriodArc():Void{
		this.arcClip.clear();   //call to clear() resets lineStyle to undefined!
		var lineWidth:Number = 3;
		this.arcClip.lineStyle(lineWidth, this.pendulumColor, 100);
		this.arcClip._alpha = 100;
		this.arcRadius = this.pendulum.length*this.scaleFactor - 50;
		this.arcClip.moveTo(0,this.arcRadius);
		var omega:Number = this.pendulum.omega;
		this.pendulumDirection = omega/Math.abs(omega);
		//trace("pend direction: "+this.pendulumDirection);
	}
	
	function fadeOutPeriodArc():Void{
		//draw very last segment of arc
		var pendulumScrnX:Number = 0;
		var pendulumScrnY:Number = this.arcRadius;
		this.arcClip.lineTo(pendulumScrnX, pendulumScrnY);
		//make arc fade away
		this.arcClip.onEnterFrame = function(){
			this._alpha -= 0.5;
			if (this._alpha <= 1){
				this.onEnterFrame = undefined;
			}
		}
	}//endPeriodArc()
	
	
	function update():Void{
		this.clip.pendulum_mc._rotation = -this.pendulum.getAngleInDeg();
		updateAfterEvent();
		var R:Number = this.pendulum.length;
		var omega:Number = this.pendulum.omega;
		if(velocityShown){
			var velArrow:MovieClip = this.clip.pendulum_mc.vectorHolder_mc.velArrow_mc;
			velArrow._xscale =  velArrow._yscale = 50*R*omega;  
		}//if(velocityShown)
		if(accelerationShown){
			var accArrow:MovieClip = this.clip.pendulum_mc.vectorHolder_mc.accArrow_mc;
			var alpha:Number = this.pendulum.alpha;
			var omegaSq:Number = omega*omega;
			var alphaMag:Number = R*Math.sqrt(alpha*alpha + omegaSq*omegaSq);
			var angleInDeg:Number = Math.atan2(omegaSq, alpha)*180/Math.PI;
			accArrow._xscale =  accArrow._yscale = 20*alphaMag;   //fudge factor hand-adjusted
			accArrow._rotation = -angleInDeg;
		}//if(acceleration)
		if(this.pendulum.gettingPeriod && this.pendulum.tripCount != 0){
			var omega:Number = this.pendulum.omega;
			var direction:Number = omega/Math.abs(omega);
			var theta:Number = this.pendulum.theta;
			if(direction != this.pendulumDirection){
				this.arcRadius -= 8;
				this.pendulumDirection = direction;
			}
			var pendulumScrnX:Number = this.arcRadius*Math.sin(theta);
			var pendulumScrnY:Number = this.arcRadius*Math.cos(theta);
			this.arcClip.lineTo(pendulumScrnX, pendulumScrnY);
		}//if(this.pendulum....)
	}//update()
	
}//end of class