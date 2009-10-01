package edu.colorado.phet.testaway3d {
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.BitmapDataChannel;
	import flash.display.MovieClip;
	import flash.display.Sprite;
    import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.ColorTransform;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.draw.*;
	import away3d.core.filter.*;
	import away3d.core.geom.*;
	import away3d.core.light.*;
	import away3d.core.math.*;
	import away3d.core.render.*;
	import away3d.events.*;
	import away3d.lights.*;
	import away3d.materials.*;
	import away3d.primitives.*;
	
	import edu.colorado.phet.testaway3d.WallBitmapData;
	
	public class Block extends Cube {
		
		private var frontSprite : Sprite;
		private var mass : Number;
		
		public function Block( initialMass : Number, size : Number, color : ColorTransform ) {
			this.width = size;
			this.height = size;
			this.depth = size;
			this.z = size / 2 + 101;
			this.useHandCursor = true;
			this.mass = initialMass;
			
			frontSprite = new Sprite();
			var wallData : BitmapData = new WallBitmapData( size, size );
			var imageRect : Rectangle = new Rectangle( 0, 0, wallData.width, wallData.height );
			wallData.colorTransform( imageRect, new ColorTransform( 1.0, 0.5, 0.5 ) );
			var coloredData : BitmapData = new WallBitmapData( size, size );
			if( color.redMultiplier < 0.5 ) {
				coloredData.copyChannel( wallData, imageRect, new Point( 0, 0 ), BitmapDataChannel.GREEN, BitmapDataChannel.RED );
			} else {
				coloredData.copyChannel( wallData, imageRect, new Point( 0, 0 ), BitmapDataChannel.RED, BitmapDataChannel.RED );
			}
			if( color.greenMultiplier < 0.5 ) {
				coloredData.copyChannel( wallData, imageRect, new Point( 0, 0 ), BitmapDataChannel.GREEN, BitmapDataChannel.GREEN );
			} else {
				coloredData.copyChannel( wallData, imageRect, new Point( 0, 0 ), BitmapDataChannel.RED, BitmapDataChannel.GREEN );
			}
			if( color.blueMultiplier < 0.5 ) {
				coloredData.copyChannel( wallData, imageRect, new Point( 0, 0 ), BitmapDataChannel.GREEN, BitmapDataChannel.BLUE );
			} else {
				coloredData.copyChannel( wallData, imageRect, new Point( 0, 0 ), BitmapDataChannel.RED, BitmapDataChannel.BLUE );
			}
			
			frontSprite.addChild( new Bitmap( coloredData ) );
			
			var tf : TextField = new TextField();
			tf.text = String( mass ) + " kg";
			tf.height = wallData.height;
			tf.width = wallData.width;
			var format : TextFormat = new TextFormat();
			format.size = int( 45 * (200 / size) );
			format.bold = true;
			format.font = "Arial";
			tf.multiline = true;
			tf.setTextFormat( format );
			frontSprite.addChild( tf );
			
			var frontMaterial : MovieMaterial = new MovieMaterial( frontSprite );
			var redWallMaterial : BitmapMaterial = new BitmapMaterial( coloredData );
			
			this.cubeMaterials.left = this.cubeMaterials.right = this.cubeMaterials.top = this.cubeMaterials.bottom = this.cubeMaterials.front = redWallMaterial;
			
			this.cubeMaterials.back = frontMaterial;
		}
	}
}