class Image {
	private var myLens:Lens;
	private var myLightSource:LightSource;
	public var xPos: Number;	//x-coord of image; origin at screen center
	public var yPos: Number;	//y-coord of image; origin at screen center
	private var dObject: Number; //x-distance from lens to object
	private var dImage: Number; //x-distance from lens to image
	
	function Image(myLens:Lens, myLightSource:LightSource){
		this.myLens = myLens;
		this.myLightSource = myLightSource;
		setPosition();
	}
	
	
	public function setPosition():Void{
		//var del = 0.00001;	//prevents infinity
		var f = this.myLens.getFocalLength();
		var h_object = this.myLightSource.yPos - this.myLens.yPos;
		this.dObject = this.myLens.xPos - this.myLightSource.xPos;

			this.dImage = this.dObject*f/(this.dObject - f);
			var d_image_y = -(h_object*f)/(this.dObject - f);
			this.xPos = this.dImage + this.myLens.xPos;
			this.yPos = d_image_y + this.myLens.yPos;

	}
	
	public function getXPos():Number{
		return this.xPos;
	}
	public function getYPos():Number{
		return this.yPos;
	}
	
	public function getDObject():Number{
		return this.dObject;
	}
	public function getDImage():Number{
		return this.dImage;
	}
		
}//end of class