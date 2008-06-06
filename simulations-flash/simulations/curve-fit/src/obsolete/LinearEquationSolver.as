//M.Dubson May, 2008

class LinearEquationSolver{
	//m x n = m x (m+1) augmented array
	var m:Number;				//order of fit = nbr of rows; m = 2, 3, ...
	var A_arr:Array;  	//augmented matrix array
	var T_arr:Array;	//matrix in reduced echelon form
	var solution_arr:Array; //solution is array of coefficients a, b, c,..
	
	function LinearEquationSolver(augmentedMatrix:Array){
		this.A_arr = augmentedMatrix;
		this.m = this.A_arr.length;
		this.T_arr = new Array(this.m);
		this.solution_arr = new Array(this.m);
	}//end of constructor
	
	function reduceMatrix():Void{
		var A:Array = this.A_arr
		for(var varCol:Number = 0; varCol < this.m-1; varCol++){   //varCol = variable column = x, y  (if variables are x, y, and z)
			//trace("variable column: "+varCol);
			/*
			//pivot algorithm -- supposed to improve stability
			var maxPivotRow:Number = varCol;
			for(var i:Number = varCol+1; i < this.m; i++){
				if(Math.abs(A[i][varCol]) > Math.abs(A[maxPivotRow][varCol])){
					maxPivotRow = i;
				}
			}
			trace("maxPivotRow: "+maxPivotRow + "   varCol: "+varCol);
			trace("Before matrix: ");
			if(maxPivotRow != varCol){
				this.displayMatrix();
				var tempRow:Array = new Array(this.m+1);
				for(var j:Number = 0; j < this.m+1; j++){
					tempRow[j] = A[varCol][j];
					A[varCol][j] = A[maxPivotRow][j];
					A[maxPivotRow][j] = tempRow[j];
				}
				trace("After matrix: ");
				this.displayMatrix();
			}
			*/
			
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
	
	function solveReducedMatrix():Array{
		var endCol:Number = this.m;  //end column = inhomogeneous terms (first col = 0)
		var A:Array = this.A_arr;
		for(var row:Number = this.m - 1; row >=0; row--){
			A[row][endCol] = A[row][endCol]/A[row][row];
			A[row][row] = 1;
			this.solution_arr[row] = A[row][endCol];
			for(var rowB:Number = 0; rowB < row; rowB++){
				A[rowB][endCol] -= A[rowB][row]*this.solution_arr[row];
				A[rowB][row] = 0;
			}
		}
		return this.solution_arr
	}//end of solve solveReducedMatrix()
	
	function displayMatrix():Void{
		for (var i = 0; i < this.m ; i++){
			var row_str:String = "";
			for (var j = 0; j < this.m+1; j++){
				row_str += this.A_arr[i][j] + "      ";
			}//end of for j
			trace(row_str);
		}//end of for i
	}//end of displayMatrix
}//end of class