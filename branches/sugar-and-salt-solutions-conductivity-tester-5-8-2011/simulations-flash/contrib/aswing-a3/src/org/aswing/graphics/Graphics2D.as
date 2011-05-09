/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.graphics
{

import flash.display.Graphics;

/**
 * Encapsulate and enhance flash.display.graphics drawing API.
 * <br>
 * To draw with this API, you need to create a pen.
 * To fill an area with this API, you need to create a brush.
 * <br>
 * You can find sampe usage of this api in the test package which will come with the source package.
 * <br>
 * <br>
 * Here is an sample on how to use the org.aswing.grphics package to draw stuff
 * 
 * <listing version="3.0"> 
 * 			var mySprite:Sprite = new Sprite();
 * 			g = new Graphics2D(mySprite.graphics);
 *			var color:ASColor = new ASColor();
 *			var matrix:Matrix = new Matrix();
 *			matrix.createGradientBox(100,100,90/(Math.PI*2),200,200);
 *			
 *			
 *			var pen:Pen = new Pen(2,color.getRGB());
 *			g.drawLine(pen,0,0,100,100);
 *			 
 *
 *			var gpen:GradientPen = new GradientPen(1,GradientType.LINEAR,[0xff00ff,0x00ff00,0xffff00],[1,1,1],[0,200,255],matrix);
 *			g.drawLine(gpen,400,100,500,500);
 *			
 *			var gBrush:GradientBrush= new GradientBrush(GradientType.LINEAR,[0xff00ff,0x00ff00,0xffff00],[1,1,1],[0,200,255],matrix);
 *			var sBrush:SolidBrush = new SolidBrush(ASColor.HALO_BLUE.getRGB(),0.3);
 *			var bBrush:BitmapBrush = new BitmapBrush(new BitmapData(50,50,false,0xffff00));
 *			
 *			g.fillEllipse(gBrush,100,100,300,200);
 *			g.fillRectangle(sBrush,100,100,100,100);
 *			g.fillRectangle(bBrush,0,0,50,50);
 * 
 * 			addChild(mySprite);
 * </listing> 
 * 
 * @see org.aswing.graphics
 * @author iiley
 */
public class Graphics2D {
	
	protected var target:Graphics;
	private var brush:IBrush;
	
	/**
	 * Constructor take instance of flash.display.Graphics as parameter. usually, you can the instance from a displayObject's graphics property
	 * 
	 * @param target where the graphics contexts will be paint on. the target is an instance of flash.display.Graphics
	 * @see http://livedocs.macromedia.com/flex/2/langref/flash/display/Graphics.html
	 */
	public function Graphics2D(target:Graphics){
		this.target = target;
	}
	
	protected function setTarget(target:Graphics):void{
		this.target = target;
	}
	
	protected function dispose():void{
		target = null;
	}
	
	private function startPen(p:IPen):void{
		p.setTo(target);
	}
	
	private function endPen():void{
		target.lineStyle();
		target.moveTo(0, 0); //avoid a drawing error
	}
	
	private function startBrush(b:IBrush):void{
		brush = b;
		b.beginFill(target);
	}
	
	private function endBrush():void{
		brush.endFill(target);
		target.moveTo(0, 0); //avoid a drawing error
	}
	
	//-------------------------------Public Functions-------------------
	/**
	 * Clears the graphics contexts drawn on the target Graphics.
	 */
	public function clear():void {
		if(target!= null) target.clear();
	}
	
	/**
	 * Draw a line between the points (x1, y1) and (x2, y2) in the target Graphics. 
	 * @param p the pen to draw. Pen can be a normal Pen or a GradientPen
	 * @param x1 the x corrdinate of the first point.
	 * @param y1 the y corrdinate of the first point.
	 * @param x2 the x corrdinate of the sencod point.
	 * @param y2 the y corrdinate of the sencod point.
	 */
	public function drawLine(p:IPen, x1:Number, y1:Number, x2:Number, y2:Number):void {
		startPen(p);
		line(x1, y1, x2, y2);
		endPen();
	}
	
	/**
	 * Draws a polyline. (not close figure automaticlly)<br>
	 * Start with the points[0] and end with the points[points.length-1] as a closed path. 
	 * 
	 * @param p the pen to draw
	 * @param points the Array contains all vertex points in the polygon.
	 * @see #polyline()
	 */
	public function drawPolyline(p:IPen, points:Array):void{
		startPen(p);
		polyline(points);
		endPen();
	}
	
	/**
	 * Fills a polygon.(not close figure automaticlly)<br>
	 * Start with the points[0] and end with the points[points.length-1] as a closed path. 
	 * 
	 * @param b the brush to fill.
	 * @param points the Array contains all vertex points in the polygon.
	 * @see #polyline()
	 */	
	public function fillPolyline(b:IBrush, points:Array):void{
		startBrush(b);
		polyline(points);
		endBrush();
	}
	
	/**
	 * Draws a polygon.(close figure automaticlly)<br>
	 * Start to draw a ploygon with the points[0] as the start point and go through all the points in the array then go back to the points[0] end it as a closed path. 
	 * 
	 * @param pen the pen to draw
	 * @param points the Array contains all vertex points in the polygon.
	 * @see #polygon()
	 */
	public function drawPolygon(pen:Pen, points:Array):void{
		startPen(pen);
		polygon(points);
		endPen();
	}
	
	/**
	 * Fills a polygon.(close figure automaticlly)<br>
	 * Start with the points[0] and end of the points[0] as a closed path. 
	 * 
	 * @param brush the brush to fill.
	 * @param points the Array contains all vertex points in the polygon.
	 * @see #polygon()
	 */	
	public function fillPolygon(brush:IBrush, points:Array):void{
		startBrush(brush);
		polygon(points);
		endBrush();
	}
	
	/**
	 * Fills a polygon ring.
	 * 
	 * @param b the brush to fill.
	 * @param points1 the first polygon's points.
	 * @param points2 the second polygon's points.
	 * @see #fillPolygon()
	 */
	public function fillPolygonRing(brush:IBrush, points1:Array, points2:Array):void{
		startBrush(brush);
		polygon(points1);
		polygon(points2);
		endBrush();
	}
	
	/**
	 * Draws a rectange.
	 * 
	 * @param pen the pen to draw.
	 * @param x the left top the rectange bounds' x corrdinate.
	 * @param y the left top the rectange bounds' y corrdinate.
	 * @param width the width of rectange bounds.
	 * @param height the height of rectange bounds.
	 */
	public function drawRectangle(pen:IPen, x:Number, y:Number, width:Number, height:Number):void {
		this.startPen(pen);
		this.rectangle(x, y, width, height);
		this.endPen();
	}
	
	/**
	 * Fills a rectange.
	 * 
	 * @param brush the brush to fill.
	 * @param x the left top the rectange bounds' x corrdinate.
	 * @param y the left top the rectange bounds' y corrdinate.
	 * @param width the width of rectange bounds.
	 * @param height the height of rectange bounds.
	 */	
	public function fillRectangle(brush:IBrush, x:Number, y:Number, width:Number, height:Number):void{
		startBrush(brush);
		rectangle(x,y,width,height);
		endBrush();
	}
	
	/**
	 * Fills a rectange ring.
	 * 
	 * @param brush the brush to fill.
	 * @param centerX the center of the ring's x corrdinate.
	 * @param centerY the center of the ring's y corrdinate.
	 * @param width1 the first rectange's width.
	 * @param height1 the first rectange's height.
	 * @param width2 the second rectange's width.
	 * @param height2 the second rectange's height.
	 */	
	public function fillRectangleRing(brush:IBrush, centerX:Number, centerY:Number, width1:Number, height1:Number, width2:Number, height2:Number):void{
		startBrush(brush);
		rectangle(centerX-width1/2, centerY-height1/2, width1, height1);
		rectangle(centerX-width2/2, centerY-height2/2, width2, height2);
		endBrush();
	}
	
	/**
	 * Fills a rectange ring with a specified thickness.
	 * 
	 * @param brush the brush to fill.
	 * @param x the left top the ring bounds' x corrdinate.
	 * @param y the left top the ring bounds' y corrdinate.
	 * @param width the width of ring periphery bounds.
	 * @param height the height of ring periphery bounds.
	 * @param thickness the thickness of the ring.
	 */
	public function fillRectangleRingWithThickness(brush:IBrush, x:Number, y:Number, width:Number, height:Number, thickness:Number):void{
		startBrush(brush);
		rectangle(x, y, width, height);
		rectangle(x+thickness, y+thickness, width -thickness*2, height - thickness*2);
		endBrush();
	}	
	
	/**
	 * Draws a circle.
	 * 
	 * @param pen the pen to draw.
	 * @param cx the center of the circle's x corrdinate.
	 * @param cy the center of the circle's y corrdinate.
	 * @param radius the radius of the circle.
	 */
	public function drawCircle(pen:IPen, centerX:Number, centerY:Number, radius:Number):void{
		startPen(pen);
		circle(centerX, centerY, radius);
		endPen();		
	}
	
	/**
	 * Fills a circle.
	 * 
	 * @param brush the brush to draw.
	 * @param centerX the center of the circle's x corrdinate.
	 * @param centerY the center of the circle's y corrdinate.
	 * @param radius the radius of the circle.
	 */
	public function fillCircle(brush:IBrush, centerX:Number, centerY:Number, radius:Number):void{
		startBrush(brush);
		circle(centerX, centerY, radius);
		endBrush();
	}
	
	/**
	 * Fills a circle ring.
	 * 
	 * @param brush the brush to draw.
	 * @param centerX the center of the ring's x corrdinate.
	 * @param centerY the center of the ring's y corrdinate.
	 * @param radius1 the first circle radius.
	 * @param radius2 the second circle radius.
	 */
	public function fillCircleRing(brush:IBrush, centerX:Number, centerY:Number, radius1:Number, radius2:Number):void{
		startBrush(brush);
		circle(centerX, centerY, radius1);
		circle(centerX, centerY, radius2);
		endBrush();
	}
	
	/**
	 * Fills a circle ring with a specified thickness.
	 * 
	 * @param brush the brush to draw.
	 * @param centerX the center of the ring's x corrdinate.
	 * @param centerY the center of the ring's y corrdinate.
	 * @param radius the radius of circle periphery.
	 * @param thickness the thickness of the ring.
	 */
	public function fillCircleRingWithThickness(brush:IBrush, centerX:Number, centerY:Number, radius:Number, thickness:Number):void{
		startBrush(brush);
		circle(centerX, centerY, radius);
		radius -= thickness;
		circle(centerX, centerY, radius);
		endBrush();
	}
	
	/**
	 * Draws a ellipse.
	 * 
	 * @param pen  the pen to draw.
	 * @param x the left top the ellipse bounds' x corrdinate.
	 * @param y the left top the ellipse bounds' y corrdinate.
	 * @param width the width of ellipse bounds.
	 * @param height the height of ellipse bounds.
	 */	
	public function drawEllipse(pen:IPen, x:Number, y:Number, width:Number, height:Number):void{
		startPen(pen);
		ellipse(x, y, width, height);
		endPen();
	}
	
	/**
	 * Fills a rectange.
	 * @param brush the brush to fill.
	 * @param x the left top the ellipse bounds' x corrdinate.
	 * @param y the left top the ellipse bounds' y corrdinate.
	 * @param width the width of ellipse bounds.
	 * @param height the height of ellipse bounds.
	 */		
	public function fillEllipse(brush:IBrush, x:Number, y:Number, width:Number, height:Number):void{
		startBrush(brush);
		ellipse(x, y, width, height);
		endBrush();
	}
	
	/**
	 * Fill a ellipse ring.
	 * @param brush the brush to fill.
	 * @param centerX the center of the ring's x corrdinate.
	 * @param centerY the center of the ring's y corrdinate.
	 * @param width1 the first eclipse's width.
	 * @param height1 the first eclipse's height.
	 * @param width2 the second eclipse's width.
	 * @param height2 the second eclipse's height.
	 */
	public function fillEllipseRing(brush:IBrush, centerX:Number, centerY:Number, width1:Number, height1:Number, width2:Number, height2:Number):void{
		startBrush(brush);
		ellipse(centerX-width1/2, centerY-height1/2, width1, height1);
		ellipse(centerX-width2/2, centerY-height2/2, width2, height2);
		endBrush();
	}
	
	/**
	 * Fill a ellipse ring with specified thickness.
	 * 
	 * @param brush the brush to fill.
	 * @param x the left top the ring bounds' x corrdinate.
	 * @param y the left top the ring bounds' y corrdinate.
	 * @param width the width of ellipse periphery bounds.
	 * @param height the height of ellipse periphery bounds.
	 * @param thickness the thickness of the ring.
	 */
	public function fillEllipseRingWithThickness(brush:IBrush, x:Number, y:Number, width:Number, height:Number, thickness:Number):void{
		startBrush(brush);
		ellipse(x, y, width, height);
		ellipse(x+thickness, y+thickness, width-thickness*2, height-thickness*2);
		endBrush();
	}	
	
	/**
	 * Draws a round rectangle.
	 * 
	 * @param pen the pen to draw.
	 * @param x the left top the rectangle bounds' x corrdinate.
	 * @param y the left top the rectangle bounds' y corrdinate.
	 * @param width the width of rectangle bounds.
	 * @param height the height of rectangle bounds.
	 * @param radius the top left corner's round radius.
	 * @param trR (optional)the top right corner's round radius. (miss this param default to same as radius)
	 * @param blR (optional)the bottom left corner's round radius. (miss this param default to same as radius)
	 * @param brR (optional)the bottom right corner's round radius. (miss this param default to same as radius)
	 */
	public function drawRoundRect(pen:IPen, x:Number, y:Number, width:Number, height:Number, radius:Number, trR:Number=-1, blR:Number=-1, brR:Number=-1):void{
		startPen(pen);
		roundRect(x, y, width, height, radius, trR, blR, brR);
		endPen();
	}
	
	/**
	 * Fills a round rectangle.
	 * @param brush the brush to fill.
	 * @param x the left top the rectangle bounds' x corrdinate.
	 * @param y the left top the rectangle bounds' y corrdinate.
	 * @param width the width of rectangle bounds.
	 * @param height the height of rectangle bounds.
	 * @param radius the radius of the top left corner, if other corner radius is -1, will use this radius as default
	 * @param topRightRadius	 the radius of the top right corner, if omitted, use the top left as default.
	 * @param bottomLeftRadius   the radius of the bottom left corner, if omitted, use the top left as default.
	 * @param bottomRightRadius  the radius of the bottom right corner, if omitted, use the top left as default.
	 */	
	public function fillRoundRect(brush:IBrush,x:Number,y:Number,width:Number,height:Number, radius:Number, topRightRadius:Number=-1, bottomLeftRadius:Number=-1, bottomRightRadius:Number=-1):void{
		startBrush(brush);
		roundRect(x,y,width,height,radius,topRightRadius,bottomLeftRadius,bottomRightRadius);
		endBrush();
	}
	
	/**
	 * Fill a round rect ring.
	 * @param brush the brush to fill
	 * @param centerX the center of the ring's x corrdinate
	 * @param centerY the center of the ring's y corrdinate
	 * @param width1 the first round rect's width
	 * @param height1 the first round rect's height
	 * @param radius1 the first round rect's round radius
	 * @param width2 the second round rect's width
	 * @param height2 the second round rect's height
	 * @param radius2 the second round rect's round radius
	 */	
	public function fillRoundRectRing(brush:IBrush,centerX:Number,centerY:Number,width1:Number,height1:Number,radius1:Number, width2:Number, height2:Number, radius2:Number):void{
		startBrush(brush);
		roundRect(centerX-width1/2, centerY-height1/2, width1, height1, radius1);
		roundRect(centerX-width2/2, centerY-height2/2, width2, height2, radius2);
		endBrush();
	}
	
	/**
	 * Fill a round rect ring with specified thickness.
	 * @param brush the brush to fill
	 * @param x the left top the ring bounds' x corrdinate
	 * @param y the left top the ring bounds' y corrdinate
	 * @param width the width of ring periphery bounds
	 * @param height the height of ring periphery bounds
	 * @param radius the round radius of the round rect
	 * @param thickness the thickness of the ring
	 * @param innerRadius the inboard round radius, default is <code>r-t</code>
	 */	
	public function fillRoundRectRingWithThickness(brush:IBrush, x:Number, y:Number, width:Number, height:Number, radius:Number, thickness:Number, innerRadius:Number=-1):void{
		startBrush(brush);
		roundRect(x, y, width, height, radius);
		if(innerRadius == -1) innerRadius = radius - thickness;
		roundRect(x+thickness, y+thickness, width-thickness*2, height-thickness*2, innerRadius);
		endBrush();
	}	
	
	/**
	 * Start to fill a closed area with brush
	 */
	public function beginFill(brush:IBrush):void{
		startBrush(brush);
	}
	
	/**
	 * Stop filling a closed area with brush
	 */
	public function endFill():void{
		endBrush();
		target.moveTo(0, 0); //avoid a drawing error
	}
	
	/**
	 * Start to draw lines with pen 
	 */
	public function beginDraw(pen:IPen):void{
		startPen(pen);
	}
	
	/**
	 * Stop drawing
	 */
	public function endDraw():void{
		endPen();
		target.moveTo(0, 0); //avoid a drawing error
	}
	
	/**
	 * Delegate Graphics.moveTo(x,y);
	 */
	public function moveTo(x:Number, y:Number):void{
		target.moveTo(x, y);
	}
	
	/**
	 * Paths a curve 
	 * @see http://livedocs.macromedia.com/flex/2/langref/flash/display/Graphics.html#curveTo()
	 */
	public function curveTo(controlX:Number, controlY:Number, anchorX:Number, anchorY:Number):void{
		target.curveTo(controlX, controlY, anchorX, anchorY);
	}
	public function lineTo(x:Number, y:Number):void{
		target.lineTo(x, y);
	}
	
	//---------------------------------------------------------------------------
	/**
	 * Paths a line between the points (x1, y1) and (x2, y2) in the target Graphics. 
	 * 
	 * @param x1 the x corrdinate of the first point.
	 * @param y1 the y corrdinate of the first point.
	 * @param x2 the x corrdinate of the sencod point.
	 * @param y2 the y corrdinate of the sencod point.
	 */
	public function line(x1:Number, y1:Number, x2:Number, y2:Number):void{
		target.moveTo(x1, y1);
		target.lineTo(x2, y2);
	}
	
	/**
	 * Paths a polygon.(close figure automaticlly)
	 * 
	 * @param points the points of the polygon, the array length should be larger than 1
	 * @see #drawPolygon()
	 * @see #fillPolygon()
	 * @see #polyline()
	 */
	public function polygon(points:Array):void{
		if(points.length > 1){
			polyline(points);
			target.lineTo(points[0].x, points[0].y);
		}
	}
	
	/**
	 * Paths a polyline(not close figure automaticlly).
	 * 
	 * @param points the points of the polygon, the array length should be larger than 1
	 * @see #drawPolyline()
	 * @see #fillPolyline()
	 * @see #polygon()
	 */
	public function polyline(points:Array):void{
		if(points.length > 1){
			target.moveTo(points[0].x, points[0].y);
			for(var i:Number=1; i<points.length; i++){
				target.lineTo(points[i].x, points[i].y);
			}
		}
	}
	
	/**
	 * Paths a rectangle.
	 * 
	 * @param x the x corrdinate of the rectangle.
	 * @param y the y corrdinate of the rectangle.
	 * @param width  the width corrdinate of rectangle.
	 * @param height the width corrdinate of rectangle.
	 * @see #drawRectangle()
	 * @see #fillRectangle()
	 */
	public function rectangle(x:Number,y:Number,width:Number,height:Number):void{
		
		target.drawRect(x,y,width,height);
		/* target.moveTo(x, y);
		target.lineTo(x+width,y);
		target.lineTo(x+width,y+height);
		target.lineTo(x,y+height);
		target.lineTo(x,y);*/	
 	}
	
	/**
	 * Paths an ellipse.
	 * @param x the x corrdinate of the ellipse.
	 * @param y the y corrdinate of the ellipse.
	 * @param width  the width corrdinate of ellipse.
	 * @param height the width corrdinate of ellipse.
	 * @see #drawEllipse()
	 * @see #fillEllipse()
	 */
	public function ellipse(x:Number, y:Number, width:Number, height:Number):void{
		
		target.drawEllipse(x,y,width,height);
		/* var pi:Number = Math.PI;
        var xradius:Number = width/2;
        var yradius:Number = height/ 2;
        var cx:Number = x + xradius;
        var cy:Number = y + yradius;
        var tanpi8:Number = Math.tan(pi / 8);
        var cospi4:Number = Math.cos(pi / 4);
        var sinpi4:Number = Math.sin(pi / 4);
        target.moveTo(xradius + cx, 0 + cy);
        target.curveTo(xradius + cx, (yradius * tanpi8) + cy, (xradius * cospi4) + cx, (yradius * sinpi4) + cy);
        target.curveTo((xradius * tanpi8) + cx, yradius + cy, 0 + cx, yradius + cy);
        target.curveTo(((-xradius) * tanpi8) + cx, yradius + cy, ((-xradius) * cospi4) + cx, (yradius * sinpi4) + cy);
        target.curveTo((-xradius) + cx, (yradius * tanpi8) + cy, (-xradius) + cx, 0 + cy);
        target.curveTo((-xradius) + cx, ((-yradius) * tanpi8) + cy, ((-xradius) * cospi4) + cx, ((-yradius) * sinpi4) + cy);
        target.curveTo(((-xradius) * tanpi8) + cx, (-yradius) + cy, 0 + cx, (-yradius) + cy);
        target.curveTo((xradius * tanpi8) + cx, (-yradius) + cy, (xradius * cospi4) + cx, ((-yradius) * sinpi4) + cy);
        target.curveTo(xradius + cx, ((-yradius) * tanpi8) + cy, xradius + cx, 0 + cy);		
 */	}
	
	/**
	 * Paths a circle
	 * 
	 * @param centerX x corrdinate of the center of the circle.
	 * @param centerY y corrdinate of the center of the circle.
	 * @param radius  the radius of circle.
	 * @see #drawCircle()
	 * @see #fillCircle()
	 */
	public function circle(centerX:Number, centerY:Number, radius:Number):void{
		target.drawCircle(centerX,centerY,radius);
		//ellipse(cx-r, cy-r, r*2, r*2);
//		target.moveTo(cx, cy - r);
//		target.curveTo(cx + r, cy - r, cx + r, cy);
//		target.curveTo(cx + r, cy + r, cx, cy + r);
//		target.curveTo(cx - r, cy + r, cx - r, cy);
//		target.curveTo(cx - r, cy - r, cx, cy - r);
	}
	
	/**
	 * Paths a round rect.
	 * 
	 * @param x the x corrdinate of the roundRect.
	 * @param y the y corrdinate of the roundRect.
	 * @param width  the width corrdinate of roundRect.
	 * @param height the width corrdinate of roundRect.
	 * @param radius the radius of the top left corner, if other corner radius is -1, will use this radius as default
	 * @param topRightRadius	 (optional)the radius of the top right corner, if omitted, use the top left as default.
	 * @param bottomLeftRadius   (optional)the radius of the bottom left corner, if omitted, use the top left as default.
	 * @param bottomRightRadius  (optional)the radius of the bottom right corner, if omitted, use the top left as default.
	 * @see #drawRoundRect()
	 * @see #fillRoundRect()
	 */
	public function roundRect(x:Number,y:Number,width:Number,height:Number, radius:Number, topRightRadius:Number=-1, bottomLeftRadius:Number=-1, bottomRightRadius:Number=-1):void{
		//var tlR:Number = radius;
		if(topRightRadius == -1) topRightRadius = radius;
		if(bottomLeftRadius == -1) bottomLeftRadius = radius;
		if(bottomRightRadius == -1) bottomRightRadius = radius;
		
		target.drawRoundRectComplex(x,y,width,height,radius,topRightRadius,bottomLeftRadius,bottomRightRadius);
		
		/* 
		//Bottom right
		target.moveTo(x+blR, y+height);
		target.lineTo(x+width-brR, y+height);
		target.curveTo(x+width, y+height, x+width, y+height-blR);
		//Top right
		target.lineTo (x+width, y+trR);
		target.curveTo(x+width, y, x+width-trR, y);
		//Top left
		target.lineTo (x+tlR, y);
		target.curveTo(x, y, x, y+tlR);
		//Bottom left
		target.lineTo (x, y+height-blR );
		target.curveTo(x, y+height, x+blR, y+height); */
	}
	
	/**
	 * @private
	 * don't generate doc for this method yet
	 * Paths a wedge.
	 */
	public function wedge(radius:Number, x:Number, y:Number, angle:Number):void {
		target.moveTo(0, 0);
		target.lineTo(radius, 0);
		var nSeg:Number = Math.floor(angle/30);
		var pSeg:Number = angle-nSeg*30;
		var a:Number = 0.268;
		var endx:Number;
		var endy:Number;
		var ax:Number;
		var ay:Number;
		var storeCount:Number=0;
		for (var i:Number = 0; i<nSeg; i++) {
			endx = radius*Math.cos((i+1)*30*(Math.PI/180));
			endy = radius*Math.sin((i+1)*30*(Math.PI/180));
			ax = endx+radius*a*Math.cos(((i+1)*30-90)*(Math.PI/180));
			ay = endy+radius*a*Math.sin(((i+1)*30-90)*(Math.PI/180));
			target.curveTo(ax, ay, endx, endy);
			storeCount=i+1;
		}
		if (pSeg>0) {
			a = Math.tan(pSeg/2*(Math.PI/180));
			endx = radius*Math.cos((storeCount*30+pSeg)*(Math.PI/180));
			endy = radius*Math.sin((storeCount*30+pSeg)*(Math.PI/180));
			ax = endx+radius*a*Math.cos((storeCount*30+pSeg-90)*(Math.PI/180));
			ay = endy+radius*a*Math.sin((storeCount*30+pSeg-90)*(Math.PI/180));
			target.curveTo(ax, ay, endx, endy);
		}
		target.lineTo(0, 0);
	/* 	target._rotation = rot;
		target._x = x;
		target._y = y; */
	}	
	
	
}


}