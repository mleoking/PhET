//Class Lens
class Lens{
	private var n:Number;  //index of refraction of glass
	private var r:Number;	//radius of curvature of one side
	private var f:Number;	//focal length of lens
	public var xPos:Number; //x position of lens center
	public var yPos:Number; //y position of lens center
	
	//constructor
	function Lens(n,r,xPos,yPos){
		//should check that n > 1, r > 0, etc
		if(n <= 1){trace("Error: index n must be greater than 1.");}
		if(r <= 0){trace("Possible Error: radius r is negative.");}
		this.n = n;
		this.r = r;
		this.xPos = xPos;
		this.yPos = yPos;
		this.f = r/(2*(n-1));
	}
	
	private function setFocalLength():Boolean{
		this.f = this.r/(2*(this.n - 1));
		//trace(f);
		return true;
	}
	public function getFocalLength():Number{
		return this.f;
	}
	public function setIndex(n:Number):Void{
		this.n = n;
		setFocalLength();
	}
	public function getIndex():Number{
		return this.n;
	}

	public function setRadius(r:Number):Void{
		this.r = r;
		setFocalLength();
	}
	public function getRadius():Number{
		return this.r;
	}
	
	//xPos, yPos are public so no getters or setters
}//end of class Lens