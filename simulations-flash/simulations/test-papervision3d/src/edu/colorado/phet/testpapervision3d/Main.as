package edu.colorado.phet.testpapervision3d {
	
	import edu.colorado.phet.testpapervision3d.PaperBase;	
	import org.papervision3d.objects.primitives.Cone;
	import org.papervision3d.materials.ColorMaterial;
	
	public class Main extends PaperBase {
		public var cone : Cone = new Cone( new ColorMaterial( 0x0000FF, 0.5, true ) );
		protected var sceneWidth:Number;
        protected var sceneHeight:Number;
		
		public function Main() {
			sceneWidth = stage.stageWidth
            sceneHeight = stage.stageHeight;
			init( sceneWidth, sceneHeight );
		}
		
		override protected function init3d() : void {
			cone.scale = 3;			
			cone.pitch( -10 );
			default_scene.addChild( cone );
		}
		
		override protected function processFrame() : void {
			cone.yaw( 1 );
		}
	}
}