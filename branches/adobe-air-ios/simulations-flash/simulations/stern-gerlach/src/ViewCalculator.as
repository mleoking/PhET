//view of System of SGBoxes  
//use coords where z is into page, x to right, y down
class ViewCalculator{
	private var mySGSystem:SGSystem;	//SG = Stern Gerlach
	private var mySoundMaker:SoundMaker;
	private var numBoxes:Number; //number of boxes (SG magnets) in the system
	private var boxLength:Number; //length (in z-direction) of SG box = 0.6
	private var zDist:Number;    //distance between the box centers along z-axis (in units where box length = 0.6)
	private var rDist:Number;	//distance from box axle to box output hole
	private var rUp:Number; 	//distance from box axle to up output hole
	private var rDown:Number; 	//distance from box axle to down output hole
	private var cameraDist:Number; //distance from camera to middle of box array
	private var f:Number; 		//focal length of view (in standard units where box depth = 0.6);
	private var viewHolder:MovieClip;
	private var phi:Number;		//angle in rads for view rotation about y-axis
	private var theta:Number;  	//angle in rads for view rotation about x-axis
	private var cosPhi:Number; 	// = Math.cos(this.phi);
	private var sinPhi:Number;	// = Math.sin(this.phi);
	private var cosTheta:Number;	// = Math.cos(this.theta);
	private var sinTheta:Number;	// = Math.sin(this.theta);
	private var nFactor:Number;	//size scaling of SGBox movieclip, nFactor = 100 if single SG Box, = 83 if two boxes shown, =73 for 3 boxes
	private var pixFactor1:Number;  //used to convert from VW distance to screen distance
	
	private var z1:Number; private var z2:Number; private var z3:Number;  //VW z-coords of centers of SG boxes
	private var x1:Number = 0; private var x2:Number = 0; private var x3:Number = 0; 
	private var y1:Number = 0; private var y2:Number = 0; private var y3:Number = 0;
	//Virtual World coordinates of spinBall
	private var zB:Number;	//current VR coords of moving spinBall (B for "ball", so as not to confuse with S for "screen Coords")
	private var xB:Number; private var yB:Number;
	private var zB0:Number;	//point of origin of spinBall = mouth of spin source
	private var xB0:Number; private var yB0:Number;
	private var zB1:Number;	//exit point of spinBall from SG box 1
	private var xB1:Number; private var yB1:Number;
	private var zB2:Number;	//exit point of spinBall from SG box 2
	private var xB2:Number; private var yB2:Number;
	private var zB3:Number;	//exit point of spinBall from SG box 3
	private var xB3:Number; private var yB3:Number;
	private var zB4:Number;	//final distination of spinBall off-screen to right
	private var xB4:Number; private var yB4:Number;
	private var zM1:Number;	//detection point on mask of SG Box 1 (M for "mask")
	private var xM1:Number;
	private var yM1:Number;
	private var zM2:Number;	//detection point on mask of SG Box 1 (M for "mask")
	private var xM2:Number;
	private var yM2:Number;
	private var zM3:Number;	//detection point on mask of SG Box 1 (M for "mask")
	private var xM3:Number;
	private var yM3:Number;
	private var zBStep:Number; //z-increment of spinBall during motion
	private var screenBallCoords:Array;
	private var ballNbr:Number;  //1, 2, 3, or 4 index for screen ball movieclip, to keep depths correct
	private var trajectoryID:Number;
	private var autoFireOn:Boolean;
	private var beamWidth:Number;
	public var beamIsOn:Boolean;
	private var lastTime:Number;
	//private var fireOneCounter:Number;
	private var speedSetting:String;  //"slow" or "fast"
	private var speedNumber:Number;  // 0 through 10,  0 = slowest, 10 = fastest
	private var nbrSpinsThrown:Number;  //nbr spins thru in one shot for "fast setting"
	private var overallBeamAlpha:Number;
	
	
	function ViewCalculator(mySGSystem:SGSystem){
		this.mySGSystem = mySGSystem;
		this.mySoundMaker = new SoundMaker();
		this.numBoxes = this.mySGSystem.getNumBoxes();
		this.boxLength = 0.6;
		this.zBStep = 0.1*this.boxLength;  //Standard step length is 0.125*this.boxLength;
		this.speedSetting = "slow";
		this.speedNumber = 3;
		this.zDist = 1.9*this.boxLength;  //should be 2.2*0.6
		this.rDist = 0.1;
		this.rUp = 0.26*0.4;	//0.104
		this.rDown = 0.24*0.4;	//0.96
		this.screenBallCoords = new Array(3);
		var xC:Number = -3;	//camera position x-coord
		var yC:Number = 1;
		var zC:Number = 6;
		this.cameraDist = 1.0*Math.sqrt(xC*xC + yC*yC + zC*zC);
		//trace("cameraDist:"+cameraDist);
		this.f = 0.2;	//should be 0.2
		this.pixFactor1 = 324;//324;
		this.phi = Math.acos(zC/(Math.sqrt(zC*zC + xC*xC)));
		this.theta = Math.asin(yC/(Math.sqrt(xC*xC + yC*yC + zC*zC)));
		this.cosPhi = Math.cos(this.phi);
		this.sinPhi = Math.sin(this.phi);
		this.cosTheta = Math.cos(this.theta);
		this.sinTheta = Math.sin(this.theta);
		this.viewHolder = _root["viewHolder"+this.numBoxes];
		this.beamWidth = 5;  
		
		this.viewHolder.attachMovie("spinBallSource", "spinBallSource_mc",this.viewHolder.getNextHighestDepth());
		
		for(var i = 1; i <= this.numBoxes; i++){
			this.viewHolder.attachMovie("spinBall","spinBall"+i,this.viewHolder.getNextHighestDepth());
			this.viewHolder.createEmptyMovieClip("ray"+i,this.viewHolder.getNextHighestDepth());  //holds ray drawing for fire100 stream
			this.viewHolder.attachMovie("sgBoxMask","sg"+i+"_mc",this.viewHolder.getNextHighestDepth());
			this.viewHolder.attachMovie("maskSplash", "maskHit"+i, this.viewHolder.getNextHighestDepth());
		}
		//marker_mc to test accuracy of placement on screen
		//this.viewHolder.attachMovie("marker","marker_mc",this.viewHolder.getNextHighestDepth());
		
		var lastNbr:Number = this.numBoxes + 1; 
		this.viewHolder.attachMovie("spinBall","spinBall"+lastNbr,this.viewHolder.getNextHighestDepth());
		this.viewHolder.createEmptyMovieClip("ray"+lastNbr,this.viewHolder.getNextHighestDepth());
			
		this.hideSpinBalls();
		
		for(var i = 1; i <= this.numBoxes+1; i++){
			this.viewHolder["maskHit"+i]._y = -200; //hide maskHits
		}

		this.autoFireOn = false;
		
		this.setView();
		//this.positionMarker();
		//trace("phi = "+ this.phi*180/Math.PI + "       theta = " + this.theta*180/Math.PI);
	}//end of constructor
	//following two functions for test purposes only
	function positionMarker():Void{
		var outlet1:Number = this.mySGSystem.SGBoxes[0].getOutletRadius();
		z2 = -this.zDist/2; x2 = 0; y2 = -outlet1*this.rDist;
		var length = this.boxLength;
		var width = (1/3)*this.boxLength;
		var height = (2/3)*this.boxLength;
		//back bottom left corner
		var zM = z2 + 0.5*length;
		var xM = x2 - 0.5*width;
		var yM = y2 + 0.5*height;
		this.plotMarker(xM, yM, zM, 1);
	    yM = y2 - 0.5*height; //top back left corner
		this.plotMarker(xM, yM, zM, 2);
		xM = x2 + 0.5*width;//top back right corner
		this.plotMarker(xM, yM, zM, 3);
		zM = z2 - 0.5*length;//top front right corner
		this.plotMarker(xM, yM, zM, 4);
		yM = y2 + 0.5*height;//bottom front right corner
		this.plotMarker(xM, yM, zM, 5);
		xM -= width;//bottom left front corner
		this.plotMarker(xM, yM, zM, 6);
		yM -= height;//top left front corner
		this.plotMarker(xM, yM, zM, 7);
		//xM = 0;
		//yM = 0.24*height;
		//zM = -0.57*length;
		//this.plotMarker(xM, yM, zM, 7);
		//yM = -0.26*height;
		//zM = -0.65*length;
		//this.plotMarker(xM, yM, zM, 8);
	}
	function plotMarker(xv:Number, yv:Number, zv:Number, i:Number):Void{
		var posMarker:Array = transformCoords(xv, yv, zv);
		this.viewHolder.marker_mc.duplicateMovieClip("mark"+i, this.viewHolder.getNextHighestDepth());
		this.viewHolder["mark" + i]._x = posMarker[0];
		this.viewHolder["mark" + i]._y = posMarker[1];
	}
	//Construct current view of orientation of SG boxes.  Called when dials or switches are used
	function setView():Void{
		//clearInterval(trajectoryID);
		this.hideSpinBalls();
		var r = this.rDist;  
		//VW coords of centers of SG boxes 1, 2, and 3
		x1 = 0; x2 = 0; x3 = 0;
		y1 = 0; y2 = 0; y3 = 0;
		//var z1:Number; var z2:Number; var z3:Number;
		//var x1:Number = 0; var x2:Number = 0; var x3:Number = 0; 
		//var y1:Number = 0; var y2:Number = 0; var y3:Number = 0;
		
		this.xB0 = 0; this.yB0 = 0;  //spin ball source always on axis
		xB = xB0; yB = yB0;  //spin ball always starts at source
		if(this.numBoxes == 1){
			z1 = 0;
			zB0 = z1 + 1.5*this.zDist;
			zB1 = z1 - 0.56*this.boxLength;
			zB4 = z1 - 2.5*this.zDist;  //far enough to be off-screen

		}else if(this.numBoxes == 2){
			z1 = +this.zDist/2;
			z2 = -this.zDist/2;
			zB0 = z1 + 1.5*this.zDist;
			zB1 = z1 - 0.57*this.boxLength;
			zB2 = z2 - 0.50*this.boxLength; //kludge!  Should be -0.57, not -0.50
			zB4 = z2 - 2*this.zDist;
			
		}else if(this.numBoxes == 3){
			//box order: 1 in back, 2 in middle, 3 in front
			z1 = this.zDist;
			z2 = 0;
			z3 = -this.zDist;
			zB0 = z1 + 1.5*this.zDist;
			zB1 = z1 - 0.63*this.boxLength;//kludge
			zB2 = z2 - 0.55*this.boxLength;//kludge
			zB3 = z3 - 0.50*this.boxLength;//kludge
			zB4 = z3 - 2*this.zDist;
		}//end of if else 
		this.zB = zB0;  //spin ball always starts at source
		
		//orient according to box angles and mask states
			
			var angle1:Number = this.mySGSystem.SGBoxes[0].getAngleInRads();
			var angle2:Number = this.mySGSystem.SGBoxes[1].getAngleInRads();
			var angle3:Number = this.mySGSystem.SGBoxes[2].getAngleInRads();
			var outlet1:Number = this.mySGSystem.SGBoxes[0].getOutletRadius();
			var outlet2:Number = this.mySGSystem.SGBoxes[1].getOutletRadius();
			var outlet3:Number = this.mySGSystem.SGBoxes[2].getOutletRadius();
			
			x2 = outlet1*r*Math.sin(angle1);
			y2 = -outlet1*r*Math.cos(angle1);
			x3 = x2 + outlet2*r*Math.sin(angle2);
			y3 = y2 - outlet2*r*Math.cos(angle2);
			
			//determine mask positions
			xM1 = -x2;	yM1 = -y2;
			xM2 = x2 - outlet2*r*Math.sin(angle2);	
			yM2 = y2 + outlet2*r*Math.cos(angle2);
			xM3 = x3 - outlet3*r*Math.sin(angle3);
			yM3 = y3 + outlet3*r*Math.cos(angle3);
			zM1 = zB1 - 0.1*this.boxLength;
			zM2 = zB2 - 0.1*this.boxLength;
			zM3 = zB3 - 0.1*this.boxLength;
			
			
			xB1 = x2; yB1 = y2;  //xy pos of ball exiting box 1 = xy pos of box 2
			xB2 = x3; yB2 = y3;
			xB3 = x3 + outlet3*r*Math.sin(angle3);
			yB3 = y3 - outlet3*r*Math.cos(angle3);
			xB4 = xB3; yB4 = yB3;  //don't think xB4, yB4 variables are needed!
			
			//rotate about y-axis  (x and z coords change)	
			var x1R:Number = x1*cosPhi - z1*sinPhi;  //x1 after one rotation
			var z1R:Number = z1*cosPhi + x1*sinPhi;
			var x2R:Number = x2*cosPhi - z2*sinPhi;
			var z2R:Number = z2*cosPhi + x2*sinPhi;
			var x3R:Number = x3*cosPhi - z3*sinPhi;
			var z3R:Number = z3*cosPhi + x3*sinPhi;
			var y1R:Number = y1;
			var y2R:Number = y2;
			var y3R:Number = y3;
			
			//now rotate about x-axis (y and z coords change)
			var y1RR:Number = y1R*cosTheta - z1R*sinTheta; //y1 after two rotations
			var z1RR:Number = z1R*cosTheta + y1R*sinTheta;
			var y2RR:Number = y2R*cosTheta - z2R*sinTheta;
			var z2RR:Number = z2R*cosTheta + y2R*sinTheta;
			var y3RR:Number = y3R*cosTheta - z3R*sinTheta;
			var z3RR:Number = z3R*cosTheta + y3R*sinTheta;
			
			var z1Final = this.cameraDist + z1RR;
			var z2Final = this.cameraDist + z2RR;
			var z3Final = this.cameraDist + z3RR;
			
			var scale1:Number = (this.f)/(this.f + z1Final);
			var scale2:Number = (this.f)/(this.f + z2Final);
			var scale3:Number = (this.f)/(this.f + z3Final);
			//trace("scale1:"+scale1+"     scale2:"+scale2+"      scale3:"+scale3 +"    nearToFar ratio:" + scale3/scale1);
			
			var ballSourceScreenCoords:Array = this.transformCoords(xB0, yB0, zB0);
			//trace("xB0:"+xB0+"    yB0:"+yB0+"   zB0:"+ zB0);
			//trace("xS:"+ballSourceScreenCoords[0]+"   yS:"+ballSourceScreenCoords[1]+"   zS:"+ballSourceScreenCoords[2])
			//var sFactor:Number;
			if(this.numBoxes == 1){
				nFactor = 100/2.5;
			}else if(this.numBoxes == 2){
				nFactor = 86/2.5;
			}else if(this.numBoxes == 3){
				nFactor = 73/2.5;
			}
			
			
			var pixFactor:Number = pixFactor1*nFactor;	//ratio of pixel distance to VW distance  (=324 when camera dist = 2)
			var x1Final:Number = x1R*scale1*pixFactor;
			var y1Final:Number = y1RR*scale1*pixFactor;
			var x2Final:Number = x2R*scale2*pixFactor;
			var y2Final:Number = y2RR*scale2*pixFactor;
			var x3Final:Number = x3R*scale3*pixFactor;
			var y3Final:Number = y3RR*scale3*pixFactor;
			scale1 = scale1*nFactor;
			scale2 = scale2*nFactor;
			scale3 = scale3*nFactor;
			//trace("scale1:"+scale1+"     scale2:"+scale2+"      scale3:"+scale3 +"    nearToFar ratio:" + scale3/scale1);
			//trace("scale2:"+scale2);
			//trace("x1R:"+x1R +"   y1RR:"+y1RR +"   z1RR:"+z1RR +"   scale1:"+scale1);
			//trace("x3R:"+x3R +"   y3RR:"+y3RR +"   z3RR:"+z3RR +"   scale3:"+scale3);
			//trace("x1Final:"+x1Final +"   y1Final:"+y1Final +"   z1Final:"+z1Final +"   scale1:"+scale1);
			//trace("x3Final:"+x3Final+"   y3Final:"+y3Final +"   z3Final:"+z3Final +"   scale3:"+scale3);
			var xC = 0.5*Util.STAGEW;
			var yC = 0.47*Util.STAGEH;
			with(this.viewHolder){
				sg1_mc._x = xC + x1Final;
				sg1_mc._y = yC + y1Final;
				sg2_mc._x = xC + x2Final;;
				sg2_mc._y = yC + y2Final;;
				sg3_mc._x = xC + x3Final;
				sg3_mc._y = yC + y3Final;
				sg1_mc._xscale = sg1_mc._yscale = 100*scale1;
				sg2_mc._xscale = sg2_mc._yscale = 100*scale2;
				sg3_mc._xscale = sg3_mc._yscale = 100*scale3;
				
				spinBallSource_mc._x = ballSourceScreenCoords[0];
				spinBallSource_mc._y = ballSourceScreenCoords[1];
				spinBallSource_mc._xscale = spinBallSource_mc._yscale = 130*ballSourceScreenCoords[2];
			}
		if(this.beamIsOn){
			this.beamOn();
		}
	}//end of setView();
	
	function stepBallForward():Void{
		var ballCoords:Array = transformCoords(xB,yB,zB);
		var bNbr = this.ballNbr
		this.viewHolder["spinBall" + bNbr]._x = ballCoords[0];
		this.viewHolder["spinBall" + bNbr]._y = ballCoords[1];
		this.viewHolder["spinBall" + bNbr]._xscale = 100*ballCoords[2];
		this.viewHolder["spinBall" + bNbr]._yscale = 100*ballCoords[2];
		var now = getTimer();
		var dt:Number = now - this.lastTime;
		//trace(dt);
		this.lastTime = now;
		this.zB -= (dt/20)*zBStep;
		if(this.numBoxes == 1){
			if(this.zB < this.zB1 && this.ballNbr == 1 ){
				_root.updateChart(1);
				if(!this.mySGSystem.SGBoxes[0].particleGetsThru){
					this.hitMask(1);
				}else{
					this.ballThru(1);
				}
			}
		}else if(this.numBoxes == 2){
			if(this.zB < this.zB1 && this.zB > this.zB2 && this.ballNbr == 1){
				_root.updateChart(1);
				if(!this.mySGSystem.SGBoxes[0].particleGetsThru){
					this.hitMask(1);
				}else{
					this.ballThru(1);
				}
			}else if(this.zB < this.zB2 && this.ballNbr == 2){
				_root.updateChart(2);
				if(!this.mySGSystem.SGBoxes[1].particleGetsThru){
					this.hitMask(2);
				}else{
					this.ballThru(2);
				}
			}
		}else if(this.numBoxes == 3){
			if(this.zB < this.zB1 && this.zB > this.zB2 && this.ballNbr == 1){
				_root.updateChart(1);
				if(!this.mySGSystem.SGBoxes[0].particleGetsThru){
					this.hitMask(1);
				}else{
					this.ballThru(1);
				}
			}else if(this.zB < this.zB2 && this.zB > this.zB3 && this.ballNbr == 2){
				_root.updateChart(2);
				if(!this.mySGSystem.SGBoxes[1].particleGetsThru){
					this.hitMask(2);
				}else{
					this.ballThru(2);
				}
			}else if(this.zB < this.zB3 && this.ballNbr == 3){
				_root.updateChart(3);
				if(!this.mySGSystem.SGBoxes[2].particleGetsThru){
					this.hitMask(3);
				}else{
					this.ballThru(3);
				}
			}
		}
		if(this.zB < this.zB4){  //remember: negative z is in foreground, positive z is in background
			this.endTrajectory();//clearInterval(this.trajectoryID);
		}
	}
	
	function fireOneSlow():Void{
		clearInterval(this.trajectoryID);
		this.hideSpinBalls();
		this.ballNbr = 1;
		this.zB = this.zB0;
		this.xB = this.xB0;
		this.yB = this.yB0;
		this.lastTime = getTimer();
		this.trajectoryID = setInterval(this, "stepBallForward", 20);
		this.mySoundMaker.ballFired();
	}
	
	function fireNFast():Void{
		this.fireSingleSpinThruFast();
		this.endTrajectory();
	}
	
	function fireSingleSpinThruFast():Void{
		for(var i:Number = 1; i <= this.numBoxes; i++){
			_root.updateChart(i);
			//if(!this.mySGSystem.SGBoxes[i-1].particleGetsThru){
					//this.hitMask(i);
			//}
		}
		if(!this.mySGSystem.SGBoxes[0].particleGetsThru){
			this.hitMask(1);
		}
		if(this.mySGSystem.SGBoxes[0].particleGetsThru && !this.mySGSystem.SGBoxes[1].particleGetsThru){
			this.hitMask(2);
		}
		if(this.mySGSystem.SGBoxes[1].particleGetsThru && !this.mySGSystem.SGBoxes[2].particleGetsThru){
			this.hitMask(3);
		}
		
	}
	
	function fireSpinBall():Void{
		if(this.speedSetting == "slow"){
			this.beamIsOn = false;  //in case beam is on
			this.fireOneSlow();
		}else{
			clearInterval(this.trajectoryID);
			this.trajectoryID = setInterval(this, "fireNFast", 5);
		}
	}
	
	function endTrajectory():Void{
		clearInterval(this.trajectoryID);
		if(this.speedSetting == "slow"){
			this.hideSpinBalls();
			this.ballNbr = 1;
			this.zB = this.zB0;
			this.xB = this.xB0;
			this.yB = this.yB0;
			if(this.autoFireOn){
				this.mySGSystem.fireParticle();
			}
		}else if(this.speedSetting == "fast"){
			if(this.autoFireOn){
				this.mySGSystem.fireNParticles(this.nbrSpinsThrown);
			}
		}else{
			trace("ERROR: invalid speedSetting.");
		}
	}//end of endTrajectory();
	

	
	function autoFireParticles():Void{
		this.autoFireOn = true;
		if(this.speedSetting == "fast"){
			this.beamIsOn = true;
			this.beamOn();
		}else{
			this.beamIsOn = false;
			this.beamOff();
		}
		//trace("autoFireOn");
		this.mySGSystem.fireParticle();
	}
	
	function setSpeed(speed:Number):Void{
		this.speedNumber = speed;
		var boxL = this.boxLength;
		switch(speed){
			case 0:
				this.speedSetting = "slow";
				this.zBStep = boxL/40;
				this.beamOff();
				break;
			case 1:
				this.speedSetting = "slow";
				this.zBStep = boxL/20;
				this.beamOff();
				break;
			case 2:
				this.speedSetting = "slow";
				this.zBStep = boxL/12;
				this.beamOff();
				break;
			case 3:
				this.speedSetting = "slow";
				this.zBStep = boxL/8;
				this.beamOff();
				break;
			case 4:
				this.speedSetting = "slow";
				this.zBStep = boxL/5;
				this.beamOff();
				break;
			case 5:
				this.speedSetting = "slow";
				this.zBStep = boxL/2.5;
				this.beamOff();
				break;
			case 6:
				this.speedSetting = "fast";
				this.nbrSpinsThrown = 1;
				this.overallBeamAlpha = 20;
				this.hideSpinBalls();//in case any balls on screen
				if(this.autoFireOn){this.beamOn();}
				break;
			case 7:
				this.speedSetting = "fast";
				this.nbrSpinsThrown = 3;
				this.overallBeamAlpha = 40;
				this.hideSpinBalls();
				if(this.autoFireOn){this.beamOn();}
				break;
			case 8:
				this.speedSetting = "fast";
				this.nbrSpinsThrown = 11;
				this.overallBeamAlpha = 60;
				this.hideSpinBalls();
				if(this.autoFireOn){this.beamOn();}
				break;
			case 9:
				this.speedSetting = "fast";
				this.nbrSpinsThrown = 37;
				this.overallBeamAlpha = 80;
				this.hideSpinBalls();
				if(this.autoFireOn){this.beamOn();}
				break;
			case 10:
				this.speedSetting = "fast";
				this.nbrSpinsThrown = 131;
				this.overallBeamAlpha = 100;
				this.hideSpinBalls();
				if(this.autoFireOn){this.beamOn();}
				break;
		}//end of switch
	}//end of setSpeed
	
	function getSpeedNumber():Number{
		return this.speedNumber;
	}
	
	function ceaseAutoFire():Void{
		this.autoFireOn = false;
		this.beamOff();
		//trace("ceaseAutoFire");
	}
	
	function beamOn():Void{
		var probUp_array:Array = this.mySGSystem.getProbabilities();
		var outlet_array:Array = new Array(this.numBoxes);
		for(var i:Number = 0; i < this.numBoxes; i++){
			outlet_array[i] = this.mySGSystem.SGBoxes[i].getOutlet();
		}
		//trace("probUp_array.length:"+probUp_array.length);
		//for(var i:Number = 0; i < this.numBoxes; i++){
			//trace("Box "+(i+1)+" probUp:  "+probUp_array[i]);
		//}
		this.beamIsOn = true;
		var lineWidth:Number = 1;
		for (var i = 1; i <= this.numBoxes + 1; i++){
			this.viewHolder["ray" + i].clear();
			this.viewHolder["ray" + i].lineStyle(lineWidth, 0xFFFF00, 100);
			var startIndex = i - 1;
			var xVinit:Number = this["xB" + startIndex];  //i for initial position
			var yVinit:Number = this["yB" + startIndex];
			var zVinit:Number = this["zB" + startIndex];
			var startPos:Array = transformCoords(xVinit, yVinit, zVinit);
			var xi:Number = startPos[0];
			var yi:Number = startPos[1];
			var wi:Number = startPos[2]*beamWidth;

			var boxNbr = i;
			//trace("boxNbr: "+boxNbr);
			var xVf:Number; var yVf:Number; var zVf:Number; //VW coords of final position
			
			//zVf = this.zB4; //this["z" + boxNbr];
			if(boxNbr == this.numBoxes + 1){
				xVf = this["xB" + this.numBoxes];
				yVf = this["yB" + this.numBoxes];
				zVf = this.zB4;
				//trace("zB last:"+zVf);
			}else{
				xVf = this["x" + boxNbr];  //f for final position
				yVf = this["y" + boxNbr];
				zVf = this["z" + boxNbr];
			}
			var endPos:Array = transformCoords(xVf, yVf, zVf);
			var xf:Number = endPos[0];
			var yf:Number = endPos[1];
			var wf:Number = endPos[2]*this.beamWidth;
			var mainAlpha:Number = this.overallBeamAlpha;
			with(this.viewHolder["ray"+ i]){
				var probThru:Number = 1;
				if(i == 1){
					probThru *= 1;
				}else{
					for(var boxN = i-1; boxN >=1; boxN--){
						//trace("boxN:" + boxN);
						var probThruThisBox:Number;
						upProb = probUp_array[boxN - 1];
						if(outlet_array[boxN - 1] == 1){
							probThruThisBox = upProb;
						}else{
							probThruThisBox = 1 - upProb
						}
						probThru *= probThruThisBox;
					}//end of for loop
				}//end of else
				_alpha = mainAlpha*probThru;
				beginFill(0xFFFFFF,100);
				moveTo(xi, yi-wi);
				lineTo(xi, yi+wi);
				lineTo(xf, yf+wf);
				lineTo(xf, yf-wf);
				lineTo(xi, yi-wi);
				endFill();
			}
		}//end of for loop
	}//end of beamOn()
	
	function beamOff():Void{
		this.beamIsOn = false;
		for(var i = 1; i <= this.numBoxes + 1; i++){
			this.viewHolder["ray" + i].clear();
		}
		//trace("beam off");
	}
	
	function hitMask(n:Number):Void{
		var hitCoords:Array;
		var n:Number;
		this.endTrajectory();
		if(n == 1){
			hitCoords = transformCoords(xM1, yM1, zM1);
		}else if(n == 2){
			hitCoords = transformCoords(xM2, yM2, zM2);
		}else if(n == 3){
			hitCoords = transformCoords(xM3, yM3, zM3);
		}
		this.viewHolder["maskHit" + n]._x = hitCoords[0];
		this.viewHolder["maskHit" + n]._y = hitCoords[1];
		this.viewHolder["maskHit" + n]._xscale = this.viewHolder["maskHit" + n]._yscale = 100*hitCoords[2];
		this.viewHolder["maskHit" + n].gotoAndPlay(1);
		this.mySoundMaker.maskHit();
		//this.resetSpinBallPosition();
	}
	
	function ballThru(n:Number):Void{
		this.xB = this["xB" + n];
		this.yB = this["yB" + n];
		this.ballNbr = n + 1;
		this.mySoundMaker.ballThru();
	}
	
	//transfrom virtual world coordinates (xV, yV, zV) to screen coords (xS, yS, scale)
	function transformCoords(xV:Number, yV:Number, zV:Number):Array{
		//rotate about y-axis  (x and z coords change)	
		var xVR:Number = xV*cosPhi - zV*sinPhi;  //x1 after one rotation
		var zVR:Number = zV*cosPhi + xV*sinPhi;
		var yVR:Number = yV;
		//now rotate about x-axis (y and z coords change)
		var yVRR:Number = yVR*cosTheta - zVR*sinTheta; //y1 after two rotations
		var zVRR:Number = zVR*cosTheta + yVR*sinTheta;
		var xVRR:Number = xVR;
		
		var zVFinal = this.cameraDist + zVRR;
		var scale:Number = (this.f)/(this.f + zVFinal);
		
		//var nFactor = 100; //XX depends on BoxNumber
		var pixFactor:Number = pixFactor1*nFactor;	//ratio of pixel distance to VW distance
		if(pixFactor == undefined){trace("Error: pixFactor undefined");}
		var xS:Number = xVRR*scale*pixFactor;
		var yS:Number = yVRR*scale*pixFactor;
		
		//XX xC and yC should be class members, not local vars
		var xC = 0.5*Util.STAGEW;
		var yC = 0.47*Util.STAGEH;
		scale = scale*nFactor;
		xS = xC + xS;
		yS = yC + yS;
		var screenCoords:Array = new Array(xS, yS, scale);
		//this.screenBallCoords = [xS, yS, scale];
		return screenCoords;
	}
	
	function hideSpinBalls():Void{
		for(var i = 1; i <= this.numBoxes+1; i++){
			this.viewHolder["spinBall"+i]._y = -100;  //get balls off screen
		}
	}
	
	function resetSpinBallPosition():Void{
		this.zB = this.zB0;
		this.xB = 0;
		this.yB = 0;
		this.ballNbr = 1;
	}
	
	function setStepLength(dZ:Number):Void{
		this.zBStep = dZ;
	}
	
	function setSpinIndicator(spinNumber:Number):Void{
		this.viewHolder.spinBallSource_mc.spinIndicator_mc.gotoAndStop(spinNumber);
	}
	
}//end of class