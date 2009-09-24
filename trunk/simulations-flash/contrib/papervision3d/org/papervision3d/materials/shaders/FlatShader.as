package org.papervision3d.materials.shaders
{
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.geom.Matrix;
	
	import org.papervision3d.core.geom.renderables.Triangle3D;
	import org.papervision3d.core.math.Matrix3D;
	import org.papervision3d.core.proto.LightObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.shader.ShaderObjectData;
	import org.papervision3d.materials.utils.LightMaps;
	
	/**
	 * @Author Ralph Hauwert
	 */
	public class FlatShader extends LightShader implements IShader, ILightShader
	{
		
		private static var triMatrix:Matrix = new Matrix();
		private static var currentGraphics:Graphics;
		private static var zAngle:Number;
		private static var currentColor:int;
		
		private static var vx:Number;
		private static var vy:Number;
		private static var vz:Number;
		
		public var lightColor:int;
		public var ambientColor:int;
		public var specularLevel:int;
		private var _colors:Array;
		private var _colorRamp:BitmapData;
		
		public function FlatShader(light:LightObject3D, lightColor:int = 0xFFFFFF, ambientColor:int = 0x000000, specularLevel:int=0 )
		{
			super();
			this.light = light;
			this.lightColor = lightColor;
			this.ambientColor = ambientColor;
			this.specularLevel = specularLevel;
			this._colors = LightMaps.getFlatMapArray(lightColor, ambientColor, specularLevel );
			this._colorRamp = LightMaps.getFlatMap(lightColor, ambientColor, specularLevel );
		}
		
		/**
		 * Localized vars
		 */
		private static var zd:Number;
		private static var lightMatrix:Matrix3D;
		private static var sod:ShaderObjectData;
		
		override public function renderLayer(triangle:Triangle3D, renderSessionData:RenderSessionData, sod:ShaderObjectData):void
		{
			lightMatrix = Matrix3D(sod.lightMatrices[this]);
			zd = triangle.faceNormal.x * lightMatrix.n31 + triangle.faceNormal.y * lightMatrix.n32 + triangle.faceNormal.z * lightMatrix.n33;
			if(zd < 0){
				zd = 0;
			};
			zd = zd*0xFF;
			triMatrix = sod.uvMatrices[triangle] ? sod.uvMatrices[triangle] : sod.getUVMatrixForTriangle(triangle);
			currentColor = _colors[int(zd)];
			
			currentGraphics = Sprite(layers[sod.object]).graphics;
			currentGraphics.beginFill(currentColor,1);
			currentGraphics.moveTo(triMatrix.tx, triMatrix.ty);
			currentGraphics.lineTo(triMatrix.a+triMatrix.tx, triMatrix.b+triMatrix.ty);
			currentGraphics.lineTo(triMatrix.c+triMatrix.tx, triMatrix.d+triMatrix.ty);
			currentGraphics.lineTo(triMatrix.tx, triMatrix.ty);
			currentGraphics.endFill();
		}
		
		/**
		 *Localized var
		 */
		public static var scaleMatrix:Matrix = new Matrix();
		override public function renderTri(triangle:Triangle3D, renderSessionData:RenderSessionData, sod:ShaderObjectData,bmp:BitmapData):void
		{
			lightMatrix = Matrix3D(sod.lightMatrices[this]);
			if(lightMatrix){
				zd = triangle.faceNormal.x * lightMatrix.n31 + triangle.faceNormal.y * lightMatrix.n32 + triangle.faceNormal.z * lightMatrix.n33;
				if(zd < 0){zd = 0;};
				scaleMatrix.a = bmp.width;
				scaleMatrix.d = bmp.height;
				scaleMatrix.tx =-int(zd*0xFF)*bmp.width;
				bmp.draw(_colorRamp, scaleMatrix,null,layerBlendMode, bmp.rect, false);
			}
		}
	}
}