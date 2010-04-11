package edu.colorado.phet.rotator {

import flash.display.*;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.system.Security;
import flash.text.TextField;
import flash.text.TextFieldAutoSize;
import flash.text.TextFormat;

public class Rotator extends MovieClip {

    public function Rotator() {
        Security.allowDomain("192.168.1.64", "phetsims.colorado.edu", "phet.colorado.edu");
        var request : URLRequest = new URLRequest("http://192.168.1.64/files/rotator/mass-spring-lab-anim.swf");
        var loader : Loader = new Loader();

        loader.contentLoaderInfo.addEventListener(Event.COMPLETE, function( evt : Event ) {
            var clip : MovieClip = loader.content as MovieClip;
            clip.useHandCursor = true;
            clip.mouseEnabled = true;
            clip.buttonMode = true;
        });

        this.useHandCursor = true;
        this.buttonMode = true;

        loader.mouseEnabled = true;

        loader.load(request);
        addChild(loader);

        this.useHandCursor = true;

        var li : LoaderInfo = this.root.loaderInfo;

        var quantity : Number = Number(li.parameters.quantity);

        var url1 : String = li.parameters.url1;
        if ( !url1 ) { url1 = "/en/simulation/mass-spring-lab"; } // TODO: remove after dev
        var title1 : String = li.parameters.title1;
        if ( !title1 ) { title1 = "Masses & Springs"; } // TODO: remove after dev

        var format : TextFormat = new TextFormat();
        format.size = 12;
        format.bold = true;
        format.color = 0x555555;

        var titleText : TextField = new TextField();
        titleText.text = "Try \"" + title1 + "\" now >>";
        titleText.mouseEnabled = false;
        titleText.autoSize = TextFieldAutoSize.LEFT;
        titleText.backgroundColor = 0xFFFFFF;
        titleText.background = true;
        titleText.borderColor = 0x777777;
        titleText.border = true;
        titleText.setTextFormat(format);
        titleText.y = 200 - titleText.height - 1;
        addChild(titleText);

        var nextHolder : Sprite = new Sprite();
        nextHolder.mouseEnabled = true;
        nextHolder.useHandCursor = true;
        nextHolder.buttonMode = true;

        var nextText : TextField = new TextField();
        nextText.text = " > ";
        nextText.mouseEnabled = false;
        nextText.autoSize = TextFieldAutoSize.LEFT;
        nextText.backgroundColor = 0xFFFFFF;
        nextText.background = true;
        nextText.borderColor = 0x777777;
        nextText.border = true;
        nextText.setTextFormat(format);
        nextHolder.addChild(nextText);
        nextHolder.y = 200 - nextText.height - 1;
        nextHolder.x = 300 - nextText.width - 1;
        addChild(nextHolder);

        var prevHolder : Sprite = new Sprite();
        prevHolder.mouseEnabled = true;
        prevHolder.useHandCursor = true;
        prevHolder.buttonMode = true;

        var prevText : TextField = new TextField();
        prevText.text = " < ";
        prevText.mouseEnabled = false;
        prevText.autoSize = TextFieldAutoSize.LEFT;
        prevText.backgroundColor = 0xFFFFFF;
        prevText.background = true;
        prevText.borderColor = 0x777777;
        prevText.border = true;
        prevText.setTextFormat(format);
        prevHolder.addChild(prevText);
        prevHolder.y = 200 - prevText.height - 1;
        prevHolder.x = 300 - prevText.width - 1 - nextText.width;
        addChild(prevHolder);

        loader.addEventListener(MouseEvent.CLICK, function( evt : Event ) {
            navigateToURL(new URLRequest(url1), '_self');
        });

    }

}

}