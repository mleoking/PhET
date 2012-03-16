//Linear Fit
class LinearFit{
	private var model:Model; 		//main model
	private var points_arr:Array;
	public var LFit_arr:Array;		//[a, b] y = a + b*x
	private var reducedChiSquare:Number;
	private var x0:Number;			//sum of 1/sigma^2
	private var x1:Number;			//sum of x/sigma^2
	private var x2:Number;			//sum of x^2/sigma^2
	private var x0y1:Number;		//sum y/sigma^2
	private var x1y1:Number;		//sum x*y/sigma^2
	
	function LinearFit(model:Model){
		this.model = model;
		this.LFit_arr = new Array(2);
	}
	
	function makeFit():Array{
		this.points_arr = this.model.points_arr;
		var N:Number = this.points_arr.length;
		this.x0 = 0; this.x1 = 0; this.x2 = 0; 
		this.x0y1 = 0; this.x1y1 = 0; 
		var delta = 0; var a = 0; var b = 0; 
		for(var i:Number = 0; i < N; i++){
			var delY:Number = this.points_arr[i].deltaY;
			var delYSq:Number = delY*delY;
			var x:Number = this.points_arr[i].xPos;
			var y:Number = this.points_arr[i].yPos;
			this.x0 += 1/delYSq;
			this.x1 += x/delYSq;
			this.x2 += x*x/delYSq;
			this.x0y1 += y/delYSq;
			this.x1y1 += x*y/delYSq;
		}//end of for loop
		delta = x0*x2 - x1*x1;
		
		a = (1/delta)*(x0y1*x2 - x1*x1y1);
		
		b = (1/delta)*(x0*x1y1 - x1*x0y1);
		
		this.LFit_arr = [a, b]
		return this.LFit_arr;
	}

	
}//end of class