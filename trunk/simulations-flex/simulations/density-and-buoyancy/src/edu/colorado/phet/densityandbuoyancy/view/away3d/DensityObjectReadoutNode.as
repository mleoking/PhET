//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;

import flash.text.TextFormat;

//REVIEW class doc
public class DensityObjectReadoutNode {
    private var _textReadout: TextFieldMesh;
    private var fontSize: Number;

    public function DensityObjectReadoutNode( densityObject: DensityObject, fontSize: Number ) {
        this.fontSize = fontSize;
        _textReadout = new TextFieldMesh( "hello", createLabelTextFormat() );
    }

    public function get textReadout(): TextFieldMesh {
        return _textReadout;
    }

    protected function createLabelTextFormat(): TextFormat {
        var format: TextFormat = new TextFormat();
        format.size = fontSize;
        format.bold = true;
        format.font = "Arial";
        return format;
    }

    public function setReadoutText( str: String ): void {
        textReadout.text = str;
    }

    public function set x( x: Number ): void {
        textReadout.x = x;
    }

    public function set y( y: Number ): void {
        textReadout.y = y;
    }

    public function set z( z: Number ): void {
        textReadout.z = z;
    }

    public function set visible( b: Boolean ): void {
        _textReadout.visible = b;
    }
}
}