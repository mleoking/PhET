package edu.colorado.phet.densityflex {
import flash.geom.ColorTransform;

import mx.controls.Alert;

public class DensityModel {
    private var blocks : Array;
    private var cube : Block = new Block(50, 200, 450, 0, new ColorTransform(1, 0, 0));
    private var poolWidth : Number = 1500;
    private var poolHeight : Number = 750;
    private var poolDepth : Number = 500;
    private var waterHeight : Number = 550;
    private var volume : Number = poolWidth * poolDepth * waterHeight;

    public function DensityModel() {
        blocks = new Array();

        blocks.push(cube);
        blocks.push(new Block(10, 100, 150, 0, new ColorTransform(0, 1, 0)));
        blocks.push(new Block(100, 300, -150, 0, new ColorTransform(0, 0, 1)));
        blocks.push(new Block(50, 200, -450, 0, new ColorTransform(1, 1, 1)));
    }

    public function getBlocks() : Array {
        return blocks;
    }

    public function updateWater() : void {
        var block : Block;
        var sortedHeights : Array = new Array();
        for ( var key : String in blocks ) {
            block = blocks[key];
            var top : Object = new Object();
            top.y = block.getTopY();
            top.pos = 1;
            top.block = block;
            var bottom : Object = new Object();
            bottom.y = block.getBottomY();
            bottom.pos = 0;
            bottom.block = block;
            sortedHeights.push(top);
            sortedHeights.push(bottom);
        }
        sortedHeights.sortOn(["y"], [Array.NUMERIC]);

        var curHeight : Number = 0;
        var volumeToGo : Number = volume;
        var crossSection : Number = poolWidth * poolDepth;

        for ( var i : String in sortedHeights ) {
            var ob : Object = sortedHeights[i];
            var pos : Number = ob.pos;
            var by : Number = ob.y + poolHeight;
            block = ob.block;
            var idealHeight : Number = volumeToGo / crossSection + curHeight;
            if ( idealHeight < by ) {
                curHeight = idealHeight;
                volumeToGo = 0;
                break;
            }
            var heightGain : Number = by - curHeight;
            volumeToGo -= crossSection * heightGain;
            curHeight = by;
            if ( pos == 0 ) {
                // bottom of block
                crossSection -= block.getWidth() * block.getDepth();
            }
            else {
                // top of block
                crossSection += block.getWidth() * block.getDepth();
            }
        }

        // fill it up the rest of the way
        curHeight += volumeToGo / crossSection;

        waterHeight = curHeight;
    }

    public function getPoolHeight() : Number {
        return poolHeight;
    }

    public function getWaterHeight() : Number {
        return waterHeight;
    }

    public function getPoolWidth() : Number {
        return poolWidth;
    }

    public function getPoolDepth() : Number {
        return poolDepth;
    }
}
}