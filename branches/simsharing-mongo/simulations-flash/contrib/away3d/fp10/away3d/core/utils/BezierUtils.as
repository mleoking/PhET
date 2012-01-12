package away3d.core.utils
{
	import away3d.core.base.DrawingCommand;
	import away3d.core.base.Face;
	import away3d.core.base.Vertex;
	
	import flash.display.Graphics;
	
	public class BezierUtils
	{
		public function BezierUtils()
		{
			
		}

		static public function splitDrawingCommand(curve:DrawingCommand):Array
		{
			if(curve.type == DrawingCommand.MOVE)
				return [curve];
			
			var pMidOnCurve:Vertex;
			var pMidStartControl:Vertex;
			var pMidControlEnd:Vertex;
			var command1:DrawingCommand;
			var command2:DrawingCommand;
			
			var type:String = curve.type;
			if(type == DrawingCommand.CURVE)
			{
				pMidOnCurve = BezierUtils.getCoordinatesAt(0.5, curve);
				pMidStartControl = BezierUtils.getMidPoint(curve.pStart, curve.pControl);
				pMidControlEnd = BezierUtils.getMidPoint(curve.pControl, curve.pEnd);
				
				command1 = new DrawingCommand(type, curve.pStart, pMidStartControl, pMidOnCurve);
				command2 = new DrawingCommand(type, pMidOnCurve, pMidControlEnd, curve.pEnd);
			}
			else
			{
				pMidOnCurve = BezierUtils.getMidPoint(curve.pStart, curve.pEnd);
				
				command1 = new DrawingCommand(type, curve.pStart, null, pMidOnCurve);
				command2 = new DrawingCommand(type, pMidOnCurve, null, curve.pEnd);
			}
			
			return [command1, command2];
		}
		
		static public function getMidPoint(p1:Vertex, p2:Vertex):Vertex
		{
			var mX:Number = (p1.x + p2.x)/2;
			var mY:Number = (p1.y + p2.y)/2;
			
			return new Vertex(mX, mY, 0);
		}
		
		static public function getCoordinatesAt(t:Number, curve:DrawingCommand):Vertex
		{
			var tSqr:Number = t*t;
			var invT:Number = 1 - t;
			var invTSqr:Number = invT*invT;
			
			var pX:Number = invTSqr*curve.pStart.x + 2*invT*t*curve.pControl.x + tSqr*curve.pEnd.x;
			var pY:Number = invTSqr*curve.pStart.y + 2*invT*t*curve.pControl.y + tSqr*curve.pEnd.y;
			
			return new Vertex(pX, pY, 0);
		}
		
		static public function tracePoint2D(graphics:Graphics, point:Vertex):void
		{
			graphics.lineStyle();
			graphics.beginFill(0xFFFFFF, 0.5);
			graphics.drawCircle(point.x, -point.y, 5);
			graphics.endFill();
		}
		
		static public function traceCurve2D(graphics:Graphics, curve:DrawingCommand):void
		{
			graphics.lineStyle(1, 0x00FF00, 1);
			
			graphics.moveTo(curve.pStart.x, -curve.pStart.y);
			
			if(curve.type == DrawingCommand.LINE)
				graphics.lineTo(curve.pEnd.x, -curve.pEnd.y);
			else if(curve.type == DrawingCommand.CURVE)
				graphics.curveTo(curve.pControl.x, -curve.pControl.y, curve.pEnd.x, -curve.pEnd.y);
		}
	}
}