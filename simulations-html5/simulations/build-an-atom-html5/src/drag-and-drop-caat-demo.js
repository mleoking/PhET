/**
 * @license
 *
 * The MIT License
 * Copyright (c) 2010-2011 Ibon Tolosana, Hyperandroid || http://labs.hyperandroid.com/

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

(function() {
    /**
     * Startup it all up when the document is ready.
     * Change for your favorite frameworks initialization code.
     */
    window.addEventListener(
            'load',
            function() {
                CAAT.modules.initialization.init(
                        800, 500,
                        'experiment-holder',
                        [],
                        __coordinates
                );
            },
            false);

    function __coordinates(director) {

        var scene= director.createScene();

        var cc= new CAAT.ActorContainer().
                setBounds( 0,0,director.width,director.height );
        cc.setGestureEnabled(true);
        scene.addChild(cc);

        var coords = new CAAT.TextActor().
                setFont("20px sans-serif").
                setTextAlign('left').
                setTextBaseline('top').
                setText("").
                setLocation(15,20).
                setTextFillStyle('black').
                setOutlineColor('#aaa').
                setOutline(true);
        scene.addChild(coords);

        var coords2 = new CAAT.TextActor().
                setFont("20px sans-serif").
                setTextAlign('left').
                setTextBaseline('top').
                setText("").
                setLocation(15,42).
                setTextFillStyle('black').
                setOutlineColor('#aaa').
                setOutline(true);
        scene.addChild(coords2);

        var coords3 = new CAAT.TextActor().
                setFont("20px sans-serif").
                setTextAlign('left').
                setTextBaseline('top').
                setText("").
                setLocation(15,64).
                setTextFillStyle('black').
                setOutlineColor('#aaa').
                setOutline(true);
        scene.addChild(coords3);

        // on doubleclick, zorder maximo.
        var dblclick= function(mouseEvent) {
            var actor= mouseEvent.source;
            if( null==actor ) {
                return;
            }

            var parent= actor.parent;
            parent.setZOrder(actor,Number.MAX_VALUE);
        };

        var np = 20;
        var s = 80;
        for ( var i = 0; i < np; i++) {
            var sc = 1 + Math.random() * .25;

            var p = new CAAT.ActorContainer().
                    setBounds(
                    Math.random() * director.canvas.width,
                    Math.random()* director.canvas.height,
                    s,
                    s).
                    setRotation( Math.PI*2*Math.random() ).
                    setScale( sc, sc ).
                    setFillStyle('#ff3fff');
            p.setGestureEnabled(true);
            p.mouseEnter= function(ev) {
                this.pointed= true;
            }
            p.mouseExit= function(ev) {
                this.pointed= false;
            }
            p.paint= function(director, time) {

                var canvas= director.ctx;

                if ( null!=this.parent && null!=this.fillStyle ) {
                    canvas.fillStyle= this.pointed ? 'orange' : (this.fillStyle!=null ? this.fillStyle : 'white'); //'white';
                    canvas.fillRect(0,0,this.width,this.height );
                }

                canvas.strokeStyle= this.pointed ? 'red' : 'black';
                canvas.strokeRect(0,0,this.width,this.height );

                canvas.strokeStyle='white';
                canvas.beginPath();
                canvas.moveTo(5,10);
                canvas.lineTo(20,10);
                canvas.lineTo(15,5);

                canvas.moveTo(20,10);
                canvas.lineTo(15,15);

                canvas.lineWidth=2;
                canvas.lineJoin='round';
                canvas.lineCap='round';

                canvas.stroke();


            };
            p.mouseDblClick= dblclick;
            cc.addChild(p);

            var fpaint= function(director,time) {
                var canvas= director.crc;

                if ( null!=this.parent && null!=this.fillStyle ) {
                    canvas.fillStyle= this.pointed ? 'orange' : (this.fillStyle!=null ? this.fillStyle : 'white'); //'white';
                    canvas.fillRect(0,0,this.width,this.height );
                }

                canvas.fillStyle='black';
                canvas.fillRect(1,1,5,5);
            };

            var p0= new CAAT.Actor().
                    setBounds( s/4, s/4, s/4, s/4 ).
                    setRotation( Math.PI*2*Math.random() ).
                    setFillStyle('#a03f00');
            p0.mouseDblClick= dblclick;
            p0.paint=fpaint;
            p.addChild(p0);

            var p1= new CAAT.Actor().
                    setBounds( s/2, s/2, s/4, s/4 ).
                    setRotation( Math.PI*2*Math.random() ).
                    setFillStyle('#ffff3f');
            p1.mouseDblClick= dblclick;
            p1.paint=fpaint;

            p.addChild(p1);
            p1.enableDrag();
            p0.enableDrag();
            p.enableDrag();

            p1.__mouseMove= p1.mouseMove;
            p0.__mouseMove= p0.mouseMove;
            p.__mouseMove= p.mouseMove;

            var mouseMoveHandler= function(mouseEvent) {
                var actor= mouseEvent.source;
                actor.__mouseMove(mouseEvent);

                coords.setText("Local Coord: ("+
                               ((mouseEvent.point.x*100)>>0)/100+","+
                               ((mouseEvent.point.y*100)>>0)/100+")");
                coords2.setText("Screen Coord: ("+
                                mouseEvent.screenPoint.x+","+
                                mouseEvent.screenPoint.y+")");
                coords3.setText(
                        "Parent Pos: ("+((actor.x*100)>>0)/100+","+((actor.y*100)>>0)/100+")" );
            };

            p.mouseMove= mouseMoveHandler;
            p0.mouseMove= mouseMoveHandler;
            p1.mouseMove= mouseMoveHandler;
        }

        cc.__mouseMove= scene.mouseMove;
        cc.mouseMove= function(mouseEvent) {
            mouseEvent.source.__mouseMove(mouseEvent);
            coords.setText("Local Coord: ("+mouseEvent.point.x+","+mouseEvent.point.y+")");
            coords2.setText("");
            coords3.setText("");
        };

        var gradient= director.crc.createLinearGradient(0,0,0,50);
        gradient.addColorStop(0,'green');
        gradient.addColorStop(0.5,'red');
        gradient.addColorStop(1,'yellow');

        // texts
        var cc1= new CAAT.ActorContainer().
                setBounds( 480,30, 300, 140 ).
                enableEvents(false).
                addBehavior(
                new CAAT.RotateBehavior().
                        setCycle(true).
                        setFrameTime( 0, 4000 ).
                        setValues( -Math.PI/8, Math.PI/8, .50, 0 ).  // anchor at 50%, 0%
                        setInterpolator(
                        new CAAT.Interpolator().createCubicBezierInterpolator(
                                {x:0,y:0},
                                {x:1,y:0},
                                {x:0,y:1},
                                {x:1,y:1},
                                true ) )
        );
        scene.addChild(cc1);

        var text= new CAAT.TextActor().
                setFont("30px sans-serif").
                setText("Perfect Pixel").
                calcTextSize(director).
                setTextAlign("center").
                setTextFillStyle(gradient).
                setOutline(true).
                cacheAsBitmap();
        cc1.addChild(text.setLocation((cc1.width-text.width)/2,0));

        var text2= new CAAT.TextActor().
                setFont("30px sans-serif").
                setTextAlign("center").
                setText("Collision detection").
                calcTextSize(director).
                setTextFillStyle(gradient).
                setOutline(true).
                cacheAsBitmap();
        cc1.addChild(text2.setLocation((cc1.width-text2.width)/2,30));

        var text3= new CAAT.TextActor().
                setFont("30px sans-serif").
                setTextAlign("center").
                setText("Drag Enabled").
                calcTextSize(director).
                setTextFillStyle(gradient).
                setOutline(true).
                cacheAsBitmap();
        cc1.addChild(text3.setLocation((cc1.width-text3.width)/2,60));

        var text4= new CAAT.TextActor().
                setFont("20px sans-serif").
                setTextAlign("center").
                setText("Drag + [Control,Shift,Alt]").
                calcTextSize(director).
                setTextFillStyle('black').
                setOutline(true).
                cacheAsBitmap();
        cc1.addChild(text4.setLocation((cc1.width-text4.width)/2,100));

        var text5= new CAAT.TextActor().
                setFont("20px sans-serif").
                setTextAlign("center").
                setText("Double Click").
                calcTextSize(director).
                setTextFillStyle('black').
                setOutline(true).
                cacheAsBitmap();
        cc1.addChild(text5.setLocation((cc1.width-text5.width)/2,120));

        return scene;
    }

})();
