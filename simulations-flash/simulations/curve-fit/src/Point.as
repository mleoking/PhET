//xy point on graph
class Point{
	private var xPos:Number;
	private var yPos:Number;
	var clip_mc:MovieClip;
	var clipInitializer:PointClipInitializer;
	private var ptIndex:Number; //integer index which labels the point, never changes
	private var positionInArray:Number; //location of point in myModel.points_arr, changes when old points are deleted
	private var deltaY:Number; //1-sigma uncertainty in y
	private var scale:Number;
	var myModel:Model;
	
	//constructor
	function Point(x:Number, y:Number, clip:MovieClip, model:Model){
		this.xPos = x;
		this.yPos = y;
		this.clip_mc = clip;
		this.myModel = model;
		var delY:Number = clip.errorBar_mc.bottom_mc._y;
		this.scale = this.myModel.mainView.scale;
		this.deltaY = this.scale*delY;  	
		this.clipInitializer = new PointClipInitializer(this);
		this.clipInitializer.initializePointClip();
		//this.myModel.makeLinearFit();
		//this.setPositionInArray();
	}
	
	function setPositionInArray(index:Number):Void{
		this.positionInArray = index;
	}
	
	function getPositionInArray():Number{
		return this.positionInArray;
	}
	
	function setXY(x:Number, y:Number):Void{
		this.xPos = x;
		this.yPos = y;
		this.myModel.makeFit();
		//this.myModel.updateFit(this.positionInArray);
	}
	
	function setDeltaY(delY:Number):Void{
		this.deltaY = delY;
		this.myModel.makeFit();
	}
	
	function setVerticalBarVisibility(tOrF:Boolean):Void{
		this.clip_mc.errorBar_mc.middle_mc._visible = tOrF;
		if(tOrF){
			this.clip_mc.errorBar_mc.top_mc._alpha = 100;
			this.clip_mc.errorBar_mc.bottom_mc._alpha = 100;
		}else{
			this.clip_mc.errorBar_mc.top_mc._alpha = 20;
			this.clip_mc.errorBar_mc.bottom_mc._alpha = 20;
		}
	}

}//end of class