package away3d.primitives
{
	import away3d.cameras.Camera3D;
	import away3d.containers.View3D;
	import away3d.core.base.Object3D;
	import away3d.core.base.Vertex;
	import away3d.core.draw.ScreenVertex;
	import away3d.core.math.MatrixAway3D;
	import away3d.core.math.Number3D;
	import away3d.events.ViewEvent;
	import away3d.materials.BitmapMaskMaterial;
	import away3d.materials.BitmapMaterial;
	import away3d.materials.CompositeMaterial;
	
	import flash.display.BitmapData;
	import flash.display.BitmapDataChannel;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.filters.BlurFilter;
	import flash.filters.DisplacementMapFilter;
	import flash.filters.DisplacementMapFilterMode;
	import flash.geom.ColorTransform;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	/* 
		This class is a work in progress...
		
		Experimenting reflections by injecting the view of a secondary camera into the material of a plane.
	*/
	public class ReflectivePlane extends Plane
	{	
		//---------------------------------------------------------------------------------------------------------
		// private fields
		//---------------------------------------------------------------------------------------------------------
		
		private var _zeroPoint:Point;
		private var _viewRect:Rectangle;
		private var _effectsBounds:Rectangle;
		private var _planeBounds:Rectangle;
		private var _identityColorTransform:ColorTransform;
		
		private var _camera:Camera3D;
		private var _view:View3D;
		private var _reflectionCamera:Camera3D;
		private var _reflectionView:View3D;
		private var _reflectionViewHolder:Sprite;
		
		private var _normal:Number3D;
		private var _reflectionMatrixAway3D:MatrixAway3D;
		private var _reflectionMatrix2D:Matrix;
		private var _plane2DRotation:Number = 0;
		
		private var _reflectionAlpha:Number = 1;
		private var _reflectionColorTransform:ColorTransform;
		private var _reflectionBlur:BlurFilter;
		
		private var _hideList:Array;
		private var _cameraOnFrontSide:Boolean;
		
		private var _materialBoundTolerance:Number = 0;
		private var _scaling:Number = 1;
		
		private var _backgroundImage:BitmapData;
		private var _smoothMaterials:Boolean;
		private var _backgroundColor:int = 0xFFFFFF;
		private var _backgroundAlpha:Number = 1;
		private var _redrawMatrix:Matrix;
		private var _compositeMaterial:CompositeMaterial;
		private var _backgroundMaterial:BitmapMaterial;
		private var _reflectionMaterial:BitmapMaskMaterial;
		private var _backgroundBmd:BitmapData;
		private var _reflectionBmd:BitmapData;
		
		private var _v0:Vertex;
		private var _v1:Vertex;
		private var _v2:Vertex;
		private var _v3:Vertex;
		private var _sv0:ScreenVertex;
		private var _sv1:ScreenVertex;
		private var _sv2:ScreenVertex;
		private var _sv3:ScreenVertex;
		
		private var _useBackgroundImageForDistortion:Boolean = true;
		private var _bumpMapDummyPlane:Plane;
		private var _bumpMapContainer:Sprite;
		private var _distortionStrength:Number;
		private var _bumpMapBmd:BitmapData;
		private var _displacementMap:DisplacementMapFilter;
		private var _distortionChannel:uint = BitmapDataChannel.RED;
		private var _distortionImage:BitmapData;
		
		//---------------------------------------------------------------------------------------------------------
		// setters & getters
		//---------------------------------------------------------------------------------------------------------
		
		//Virtual view.
		
			public function get reflectionView():View3D
			{
				return _reflectionView;
			}
		
		//Rendering.
		
			public function set reflectionQuality(value:Number):void
			{
				value = value <= 0 ? 0.01 : value;
				value = value > 1 ? 1 : value;
				
				_scaling = 1/value;
				
				_reflectionViewHolder.scaleX = -_scaling;
				_reflectionViewHolder.scaleY = _scaling;
			}
			public function get reflectionQuality():Number
			{
				return 1/_scaling;
			}
		
		//Materials.
		
			public function set boundTolerance(value:Number):void
			{
				_materialBoundTolerance = value;
			}
			public function get boundTolerance():Number
			{
				return _materialBoundTolerance;
			}
		
			public function set smoothMaterials(value:Boolean):void
			{
				_smoothMaterials = value;
				
				if(!_compositeMaterial)
					return;
					
				_backgroundMaterial.smooth = value;
				_reflectionMaterial.smooth = value;
			}
			public function get smoothMaterials():Boolean
			{
				return _smoothMaterials;
			}
		
			public function set backgroundImage(value:BitmapData):void
			{
				_backgroundImage = value;
				_backgroundColor = -1;
				
				buildMaterials();
			}
			public function get backgroundImage():BitmapData
			{
				return _backgroundBmd;
			}
		
			public function set backgroundColor(value:uint):void
			{
				_backgroundColor = value;
				_backgroundImage = null;
				
				if(_useBackgroundImageForDistortion)
					_displacementMap = null;
				
				buildMaterials();
			}
			public function get backgroundColor():uint
			{
				return _backgroundColor;
			}
		
			public function set backgroundAlpha(value:Number):void
			{
				_backgroundAlpha = value;
				_backgroundMaterial.alpha = value;
			}
			public function get backgroundAlpha():Number
			{
				return _backgroundAlpha;
			}
		
			public function get reflectionBmd():BitmapData
			{
				return _reflectionBmd;
			}
		
		//Optical adjustments.
		
			public function set reflectionBlur(value:Number):void
			{
				_reflectionBlur.blurX = value;
				_reflectionBlur.blurY = value;
			}
			public function get reflectionBlur():Number
			{
				return _reflectionBlur.blurX;
			}
			
			public function set reflectionBlurFilter(blur:BlurFilter):void
			{
				_reflectionBlur = blur;
			}
			public function get reflectionBlurFilter():BlurFilter
			{
				return _reflectionBlur;
			}
		
			public function set reflectionColorTransform(value:ColorTransform):void
			{
				_reflectionAlpha = value.alphaMultiplier;
				
				_reflectionColorTransform = value;
			}
			public function get reflectionColorTransform():ColorTransform
			{
				return _reflectionColorTransform;
			}
			
			public function set reflectionAlpha(value:Number):void
			{
				_reflectionAlpha = value;
				
				_reflectionColorTransform.alphaMultiplier = value;
			}
			public function get reflectionAlpha():Number
			{
				return _reflectionAlpha;
			}
		
		//Distortion.
		
			public function set distortionStrength(value:Number):void
			{
				_distortionStrength = value;
			}
			public function get distortionStrength():Number
			{
				return _distortionStrength;
			}
			
			public function set distortionChannel(value:uint):void
			{
				_distortionChannel = value;
			}
			public function get distortionChannel():uint
			{
				return _distortionChannel;
			}
			
			public function set distortionImage(value:BitmapData):void
			{
				_distortionImage = value;
				
				buildDummyPlane();
			}
			public function get distortionImage():BitmapData
			{
				return _distortionImage;
			}
			
			public function set useBackgroundImageForDistortion(value:Boolean):void
			{
				if(_useBackgroundImageForDistortion == value)
					return;
					
				_useBackgroundImageForDistortion = value;
				
				if(value)
				{
					_distortionImage = null;
					
					if(this._backgroundColor != -1)
						_displacementMap = null;
					else
						buildDummyPlane();
				}
				else
					buildDummyPlane();
			}
			public function get useBackgroundImageForDistortion():Boolean
			{
				return _useBackgroundImageForDistortion;
			}
			
		//---------------------------------------------------------------------------------------------------------
		// constructor and init
		//---------------------------------------------------------------------------------------------------------
		
		public function ReflectivePlane(view:View3D)
		{
			super();
			
			_view = view;
			_camera = view.camera;
			
			_zeroPoint = new Point();
			_planeBounds = new Rectangle();
			_reflectionColorTransform = new ColorTransform();
			_identityColorTransform = new ColorTransform();
			_reflectionBlur = new BlurFilter(0, 0, 1);
			
			_backgroundBmd = new BitmapData(1, 1, true, 0);
			_reflectionBmd = new BitmapData(1, 1, true, 0);
			_backgroundMaterial = new BitmapMaterial(_backgroundBmd);
			_reflectionMaterial = new BitmapMaskMaterial(_reflectionBmd);
			
			//Listens for scene change to trigger init().
			this.addOnSceneChange(sceneChangeHandler);
		}
		
		private function init():void
		{
			this.removeOnSceneChange(sceneChangeHandler);
			
			initSubScene();
			buildMaterials();
			calculatePlaneData();
			
			this.bothsides = true;
			
			//Listens for transform change to update plane data (normal and reflection matrixes) and dummy plane.
			this.addOnTransformChange(transformChangeHandler);
			this.addOnDimensionsChange(dimensionsChangeHandler);
		}
		
		private function initSubScene():void
		{
			//Imitatest the main camera.
			_reflectionCamera = new Camera3D();
			_reflectionCamera.name = "virtualReflectionCamera";
			_reflectionCamera.focus = _camera.focus;
			_reflectionCamera.zoom = _camera.zoom;
			
			_reflectionView = new View3D({scene:this.scene, camera:_reflectionCamera});
			_reflectionView.name = "virtualReflectionView";
			//TODO: There could be a big performance boost here if the reflection view's clipping adapted more
			//intelligently to what will be actually redrawn on the plane. For now, it mimics the clipping of the main view.
			_reflectionView.clipping = _view.clipping;
			_reflectionView.mouseEnabled = false;
			_reflectionView.mouseChildren = false;
			_viewRect = new Rectangle(0, 0, _view.clipping.maxX - _view.clipping.minX, _view.clipping.maxY - _view.clipping.minY);
			
			//The reflection is placed on a holder, so that bitmap scaling techniques can be used
			//to control redrawing quality.
			_reflectionViewHolder = new Sprite();
			_reflectionViewHolder.x = _view.x;
			_reflectionViewHolder.y = _view.y;
			_reflectionViewHolder.addChildAt(_reflectionView, 0);
			_reflectionViewHolder.visible = false;
			_view.parent.addChild(_reflectionViewHolder); //TODO: Do not add at cero, add underneath.
			
			//TODO: Remove on final version.
			_reflectionViewHolder.alpha = 0.25;
			
			//TODO: Change this type of autorendering to an override of updateMaterials().
			_view.addEventListener(ViewEvent.RENDER_COMPLETE, mainViewRenderCompleteHandler);
			
			//A lot of comments to what the dummy plane is for below.
			if(_distortionStrength != 0)
				buildDummyPlane();
		}
		
		//---------------------------------------------------------------------------------------------------------
		// private methods
		//---------------------------------------------------------------------------------------------------------
		
		private function renderReflection():void
		{
			//Determines wether the camera is looking at the front,
			//or the backside of the plane.
			_cameraOnFrontSide = onFrontSide(_camera.position, false);
			
			//If camera is on the back side and the back material is externally set by the user,
			//no need to draw any reflection or refraction.
			if(!_cameraOnFrontSide && back)
				return;
			
			positionReflectionCamera(); //Uses the reflection matrix to mirror the refl camera.
			getPlaneBounds(); //Redrawing of the refl view occurs only for the relevant area, so plane bounds must be known.	
			
			//Uses a dummy plane to get a perspectived image of the plane material or distortion image.
			//This image is used in a displacement map to distort the material. Might be overkill
			//but couldn't find another solution.
			updateBumpMapSource();
			
			hideObjectsOnSide(); //Hides all objects on same side of refl camera including the plane (avoid holograms).
			_reflectionView.render();
			restoreObjectsOnSide(); //Restores hiden objects.
			
			//Redraws the reflection into the plane.
			updateReflectionMaterial();
			
			this.material = _compositeMaterial;
		}
		
		//Redraws the refl view into the material of the plane.
		//Also uses scaling of the refl view to manage redrawing quality.
		//Also applies blur, colorTransform and distortion effects to the redrawn image.
		private function updateReflectionMaterial():void
		{
			if(_planeBounds.width < 1 || _planeBounds.height < 1)
				return;
				
			if(_planeBounds.width > 2880 || _planeBounds.height > 2880)
				return;
			
			_reflectionBmd = new BitmapData(_planeBounds.width/_scaling, _planeBounds.height/_scaling, true, 0x00000000);
			_redrawMatrix = new Matrix();
			_redrawMatrix.scale(1/_scaling, 1/_scaling);
			_redrawMatrix.translate(-_planeBounds.x/_scaling, -_planeBounds.y/_scaling);
			_reflectionBmd.draw(_reflectionViewHolder, _redrawMatrix);
			
			 if(_reflectionColorTransform != _identityColorTransform)
				_reflectionBmd.colorTransform(_effectsBounds, _reflectionColorTransform);
			
			if(_reflectionBlur)
			{
				if(_reflectionBlur.blurX != 0 || _reflectionBlur.blurY != 0)
					_reflectionBmd.applyFilter(_reflectionBmd, _effectsBounds, _zeroPoint, _reflectionBlur);
			}
			
			if(_distortionStrength != 0 && _displacementMap)
				_reflectionBmd.applyFilter(_reflectionBmd, _effectsBounds, _zeroPoint, _displacementMap); 
				
			//Reflection and refraction materials are BitmapMaskMaterials.
			//These are identical to BitmapMaterials except that they dont pass a transformation matrix to 
			//AbstractRenderSession. They use renderTriangleBitmapMask in this method, which is just as
			//renderTriangleBitmap, but ignores transformations, except offsets and scales.
			_reflectionMaterial.bitmap = _reflectionBmd;
			_reflectionMaterial.scaling = _scaling;
			_reflectionMaterial.offsetX = _planeBounds.x;
			_reflectionMaterial.offsetY = _planeBounds.y;
		}
		
		//Redraws the container of the dummy plane and uses this image to distort the
		//reflection and refraction materials when updated.
		private function updateBumpMapSource():void
		{	
			if(_distortionStrength == 0)
				return;
			
			if(_useBackgroundImageForDistortion && _backgroundImage == null)
				return;
			
			if(_bumpMapContainer.width < 1 || _bumpMapContainer.height < 1)
				return;
			
			if(_bumpMapContainer.width > 2880 || _bumpMapContainer.height > 2880)
				return;
				
			if(_planeBounds.width < 1 || _planeBounds.height < 1)
				return;
				
			if(_planeBounds.width > 2880 || _planeBounds.height > 2880)
				return;
				
			_bumpMapBmd = new BitmapData(_planeBounds.width/_scaling, _planeBounds.height/_scaling, false, 0x000000);
			_redrawMatrix = new Matrix();
			_redrawMatrix.scale(1/_scaling, 1/_scaling);
			_redrawMatrix.translate(-_planeBounds.x/_scaling, -_planeBounds.y/_scaling);
			_bumpMapBmd.draw(_bumpMapContainer, _redrawMatrix);
			
			var k:int = _cameraOnFrontSide ? 1 : -1;
			_displacementMap = new DisplacementMapFilter(_bumpMapBmd, _zeroPoint,
					_distortionChannel, _distortionChannel, k*_distortionStrength,
					k*_distortionStrength, DisplacementMapFilterMode.IGNORE, 0xFFFF0000); 
		}
		
		//Looks for the plane's edges and determines the screen bounds for it. This is used to
		//redraw the reflectionView and main view into the plane materials.
		//It also considers inflating the bounds in order to give redrawing a bit of flexibility.
		private function getPlaneBounds():void
		{
			//TODO: Perhaps there is a faster way to fetch the on-screen plane bounds.
			//The array sorting below might be slow.
			
			_v0 = new Vertex(this.minX, this.minY, this.minZ);
			_v1 = new Vertex(this.maxX, this.minY, this.minZ);
			_v2 = new Vertex(this.maxX, this.maxY, this.maxZ);
			_v3 = new Vertex(this.minX, this.minY, this.maxZ);
			
			_sv0 = _view.camera.screen(this, _v0) || new ScreenVertex();
			_sv1 = _view.camera.screen(this, _v1) || new ScreenVertex();
			_sv2 = _view.camera.screen(this, _v2) || new ScreenVertex();
			_sv3 = _view.camera.screen(this, _v3) || new ScreenVertex();
			
			var xS:Array = [{x:_sv0.x}, {x:_sv1.x}, {x:_sv2.x}, {x:_sv3.x}];
			var yS:Array = [{y:_sv0.y}, {y:_sv1.y}, {y:_sv2.y}, {y:_sv3.y}];
			xS.sortOn("x", Array.NUMERIC);
			yS.sortOn("y", Array.NUMERIC);
			
			var minX:Number = Math.max(-_viewRect.width/2, xS[0].x);
			var minY:Number = Math.max(-_viewRect.height/2, yS[0].y);
			var maxX:Number = Math.min(_viewRect.width/2, xS[xS.length-1].x);
			var maxY:Number = Math.min(_viewRect.height/2, yS[yS.length-1].y);
			
			_planeBounds.x = minX;
			_planeBounds.y = minY;
			_planeBounds.width = maxX - minX;
			_planeBounds.height = maxY - minY;
			
			_planeBounds.inflate(_materialBoundTolerance, _materialBoundTolerance);
			
			_effectsBounds = new Rectangle(0, 0, _planeBounds.width, _planeBounds.height);
		}
		
		//Hides all scene objects that are on the same side of the refl camera according to the
		//reflection plane. Without this, the rendering of the reflectionView produces hologram reflections.
		private function hideObjectsOnSide():void
		{
			_hideList = [];
			
			var _scene_children:Array  = this.scene.children;
			for each(var obj:Object3D in _scene_children)
			{
				if(obj.visible && !onFrontSide(obj.position))
				{
					obj.visible = false;
					_hideList.push(obj);
				}
			}
		}
		private function restoreObjectsOnSide():void
		{
			for each(var obj:Object3D in _hideList)
				obj.visible = true;
		}
		
		//Determines if an object is on the front side of the plane.
		private function onFrontSide(point:Number3D, allowCameraEval:Boolean = true):Boolean
		{
			var delta:Number3D = new Number3D();
			delta.sub(point, this.position);
			var proj:Number = delta.dot(_normal);
			
			if(allowCameraEval && !_cameraOnFrontSide)
				proj *= -1;
				
			return proj > 0;
		}
		
		//Reconstructs the composite material of the plane.
		//2 materials are used: 1 for the reflection and 1 for the background.
		private function buildMaterials():void
		{
			if(_backgroundImage)
			{
				_backgroundBmd = _backgroundImage; 
				_backgroundMaterial = new BitmapMaterial(_backgroundBmd);
			}
			else
				_backgroundBmd = new BitmapData(800, 600, false, _backgroundColor);
			_backgroundMaterial = new BitmapMaterial(_backgroundBmd);
			
			_compositeMaterial = new CompositeMaterial();
			_compositeMaterial.addMaterial(_backgroundMaterial);
			_compositeMaterial.addMaterial(_reflectionMaterial);
				
			_backgroundMaterial.smooth = _smoothMaterials;
			_reflectionMaterial.smooth = _smoothMaterials;
				
			buildDummyPlane();
			
			backgroundAlpha = _backgroundAlpha;
		}
				
		//Applyes the 3D reflection matrix to position the refl camera as a mirror of the main camera,
		//according to the plane.
		private function positionReflectionCamera():void
		{
			_reflectionCamera.position = reflectPoint(_camera.position);
			_reflectionCamera.lookAt(reflectPoint(_camera.lookingAtTarget));
		}
		
		//Applies the 3D reflection matrix to any point and reflects it according to the plane.
		private function reflectPoint(point:Number3D):Number3D
		{
			var reflectedPoint:Number3D = new Number3D();
			reflectedPoint.sub(point, this.position);
			reflectedPoint.transform(reflectedPoint, _reflectionMatrixAway3D);
			reflectedPoint.add(reflectedPoint, this.position);
			
			return reflectedPoint;
		}
		
		//This is called on plane init() and each time it moves.
		//Calculates the global normal of the plane and the reflection matrixes for it.
		private function calculatePlaneData():void
		{
			var p0:Number3D = getVertexGlobalPosition(this.vertices[0]);
			var p1:Number3D = getVertexGlobalPosition(this.vertices[1]);
			var p2:Number3D = getVertexGlobalPosition(this.vertices[2]);
			
			var d0:Number3D = new Number3D();
			d0.sub(p1, p0);
			var d1:Number3D = new Number3D();
			d1.sub(p2, p0);
			
			_normal = new Number3D();
			_normal.cross(d0, d1);
			_normal.normalize();
			
			var a:Number = _normal.x;
			var b:Number = _normal.y;
			var c:Number = _normal.z;
			
			//This matrix is used to reflect any point in the scene according to the plane position
			//and orientation.
			_reflectionMatrixAway3D = new MatrixAway3D();
			_reflectionMatrixAway3D.sxx = 1 - 2*a*a;
			_reflectionMatrixAway3D.sxy = -2*a*b;
			_reflectionMatrixAway3D.sxz = -2*a*c;
			_reflectionMatrixAway3D.syx = -2*a*b;
			_reflectionMatrixAway3D.syy = 1 - 2*b*b;
			_reflectionMatrixAway3D.syz = -2*b*c;
			_reflectionMatrixAway3D.szx = -2*a*c;
			_reflectionMatrixAway3D.szy = -2*b*c;
			_reflectionMatrixAway3D.szz = 1 - 2*c*c;
			
			//This matrix is used to flip what the refl camera see's so that
			//it emulates the correct position of virtual objects in the refl view and hence
			//the reflection effect.
			_plane2DRotation = -this.rotationZ*Math.PI/180;
			_reflectionMatrix2D = _reflectionView.transform.matrix;
			_reflectionMatrix2D.a = Math.cos(2*_plane2DRotation);
			_reflectionMatrix2D.b = Math.sin(2*_plane2DRotation);
			_reflectionMatrix2D.c = Math.sin(2*_plane2DRotation);
			_reflectionMatrix2D.d = -Math.cos(2*_plane2DRotation);
			_reflectionView.transform.matrix = _reflectionMatrix2D;
		}
		
		//TODO: This is used to obtain global normal, maybe its not needed.
		//Quicker way?
		private function getVertexGlobalPosition(vertex:Vertex):Number3D
		{
			var m:MatrixAway3D = new MatrixAway3D();
	        m.tx = vertex.x;
	        m.ty = vertex.y;
	        m.tz = vertex.z;
	        m.multiply(this.transform, m);
			return new Number3D(m.tx, m.ty, m.tz);
		}
		
		//This is the only way I found to obtain a perspectived image of the plane's background material
		//for use in the distortion displacement map. It seems overkill and there must be a better way.
		//Besides, I dont like having a separate object in the scene for this, which the user has no
		//control of.
		private function buildDummyPlane():void
		{
			if(!this.scene)
				return;
			
			if(this.scene.children.indexOf(_bumpMapDummyPlane) != -1)
				this.scene.removeChild(_bumpMapDummyPlane);
			
			_bumpMapDummyPlane = new Plane();
			_bumpMapDummyPlane.name = "reflectionDistortionDummyPlane";
			_bumpMapDummyPlane.segmentsH = this.segmentsH;
			_bumpMapDummyPlane.segmentsW = this.segmentsW;
			_bumpMapDummyPlane.ownCanvas = true;
			_bumpMapDummyPlane.bothsides = true;
			
			updatePlaneDummyDimensions();
			updatePlaneDummyPosition();
			
			if(_distortionImage)
				_bumpMapDummyPlane.material = new BitmapMaterial(_distortionImage);
			else
			{	
				_bumpMapDummyPlane.material = _backgroundMaterial;
				BitmapMaterial(_bumpMapDummyPlane.material).alpha = 1;
			}
			
			//_bumpMapDummyPlane.pushback = true;
			_bumpMapDummyPlane.alpha = 0;
			
			scene.addChild(_bumpMapDummyPlane);
			_bumpMapContainer = _bumpMapDummyPlane.ownSession.getContainer(_view) as Sprite;
		}
		private function updatePlaneDummyDimensions():void
		{
			if(!_bumpMapDummyPlane)
				return;
			
			_bumpMapDummyPlane.width = this.width;
			_bumpMapDummyPlane.height = this.height;
		}
		private function updatePlaneDummyPosition():void
		{
			if(!_bumpMapDummyPlane)
				return;
				
			_bumpMapDummyPlane.position = this.position;
			_bumpMapDummyPlane.rotationX = this.rotationX;
			_bumpMapDummyPlane.rotationY = this.rotationY;
			_bumpMapDummyPlane.rotationZ = this.rotationZ;
		}
		
		//---------------------------------------------------------------------------------------------------------
		// event handlers
		//---------------------------------------------------------------------------------------------------------
		
		private function sceneChangeHandler(event:Event):void
		{
			init();
		}
		
		private function transformChangeHandler(event:Event):void
		{
			calculatePlaneData();
			updatePlaneDummyPosition();
		}
		
		private function dimensionsChangeHandler(event:Event):void
		{
			updatePlaneDummyDimensions();
		}
		
		private function mainViewRenderCompleteHandler(event:Event):void
		{
			renderReflection();
		}
	}
}