package org.ascollada.fx 
{
	import org.ascollada.ASCollada;	
	import org.ascollada.core.DaeDocument;	
	import org.ascollada.core.DaeEntity;
	import org.ascollada.namespaces.collada;

	/**
	 * @author Tim Knip / floorplanner.com
	 */
	public class DaeBindMaterial extends DaeEntity 
	{
		use namespace collada;
		
		/** */
		public var instanceMaterials : Array;
		
		/**
		 * 
		 */
		public function DaeBindMaterial(document : DaeDocument, node : XML = null, async : Boolean = false) 
		{
			super(document, node, async);
		}

		/**
		 * 
		 */
		override public function destroy() : void 
		{
			super.destroy();
			
			var element : DaeInstanceMaterial;
			
			if(this.instanceMaterials)
			{
				for each(element in this.instanceMaterials)
				{
					element.destroy();
				}
				this.instanceMaterials = null;
			}
		}

		/**
		 * 
		 */
		public function getInstanceMaterialBySymbol(symbol : String) : DaeInstanceMaterial
		{
			if(this.instanceMaterials)
			{
				for each(var instanceMaterial : DaeInstanceMaterial in this.instanceMaterials)
				{
					if(instanceMaterial.symbol == symbol)
					{
						return instanceMaterial;
					}
				}
			}
			return null;
		}

		/**
		 * 
		 */
		override public function read(node : XML) : void 
		{
			super.read(node);
			
			var list : XMLList = node..collada::[ASCollada.DAE_INSTANCE_MATERIAL_ELEMENT];
			var num : int = list.length();
			var i : int;
			
			this.instanceMaterials = new Array();
			
			for(i = 0; i < num; i++)
			{
				this.instanceMaterials.push(new DaeInstanceMaterial(this.document, list[i]));
			}
		}
	}
}
