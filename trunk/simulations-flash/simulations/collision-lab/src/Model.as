//The Model for Collision Lab
package{
	import flash.events.*;
	import flash.utils.*;
	
	public class Model{
		var nbrBalls:int;  		//nbr of interacting balls
		var ball_arr:Array;		//array of balls
		private var initPos:Array;			//array of initial positions of balls
		private var initVel:Array;			//array of initial velocities of balls
		var borderOn:Boolean;	//if true, balls elastically reflect from border 
		var borderWidth:int;	//length of horizontal border in meters
		var borderHeight:int;	//length of vertical border in meters
		//var elasticity:Number;	//elasticity = 0 to 1:
		var e:Number;			//elasticity = 0 to 1: 0 = perfectly inelastic, 1 = perfectly elastic
		var time:Number;		//simulation time in seconds = real time
		var lastTime:Number;	//time of previous step
		var timeStep:Number;	//time step in seconds
		var msTimer:Timer;		//millisecond timer
		var playing:Boolean;	//true if motion is playing, false if paused
		var starting:Boolean;	//true if playing and 1st step not yet taken;
		//var realTimer:Timer;	//real timer, used to maintain time-based updates
		var timeHolder:int;		//scatch for hold results of getTimer
		var timeRate:Number;	//0 to 1: to slow down or speed up action, 1 = realtime, 0 = paused
		var updateRate:int;		//number of time steps between graphics updates
		var frameCount:int;		//when frameCount reaches frameRate, update graphics
		var colliders:Array;	//2D array of ij pairs: value = 1 if pair colliding, 0 if not colliding.
		var view_arr:Array;		//views of this model
		var nbrViews:int;		//number of views
		
		public function Model(){
			this.borderOn = true;
			this.borderWidth = 3;
			this.borderHeight = 2;
			//
			//this.elasticity = 1;
			this.e = 1;				//set elasticity of collisions, 1 = perfectly elastic
			this.nbrBalls = 3;
			this.ball_arr = new Array(nbrBalls);
			this.initializeBalls();
			this.time = 0;
			this.timeStep = 0.01;
			this.updateRate = 3;
			this.frameCount = 0;
			
			this.msTimer = new Timer(this.timeStep*1000);
			msTimer.addEventListener(TimerEvent.TIMER, stepForward);
			this.stopMotion();
			this.e = 1;				//set elasticity of collisions, 1 = perfectly elastic
			//this.realTimer = new Timer(1000);  //argument 1000 is irrelevant
			this.view_arr = new Array(0);
			this.nbrViews = 0;
			
		}//end of constructor
		
		public function initializeBalls():void{
			this.initPos = new Array(this.nbrBalls);
			this.initVel = new Array(this.nbrBalls);
			initPos[0] = new TwoVector(0.2,0.2);
			initPos[1] = new TwoVector(0.5,0.5);
			initPos[2] = new TwoVector(1,1);
			initVel[0] = new TwoVector(0.7,0.8);
			initVel[1] = new TwoVector(0.12,2);
			initVel[2] = new TwoVector(0.1,1);
			for (var i = 0; i < this.nbrBalls; i++){
				//new Ball(mass, position, velocity);
				this.ball_arr[i] = new Ball(1.0, initPos[i].clone(), initVel[i].clone());
			}
			//trace("myModel.initPos[0]: "+this.initPos[0].getX());
			
			var N:int = this.nbrBalls;
			//initialize colliders array
			this.colliders = new Array(N);
			//in AS3, duplicate variable definition in same method causes compile error message
			//so do not use var i:int more than one
			for (i = 0; i < N; i++){
				this.colliders[i] = new Array(N);
			}
			for (i = 0; i < N; i++){
				for (var j:int = 0; j < N; j++){
					this.colliders[i][j] = 0;  //0 if not colliding
				}
			}
			//No point in updating views, since views not created yet
		}//end of initializeBalls()
		
		public function initializePositions():void{
			//trace("Model.initializePositions() called");
			for (var i = 0; i < this.nbrBalls; i++){
				//new Ball(mass, position, velocity);
				this.ball_arr[i].position = initPos[i].clone();
				this.ball_arr[i].velocity = initVel[i].clone();
			}
			//trace("myModel.ball_arr[0].position.getX(): "+this.ball_arr[0].position.getX());
			this.time = 0;
			this.updateViews();
		}//end of initializePositions()
		
		public function startMotion():void{
			msTimer.start();  
			this.playing = true;
			this.starting = true;
		}//startMotion()
		
		public function stopMotion():void{
			msTimer.stop();
			this.playing = false;
			this.starting = false;
		}
		
		public function setTimeRate(rate:Number):void{
			this.timeRate = rate;
			trace("Model.timeRate: "+timeRate);
		}
		
		public function setElasticity(e:Number):void{
			this.e = e;
			trace("Model:elasticity: "+this.e);
		}
		
		public function stepForward(evt:TimerEvent):void{
			//trace("stepForward called.  elasticity is " + this.e);
			//need function without event argument
			this.singleStep();
			
		}//stepForward
		
		public function singleStep():void{
			var dt:Number;
			
			if(playing && !starting){
				//time-based aminimation
				var realDt = getTimer() - timeHolder;
				dt = realDt/1000;
			}else{
				dt = this.timeStep;
			}
			if(starting){
				this.starting = false;
			}
			
			this.time += dt;
			//trace("dt: "+dt);
			//trace("actualTimeStep"+actualTimeStep);
			for(var i:int = 0; i < this.nbrBalls; i++){
				var x:Number = this.ball_arr[i].position.getX();
				var y:Number = this.ball_arr[i].position.getY();
				var vX:Number = this.ball_arr[i].velocity.getX();
				var vY:Number = this.ball_arr[i].velocity.getY();
				var xLast = x;	//previous value of x before update
				var yLast = y;	//previous value of y before update
				x += vX*dt;
				y += vY*dt;
				this.ball_arr[i].position.setXY(x,y);
				//trace("i: "+i+"  x: "+this.ball_arr[i].position.getX());
				//trace("i: "+i+"  y: "+this.ball_arr[i].position.getY());
				
				//reflect at borders
				var radius:Number = this.ball_arr[i].radius;
				/*
				//if ball beyond border, then backup to previous position and reflect
				//this guarantees no penetration of border
				if((x+radius) > this.borderWidth || (x-radius)< 0){
					this.ball_arr[i].position.setXY(xLast,yLast);
					this.ball_arr[i].velocity.setX(-vX);
				}else if((y+radius) > this.borderHeight || (y-radius)< 0){
					this.ball_arr[i].position.setXY(xLast,yLast);
					this.ball_arr[i].velocity.setY(-vY);
				}
				*/

				if((x+radius) > this.borderWidth){
					this.ball_arr[i].velocity.setX(-Math.abs(vX));
				}else if((x-radius) < 0){
					this.ball_arr[i].velocity.setX(Math.abs(vX));
				}
				if((y+radius) > this.borderHeight){
					this.ball_arr[i].velocity.setY(-Math.abs(vY));
				}else if((y-radius) < 0){
					this.ball_arr[i].velocity.setY(Math.abs(vY));
				}
				
			}//for loop
			this.timeHolder = getTimer();
			
			this.detectCollision();
			this.frameCount += 1;
			if(this.frameCount == this.updateRate){
				//var interval:int = getTimer() - this.timeHolder;
				//trace("getTimer()"+ interval);
				this.frameCount = 0;
				this.updateViews();
				//this.timeHolder = getTimer();
			}
			this.lastTime = this.time;
		}//end of singleStep()
		
		//move forward one Frame = several steps
		public function singleFrame():void{
			for (var i:int = 0; i < this.updateRate; i++){
				this.singleStep();
			}
		}
		


		public function detectCollision():void{
			//var colliders_arr:Array = new Array(2);
			var N:int = this.nbrBalls;
			for (var i:int = 0; i < N; i++){
				for (var j:int = i+1; j < N; j++){
					var xi:Number = ball_arr[i].position.getX();
					var yi:Number = ball_arr[i].position.getY();
					var xj:Number = ball_arr[j].position.getX();
					var yj:Number = ball_arr[j].position.getY();
					var dist:Number = Math.sqrt((xj-xi)*(xj-xi)+(yj-yi)*(yj-yi));
					var distMin:Number = ball_arr[i].radius + ball_arr[j].radius;
					if(dist < distMin){
						//trace("elasticity before collision: "+this.e);
						this.collideBalls(i, j);
						this.colliders[i][j] = 1;  //ball have collided
					}else {
						this.colliders[i][j] = 0;	//balls not touching
					}
					
				}//for(j=..)
			}//for(i=..)
		}//detectCollision
		
		public function collideBalls(i:int, j:int):void{
			if(colliders[i][j] == 0){ //if balls not collided yet
				//trace("collision between i: " + i + " and j: " + j);
				//var totP:TwoVector = this.getTotalMomentum();
				//trace("Before:   pX: " + totP.getX() + "   pY:" + totP.getY());
				var x1:Number = ball_arr[i].position.getX();
				var x2:Number = ball_arr[j].position.getX();
				var y1:Number = ball_arr[i].position.getY();
				var y2:Number = ball_arr[j].position.getY();
				var delX:Number = x2 - x1;
				var delY:Number = y2 - y1;
				var d:Number = Math.sqrt(delX*delX + delY*delY);
				var v1x:Number = ball_arr[i].velocity.getX();
				var v2x:Number = ball_arr[j].velocity.getX();
				var v1y:Number = ball_arr[i].velocity.getY();
				var v2y:Number = ball_arr[j].velocity.getY();
				//normal and tangential components of initial velocities
				var v1n:Number = (1/d)*(v1x*delX + v1y*delY);
				var v2n:Number = (1/d)*(v2x*delX + v2y*delY);
				var v1t:Number = (1/d)*(-v1x*delY + v1y*delX);
				var v2t:Number = (1/d)*(-v2x*delY + v2y*delX);
				var m1:Number = ball_arr[i].mass;
				var m2:Number = ball_arr[j].mass;
				//normal components of velocities after collision (P for prime = after)
				trace("Model.e: "+this.e);
				var v1nP:Number = ((m1 - m2*this.e)*v1n + m2*(1+this.e)*v2n)/(m1 + m2);
				var v2nP:Number = this.e*(v1n - v2n) + v1nP;
				var v1xP = (1/d)*(v1nP*delX - v1t*delY);
				var v1yP = (1/d)*(v1nP*delY + v1t*delX);
				var v2xP = (1/d)*(v2nP*delX - v2t*delY);
				var v2yP = (1/d)*(v2nP*delY + v2t*delX);
				this.ball_arr[i].velocity.setXY(v1xP, v1yP);
				this.ball_arr[j].velocity.setXY(v2xP, v2yP);
				//backup to step just before penentration so balls cannot get stuck
				this.ball_arr[i].backupOneStep();
				this.ball_arr[j].backupOneStep();
				//var v1Mag:Number = Math.sqrt(v1n*v1n + v1t*v1t);
				//var v2Mag:Number = Math.sqrt(v2n*v2n + v2t*v2t);
				//var nHat:TwoVector = new TwoVector()
				
				//var totP:TwoVector = this.getTotalMomentum();
				//trace("After:   pX: " + totP.getX() + "   pY:" + totP.getY());
				//trace("KETot: "+this.getTotalKE());
				//trace("v1: "+v1Mag+"  v1:"+ball_arr[i].velocity.getLength());
				//trace("v2: "+v2Mag+"  v1:"+ball_arr[j].velocity.getLength());
			}
		}//collideBalls
		
		public function getTotalKE():Number{
			var KETot:Number = 0;
			for(var i:Number = 0; i < this.nbrBalls; i++){
				KETot += this.ball_arr[i].getKE();
			}
			return KETot;
		}//getKETotal
		
		public function getTotalMomentum():TwoVector{
			var pX:Number = 0;	//x-component of momentum
			var pY:Number = 0;	//y-component
			for(var i:Number = 0; i < this.nbrBalls; i++){
				pX += this.ball_arr[i].getMomentum().getX();
				pY += this.ball_arr[i].getMomentum().getY();
			}
			var totP:TwoVector = new TwoVector(pX, pY);
			return totP;
		}
		
		public function registerView(aView:Object):void{
			this.nbrViews += 1;
			this.view_arr.push(aView);
		}
		
		public function updateViews():void{
			for (var i:int = 0; i < nbrViews; i++){
				this.view_arr[i].update();
			}
		}
		
	}//end of class
}//end of package