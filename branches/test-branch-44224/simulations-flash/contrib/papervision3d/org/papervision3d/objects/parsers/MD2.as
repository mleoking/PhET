package org.papervision3d.objects.parsers {	import org.papervision3d.Papervision3D;	
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	import flash.utils.Endian;
	
	import org.papervision3d.core.animation.IAnimatable;	
	import org.papervision3d.core.animation.IAnimationProvider;
	import org.papervision3d.core.animation.clip.AnimationClip3D;		import org.papervision3d.core.controller.IControllerProvider;	import org.papervision3d.core.controller.IObjectController;		import org.papervision3d.core.animation.key.LinearCurveKey3D;	
	import org.papervision3d.core.animation.curve.Curve3D;	
	import org.papervision3d.core.animation.channel.geometry.VerticesChannel3D;	
	import org.papervision3d.core.animation.channel.Channel3D;	
	import org.papervision3d.core.controller.AnimationController;	
	import org.papervision3d.core.geom.TriangleMesh3D;
	import org.papervision3d.core.geom.renderables.*;
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.events.FileLoadEvent;
	import org.papervision3d.objects.DisplayObject3D;

	/**
	 * Loads Quake 2 MD2 file with animation!
	 * </p>Please feel free to use, but please mention me!</p>
	 * 
	 * @author Philippe Ajoux (philippe.ajoux@gmail.com) adapted by Tim Knip(tim.knip at gmail.com).
	 * @website www.d3s.net
	 * @version 04.11.07:11:56
	 */
	public class MD2 extends TriangleMesh3D implements IAnimatable, IAnimationProvider, IControllerProvider
	{
		/**
		 * 
		 */
		protected var _animation : AnimationController;
		
		/**
		 * 
		 */
		protected var _controllers : Array;
		
		/**
		 * Variables used in the loading of the file
		 */
		protected var file:String;
		protected var loader:URLLoader;
		protected var loadScale:Number;
		
		/**
		 * MD2 Header data
		 * These are all the variables found in the md2_header_t
		 * C style struct that starts every MD2 file.
		 */
		protected var ident:int, version:int;
		protected var skinwidth:int, skinheight:int;
		protected var framesize:int;
		protected var num_skins:int, num_vertices:int, num_st:int;
		protected var num_tris:int, num_glcmds:int, num_frames:int;
		protected var offset_skins:int, offset_st:int, offset_tris:int;
		protected var offset_frames:int, offset_glcmds:int, offset_end:int;
		protected var _fps:int;
		protected var _autoPlay:Boolean;
		
		/**
		 * Constructor.
		 * 
		 * @param	autoPlay	Whether to start the _animation automatically.
		 */
		public function MD2(autoPlay:Boolean=true):void
		{
			super(null, new Array(), new Array());
			
			_autoPlay = autoPlay;
		}
		
		/**		 * Gets / sets the animation controller.		 * 		 * @see org.papervision3d.core.controller.AnimationController		 */		public function set animation(value : AnimationController) : void		{			_animation = value;		}		
		public function get animation() : AnimationController
		{
			return _animation;
		}
				/**		 * Gets / sets all controlllers.		 * 		 * @return	Array of controllers.		 * 		 * @see org.papervision3d.core.controller.IObjectController		 * @see org.papervision3d.core.controller.AnimationController		 * @see org.papervision3d.core.controller.MorphController		 * @see org.papervision3d.core.controller.SkinController		 */		public function set controllers(value : Array) : void		{			_controllers = value;		}				public function get controllers() : Array		{			return _controllers;			}		
		/**
		 * Pauses the animation.
		 */ 
		public function pause():void
		{
			if(_animation)
			{
				_animation.pause();
			}
		}
		
		/**
		 * Plays the animation.
		 * 
		 * @param 	clip	Clip to play. Default is "all"
		 * @param 	loop	Whether the animation should loop. Default is true.
		 */ 
		public function play(clip:String="all", loop:Boolean=true):void
		{
			if(_animation)
			{
				_animation.play(clip, loop);
			}
		}
		
		/**
		 * Resumes a paused animation.
		 * 
		 * @param loop 	Whether the animation should loop. Defaults is true.
		 */ 
		public function resume(loop : Boolean=true):void
		{
			if(_animation)
			{
				_animation.resume(loop);
			}
		}
		
		/**
		 * Stops the animation.
		 */ 
		public function stop():void
		{
			if(_animation)
			{
				_animation.stop();
			}
		}
		
		/**
		 * Whether the animation is playing. This property is read-only.
		 * 
		 * @return True when playing.
		 */
		public function get playing() : Boolean
		{
			return _animation ? _animation.playing : false;
		}
		
		/**
		 * Loads the MD2.
		 * 
		 * @param	asset	URL or ByteArray
		 * @param	material	The material for the MD2
		 * @param	fps		Frames per second
		 * @param	scale	Scale
		 */
		public function load(asset:*, material:MaterialObject3D = null, fps:int = 6, scale:Number = 1):void
		{
			this.loadScale = scale;
			this._fps = fps;
			this.visible = false;
			this.material = material || MaterialObject3D.DEFAULT;
			
			if(asset is ByteArray)
			{
				this.file = "";
				parse(asset as ByteArray);
			}
			else
			{
				this.file = String(asset);
				
				loader = new URLLoader();
				loader.dataFormat = URLLoaderDataFormat.BINARY;
				loader.addEventListener(Event.COMPLETE, loadCompleteHandler);
				loader.addEventListener(ProgressEvent.PROGRESS, loadProgressHandler);
				
				try
				{
		            loader.load(new URLRequest(this.file));
				}
				catch(e:Error)
				{
					PaperLogger.error("error in loading MD2 file (" + this.file + ")");
				}
			}
		}
		
		/**
		 * Project.
		 * 
		 * @param	parent
		 * @param	renderSessionData
		 * 
		 * @return	Number
		 */ 
		public override function project(parent:DisplayObject3D, renderSessionData:RenderSessionData):Number
		{
			// update controllers			if(_controllers)			{
				for each(var controller:IObjectController in _controllers)
				{
					controller.update();
				}
			}			
			return super.project(parent, renderSessionData);
		}
		
		/**
		 * <p>Parses the MD2 file. This is actually pretty straight forward.
		 * Only complicated parts (bit convoluded) are the frame loading
		 * and "metaface" loading. Hey, it works, use it =)</p>
		 * 
		 * @param	data	A ByteArray
		 */
		protected function parse(data:ByteArray):void
		{
			var i:int, uvs:Array = new Array();
			var metaface:Object;
			
			_animation = new AnimationController();
			
			_controllers = new Array();
			_controllers.push(this._animation);
			
			data.endian = Endian.LITTLE_ENDIAN;
			
			// Read the header and make sure it is valid MD2 file
			readMd2Header(data);
			if (ident != 844121161 || version != 8)
				throw new Error("error loading MD2 file (" + file + "): Not a valid MD2 file/bad version");
				
			//---Vertice setup
			// be sure to allocate memory for the vertices to the object
			for (i = 0; i < num_vertices; i++)
				geometry.vertices.push(new Vertex3D());

			//---UV coordinates
			data.position = offset_st;
			for (i = 0; i < num_st; i++)
			{
				var uv:NumberUV = new NumberUV(data.readShort() / skinwidth, data.readShort() / skinheight);
				//uv.u = 1 - uv.u;
				uv.v = 1 - uv.v;
				uvs.push(uv);
			}

			//---Frame _animation data
			data.position = offset_frames;
			readFrames(data);
			
			//---Faces
			// make sure to push the faces with allocated vertices to the object!
			data.position = offset_tris;
			for (i = 0; i < num_tris; i++)
			{
				metaface = {a: data.readUnsignedShort(), b: data.readUnsignedShort(), c: data.readUnsignedShort(),
					        ta: data.readUnsignedShort(), tb: data.readUnsignedShort(), tc: data.readUnsignedShort()};
				
				var v0:Vertex3D = geometry.vertices[metaface["a"]];
				var v1:Vertex3D = geometry.vertices[metaface["b"]];
				var v2:Vertex3D = geometry.vertices[metaface["c"]];
				
				var uv0:NumberUV = uvs[metaface["ta"]];
				var uv1:NumberUV = uvs[metaface["tb"]];
				var uv2:NumberUV = uvs[metaface["tc"]];

				geometry.faces.push(new Triangle3D(this, [v2, v1, v0], material, [uv2, uv1, uv0]));
			}
			
			geometry.ready = true;
	
			visible = true;
						
			PaperLogger.info("Parsed MD2: " + file + "\n vertices:" + 
							  geometry.vertices.length + "\n texture vertices:" + uvs.length +
							  "\n faces:" + geometry.faces.length + "\n frames: " + num_frames);

			dispatchEvent(new FileLoadEvent(FileLoadEvent.LOAD_COMPLETE, this.file));
			dispatchEvent(new FileLoadEvent(FileLoadEvent.ANIMATIONS_COMPLETE, this.file));
			
			if(_autoPlay)
			{
				this._animation.play();
			}
		}
		
		/**
		 * Reads in all the frames
		 */
		protected function readFrames(data:ByteArray):void
		{
			var sx:Number, sy:Number, sz:Number;
			var tx:Number, ty:Number, tz:Number;
			var i:int, j:int, char:int;
			var duration:Number = 1 / _fps;
			
			var curves : Array = new Array(num_vertices);
			var curName:String = "all";
			var clip : AnimationClip3D;
			var clipPos:int = 0;
			
			for (i = 0; i < num_frames; i++)
			{				
				sx = data.readFloat();
				sy = data.readFloat();
				sz = data.readFloat();
				
				tx = data.readFloat();
				ty = data.readFloat();
				tz = data.readFloat();
				
				var frameName:String = "";
				
				for (j = 0; j < 16; j++)
					if ((char = data.readUnsignedByte()) != 0)
						frameName += String.fromCharCode(char);
				
				var shortName:String = frameName.replace(/\d+/, "");
				
				if(curName != shortName)
				{
					if(clip)
					{
						clip.endTime = (i-1) * duration;
						this._animation.addClip(clip);
					}
					
					clip = new AnimationClip3D(shortName, i * duration);
					curName = shortName;
					clipPos = 0;
				}

				// Note, the extra data.position++ in the for loop is there 
				// to skip over a byte that holds the "vertex normal index"
				for (j = 0; j < num_vertices; j++, data.position++)
				{
					var v:Vertex3D = new Vertex3D(
						((sx * data.readUnsignedByte()) + tx) * loadScale, 
						((sy * data.readUnsignedByte()) + ty) * loadScale,
						((sz * data.readUnsignedByte()) + tz) * loadScale);
					
					v.x = Papervision3D .useRIGHTHANDED  ? v.x : - v.x;
					
					if(!curves[j])
					{
						curves[j] = new Array(3);	
						curves[j][0] = new Curve3D();
						curves[j][1] = new Curve3D();
						curves[j][2] = new Curve3D();
					}
					
					curves[j][0].addKey(new LinearCurveKey3D(i * duration, v.x));
					curves[j][1].addKey(new LinearCurveKey3D(i * duration, v.y));
					curves[j][2].addKey(new LinearCurveKey3D(i * duration, v.z));
					
					if( i == 1 )
					{
						this.geometry.vertices[j].x = v.x;
						this.geometry.vertices[j].y = v.y;
						this.geometry.vertices[j].z = v.z;
					}
				}
				
				clipPos++;
			}

			var channel : VerticesChannel3D = new VerticesChannel3D(this.geometry);
			
			for(i = 0; i < num_vertices; i++)
			{
				var update : Boolean = (i == num_vertices - 1);
				
				channel.addCurve(curves[i][0], update);	
				channel.addCurve(curves[i][1], update);
				channel.addCurve(curves[i][2], update);
			}
			
			_animation.addChannel(channel);
			if(clip)
			{
				clip.endTime = _animation.endTime;
				_animation.addClip(clip);
			}
		}

		/**
		 * Reads in all that MD2 Header data that is declared as private variables.
		 * I know its a lot, and it looks ugly, but only way to do it in Flash
		 */
		protected function readMd2Header(data:ByteArray):void
		{
			ident = data.readInt();
			version = data.readInt();
			skinwidth = data.readInt();
			skinheight = data.readInt();
			framesize = data.readInt();
			num_skins = data.readInt();
			num_vertices = data.readInt();
			num_st = data.readInt();
			num_tris = data.readInt();
			num_glcmds = data.readInt();
			num_frames = data.readInt();
			offset_skins = data.readInt();
			offset_st = data.readInt();
			offset_tris = data.readInt();
			offset_frames = data.readInt();
			offset_glcmds = data.readInt();
			offset_end = data.readInt();
		}

		/**
		 * 
		 */ 
		protected function loadCompleteHandler(event:Event):void
		{
			var loader:URLLoader = event.target as URLLoader;
			var data:ByteArray = loader.data;
			parse(data);
		}
		
		/**
		 * 
		 * @param	event
		 * @return
		 */
		protected function loadProgressHandler( event:ProgressEvent ):void
		{
			dispatchEvent(event);
		}
	}
}
