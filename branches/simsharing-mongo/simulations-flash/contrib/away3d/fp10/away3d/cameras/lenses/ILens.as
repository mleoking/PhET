package away3d.cameras.lenses
{
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.geom.*;
	import away3d.core.math.*;
	
	public interface ILens
	{
		function get near():Number;
		
		function get far():Number;
		
		function setView(val:View3D):void
		
		function getFrustum(node:Object3D, viewTransform:MatrixAway3D):Frustum;
		
		function getFOV():Number;
		
		function getZoom():Number;
        
        function getPerspective(screenZ:Number):Number;
        
       /**
        * Projects the vertices to the screen space of the view.
        */
        function project(viewTransform:MatrixAway3D, vertices:Array, screenVertices:Array):void;
	}
}