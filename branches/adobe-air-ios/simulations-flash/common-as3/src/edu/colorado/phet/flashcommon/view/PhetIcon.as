
package edu.colorado.phet.flashcommon.view {

import flash.display.*;
import flash.text.*;

public class PhetIcon extends Sprite {

    private var phet_txt: TextField;
    private var tFormat: TextFormat;

    public function PhetIcon() {
        this.phet_txt = new TextField();
        this.tFormat = new TextFormat();
        this.makeIcon();
        this.addChild( phet_txt );
    }//end of constructor

    private function makeIcon(): void {
        this.tFormat.font = "Times New Roman";
        this.tFormat.italic = true;
        this.tFormat.color = 0x0000ff;
        this.tFormat.size = 20;
        this.phet_txt.defaultTextFormat = this.tFormat;
        this.phet_txt.autoSize = TextFieldAutoSize.LEFT;
        this.phet_txt.text = " PhET ";	//need white spaces or autosize border clips italic text
        this.phet_txt.selectable = false;
        this.phet_txt.type = TextFieldType.DYNAMIC;
        //this.phet_txt.border = true;
    }//end makeIcon

}//end of class
}//end of package