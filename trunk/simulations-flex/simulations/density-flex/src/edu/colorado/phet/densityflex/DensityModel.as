package edu.colorado.phet.densityflex {
import flash.geom.ColorTransform;

public class DensityModel {
    private var blocks:Array;
    public function DensityModel() {
        blocks = new Array();
        blocks.push(new Block(50, 200, 450,0,new ColorTransform(1, 0, 0)));
        blocks.push(new Block(10, 100, 150,0,new ColorTransform(0, 1, 0)));
        blocks.push(new Block(100, 300, -150,0,new ColorTransform(0, 0, 1)));
        blocks.push(new Block(50, 200, -450,0,new ColorTransform(1, 1, 1)));
    }
    public function getBlocks():Array{
        return blocks;
    }
}
}