//M.Dubson May, 2008

class FitMaker{
	private var model:Object; 
	private var points_arr:Array;
	var m:Number;		//nbr of rows = min of nbr of Points and orderOfFit+1; m = 2, 3, ...
	var A:Array;  		//m x n = m x (m+1) augmented matrix array
	var maxM:Number; 	//maximum number of rows in matrix = max orderOfFit +1
	//var T_arr:Array;	//matrix in reduced echelon form
	var solution_arr:Array; //solution is array of coefficients a, b, c,..
	
	function FitMaker(model:Model){
		this.model = model;
		//create augmented matrix of maximum possible size
		maxM = this.model.maxOrderOfFit + 1; 	//m = nbr of rows in matrix = orderOfFit+1 (max O.O.F = 4, quartic fit with x^4 term)
		this.A = new Array(maxM);	//m rows in m x (m+1) augmented matrix
		this.solution_arr = new Array(maxM);  
		for (var i:Number = 0; i < maxM; i++){
			A[i] = new Array(maxM + 1);
		}
		//this.A = augmentedMatrix;
		//this.m = this.A.length;
		//this.T_arr = new Array(this.m);
		//this.solution_arr = new Array(this.m);
	}//end of constructor
	
	function getFit():Array{
		this.makeAugmentedMatrix();
		this.reduceMatrix();
		this.solveReducedMatrix();
		//trace("this.m: "+ this.m + "   fitMaker.solution_arr: "+this.solution_arr );
		return this.solution_arr;
	}
	function zeroMatrix():Void{
		for(var row:Number = 0; row < maxM; row++){
				for(var col:Number = 0; col < maxM+1; col++){
					A[row][col] = 0;
				}
		}
	}
	
	function makeAugmentedMatrix():Void{
		this.points_arr = this.model.points_arr;
		var N:Number = this.points_arr.length;
		this.m = Math.min(this.model.orderOfFit + 1, N);   //m = nbr of rows in matrix = orderOfFit + 1
		this.zeroMatrix();
		for(var i:Number = 0; i < N; i++){
			var delY:Number = this.points_arr[i].deltaY;
			var delYSq:Number = delY*delY;
			var x:Number = this.points_arr[i].xPos;
			var y:Number = this.points_arr[i].yPos;
			for(var row:Number = 0; row < m; row++){
				for(var col:Number = 0; col < m; col++){
					A[row][col] += Math.pow(x,row+col)/delYSq;
				}//end of for col loop
				A[row][m] += Math.pow(x,row)*y/delYSq;  //last inhomogeneous column
			}//end of for row loop
		}//end of for N loop
	}//end of makeAugmentedMatrix()
	
	function reduceMatrix():Void{
		//var A:Array = this.A
		for(var varCol:Number = 0; varCol < this.m-1; varCol++){   //varCol = variable column = x, y  (if variables are x, y, and z)
			//trace("variable column: "+varCol);
			
			//pivot algorithm -- supposed to improve stability
			/*
			var maxPivotRow:Number = varCol;
			for(var i:Number = varCol+1; i < this.m; i++){
				if(Math.abs(A[i][varCol]) > Math.abs(A[maxPivotRow][varCol])){
					maxPivotRow = i;
				}
			}
			//trace("maxPivotRow: "+maxPivotRow + "   varCol: "+varCol);
			//trace("Before matrix: ");
			if(maxPivotRow != varCol){
				this.displayMatrix();
				var tempRow:Array = new Array(this.m+1);
				for(var j:Number = 0; j < this.m+1; j++){
					tempRow[j] = A[varCol][j];
					A[varCol][j] = A[maxPivotRow][j];
					A[maxPivotRow][j] = tempRow[j];
				}
				//trace("After matrix: ");
				this.displayMatrix();
			}
			*/
			//end of pivot algorithm -- supposed to improve stability
			
			for(var i:Number = varCol+1; i < this.m; i++){  //loop thru rows below , starting with 2nd row: i = 1
				var coeff:Number = A[i][varCol]/A[varCol][varCol];
				for(var j:Number = 0; j < this.m+1; j++){//loop thru all columns
					//trace("row" + i + "  column: "+j  + "  A_before: " + A[i][j]);
					//trace("coefficient: " + coeff);
					A[i][j] = A[i][j] - coeff*A[varCol][j];
					//trace("A_after: " + A[i][j]);
					//trace(" ");
				}//end of for j
			}//end of for i
		}//end of for varColumn loop
		this.solveReducedMatrix();
	}
	
	function solveReducedMatrix():Void{
		var endCol:Number = this.m;  //end column = inhomogeneous terms (first col = 0)
		//var A:Array = this.A;
		for(var row:Number = this.m - 1; row >=0; row--){
			A[row][endCol] = A[row][endCol]/A[row][row];
			A[row][row] = 1;
			this.solution_arr[row] = A[row][endCol];
			for(var rowB:Number = 0; rowB < row; rowB++){
				A[rowB][endCol] -= A[rowB][row]*this.solution_arr[row];
				A[rowB][row] = 0;
			}
		}
		for(var i:Number = this.m; i < this.maxM; i++){
			this.solution_arr[i] = 0;
		}
		//trace("fitMaker.solution_arr: "+this.solution_arr);
		//return this.solution_arr
	}//end of solve solveReducedMatrix()
	
	function displayMatrix():Void{
		for (var i = 0; i < this.m ; i++){
			var row_str:String = "";
			for (var j = 0; j < this.m+1; j++){
				row_str += this.A[i][j] + "      ";
			}//end of for j
			trace(row_str);
		}//end of for i
	}//end of displayMatrix
}//end of class