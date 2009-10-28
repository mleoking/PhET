package edu.colorado.phet.densityflex {
import away3d.materials.*;
import away3d.primitives.*;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.display.BitmapDataChannel;
import flash.display.Sprite;
import flash.geom.ColorTransform;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.text.TextField;
import flash.text.TextFormat;

import mx.core.BitmapAsset;

/**
 * This class represents the model object for a block.
 */
public class Block  {
    private var mass : Number;
    private var width : Number;
    private var height: Number;
    private var depth : Number;
    private var z:Number;
    private var color:ColorTransform;

    public function Block( initialMass : Number, size : Number, color : ColorTransform ) : void {
        this.width = size;
        this.height = size;
        this.depth = size;
        this.z = size / 2 + 101;
        this.mass = initialMass;
        this.color = color;
    }

    function getWidth():Number {
        return this.width;
    }

    function getHeight():Number {
        return this.height;
    }

    function getDepth():Number {
        return this.depth;
    }

    public function getZ():Number {
        return z;
    }

    public function getColor():ColorTransform {
        return color;
    }

    function getMass():Number {
        return mass;
    }}
}