class BodyClip {
	//Body with independently movable position and velocity vector
	//The body or object is the disk; the velocity vector is the arrow

	var clip_mc:MovieClip;
	//var view:InitialView;
	var model:Model;
	var nLabel:Number;	//index labeling the body: body 1, body 2, 
	private var updateNeeded:Boolean;
	var bodyX:Number;  //x-coordinate of clip in screen coords
	var bodyY:Number;
	var pos:Vector;		//position vector in world coords
	var vel:Vector;
	var mass:Number;
	//var stageW:Number;
	//var stageH:Number;
	//var bodyMass:Number;	//mass of body, units?, currently unused
	var arrowLength:Number;	//length of velocity arrow in pixels
	var arrowAngle:Number;  //angle in degrees CCW from rightward horizontal
	var arrowX:Number;		//arrow length x-component in pixels
	var arrowY:Number;		//arrow length y-comp in screen coords
	var pi:Number = Math.PI;
	
	function BodyClip(nLabel:Number, name:String, target:MovieClip, model:Model){
		this.nLabel = nLabel;
		this.model = model;
		this.model.registerView(this);
		var i:Number = this.nLabel - 1;
		this.bodyX = 0;
		this.bodyY = 0;
		this.pos = this.model.getInitPosOfBodyI(i);
		this.vel = this.model.getInitVelOfBodyI(i);
		this.mass = this.model.getMassOfBodyI(i);
		this.clip_mc = target.attachMovie("bodyWithArrow", name, target.getNextHighestDepth());
		this.updateNeeded = true;
		this.makeClipDraggable();
	}//end of constructor
	
	private function makeClipDraggable():Void{
		var obj:BodyClip = this;
		var clip:MovieClip = this.clip_mc;
		var stageH = Util.STAGEH;
		var stageW = Util.STAGEW;
		var originX = Util.ORIGINSCREENX;
		var originY = Util.ORIGINSCREENY;
		clip.disk_mc.onPress = function(){
			//var clip:MovieClip = this._parent;
			clip.startDrag(false); //ltrb
			obj.updateNeeded = false;
			clip.onMouseMove = function(){
				obj.bodyX = clip._x;
				obj.bodyY = clip._y;
				obj.pos.x = obj.bodyX - originX;
				obj.pos.y = -(obj.bodyY - originY);
				obj.updateModelPosition();
				updateAfterEvent();
			}
		}
		clip.disk_mc.onRelease = function(){
			obj.updateModelPosition();
			clip.stopDrag();
			obj.updateNeeded = true;
			clip.onMouseMove = undefined;
		}

		clip.disk_mc.onReleaseOutside = clip.disk_mc.onRelease;
		
		clip.headHandle_mc.onPress = function() {
			this.startDrag(false);
			this.onMouseMove = function() {
				var tipX = this._x;
				var tipY = -this._y;
				obj.vel.x = tipX;
				obj.vel.y = tipY;
				obj.updateModelVelocity();
				updateAfterEvent();
			};
		};

		clip.headHandle_mc.onRelease = function(){
			obj.updateModelVelocity();
			this.stopDrag();
			this.onMouseMove = undefined;
		}
		clip.headHandle_mc.onReleaseOutside = clip.headHandle_mc.onRelease;
	}//end of makeClipDraggable()
	
	
	function updateModelPosition():Void{
		//trace("called");
		this.model.setInitPosOfBodyI(this.nLabel - 1, this.pos);
	}

	
	function updateModelVelocity():Void{
		this.model.setInitVelOfBodyI(this.nLabel - 1, this.vel);
	}

	//set diameter of disk in pixels
	function setBodySize(pixDia:Number):Void{
		this.clip_mc.disk_mc.diskBody_mc._width = this.clip_mc.disk_mc.diskBody_mc._height = pixDia;
	}
	
	function update():Void{
		if(this.model.integrationOn){
			this.clip_mc._visible = false;
		}else {
			this.clip_mc._visible = true;
		}
		if(this.clip_mc._visible && this.updateNeeded){
			var i = this.nLabel - 1;
			//trace("update() called on body:"+i);
			this.pos = this.model.getInitPosOfBodyI(i);
			this.vel = this.model.getInitVelOfBodyI(i);
			this.mass = this.model.getMassOfBodyI(i);
			this.setBodySize(2.5*Math.pow(mass,1/3) + 6);
			this.bodyX = this.pos.x + Util.ORIGINSCREENX;
			this.bodyY = -this.pos.y + Util.ORIGINSCREENY;
			this.clip_mc._x = this.bodyX;
			this.clip_mc._y = this.bodyY;
			var x:Number = vel.x;
			var y:Number = -vel.y;
			this.arrowX = x;
			this.arrowY = y;
			this.arrowLength = Math.sqrt(x*x + y*y);
			this.arrowAngle = Math.atan2(y,x)*180/this.pi;
			this.clip_mc.arrowBody_mc._rotation = 0;
			this.clip_mc.arrowBody_mc._width = arrowLength;
			this.clip_mc.arrowBody_mc._height = 10 + arrowLength/50;
			this.clip_mc.arrowBody_mc._rotation = arrowAngle;
			this.clip_mc.headHandle_mc._x = x;
			this.clip_mc.headHandle_mc._y = y;
		}
	}
	
}//end of class

 
