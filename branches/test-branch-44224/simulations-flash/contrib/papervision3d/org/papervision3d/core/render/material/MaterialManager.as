package org.papervision3d.core.render.material
{
	import flash.utils.Dictionary;
	
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.data.RenderSessionData;
	
	/**
	 * @Author Ralph Hauwert
	 * 
	 * <code>MaterialManager</code> (used internally) is a singleton that tracks 
	 * all materials. Each time a material is created, the <code>MaterialManager</code> 
	 * registers the material for access in the render engine. 
	 */
	public class MaterialManager
	{
		private static var instance:MaterialManager;
		private var materials:Dictionary;
	
		/**
		 * MaterialManager singleton constructor
		 */
		public function MaterialManager():void
		{
			if(instance){
				throw new Error("Only 1 instance of materialmanager allowed");
			}
			init();
		}
		
		/** @private */
		private function init():void
		{
			materials = new Dictionary(true);
		}
		
		/** @private */
		private function _registerMaterial(material:MaterialObject3D):void
		{
			materials[material] = true;
		
		}
		
		/** @private */
		private function _unRegisterMaterial(material:MaterialObject3D):void
		{
			delete materials[material];
		}
		
		/**
		 * Allows for materials that animate or change (e.g., MovieMaterial) to 
		 * be updated prior to the render
		 * 
		 * @param renderSessionData		the data used in updating the material
		 */
		public function updateMaterialsBeforeRender(renderSessionData:RenderSessionData):void
		{
			var um:IUpdateBeforeMaterial;
						
			for (var m:* in materials){
				if(m is IUpdateBeforeMaterial){
					um = m as IUpdateBeforeMaterial;
					if( um.isUpdateable() )
						um.updateBeforeRender(renderSessionData);
				}
			}
		}
		
		/**
		 * Allows for materials that animate or change (e.g., MovieMaterial) to 
		 * be updated after the render
		 * 
		 * @param renderSessionData		the data used in updating the material
		 */
		public function updateMaterialsAfterRender(renderSessionData:RenderSessionData):void
		{
			var um:IUpdateAfterMaterial;
			
			for (var m:* in materials){
				if(m is IUpdateAfterMaterial){
					um = m as IUpdateAfterMaterial;
					um.updateAfterRender(renderSessionData);
				}
			}
		}
		
		/**
		 * Registers a material
		 */
		public static function registerMaterial(material:MaterialObject3D):void
		{
			getInstance()._registerMaterial(material);
		}
		
		/**
		 * Unregisters a material
		 */
		public static function unRegisterMaterial(material:MaterialObject3D):void
		{
			getInstance()._unRegisterMaterial(material);
		}
		
		/**
		 * Returns a singleton instance of the <code>MaterialManager</code>
		 */
		public static function getInstance():MaterialManager
		{
			if(!instance){
				instance = new MaterialManager;
			}
			return instance;
		}
		
	}
}