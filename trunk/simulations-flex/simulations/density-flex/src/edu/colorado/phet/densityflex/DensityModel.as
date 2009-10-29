package edu.colorado.phet.densityflex {
import flash.geom.ColorTransform;

public class DensityModel {
    private var blocks:Array;
    private var cube:Block = new Block(50, 200, 450, 0, new ColorTransform(1, 0, 0));
    private var poolWidth:Number = 1500;
    private var poolHeight:Number = 750;
    private var poolDepth:Number = 500;
    private var waterHeight:Number = 550;
    private var volume:Number = poolWidth * poolDepth * waterHeight;

    public function DensityModel() {
        blocks = new Array();

        blocks.push(cube);
        blocks.push(new Block(10, 100, 150, 0, new ColorTransform(0, 1, 0)));
        blocks.push(new Block(100, 300, -150, 0, new ColorTransform(0, 0, 1)));
        blocks.push(new Block(50, 200, -450, 0, new ColorTransform(1, 1, 1)));
    }

    public function getBlocks():Array {
        return blocks;
    }

    function updateWater():void {
        var cubeVolume:Number = cube.getWidth() * cube.getHeight() * cube.getDepth();
        var idealHeight:Number = volume / (poolWidth * poolDepth);
        var highestHeight:Number = (volume + cubeVolume) / (poolWidth * poolDepth);

        if (cube.getY() - cube.getHeight() / 2 > -poolHeight + idealHeight) {
            waterHeight = idealHeight;
        }
        else if (cube.getY() + cube.getHeight() / 2 < -poolHeight + highestHeight) {
            waterHeight = highestHeight;
        }
        else {
            var bottomHeight:Number = poolHeight + (cube.getY() - cube.getHeight() / 2);
            var partialVolume:Number = volume - (bottomHeight * poolWidth * poolDepth);
            var partialHeight:Number = partialVolume / (poolWidth * poolDepth - cube.getWidth() * cube.getDepth());
            waterHeight = bottomHeight + partialHeight;
        }
    }

    function getPoolHeight():Number {
        return poolHeight;
    }

    function getWaterHeight():Number {
        return waterHeight;
    }

    function getPoolWidth():Number {
        return poolWidth;
    }

    function getPoolDepth():Number {
        return poolDepth;
    }
}
}