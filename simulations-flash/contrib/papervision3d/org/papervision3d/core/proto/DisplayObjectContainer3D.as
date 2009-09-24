package org.papervision3d.core.proto
{
	import flash.events.EventDispatcher;
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.objects.DisplayObject3D;
	
	/**
	* The DisplayObjectContainer3D class is the base class for all objects that can serve as DisplayObject3D containers.
	* <p/>
	* Each DisplayObjectContainer3D object has its own child list.
	*/
	public class DisplayObjectContainer3D extends EventDispatcher
	{
		/**
		* [read-only] [read-only] The scene, which is the top-most displayObjectContainer3D in the tree structure.
		*/
		public var root :DisplayObjectContainer3D;
		
		/**
		* [internal-use] Names indexed by children.
		*/
		protected var _children       :Dictionary;
	
		/**
		* [internal-use] Children indexed by name.
		*/
		protected var _childrenByName :Object;
	
		private   var _childrenTotal  :int;
		

		/**
		* Creates a new DisplayObjectContainer3D object.
		*/
		public function DisplayObjectContainer3D():void
		{
			this._children       = new Dictionary( false );
			this._childrenByName = new Dictionary( true );
			this._childrenTotal  = 0;
		}
	
		/**
		* Adds a child DisplayObject3D instance to this DisplayObjectContainer instance.
		*
		* [TODO: If you add a child object that already has a different display object container as a parent, the object is removed from the child list of the other display object container.]
		*
		* @param	child	The DisplayObject3D instance to add as a child of this DisplayObjectContainer3D instance.
		* @param	name	An optional name of the child to add or create. If no name is provided, the child name will be used.
		* @return	The DisplayObject3D instance that you have added or created.
		*/
		public function addChild( child :DisplayObject3D, name:String=null ):DisplayObject3D
		{
			
			if(child.parent)
			{
				PaperLogger.error("DisplayObjectContainer.addChild : DisplayObject3D already has a parent, ie is already added to scene."); 
			}
			// Choose name
			name = name || child.name || String( child.id );
			
			this._children[ child ] = name;
			this._childrenByName[ name ] = child;
			this._childrenTotal++;
	
			child.parent = this;
			child.root = this.root;
	
			return child;
		}
	
	
		/**
		* Adds all the children of a DisplayObject3D instance to this DisplayObjectContainer instance.
		*
		* @param	child	The DisplayObjectContainer3D instance that contains the children to add.
		* @return	The DisplayObject3D instance that you have added or created.
		*/
	
		public function addChildren( parent :DisplayObject3D ):DisplayObjectContainer3D
		{
			for each( var child:DisplayObject3D in parent.children )
			{
				parent.removeChild( child );
				this.addChild( child );
			}
	
			return this;
		}
	
	
	
		/**
		* Removes the specified child DisplayObject3D instance from the child list of the DisplayObjectContainer3D instance.
		* </p>
		* [TODO: The parent property of the removed child is set to null, and the object is garbage collected if no other references to the child exist.]
		* </p>
		* The garbage collector is the process by which Flash Player reallocates unused memory space. When a variable or object is no longer actively referenced or stored somewhere, the garbage collector sweeps through and wipes out the memory space it used to occupy if no other references to it exist.
		* </p>
		* @param	child	The DisplayObject3D instance to remove.
		* @return	The DisplayObject3D instance that you pass in the child parameter.
		*/
	
		/**
		* @public
		* Added from Bug #10 by John Grden 8/22/2007
		*/
		public function removeChild( child:DisplayObject3D ):DisplayObject3D
		{
			//removeChildByname(name:string) may return null // must check here
			
			if(child && _children[child]){
				delete _childrenByName[ this._children[ child ] ];
				delete _children[ child ];
			
				child.parent = null;
				child.root = null;
				
				_childrenTotal--;
				
				return child;
			}
			return null;
			
		}
	
		/**
		* Returns the child display object that exists with the specified name.
		* </p>
		* If more that one child display object has the specified name, the method returns the first object in the child list.
		* </p>
		* @param	name	The name of the child to return.* 
		* @return	The child display object with the specified name.
		*/
		public function getChildByName( name:String, recursive:Boolean = false ):DisplayObject3D
		{
			if(recursive)
				return findChildByName(name);
			else
				return this._childrenByName[ name ];
		}
	
	
		/**
		* Removes the child DisplayObject3D instance that exists with the specified name, from the child list of the DisplayObjectContainer3D instance.
		* </p>
		* If more that one child display object has the specified name, the method removes the first object in the child list.
		* </p>
		* [TODO: The parent property of the removed child is set to null, and the object is garbage collected if no other references to the child exist.]
		* </p>
		* The garbage collector is the process by which Flash Player reallocates unused memory space. When a variable or object is no longer actively referenced or stored somewhere, the garbage collector sweeps through and wipes out the memory space it used to occupy if no other references to it exist.
		* </p>
		* @param	name	The name of the child to remove.
		* @return	The DisplayObject3D instance that was removed.
		*/
		public function removeChildByName( name:String ):DisplayObject3D
		{
			return removeChild( getChildByName( name ) );
		}
	
		/**
		* Returns a string value with the list of objects.
		*
		* @return	A string.
		*/
		public override function toString():String
		{
			return childrenList();
		}
		
		/**
		* Returns a string value with the list of objects.
		*
		* @return	A string.
		*/
		public function childrenList():String
		{
			var list:String = "";
	
			for( var name:String in this._children )
				list += name + "\n";
	
			return list;
		}
		
		/**
		 * Recursively finds a child by its name.
		 * 
		 * @param	name
		 * @param	parent
		 * 
		 * @return 	The found child.
		 */ 
		private function findChildByName(name:String, parent:DisplayObject3D = null):DisplayObject3D
		{
			parent = parent || DisplayObject3D(this);
			if(!parent)
				return null;
			if(parent.name == name)
				return parent;
			for each(var child:DisplayObject3D in parent.children)	
			{
				var obj:DisplayObject3D = findChildByName(name, child);
				if(obj) 
					return obj;
			}
			return null;
		}
		
		/**
		* Returns the number of children of this object.
		*/
		public function get numChildren():int
		{
			return this._childrenTotal;
		}
	
		/**
		* Returns the children object.
		*/
		public function get children():Object
		{
			return this._childrenByName;
		}
	
	}
}