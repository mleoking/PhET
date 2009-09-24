package org.papervision3d.objects.parsers {	import org.papervision3d.core.animation.channel.Channel3D;	
	import flash.events.Event;
 	import flash.events.IOErrorEvent;
 	import flash.events.ProgressEvent;
 	import flash.system.Capabilities;
 	import flash.utils.ByteArray;
 	import flash.utils.Dictionary;
 	
 	import org.ascollada.ASCollada;
 	import org.ascollada.core.*;
 	import org.ascollada.fx.*;
 	import org.ascollada.io.DaeReader;
 	import org.ascollada.namespaces.*;
 	import org.ascollada.types.*;
 	
 	import org.papervision3d.Papervision3D;
 	import org.papervision3d.core.animation.IAnimatable;	
	import org.papervision3d.core.animation.IAnimationProvider;
	import org.papervision3d.core.animation.channel.controller.MorphWeightChannel3D;
 	import org.papervision3d.core.animation.channel.geometry.VertexChannel3D;
 	import org.papervision3d.core.animation.channel.transform.*;
 	import org.papervision3d.core.animation.clip.AnimationClip3D;	
 	import org.papervision3d.core.animation.key.LinearCurveKey3D;	
	import org.papervision3d.core.animation.curve.Curve3D;
 	import org.papervision3d.core.controller.*;
 	import org.papervision3d.core.geom.*;
 	import org.papervision3d.core.geom.renderables.*;
 	import org.papervision3d.core.log.PaperLogger;
 	import org.papervision3d.core.material.AbstractLightShadeMaterial;
 	import org.papervision3d.core.math.*;
 	import org.papervision3d.core.proto.*;
 	import org.papervision3d.core.render.data.RenderSessionData;
 	import org.papervision3d.events.FileLoadEvent;
 	import org.papervision3d.materials.*;
 	import org.papervision3d.materials.shaders.ShadedMaterial;
 	import org.papervision3d.materials.special.*;
 	import org.papervision3d.materials.utils.*;
 	import org.papervision3d.objects.DisplayObject3D;
 	import org.papervision3d.objects.special.Skin3D;
	
	/**
	 * The DAE class represents a parsed COLLADA 1.4.1 file.
	 * 
	 * <p>Typical use case:</p>
	 * <pre>
	 * var dae :DAE = new DAE();
	 * 
	 * dae.addEventListener(FileLoadEvent.LOAD_COMPLETE, myOnLoadCompleteHandler);
	 * 
	 * dae.load( "path/to/collada" );
	 * </pre>
	 * 
	 * <p>Its possible to pass you own materials via a MaterialsList:</p>
	 * <pre>
	 * var materials :MaterialsList = new MaterialsList();
	 * 
	 * materials.addMaterial( new ColorMaterial(), "MyMaterial" );
	 * 
	 * var dae :DAE = new DAE();
	 * 
	 * dae.addEventListener(FileLoadEvent.LOAD_COMPLETE, myOnLoadCompleteHandler);
	 * 
	 * dae.load( "path/to/collada", materials );
	 * </pre>
	 * <p>Note that in above case you need the material names as specified in your 3D modelling application.
	 * The material names can also be found by looking at the COLLADA file: find the xml elements 
	 * &lt;instance_material symbol="MyMaterialName" target="SomeTarget" /&gt;. The material names are specified
	 * by the symbol attribute of this element.</p>
	 * 
	 * <p>A COLLADA file can contain animations. Animations take a long time to parse, hence 
	 * animations are parsed asynchroniously. Listen for FileLoadEvent.ANIMATIONS_COMPLETE and 
	 * FileLoadEvent.ANIMATIONS_PROGRESS:</p>
	 * <pre>
	 * var dae :DAE = new DAE();
	 * 
	 * dae.addEventListener(FileLoadEvent.LOAD_COMPLETE, myOnLoadCompleteHandler);
	 * dae.addEventListener(FileLoadEvent.ANIMATIONS_COMPLETE, myOnAnimationsCompleteHandler);
	 * dae.addEventListener(FileLoadEvent.ANIMATIONS_PROGRESS, myOnAnimationsProgressHandler);
	 * 
	 * dae.load( "path/to/collada" );
	 * </pre>
	 * 
	 * @author Tim Knip
	 */ 
	public class DAE extends DisplayObject3D implements IAnimatable, IAnimationProvider, IControllerProvider
	{
		use namespace collada;
		
		public static const ROOTNODE_NAME:String = "COLLADA_Scene";

		/** Default line color for splines. */
		public static var DEFAULT_LINE_COLOR:uint = 0xffff00;
		
		/** Default line width for splines */
		public static var DEFAULT_LINE_WIDTH:Number = 0;
		
		/** change this to a value > 0 if you're DAE is picking the wrong coordinates */
		public var forceCoordSet : int = -1;
		
		/** The loaded XML. */
		public var COLLADA:XML;
	
		/** The filename - if applicable. */
		public var filename:String;
		
		/** The filetitle - if applicable. */
		public var fileTitle:String;
		
		/** Base url. */
		public var baseUrl:String;
		
		/** The COLLADA parser. */
		public var parser:DaeReader;
		
		/** The DaeDocument. @see org.ascollada.core.DaeDocument */
		public var document:DaeDocument;
		
		/** */
		protected var _animation : AnimationController;
		
		/** */
		protected var _colladaID:Dictionary;
		
		/** */
		protected var _colladaSID:Dictionary;
		
		/** */
		protected var _colladaIDToObject:Object;
		
		/** */
		protected var _colladaSIDToObject:Object;
		
		/** */
		protected var _objectToNode:Object;
		
		/** */
		protected var _rootNode:DisplayObject3D;
		
		/** */
		protected var _autoPlay:Boolean;
		
		/** */
		protected var _rightHanded:Boolean;
		
		/** */
		protected var _controllers:Array; 
		
		protected var _playerType:String;
		
		protected var _loop:Boolean = false;

		protected var _fileSearchPaths : Array;
		
		/**
		 * Constructor.
		 * 
		 * @param	autoPlay	Whether to start the _animation automatically.
		 * @param	name	Optional name for the DAE.
		 */ 
		public function DAE(autoPlay:Boolean=true, name:String=null, loop:Boolean=false)
		{
			super(name);
			_autoPlay = autoPlay;
			_rightHanded = Papervision3D.useRIGHTHANDED;
			_loop = loop;
			_playerType = Capabilities.playerType;
		}
		
		/**
		 * Gets / sets the animation controller.
		 * 
		 * @see org.papervision3d.core.controller.AnimationController
		 */
		public function set animation(value : AnimationController) : void
		{
			_animation = value;
		}
		
		public function get animation() : AnimationController
		{
			return _animation;
		}

		/**
		 * Gets all controlllers.
		 * 
		 * @return	Array of controllers.
		 * 
		 * @see org.papervision3d.core.controller.IObjectController
		 * @see org.papervision3d.core.controller.AnimationController
		 * @see org.papervision3d.core.controller.MorphController
		 * @see org.papervision3d.core.controller.SkinController
		 */
		public function get controllers() : Array
		{
			return _controllers;	
		}
		
		public function set controllers(value : Array) : void
		{
			_controllers = value;
		}
		
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
		 * 
		 */
		public function addFileSearchPath(path : String) : void
		{
			_fileSearchPaths = _fileSearchPaths || new Array();
			if(_fileSearchPaths.indexOf(path) == -1)
			{
				// remove trailing slash
				path = path.charAt(path.length-1) == "/" ? path.substr(0, path.length-1) : path;
				
				_fileSearchPaths.push(path);
			}
		}

		/**
		 * 
		 */
		override public function clone() : DisplayObject3D 
		{
			var object : DisplayObject3D = super.clone();
			
			var dae : DAE = new DAE(_autoPlay, this.name + _numClone, _loop);
			
			dae.addChild(object);
			
			var controller : IObjectController;
			var i : int, j : int;
			
			dae.controllers = new Array();
			
			// Clone controllers
			for(i = 0; i < _controllers.length; i++)
			{
				controller = _controllers[i];	
				if(controller is SkinController)
				{
					var skin : SkinController = SkinController(controller);
					var skinTarget : DisplayObject3D = dae.getChildByName(skin.target.name+"_"+_numClone, true);
					var clonedSkin : SkinController = skinTarget ? new SkinController(skinTarget as Skin3D) : null;
					
					if(!clonedSkin) continue;
					
					clonedSkin.bindShapeMatrix = Matrix3D.clone(skin.bindShapeMatrix);
					
					for(j = 0; j < skin.joints.length; j++)
					{
						var joint : DisplayObject3D = skin.joints[j];
						
						var jc : DisplayObject3D = dae.getChildByName(joint.name+"_"+_numClone, true);
						
						clonedSkin.joints.push(jc);
						clonedSkin.invBindMatrices.push(Matrix3D.clone(skin.invBindMatrices[j]));
						clonedSkin.vertexWeights.push(skin.vertexWeights[j]);
					}
					dae.controllers.push(clonedSkin);
				}
				else if(controller is MorphController)
				{
					var morph : MorphController = MorphController(controller);
					var morphTarget : DisplayObject3D = dae.getChildByName(morph.target.name+"_"+_numClone, true);
					var clonedMorph : MorphController = morphTarget ? new MorphController(morphTarget as TriangleMesh3D, morph.normalized) : null;
					
					if(!clonedMorph) continue;
					
					for(j = 0; j < morph.targets.length; j++)
					{
						clonedMorph.addMorphTarget(morph.targets[j], morph.weights[j]);
					}
					dae.controllers.push(clonedMorph);
				}
				else if(controller is AnimationController)
				{
					var animation : AnimationController = AnimationController(controller).clone();
					
					for each(var ch : Channel3D in animation.channels)					{						if(ch is TransformChannel3D)						{							var obj : DisplayObject3D = getObjectByTransform(this, TransformChannel3D(ch).transform);							if(obj)							{								obj = dae.getChildByName(obj.name+"_"+_numClone, true);								if(obj)								{									TransformChannel3D(ch).transform = obj.transform;
								}							}						}					}										dae.animation = animation;					dae.controllers.push(animation);					if(_autoPlay)					{						animation.play();					}				}
			}
			
			_numClone++;
			
			return dae;
		}
		private static var _numClone : int = 0;
				private function getObjectByTransform(object : DisplayObject3D, transform : Matrix3D) : DisplayObject3D		{			var child : DisplayObject3D;						if(object.transform === transform)			{				return object;			}						for each(child in object.children)			{				var o : DisplayObject3D = getObjectByTransform(child, transform);				if(o)				{					return o;
				}			}			return null;		}		/**
		 * Loads the COLLADA.
		 * 
		 * @param	asset The url, an XML object or a ByteArray specifying the COLLADA file.
		 * @param	materials	An optional materialsList.
		 */ 
		public function load(asset:*, materials:MaterialsList = null, asynchronousParsing : Boolean = false):void
		{
			this.materials = materials || new MaterialsList();
			
			buildFileInfo(asset);
			
			this.parser = new DaeReader(asynchronousParsing);
			this.parser.addEventListener(Event.COMPLETE, onParseComplete);
			this.parser.addEventListener(ProgressEvent.PROGRESS, onParseProgress);
			this.parser.addEventListener(IOErrorEvent.IO_ERROR, onParseError);
			
			addFileSearchPath(this.baseUrl);
			
			if(asset is XML)
			{
				this.COLLADA = asset as XML;
				this.parser.loadDocument(asset, _fileSearchPaths);
			}
			else if(asset is ByteArray)
			{
				this.COLLADA = new XML(ByteArray(asset));
				this.parser.loadDocument(asset, _fileSearchPaths);
			}
			else if(asset is String)
			{
				this.filename = String(asset);
				this.parser.read(this.filename, _fileSearchPaths);
			}
			else
			{
				throw new Error("load : unknown asset type!");
			}
		}
		
		/**
		 * Removes a child.
		 * 
		 * @param	child	The child to remove
		 * 
		 * @return	The removed child
		 */ 
		override public function removeChild(child:DisplayObject3D):DisplayObject3D
		{
			var object:DisplayObject3D = getChildByName(child.name, true);
			
			if(object)
			{
				var parent:DisplayObject3D = DisplayObject3D(object.parent);
				if(parent)
				{
					var removed:DisplayObject3D = parent.removeChild(object);
					if(removed)
						return removed;
				}
			}
			return null;	
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
			// update controllers
			if(_controllers)
			{
				for each(var controller:IObjectController in _controllers)
				{
					controller.update();
				}
			}
			return super.project(parent, renderSessionData);	
		}

		/**
		 * 
		 */
		protected function buildAnimatedTransforms(object : DisplayObject3D, node : DaeNode, channels : Array, bakeChannels : Boolean=true) : void
		{
			var transform : DaeTransform = node.transforms.length ? node.transforms[0] : null;
			var channel : DaeChannel = channels[0];
			var multiChannel : TransformStackChannel3D = new TransformStackChannel3D(object.transform);
			var isBaked : Boolean = false;
			var i : int, j : int, k : int;
			
			if( transform && transform.type == "matrix" && 
				channels.length == 1 && node.transforms.length == 1 && 
			    transform.sid == channel.syntax.targetSID)
			{
				// the node has a single <matrix> element which gets aninated by a single channel.
				isBaked = true;
			}
			
			for(i = 0; i < node.transforms.length; i++)
			{
				transform = node.transforms[i];
				channel = null;
				
				for(j = 0; j < channels.length; j++)
				{
					if(channels[j].syntax.targetSID == transform.sid)
					{
						channel = channels[j];
						break;
					}
				}
				
				var c : TransformChannel3D;
				var orig : Array = transform.values;
				var axis : Number3D = new Number3D(orig[0], orig[1], orig[2]);
				var input : Array = channel ? channel.sampler.input.values : null;
				var output : Array = channel ? channel.sampler.output.values : null;
				
				switch(transform.type)
				{
					case "rotate":
						c = new RotationChannel3D(axis);

						var rc : Curve3D = new Curve3D();
						
						if(channel && channel.syntax.member == "ANGLE")
						{
							for(j = 0; j < input.length; j++)
							{
								rc.addKey(new LinearCurveKey3D(input[j], output[j] * (Math.PI/180)));	
							}
						}
						else
						{
							rc.addKey(new LinearCurveKey3D(0, orig[3] * (Math.PI/180)));		
						}
						c.addCurve(rc);
						break;
						
					case "scale":
						c = new ScaleChannel3D(null);
						
						var sc0 : Curve3D = new Curve3D();
						var sc1 : Curve3D = new Curve3D();
						var sc2 : Curve3D = new Curve3D();
						
						if(channel && channel.syntax.member == "X")
						{
							for(j = 0; j < input.length; j++)
							{
								sc0.addKey(new LinearCurveKey3D(input[j], output[j]));
								sc1.addKey(new LinearCurveKey3D(input[j], orig[1]));
								sc2.addKey(new LinearCurveKey3D(input[j], orig[2]));
							}
						}
						else if(channel && channel.syntax.member == "Y")
						{
							for(j = 0; j < input.length; j++)
							{
								sc0.addKey(new LinearCurveKey3D(input[j], orig[0]));
								sc1.addKey(new LinearCurveKey3D(input[j], output[j]));
								sc2.addKey(new LinearCurveKey3D(input[j], orig[2]));
							}
						}
						else if(channel && channel.syntax.member == "Z")
						{
							for(j = 0; j < input.length; j++)
							{
								sc0.addKey(new LinearCurveKey3D(input[j], orig[0]));
								sc1.addKey(new LinearCurveKey3D(input[j], orig[1]));
								sc2.addKey(new LinearCurveKey3D(input[j], output[j]));	
							}
						}
						else
						{
							sc0.addKey(new LinearCurveKey3D(0, orig[0]));
							sc1.addKey(new LinearCurveKey3D(0, orig[1]));
							sc2.addKey(new LinearCurveKey3D(0, orig[2]));
						}
						c.addCurve(sc0);
						c.addCurve(sc1);
						c.addCurve(sc2);
						break;
						
					case "translate":
						c = new TranslationChannel3D(null);
						
						var tc0 : Curve3D = new Curve3D();
						var tc1 : Curve3D = new Curve3D();
						var tc2 : Curve3D = new Curve3D();
						
						if(channel && channel.syntax.member == "X")
						{
							for(j = 0; j < input.length; j++)
							{
								tc0.addKey(new LinearCurveKey3D(input[j], output[j]));
								tc1.addKey(new LinearCurveKey3D(input[j], orig[1]));
								tc2.addKey(new LinearCurveKey3D(input[j], orig[2]));	
							}
						}
						else if(channel && channel.syntax.member == "Y")
						{
							for(j = 0; j < input.length; j++)
							{
								tc0.addKey(new LinearCurveKey3D(input[j], orig[0]));
								tc1.addKey(new LinearCurveKey3D(input[j], output[j]));
								tc2.addKey(new LinearCurveKey3D(input[j], orig[2]));
							}
						}
						else if(channel && channel.syntax.member == "Z")
						{
							for(j = 0; j < input.length; j++)
							{
								tc0.addKey(new LinearCurveKey3D(input[j], orig[0]));
								tc1.addKey(new LinearCurveKey3D(input[j], orig[1]));
								tc2.addKey(new LinearCurveKey3D(input[j], output[j]));
							}
						}
						else if(channel)
						{
							for(j = 0; j < input.length; j++)
							{
								tc0.addKey(new LinearCurveKey3D(input[j], output[j][0]));
								tc1.addKey(new LinearCurveKey3D(input[j], output[j][1]));
								tc2.addKey(new LinearCurveKey3D(input[j], output[j][2]));
							}
						}
						else
						{
							tc0.addKey(new LinearCurveKey3D(0, orig[0]));
							tc1.addKey(new LinearCurveKey3D(0, orig[1]));
							tc2.addKey(new LinearCurveKey3D(0, orig[2]));
						}
						
						c.addCurve(tc0);
						c.addCurve(tc1);
						c.addCurve(tc2);
						break;
						
					case "matrix":
						
						c = new MatrixChannel3D((isBaked?object.transform:null));
						
						var mc : Array = new Array(12);
						
						for(j = 0; j < 12; j++)
						{
							mc[j] = new Curve3D();
						}
						
						if(channel && channel.syntax.isFullAccess)
						{
							for(j = 0; j < input.length; j++)
							{
								for(k = 0; k < 12; k++)
								{
									mc[k].addKey(new LinearCurveKey3D(input[j], output[j][k]));
								}	
							}
						}
						else if(channel && channel.syntax.isArrayAccess)
						{	
							if(channel.syntax.arrayIndex0 >= 0 && channel.syntax.arrayIndex1 >= 0)
							{
								var prop : int = (channel.syntax.arrayIndex1 * 4) + channel.syntax.arrayIndex0;
								for(j = 0; j < input.length; j++)
								{
									for(k = 0; k < 12; k++)
									{
										var data : Number = (k != prop) ? orig[k] : output[j];
										mc[k].addKey(new LinearCurveKey3D(input[j], data));
									}	
								}
							}
							else
							{
								for(k = 0; k < 12; k++)
								{
									mc[k].addKey(new LinearCurveKey3D(0, orig[k]));
								}
							}
						}
						else
						{
							for(k = 0; k < 12; k++)
							{
								mc[k].addKey(new LinearCurveKey3D(0, orig[k]));
							}
						}
						
						for(j = 0; j < 12; j++)
						{
							c.addCurve(mc[j]);
						}
						
						if(isBaked)
						{
							// we can leave early
							this._animation.addChannel(c);
							return;
						}
						 
						break;
						
					default:
						trace("unhandled transform type: " + transform.type);
						continue;
				}
				
				if(c) 
				{
					multiChannel.addChannel(c);
				} 
			}
			
			if(bakeChannels)
			{
				var sampleDuration : Number = 0.1;
				var numSamples : int = (multiChannel.endTime - multiChannel.startTime) / sampleDuration;
				
				var matrixChannel : MatrixChannel3D = multiChannel.bake(numSamples);
				
				this._animation.addChannel(matrixChannel);
				
				matrixChannel.transform = object.transform;
			} 
			else
			{
				this._animation.addChannel(multiChannel);
			}
		}
		
		/**
		 * Builds animated vertices if needed. 
		 * NOTE: this is a Feeling specific feature. Its not part of the COLLADA 1.4.1 spec.
		 * 
		 * @param target
		 * @param vertices
		 */
		protected function buildAnimatedVertices(target : TriangleMesh3D, vertices : DaeVertices) : void
		{
			var channels : Array = (vertices && vertices.source) ? this.document.animatables[ vertices.source.id ] : null;
			var i : int;
			
			if(channels && channels.length)
			{
				for(i = 0; i < channels.length; i++)
				{
					var channel : DaeChannel = channels[i];
					var vertexIndex : int = Math.floor(channel.syntax.arrayIndex0 / 3);
					var vertexProp : int = channel.syntax.arrayIndex0 % 3;
					var vertexChannel : VertexChannel3D = new VertexChannel3D(target.geometry, vertexIndex, vertexProp);
					var curve : Curve3D = new Curve3D();
					
					for(var j:int = 0; j < channel.sampler.input.values.length; j++)
					{
						var time : Number = channel.sampler.input.values[j];
						var data : Number = channel.sampler.output.values[j];
						
						curve.addKey(new LinearCurveKey3D(time, data));
					}
					
					if(vertexChannel.addCurve(curve))
					{
						this._animation.addChannel(vertexChannel);
					}
					else
					{
						PaperLogger.warning("DAE#buildAnimatedVertices : invalid curve for channel " + channel.id + " for object " + target.name);
					}
				}
			}
		}
		
		/**
		 * 
		 */
		protected function buildAnimationClips() : void
		{
			if(!this.document || !this.document.animation_clips)
			{
				return;
			}
			
			var daeClip : DaeAnimationClip;
			for each(daeClip in this.document.animation_clips)
			{
				if(daeClip.name && daeClip.end > daeClip.start)
				{
					var clip : AnimationClip3D = new AnimationClip3D(daeClip.name, daeClip.start, daeClip.end);
					this._animation.addClip(clip);
				}
			}
		}

		/**
		 * Builds the _animation for an object and its children.
		 * 
		 * @param object
		 */
		protected function buildAnimations(object : DisplayObject3D) : void
		{
			var node : DaeNode = _objectToNode[ object ];
			var child : DisplayObject3D;
			
			if(node)
			{
				var channels : Array = this.document.animatables[node.id];
				if(channels && channels.length)
				{
					buildAnimatedTransforms(object, node, channels);
				}
			}
			
			for each(child in object.children)
			{
				buildAnimations(child);	
			}
		}
		
		/**
		 * Links the controllers to the objects.
		 * 
		 * @param instance
		 */
		protected function buildControllers(instance : DisplayObject3D=null) : void 
		{
			var node : DaeNode = _objectToNode[instance];
			var instanceController : DaeInstanceController;
			var controller : DaeController;
			var morphController : MorphController;
			var skinController : SkinController;
			
			instance = instance || _rootNode;
			
			if(node)
			{
				// loop over all controller instances for this node
				for each(instanceController in node.controllers)
				{
					controller = this.document.controllers[ instanceController.url ];
	
					if(controller && controller.morph)
					{
						morphController = buildMorphController(instance as TriangleMesh3D, controller.morph);
						if(morphController)
						{
							_controllers.push(morphController);	
						}
					}
					else if(controller && controller.skin)
					{
						skinController = buildSkinController(instance as Skin3D, controller.skin);
						if(skinController)
						{
							_controllers.push(skinController);	
							
							for each(var c:IObjectController in _controllers)
							{
								if(c === skinController)
								{
									continue;
								}
								else if(c is MorphController && MorphController(c).target === skinController.target)
								{
									skinController.input = MorphController(c);
								}
							}
						}	
					}
				}
			}

			// recurse children
			for each(var child : DisplayObject3D in instance.children)
			{
				buildControllers(child);	
			}
		}
		
		/**
		 * 
		 * @param	asset
		 * @return
		 */
		protected function buildFileInfo( asset:* ):void
		{
			this.filename = asset is String ? String(asset) : "./meshes/rawdata_dae";
			
			// make sure we've got forward slashes!
			this.filename = this.filename.split("\\").join("/");
				
			if( this.filename.indexOf("/") != -1 )
			{
				// dae is located in a sub-directory of the swf.
				var parts:Array = this.filename.split("/");
				this.fileTitle = String( parts.pop() );
				this.baseUrl = parts.join("/");
			}
			else
			{
				// dae is located in root directory of swf.
				this.fileTitle = this.filename;
				this.baseUrl = "";
			}
		}
		
		/**
		 * 
		 */
		protected function buildGeometry(target : TriangleMesh3D, daeGeometry : DaeGeometry, daeBindMaterial : DaeBindMaterial) : void
		{
			var daePrimitive : DaePrimitive;
			var daeInstanceMaterial : DaeInstanceMaterial;
			var vertexStart : int = target.geometry.vertices.length;
			var i : int;
			
			if(daeGeometry.mesh)
			{
				target.geometry.vertices = target.geometry.vertices.concat( buildVertices(daeGeometry.mesh) );	
				
				for(i = 0; i < daeGeometry.mesh.primitives.length; i++)
				{
					daePrimitive = daeGeometry.mesh.primitives[i];
					daeInstanceMaterial = daeBindMaterial ? 
										  daeBindMaterial.getInstanceMaterialBySymbol(daePrimitive.material) : 
										  null;
					buildPrimitive(target, daePrimitive, daeInstanceMaterial, vertexStart);
				}
				
				// builds vertex aninations if any available
				buildAnimatedVertices(target, daeGeometry.mesh.vertices);
			}
			else if(daeGeometry.convex_mesh) 
			{
				PaperLogger.warning("[DAE] Don't know yet how to create a convex_mesh.");
			}
		}
		
		/**
		 * 
		 */
		protected function buildGeometryLines(target : Lines3D, daeGeometry : DaeGeometry, daeBindMaterial : DaeBindMaterial) : void
		{
			var i : int, j : int, k : int;
			
			if(daeBindMaterial)
			{
				// TODO: handle spline materials	
			}
			
			for(i = 0; i < daeGeometry.splines.length; i++)
			{
				var spline:DaeSpline = daeGeometry.splines[i];
				
				for(j = 0; j < spline.vertices.length; j++)
				{
					k = (j+1) % spline.vertices.length;
					
					var v0:Vertex3D = new Vertex3D(spline.vertices[j][0], spline.vertices[j][1], spline.vertices[j][2]);
					var v1:Vertex3D = new Vertex3D(spline.vertices[k][0], spline.vertices[k][1], spline.vertices[k][2]);
				
					var line:Line3D = new Line3D(target, target.material as LineMaterial, DEFAULT_LINE_WIDTH, v0, v1);
					
					target.addLine(line);
				}
			}
		}
		
		/**
		 * 
		 */
		protected function buildMaterialInstance(daeInstanceMaterial : DaeInstanceMaterial, outBVI : DaeBindVertexInput) : MaterialObject3D
		{
			if(!daeInstanceMaterial)
			{
				return null;
			}
			
			var material : MaterialObject3D = this.materials.getMaterialByName(daeInstanceMaterial.target);
			var daeMaterial : DaeMaterial = this.document.materials[ daeInstanceMaterial.target ];
			var daeEffect : DaeEffect = daeMaterial ? this.document.effects[ daeMaterial.effect ] : null;
			var daeLambert : DaeLambert = daeEffect ? daeEffect.color as DaeLambert : null;
			var daeColorOrTexture : DaeColorOrTexture = daeLambert ? daeLambert.diffuse || daeLambert.emission : null;	
			var daeTexture : DaeTexture = daeColorOrTexture ? daeColorOrTexture.texture : null;
			var daeImage : DaeImage = (daeEffect && daeEffect.texture_url) ? this.document.images[daeEffect.texture_url] : null;
			var daeBVI : DaeBindVertexInput;

			if(daeTexture && daeTexture.texture)
			{
				daeBVI = daeInstanceMaterial.findBindVertexInput(daeTexture.texcoord);				
				outBVI.input_set = daeBVI ? daeBVI.input_set : -1;
			}
			
			if(material) 
			{
				// material already exists in #materials
				return material;
			}
			
			if(daeImage && daeImage.bitmapData) 
			{
				material = new BitmapMaterial(daeImage.bitmapData.clone());
				material.tiled = true;
			}
			else if(daeColorOrTexture && daeColorOrTexture.color)
			{
				var r : int = daeColorOrTexture.color[0] * 0xff;
				var g : int = daeColorOrTexture.color[1] * 0xff;
				var b : int = daeColorOrTexture.color[2] * 0xff;	
				
				var rgb : uint = r << 16 | g << 8 | b;
				
				if(daeEffect.wireframe)
				{
					material = new WireframeMaterial(rgb, daeColorOrTexture.color[3]);	
				} 
				else
				{
					material = new ColorMaterial(rgb, daeColorOrTexture.color[3]);
				}
			}
			
			if(material)
			{
				if(daeEffect && daeEffect.double_sided)
				{
					material.doubleSided = true;
				}
				this.materials.addMaterial(material, daeInstanceMaterial.target);	
			}
			
			return material;
		}
		
		/**
		 * Builds a Matrix3D from a node's transform array. @see org.ascollada.core.DaeNode#transforms
		 * 
		 * @param	node
		 * 
		 * @return
		 */
		protected function buildMatrix(node:DaeNode):Matrix3D 
		{
			var stack:Array = buildMatrixStack(node);
			var matrix:Matrix3D = Matrix3D.IDENTITY;
			for( var i:int = 0; i < stack.length; i++ ) 
				matrix.calculateMultiply(matrix, stack[i]);
			return matrix;
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		protected function buildMatrixFromTransform(transform:DaeTransform):Matrix3D
		{
			var matrix:Matrix3D;
			var toRadians:Number = Math.PI/180;
			var v:Array = transform.values;
			
			switch(transform.type)
			{
				case ASCollada.DAE_ROTATE_ELEMENT:
					matrix = Matrix3D.rotationMatrix(v[0], v[1], v[2], v[3] * toRadians);
					break;
				case ASCollada.DAE_SCALE_ELEMENT:
					matrix = Matrix3D.scaleMatrix(v[0], v[1], v[2]);
					break;
				case ASCollada.DAE_TRANSLATE_ELEMENT:
					matrix = Matrix3D.translationMatrix(v[0], v[1], v[2]);
					break;
				case ASCollada.DAE_MATRIX_ELEMENT:
					matrix = new Matrix3D(v);
					break;
				default:
					throw new Error("Unknown transform type: " + transform.type);
			}
			return matrix;
		}
		
		/**
		 * 
		 * @param	node
		 * @return
		 */
		protected function buildMatrixStack(node:DaeNode):Array
		{
			var stack:Array = new Array();	
			for( var i:int = 0; i < node.transforms.length; i++ ) 
				stack.push(buildMatrixFromTransform(node.transforms[i]));
			return stack;
		}
		
		/**
		 * 
		 */
		protected function buildMesh(node : DaeNode) : DisplayObject3D
		{
			var mesh : DisplayObject3D;
			var daeInstanceGeometry : DaeInstanceGeometry;
			var daeGeometry : DaeGeometry;
			var daeInstanceController : DaeInstanceController;
			var daeController : DaeController;
			var daeMorphController : DaeController;
			var daeBindMaterial : DaeBindMaterial;
			var i : int;
			
			// handle instance geometries
			for(i = 0; i < node.geometries.length; i++)
			{
				daeInstanceGeometry = node.geometries[i];
				daeBindMaterial = daeInstanceGeometry.bindMaterial;
				daeGeometry = this.document.geometries[ daeInstanceGeometry.url ];
				
				if(daeGeometry.mesh || daeGeometry.convex_mesh) 
				{
					mesh = new TriangleMesh3D(null, [], [], node.name);
					
					buildGeometry(mesh as TriangleMesh3D, daeGeometry, daeBindMaterial);
				}
				else if(daeGeometry.spline && daeGeometry.splines)
				{
					mesh = new Lines3D(new LineMaterial(DEFAULT_LINE_COLOR), node.name);
					
					buildGeometryLines(mesh as Lines3D, daeGeometry, daeBindMaterial);
				}
			}
			
			if(mesh)
			{
				mesh.geometry.ready = true;
				return mesh;
			}
			
			// handle geometries instantiated by controllers
			for(i = 0; i < node.controllers.length; i++)
			{
				daeGeometry = null;
				daeInstanceController = node.controllers[i];
				daeBindMaterial = daeInstanceController.bindMaterial;
				daeController = this.document.controllers[ daeInstanceController.url ];
				
				if(daeController.skin)
				{
					daeGeometry = this.document.geometries[ daeController.skin.source ];
					if(!daeGeometry)
					{
						// geometry may be output of a morph controller
						daeMorphController = this.document.controllers[ daeController.skin.source ];
						if(daeMorphController.morph)
						{
							daeGeometry = this.document.geometries[ daeMorphController.morph.source ];
						}
					}
					mesh = new Skin3D(null, [], [], node.name);
				}
				else if(daeController.morph)
				{
					mesh = new TriangleMesh3D(null, [], [], node.name);
					daeGeometry = this.document.geometries[ daeController.morph.source ];	
				}
				
				if(daeGeometry)
				{
					buildGeometry(mesh as TriangleMesh3D, daeGeometry, daeBindMaterial);
				}
				
				if(mesh)
				{
					if(daeMorphController)
					{
						var morphController : MorphController = buildMorphController(mesh as TriangleMesh3D, daeMorphController.morph);
						if(morphController)
						{
							_controllers.push(morphController);
						}
					}
					mesh.geometry.ready = true;
					return mesh;
				}
			}
			
			if(mesh is TriangleMesh3D)
			{
				mesh.geometry.ready = true;
			}
			
			return mesh;
		}

		/**
		 * Builds a morph controller.
		 * 
		 * @param instance
		 * @param morph
		 * @param bindMaterial
		 * 
		 * @return
		 */ 
		protected function buildMorphController(instance : TriangleMesh3D, morph : DaeMorph) : MorphController 
		{
			var controller : MorphController = new MorphController(instance);
			var daeGeometry : DaeGeometry;
			var i : int;

			for(i = 0; i < morph.targets.values.length; i++)
			{
				var target : String = morph.targets.values[i];
				var weight : Number = morph.weights.values[i];
				
				daeGeometry = this.document.geometries[ target ];
				
				var targetMesh : TriangleMesh3D = new TriangleMesh3D(null, [], [], instance.name + " " + target);
				
				buildGeometry(targetMesh, daeGeometry, null);
				
				controller.addMorphTarget(targetMesh, weight);
			}
			
			// Check whether morph weights are animated
			var channels : Array = this.document.animatables[ morph.weights.id ];
			if(channels)
			{
				for(i = 0; i < channels.length; i++)
				{
					var channel : DaeChannel = channels[i];
					var index : int = channel.syntax.arrayIndex0;
					var track : MorphWeightChannel3D = new MorphWeightChannel3D(controller, index);
					var curve : Curve3D = new Curve3D();
					var input : Array = channel.sampler.input.values;
					var output : Array = channel.sampler.output.values;
					var j : int;
					
					for(j = 0; j < input.length; j++)
					{
						curve.addKey(new LinearCurveKey3D(input[j], output[j]));
					}
					
					if(track.addCurve(curve))
					{
						this._animation.addChannel(track);
					}
					else
					{
						PaperLogger.warning("DAE#buildMorphController : invalid animation curve...");
					}
				}
			}
			
			return controller;
		}
		
		/**
		 * Builds a DisplayObject3D from a node. @see org.ascollada.core.DaeNode
		 * 
		 * @param	node	
		 * 
		 * @return	The created DisplayObject3D. @see org.papervision3d.objects.DisplayObject3D
		 */ 
		protected function buildNode(node:DaeNode, parent:DisplayObject3D):void
		{
			var instance:DisplayObject3D;
			var i:int;
			
			if(node.geometries.length || node.controllers.length)
			{
				// the node instantiates some kind of geometry
				instance = buildMesh(node);
			}
			else
			{
				// no geometry, simply create a DisplayObject3D
				instance = new DisplayObject3D(node.name);
			}
			
			// recurse node instances
			for(i = 0; i < node.instance_nodes.length; i++)
			{
				var dae_node:DaeNode = document.getDaeNodeById(node.instance_nodes[i].url);
				buildNode(dae_node, instance);
			}

			// setup the initial transform
			instance.copyTransform(buildMatrix(node));	
			
			// recurse node children
			for(i = 0; i < node.nodes.length; i++)
				buildNode(node.nodes[i], instance);
					
			// save COLLADA id, sid
			_colladaID[instance] = node.id;
			_colladaSID[instance] = node.sid;
			_colladaIDToObject[node.id] = instance;
			_colladaSIDToObject[node.sid] = instance;
			_objectToNode[instance] = node;
			
			instance.flipLightDirection = true;
				
			parent.addChild(instance);
		}
		
		/**
		 * Builds a primitive.
		 * 
		 * @param mesh
		 * @param daePrimitive
		 * @param daeInstanceMaterial
		 * @param vertexStart
		 */
		protected function buildPrimitive(mesh : TriangleMesh3D, daePrimitive : DaePrimitive, daeInstanceMaterial : DaeInstanceMaterial, vertexStart : int):void
		{
			var material : MaterialObject3D;
			var texcoords : Array = new Array(), texCoordSet : Array;
			var daeBVI : DaeBindVertexInput = new DaeBindVertexInput(this.document);
			var idx0 : int, idx1 : int, idx2 : int;
			var v0 : Vertex3D, v1 : Vertex3D, v2 : Vertex3D;
			var t0 : NumberUV, t1 : NumberUV, t2 : NumberUV;
			var hasUV : Boolean = false;
			var i : int, j : int, k : int;
			
			daeBVI.input_set = -1;
			daeBVI.input_semantic = "TEXCOORD";
			
			material = buildMaterialInstance(daeInstanceMaterial, daeBVI) || MaterialObject3D.DEFAULT;
			
			if(material is AbstractLightShadeMaterial || material is ShadedMaterial) 
			{
				material.registerObject(mesh);
			}
			
			// choose the correct texcoord set
			if(this.forceCoordSet >= 0)
			{
				// forced
				texCoordSet = daePrimitive.getTexCoords(this.forceCoordSet);
			}
			else if(daeBVI.input_set < 0)
			{
				// no BindVertexInput defined, lets use the default texcoords
				texCoordSet = daePrimitive.getFirstInput("TEXCOORD");
			}
			else
			{
				// BindVertexInput is defined, select the specified texcoord set.
				texCoordSet = daePrimitive.getTexCoords(daeBVI.input_set);
			}
			
			texCoordSet = texCoordSet || new Array();
			
			// texture coords
			for( i = 0; i < texCoordSet.length; i++ ) 
			{
				if(Papervision3D.useRIGHTHANDED)
				{
					texcoords.push(new NumberUV(1.0-texCoordSet[i][0], texCoordSet[i][1]));
				}
				else
				{
					texcoords.push(new NumberUV(texCoordSet[i][0], texCoordSet[i][1]));
				}
			}
			
			hasUV = (texcoords.length == daePrimitive.vertices.length);
			
			switch(daePrimitive.type)
			{
				// Each line described by the mesh has two vertices. The first line is formed 
				// from first and second vertices. The second line is formed from the third and fourth 
				// vertices and so on.
				case ASCollada.DAE_LINES_ELEMENT:					/*
					for(i = 0; i < daePrimitive.vertices.length; i += 2) 
					{
						v0 = geometry.vertices[ daePrimitive.vertices[i] ];
						v1 = geometry.vertices[ daePrimitive.vertices[i+1] ];
						t0 = hasUV ? texcoords[  i  ] : new NumberUV();
						t1 = hasUV ? texcoords[ i+1 ] : new NumberUV();
					}					 					 */
					break;
					
				// polygon with *no* holes
				case ASCollada.DAE_POLYLIST_ELEMENT:
					for(i = 0, k = 0; i < daePrimitive.vcount.length; i++) 
					{
						var poly:Array = new Array();
						var uvs:Array = new Array();
						
						for( j = 0; j < daePrimitive.vcount[i]; j++ ) 
						{
							uvs.push( (hasUV ? texcoords[ k ] : new NumberUV()) );
							poly.push( mesh.geometry.vertices[daePrimitive.vertices[k++]] );
						}
							
						v0 = poly[0];
						t0 = uvs[0];

						for(j = 1; j < poly.length - 1; j++)
						{
							v1 = poly[j];
							v2 = poly[j+1];
							t1 = uvs[j];
							t2 = uvs[j+1];
							
							if( v0 is Vertex3D && v1 is Vertex3D && v2 is Vertex3D)
							{
								mesh.geometry.faces.push(new Triangle3D(mesh, [v0, v1, v2], material, [t0, t1, t2]));
							}
							else
							{
								PaperLogger.error("" +daePrimitive.name+ " "+ poly.length +" "+daePrimitive.vertices.length+" "+mesh.geometry.vertices.length);
							}
						}
					}
					break;
				
				// polygons *with* holes (but holes not yet processed...)
				case ASCollada.DAE_POLYGONS_ELEMENT:
					for(i = 0, k = 0; i < daePrimitive.polygons.length; i++)
					{
						var p:Array = daePrimitive.polygons[i];
						var np:Array = new Array();
						var nuv:Array = new Array();
						
						for(j = 0; j < p.length; j++)
						{
							nuv.push( (hasUV ? texcoords[ k ] : new NumberUV()) );
							np.push( mesh.geometry.vertices[daePrimitive.vertices[k++]] );
						}
						
						v0 = np[0];
						t0 = nuv[0];
						
						for(j = 1; j < np.length - 1; j++)
						{
							v1 = np[j];
							v2 = np[j+1];
							t1 = nuv[j];
							t2 = nuv[j+1];
				
							mesh.geometry.faces.push(new Triangle3D(mesh, [v0, v1, v2], material, [t0, t1, t2]));
						}
					}
					break;
						
				// simple triangles
				case ASCollada.DAE_TRIANGLES_ELEMENT:
				default:	
					for(i = 0; i < daePrimitive.vertices.length; i += 3) 
					{
						idx0 = vertexStart + daePrimitive.vertices[i];
						idx1 = vertexStart + daePrimitive.vertices[i+1];
						idx2 = vertexStart + daePrimitive.vertices[i+2];
						
						v0 = mesh.geometry.vertices[ idx0 ];
						v1 = mesh.geometry.vertices[ idx1 ];
						v2 = mesh.geometry.vertices[ idx2 ];
						
						t0 = hasUV ? texcoords[ i+0 ] : new NumberUV();
						t1 = hasUV ? texcoords[ i+1 ] : new NumberUV();
						t2 = hasUV ? texcoords[ i+2 ] : new NumberUV();

						mesh.geometry.faces.push(new Triangle3D(mesh, [v0, v1, v2], material, [t0, t1, t2]));
					}
					break;
			}
		}
		
		/**
		 * Builds the scene.
		 */ 
		protected function buildScene():void
		{
			_controllers = new Array();
			
			this._animation = new AnimationController();
			
			_controllers.push(this._animation);

			_rootNode = new DisplayObject3D(ROOTNODE_NAME);
			
			for(var i:int = 0; i < this.document.vscene.nodes.length; i++)
			{
				buildNode(this.document.vscene.nodes[i], _rootNode);
			}
			
			this.addChild(_rootNode);
			
			// build controllers
			buildControllers();
			
			if(this.yUp)
			{
				
			}
			else
			{
				_rootNode.rotationX = -90;
				_rootNode.rotationY = _rightHanded ? 0 : 180;
			}
			
			if(!_rightHanded)
				_rootNode.scaleX = -_rootNode.scaleX;
			
			// _animation stuff
			onParseAnimationsComplete();
			
			this.document.destroy();
			this.document = null;
			
			this.COLLADA = null;
			
			if(this.parser)
			{
				if(this.parser.document)
				{
					this.parser.document.destroy();
					this.parser.document = null;
				}
				this.parser = null;
			}
			
			dispatchEvent(new FileLoadEvent(FileLoadEvent.LOAD_COMPLETE, this.filename));
		}
		
		/**
		 * Builds a skin controller.
		 * 
		 * @param instance
		 * @param skin
		 */ 
		protected function buildSkinController(instance : DisplayObject3D, skin : DaeSkin) : SkinController 
		{			
			var i:int;
			var found:Object = new Object();

			if(!skin)
			{
				return null;
			}

			var controller:SkinController = new SkinController(instance as Skin3D);

			controller.bindShapeMatrix = new Matrix3D(skin.bind_shape_matrix);
			controller.joints = new Array();
			controller.vertexWeights = new Array();
			controller.invBindMatrices = new Array();
			
			for(i = 0; i < skin.joints.length; i++)
			{
				var jointId:String = skin.joints[i];
				
				if(found[jointId])
					continue;
					
				var joint:DisplayObject3D = _colladaIDToObject[jointId];
				if(!joint)
					joint = _colladaSIDToObject[jointId];
				if(!joint)
					throw new Error("Couldn't find the joint id = " + jointId);

				var vertexWeights:Array = skin.findJointVertexWeightsByIDOrSID(jointId);
				if(!vertexWeights)
					throw new Error("Could not find vertex weights for joint with id = " + jointId);
					
				var bindMatrix:Array = skin.findJointBindMatrix2(jointId);
				if(!bindMatrix || bindMatrix.length != 16)
					throw new Error("Could not find inverse bind matrix for joint with id = " + jointId);
				
				controller.joints.push(joint);
				controller.invBindMatrices.push(new Matrix3D(bindMatrix));
				controller.vertexWeights.push(vertexWeights);
				
				found[jointId] = true;
			}
			
			return controller;
		}
		
		/**
		 * Builds vertices from a COLLADA mesh.
		 * 
		 * @param	mesh	The COLLADA mesh. @see org.ascollada.core.DaeMesh
		 * 
		 * @return	Array of Vertex3D
		 */
		protected function buildVertices(mesh:DaeMesh):Array
		{
			var vertices:Array = new Array();
			for( var i:int = 0; i < mesh.vertices.source.values.length; i++ )
				vertices.push(new Vertex3D(mesh.vertices.source.values[i][0], mesh.vertices.source.values[i][1], mesh.vertices.source.values[i][2]));
			return vertices;
		}
		
		/**
		 * Called when the parser completed parsing animations.
		 * 
		 * @param	event
		 */ 
		protected function onParseAnimationsComplete(event:Event=null):void
		{	
			buildAnimations(this);
			buildAnimationClips();
			
			if(this._animation.numChannels > 0)
			{
				if(_autoPlay)
				{
					this._animation.play("all", _loop);
				}
				PaperLogger.info( "animations COMPLETE " + this._animation);
			}	
			dispatchEvent(new FileLoadEvent(FileLoadEvent.ANIMATIONS_COMPLETE, this.filename));
		}
		
		/**
		 * Called on parse animations progress.
		 * 
		 * @param	event
		 */ 
		protected function onParseAnimationsProgress(event:ProgressEvent):void
		{
			PaperLogger.info( "animations #" + event.bytesLoaded + " of " + event.bytesTotal);
			dispatchEvent(new FileLoadEvent(FileLoadEvent.ANIMATIONS_PROGRESS, this.filename, event.bytesLoaded, event.bytesTotal));
		}
		
		/**
		 * Called when the DaeReader completed parsing.
		 * 
		 * @param	event
		 */
		protected function onParseComplete(event:Event):void
		{
			var reader:DaeReader = event.target as DaeReader;
			
			this.document = reader.document;
			
			_colladaID = new Dictionary(true);
			_colladaSID = new Dictionary(true);
			_colladaIDToObject = new Object();
			_colladaSIDToObject = new Object();
			_objectToNode = new Object();

			if(this.parser.hasEventListener(Event.COMPLETE))
				this.parser.removeEventListener(Event.COMPLETE, onParseComplete);
			if(this.parser.hasEventListener(ProgressEvent.PROGRESS))
				this.parser.removeEventListener(ProgressEvent.PROGRESS, onParseProgress);
			if(this.parser.hasEventListener(IOErrorEvent.IO_ERROR))
				this.parser.removeEventListener(IOErrorEvent.IO_ERROR, onParseError);
				
			buildScene();
		}
	
		/**
		 * Called on parsing error (invalid file name)
		 * 
		 * @param	event
		 */ 
		
		protected function onParseError(event:IOErrorEvent):void{
			dispatchEvent(event);
		}
		
		/**
		 * Called on parsing progress.
		 * 
		 * @param	event
		 */ 
		protected function onParseProgress(event:ProgressEvent):void
		{
			var message : String = this.parser.parseMessage;
			dispatchEvent(new FileLoadEvent(FileLoadEvent.LOAD_PROGRESS, this.filename, event.bytesLoaded, event.bytesTotal, message, null, true, false));
		}
		
		/** Whether the COLLADA uses Y-up, Z-up otherwise. */
		public function get yUp():Boolean
		{
			if(this.document){
				return (this.document.asset.yUp == ASCollada.DAE_Y_UP);
			}else{
				return false;
			}
		}
		
		public function set rootNode(value : DisplayObject3D) : void
		{
			_rootNode = value;
		}
		public function get rootNode() : DisplayObject3D
		{
			return _rootNode;
		}
	}
}

