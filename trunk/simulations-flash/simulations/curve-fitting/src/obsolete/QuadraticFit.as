//Quadratic Fit.  Model variable
class QuadraticFit{
	private var model:Object; 		//main model
	private var points_arr:Array;
	public var QFit_arr:Array;		//[a, b, c] y = a + b*x + c*x^2
	private var reducedChiSquare:Number;
	private var x0:Number;			//sum of 1/sigma^2
	private var x1:Number;			//sum of x/sigma^2
	private var x2:Number;			//sum of x^2/sigma^2
	private var x3:Number;			//sum of x^3/sigma^2
	private var x4:Number;			//sum of x^4/sigma^2
	private var x0y1:Number;		//sum y/sigma^2
	private var x1y1:Number;		//sum x*y/sigma^2
	private var x2y1:Number;		//sum x^2*y/sigma^2
	
	
	function QuadraticFit(model:Object){
		this.model = model;
		this.QFit_arr = new Array(3);
		//this.x0 = 0; this.x1 = 0; this.x2 = 0; this.x3 = 0; this.x4 = 0;
		//this.x0y1 = 0; this.x1y1 = 0; this.x2y1 = 0;
	}
	
	function makeFit():Array{
		this.points_arr = this.model.points_arr;
		var N:Number = this.points_arr.length;
		//this.x0 = this.points_arr.length;
		this.x0 = 0;
		this.x1 = 0; this.x2 = 0; this.x3 = 0; this.x4 = 0;
		this.x0y1 = 0; this.x1y1 = 0; this.x2y1 = 0;
		var delta = 0; var a = 0; var b = 0; var c = 0;
		for(var i:Number = 0; i < N; i++){
			var delY:Number = this.points_arr[i].deltaY;
			var delYSq:Number = delY*delY;
			var x:Number = this.points_arr[i].xPos;
			var y:Number = this.points_arr[i].yPos;
			this.x0 += 1/delYSq;
			this.x1 += x/delYSq;
			this.x2 += x*x/delYSq;
			this.x3 += x*x*x/delYSq;
			this.x4 += x*x*x*x/delYSq;
			this.x0y1 += y/delYSq;
			this.x1y1 += x*y/delYSq;
			this.x2y1 += x*x*y/delYSq;
		}//end of for loop
		delta = x0*(x2*x4 - x3*x3) - x1*(x1*x4 - x3*x2) + x2*(x1*x3 - x2*x2);
		
		a = (1/delta)*(x0y1*(x2*x4 - x3*x3) - x1y1*(x1*x4 - x3*x2) + x2y1*(x1*x3 - x2*x2));
		
		b = (1/delta)*(-x0y1*(x1*x4 - x3*x2) + x1y1*(x0*x4 - x2*x2) - x2y1*(x0*x3 - x1*x2));
		
		c = (1/delta)*(x0y1*(x1*x3 - x2*x2) - x1y1*(x0*x3 - x1*x2) + x2y1*(x0*x2 - x1*x1));
		this.QFit_arr = [a, b, c]
		return this.QFit_arr;
	}
	
}//end of class