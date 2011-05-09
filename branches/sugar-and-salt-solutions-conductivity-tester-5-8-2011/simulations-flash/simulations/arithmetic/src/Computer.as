class Computer{
	private var gridSize:Number;  //edge length of matrix: 6, 9, or 12
	private var level:Number;  //level of play: 1, 2, or 3 
	private var operatorState:Number; //1 = addition, 2 = subtraction, 3 = multiplication
	
	//constructor
	function Computer():Void{
		setLevel(1);
		
	}//end of constructor
	
	public function setLevel(level:Number):Void{
		this.level = level;
		switch(this.level){
		case 1:
			this.gridSize = 6;
			break;
		case 2:
			this.gridSize = 9;
			break;
		case 3:
			this.gridSize = 12;
			break;
		default:
			trace("Error: level number must be 1, 2, or 3");
			break;
		}//end of switch
	}//end of setLevel()
	
	public function setOperatorState(operatorState:Number):Void{  //addition, subtraction, or multiplication
		if (operatorState == 1 || operatorState == 2 ||operatorState == 3){
			this.operatorState = operatorState;
		} else {trace("Error: operatorState must be 1, 2, or 3.");}
	}//end of setOperatorState
	
	public function computeResult(x:Number, y:Number):Number{
		if(x%1 != 0 || y%1 != 0){
			trace("Error: operand must be interger");
		}
		if(x > this.gridSize || y > this.gridSize){
			trace("Error: operand must be smaller than gridSize");
		}
		var result:Number;
		switch(this.operatorState){
			case 1:
				result = x + y;
				break;
			case 2:
				result = x - y;
				break;
			case 3:
				result = x*y;
				break;
			default:
				trace("Error: operatorState must be 1, 2, or 3);
				break;
		}//end of switch
		return result;
	}//end of computeResult
	
	
}//end of class