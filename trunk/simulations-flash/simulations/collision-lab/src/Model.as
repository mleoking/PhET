//The Model for Collision Lab
package{
	import flash.events.*;
	import flash.utils.*;
	
	public class Model{
		var nbrBalls:int;  		//nbr of interacting balls
		var ball_arr:Array;		//array of balls
		var pos:Array;			//array of initial positions of balls
		var vel:Array;			//array of initial velocities of balls
		var borderOn:Boolean;	//if true, balls elastically reflect from border 
		var borderWidth:int;	//length of horizontal border in meters
		var borderHeight:int;	//length of vertical border in meters
		var elasticity:Number;	//0 to 1: 0 = perfectly inelastic, 1 = perfectly elastic
		var time:Number;		//simulation time in seconds = real time
		var lastTime:Number;	//time of previous step
		var timeStep:Number;	//time step in seconds
		var msTimer:Timer;		//millisecond timer
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
			this.nbrBalls = 3;
			this.ball_arr = new Array(nbrBalls);
			this.initializeBalls();
			this.time = 0;
			this.timeStep = 0.005;
			this.updateRate = 5;
			this.frameCount = 0;
			
			this.msTimer = new Timer(this.timeStep*1000, 2000);
			msTimer.addEventListener(TimerEvent.TIMER, stepForward);
			//this.realTimer = new Timer(1000);  //argument 1000 is irrelevant
			this.view_arr = new Array(0);
			this.nbrViews = 0;
			
		}//end of constructor
		
		public function initializeBalls():void{
			this.pos = new Array(3);
			this.vel = new Array(3);
			pos[0] = new TwoVector(0.2,0.2);
			pos[1] = new TwoVector(0.5,0.5);
			pos[2] = new TwoVector(1.8,1);
			vel[0] = new TwoVector(0.7,0.8);
			vel[1] = new TwoVector(0.12,0.55);
			vel[2] = new TwoVector(4,-0.1);
			
			for (var i = 0; i < this.nbrBalls; i++){
				//new Ball(mass, position, velocity);
				this.ball_arr[i] = new Ball(1.0, pos[i], vel[i]);
			}
			
			//initialize colliders array
			var N:int = this.nbrBalls;
			this.colliders = new Array(N);
			for (var i:int = 0; i < N; i++){
				this.colliders[i] = new Array(N);
			}
			for (var i:int = 0; i < N; i++){
				for (var j:int = 0; j < N; j++){
					this.colliders[i][j] = 0;  //0 if not colliding
				}
			}
				
			//trace("ball1 speed:  "+this.ball_arr[1].velocity.getLength());
		}//end of initializeBalls()
		
		public function startMotion():void{
			msTimer.start();  
		}//startMotion()
		
		public function stopMotion():void{
			msTimer.stop();
		}
		
		public function stepForward(evt:TimerEvent):void{
			//trace("stepForward called.");
			var dt:Number = this.timeStep;
			//var realDt = getTimer() - timeHolder;
			//var dt:Number = realDt/1000;
			this.time += dt;
			//trace("dt: "+dt);
			//trace("actualTimeStep"+actualTimeStep);
			for(var i:int = 0; i < this.nbrBalls; i++){
				var x:Number = this.ball_arr[i].position.getX();
				var y:Number = this.ball_arr[i].position.getY();
				var vX:Number = this.ball_arr[i].velocity.getX();
				var vY:Number = this.ball_arr[i].velocity.getY();
				x += vX*dt;
				y += vY*dt;
				this.ball_arr[i].position.setXY(x,y);
				//trace("i: "+i+"  x: "+this.ball_arr[i].position.getX());
				//trace("i: "+i+"  y: "+this.ball_arr[i].position.getY());
				
				//reflect at borders
				var radius:Number = this.ball_arr[i].radius;
				if((x+radius) > this.borderWidth){
					this.ball_arr[i].velocity.setX(-Math.abs(vX));
					/*
					var delX:Number = (x+radius) - this.borderWidth;  //penetration into wall
					var tc:Number = this.time - delX/vX;  //time of first contact with wall
					//reverse x-motion to previous time 
					x -= vX*dt;
					//now go forward to contact wall
					x += vX*(tc - this.lastTime);
					this.ball_arr[i].velocity.setX(-vX)
					x += -vX*(this.time - tc);
					*/
				}else if((x-radius) < 0){
					this.ball_arr[i].velocity.setX(Math.abs(vX));
					/*
					var dX:Number = x-radius;
					var tc:Number = this.time - delX/vX; 
					x -= vX*dt;
					x += vX*(tc - this.lastTime);
					this.ball_arr[i].velocity.setX(-vX)
					x += -vX*(this.time - tc);
					*/
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
			
		}//stepForward
		
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
						this.collideBalls(i, j);
						this.colliders[i][j] = 1;
					}else {
						this.colliders[i][j] = 0;
					}
					
				}//for(j=..)
			}//for(i=..)
		}//detectCollision
		
		public function collideBalls(i:int, j:int):void{
			if(colliders[i][j] == 0){
				trace("collision between i: "+i+" and j: "+j);
				//this.ball_arr[i].reverseVelocity();
				//this.ball_arr[j].reverseVelocity();
			}
		}//collideBalls
		
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