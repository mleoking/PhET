/**
 * EaselJS
 * Visit http://easeljs.com/ for documentation, updates and examples.
 *
 * Copyright (c) 2011 Grant Skinner
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 **/
(function( k ) {
    var c = function() {
        throw"UID cannot be instantiated";
    };
    c._nextID = 0;
    c.get = function() {
        return c._nextID++
    };
    k.UID = c
})( window );
(function( k ) {
    var c = function() {
        throw"Ticker cannot be instantiated.";
    };
    c.useRAF = null;
    c._listeners = null;
    c._pauseable = null;
    c._paused = false;
    c._inited = false;
    c._startTime = 0;
    c._pausedTime = 0;
    c._ticks = 0;
    c._pausedTickers = 0;
    c._interval = 50;
    c._lastTime = 0;
    c._times = null;
    c._tickTimes = null;
    c._rafActive = false;
    c._timeoutID = null;
    c.addListener = function( b, a ) {
        c._inited || c.init();
        c.removeListener( b );
        c._pauseable[c._listeners.length] = a == null ? true : a;
        c._listeners.push( b )
    };
    c.init = function() {
        c._inited = true;
        c._times = [];
        c._tickTimes =
        [];
        c._pauseable = [];
        c._listeners = [];
        c._times.push( c._startTime = c._getTime() );
        c.setInterval( c._interval )
    };
    c.removeListener = function( b ) {
        c._listeners != null && (b = c._listeners.indexOf( b ), b != -1 && (c._listeners.splice( b, 1 ), c._pauseable.splice( b, 1 )))
    };
    c.removeAllListeners = function() {
        c._listeners = [];
        c._pauseable = []
    };
    c.setInterval = function( b ) {
        c._lastTime = c._getTime();
        c._interval = b;
        c.timeoutID != null && clearTimeout( c.timeoutID );
        if ( c.useRAF ) {
            if ( c._rafActive ) {
                return;
            }
            c._rafActive = true;
            var a = k.requestAnimationFrame ||
                    k.webkitRequestAnimationFrame || k.mozRequestAnimationFrame || k.oRequestAnimationFrame || k.msRequestAnimationFrame;
            if ( a ) {
                a( c._handleAF );
                return
            }
        }
        if ( c._inited ) {
            c.timeoutID = setTimeout( c._handleTimeout, b )
        }
    };
    c.getInterval = function() {
        return c._interval
    };
    c.setFPS = function( b ) {
        c.setInterval( 1E3 / b )
    };
    c.getFPS = function() {
        return 1E3 / c._interval
    };
    c.getMeasuredFPS = function( b ) {
        if ( c._times.length < 2 ) {
            return-1;
        }
        b == null && (b = c.getFPS() >> 1);
        b = Math.min( c._times.length - 1, b );
        return 1E3 / ((c._times[0] - c._times[b]) / b)
    };
    c.setPaused =
    function( b ) {
        c._paused = b
    };
    c.getPaused = function() {
        return c._paused
    };
    c.getTime = function( b ) {
        return c._getTime() - c._startTime - (b ? c._pausedTime : 0)
    };
    c.getTicks = function( b ) {
        return c._ticks - (b ? c._pausedTickers : 0)
    };
    c._handleAF = function( b ) {
        b - c._lastTime >= c._interval - 1 && c._tick();
        c.useRAF ? (k.requestAnimationFrame || k.webkitRequestAnimationFrame || k.mozRequestAnimationFrame || k.oRequestAnimationFrame || k.msRequestAnimationFrame)( c._handleAF, c.animationTarget ) : c._rafActive = false
    };
    c._handleTimeout = function() {
        c._tick();
        if ( !c.useRAF ) {
            c.timeoutID = setTimeout( c._handleTimeout, c._interval )
        }
    };
    c._tick = function() {
        c._ticks++;
        var b = c._getTime(), a = b - c._lastTime, n = c._paused;
        n && (c._pausedTickers++, c._pausedTime += a);
        c._lastTime = b;
        for ( var i = c._pauseable, e = c._listeners.slice(), d = e ? e.length : 0, f = 0; f < d; f++ ) {
            var g = i[f], j = e[f];
            j == null || n && g || j.tick == null || j.tick( a, n )
        }
        for ( c._tickTimes.unshift( c._getTime() - b ); c._tickTimes.length > 100; ) {
            c._tickTimes.pop();
        }
        for ( c._times.unshift( b ); c._times.length > 100; ) {
            c._times.pop()
        }
    };
    c._getTime = function() {
        return(new Date).getTime()
    };
    k.Ticker = c
})( window );
(function( k ) {
    var c = function( a, b, i, c, d ) {
        this.initialize( a, b, i, c, d )
    }, b = c.prototype;
    b.stageX = 0;
    b.stageY = 0;
    b.type = null;
    b.nativeEvent = null;
    b.onMouseMove = null;
    b.onMouseUp = null;
    b.target = null;
    b.initialize = function( a, b, i, c, d ) {
        this.type = a;
        this.stageX = b;
        this.stageY = i;
        this.target = c;
        this.nativeEvent = d
    };
    b.clone = function() {
        return new c( this.type, this.stageX, this.stageY, this.target, this.nativeEvent )
    };
    b.toString = function() {
        return"[MouseEvent (type=" + this.type + " stageX=" + this.stageX + " stageY=" + this.stageY + ")]"
    };
    k.MouseEvent =
    c
})( window );
(function( k ) {
    var c = function( a, b, i, c, d, f ) {
        this.initialize( a, b, i, c, d, f )
    }, b = c.prototype;
    c.identity = null;
    c.DEG_TO_RAD = Math.PI / 180;
    b.a = 1;
    b.b = 0;
    b.c = 0;
    b.d = 1;
    b.tx = 0;
    b.ty = 0;
    b.alpha = 1;
    b.shadow = null;
    b.compositeOperation = null;
    b.initialize = function( a, b, i, c, d, f ) {
        if ( a != null ) {
            this.a = a;
        }
        this.b = b || 0;
        this.c = i || 0;
        if ( c != null ) {
            this.d = c;
        }
        this.tx = d || 0;
        this.ty = f || 0
    };
    b.prepend = function( a, b, i, c, d, f ) {
        var g = this.tx;
        if ( a != 1 || b != 0 || i != 0 || c != 1 ) {
            var j = this.a, h = this.c;
            this.a = j * a + this.b * i;
            this.b = j * b + this.b * c;
            this.c = h * a + this.d * i;
            this.d =
            h * b + this.d * c
        }
        this.tx = g * a + this.ty * i + d;
        this.ty = g * b + this.ty * c + f
    };
    b.append = function( a, b, c, e, d, f ) {
        var g = this.a, j = this.b, h = this.c, l = this.d;
        this.a = a * g + b * h;
        this.b = a * j + b * l;
        this.c = c * g + e * h;
        this.d = c * j + e * l;
        this.tx = d * g + f * h + this.tx;
        this.ty = d * j + f * l + this.ty
    };
    b.prependMatrix = function( a ) {
        this.prepend( a.a, a.b, a.c, a.d, a.tx, a.ty );
        this.prependProperties( a.alpha, a.shadow, a.compositeOperation )
    };
    b.appendMatrix = function( a ) {
        this.append( a.a, a.b, a.c, a.d, a.tx, a.ty );
        this.appendProperties( a.alpha, a.shadow, a.compositeOperation )
    };
    b.prependTransform = function( a, b, i, e, d, f, g, j, h ) {
        if ( d % 360 ) {
            var l = d * c.DEG_TO_RAD, d = Math.cos( l ), l = Math.sin( l );
        }
        else {
            d = 1, l = 0;
        }
        if ( j || h ) {
            this.tx -= j, this.ty -= h;
        }
        f || g ? (f *= c.DEG_TO_RAD, g *= c.DEG_TO_RAD, this.prepend( d * i, l * i, -l * e, d * e, 0, 0 ), this.prepend( Math.cos( g ), Math.sin( g ), -Math.sin( f ), Math.cos( f ), a, b )) : this.prepend( d * i, l * i, -l * e, d * e, a, b )
    };
    b.appendTransform = function( a, b, i, e, d, f, g, j, h ) {
        if ( d % 360 ) {
            var l = d * c.DEG_TO_RAD, d = Math.cos( l ), l = Math.sin( l );
        }
        else {
            d = 1, l = 0;
        }
        f || g ? (f *= c.DEG_TO_RAD, g *= c.DEG_TO_RAD, this.append( Math.cos( g ),
                                                                     Math.sin( g ), -Math.sin( f ), Math.cos( f ), a, b ), this.append( d * i, l * i, -l * e, d * e, 0, 0 )) : this.append( d * i, l * i, -l * e, d * e, a, b );
        if ( j || h ) {
            this.tx -= j * this.a + h * this.c, this.ty -= j * this.b + h * this.d
        }
    };
    b.rotate = function( a ) {
        var b = Math.cos( a ), a = Math.sin( a ), c = this.a, e = this.c, d = this.tx;
        this.a = c * b - this.b * a;
        this.b = c * a + this.b * b;
        this.c = e * b - this.d * a;
        this.d = e * a + this.d * b;
        this.tx = d * b - this.ty * a;
        this.ty = d * a + this.ty * b
    };
    b.skew = function( a, b ) {
        a *= c.DEG_TO_RAD;
        b *= c.DEG_TO_RAD;
        this.append( Math.cos( b ), Math.sin( b ), -Math.sin( a ), Math.cos( a ), 0, 0 )
    };
    b.scale =
    function( a, b ) {
        this.a *= a;
        this.d *= b;
        this.tx *= a;
        this.ty *= b
    };
    b.translate = function( a, b ) {
        this.tx += a;
        this.ty += b
    };
    b.identity = function() {
        this.alpha = this.a = this.d = 1;
        this.b = this.c = this.tx = this.ty = 0;
        this.shadow = this.compositeOperation = null
    };
    b.invert = function() {
        var a = this.a, b = this.b, c = this.c, e = this.d, d = this.tx, f = a * e - b * c;
        this.a = e / f;
        this.b = -b / f;
        this.c = -c / f;
        this.d = a / f;
        this.tx = (c * this.ty - e * d) / f;
        this.ty = -(a * this.ty - b * d) / f
    };
    b.isIdentity = function() {
        return this.tx == 0 && this.ty == 0 && this.a == 1 && this.b == 0 && this.c == 0 && this.d ==
                                                                                            1
    };
    b.decompose = function( a ) {
        a == null && (a = {});
        a.x = this.tx;
        a.y = this.ty;
        a.scaleX = Math.sqrt( this.a * this.a + this.b * this.b );
        a.scaleY = Math.sqrt( this.c * this.c + this.d * this.d );
        var b = Math.atan2( -this.c, this.d ), i = Math.atan2( this.b, this.a );
        b == i ? (a.rotation = i / c.DEG_TO_RAD, this.a < 0 && this.d >= 0 && (a.rotation += a.rotation <= 0 ? 180 : -180), a.skewX = a.skewY = 0) : (a.skewX = b / c.DEG_TO_RAD, a.skewY = i / c.DEG_TO_RAD);
        return a
    };
    b.reinitialize = function( a, b, c, e, d, f, g, j, h ) {
        this.initialize( a, b, c, e, d, f );
        this.alpha = g || 1;
        this.shadow = j;
        this.compositeOperation =
        h;
        return this
    };
    b.appendProperties = function( a, b, c ) {
        this.alpha *= a;
        this.shadow = b || this.shadow;
        this.compositeOperation = c || this.compositeOperation
    };
    b.prependProperties = function( a, b, c ) {
        this.alpha *= a;
        this.shadow = this.shadow || b;
        this.compositeOperation = this.compositeOperation || c
    };
    b.clone = function() {
        var a = new c( this.a, this.b, this.c, this.d, this.tx, this.ty );
        a.shadow = this.shadow;
        a.alpha = this.alpha;
        a.compositeOperation = this.compositeOperation;
        return a
    };
    b.toString = function() {
        return"[Matrix2D (a=" + this.a + " b=" + this.b +
              " c=" + this.c + " d=" + this.d + " tx=" + this.tx + " ty=" + this.ty + ")]"
    };
    c.identity = new c( 1, 0, 0, 1, 0, 0 );
    k.Matrix2D = c
})( window );
(function( k ) {
    var c = function( a, b ) {
        this.initialize( a, b )
    }, b = c.prototype;
    b.x = 0;
    b.y = 0;
    b.initialize = function( a, b ) {
        this.x = a == null ? 0 : a;
        this.y = b == null ? 0 : b
    };
    b.clone = function() {
        return new c( this.x, this.y )
    };
    b.toString = function() {
        return"[Point (x=" + this.x + " y=" + this.y + ")]"
    };
    k.Point = c
})( window );
(function( k ) {
    var c = function( a, b, c, e ) {
        this.initialize( a, b, c, e )
    }, b = c.prototype;
    b.x = 0;
    b.y = 0;
    b.width = 0;
    b.height = 0;
    b.initialize = function( a, b, c, e ) {
        this.x = a == null ? 0 : a;
        this.y = b == null ? 0 : b;
        this.width = c == null ? 0 : c;
        this.height = e == null ? 0 : e
    };
    b.clone = function() {
        return new c( this.x, this.y, this.width, this.height )
    };
    b.toString = function() {
        return"[Rectangle (x=" + this.x + " y=" + this.y + " width=" + this.width + " height=" + this.height + ")]"
    };
    k.Rectangle = c
})( window );
(function( k ) {
    var c = function( a, b, c, e ) {
        this.initialize( a, b, c, e )
    }, b = c.prototype;
    c.identity = null;
    b.color = null;
    b.offsetX = 0;
    b.offsetY = 0;
    b.blur = 0;
    b.initialize = function( a, b, c, e ) {
        this.color = a;
        this.offsetX = b;
        this.offsetY = c;
        this.blur = e
    };
    b.toString = function() {
        return"[Shadow]"
    };
    b.clone = function() {
        return new c( this.color, this.offsetX, this.offsetY, this.blur )
    };
    c.identity = new c( "transparent", 0, 0, 0 );
    k.Shadow = c
})( window );
(function( k ) {
    var c = function( a ) {
        this.initialize( a )
    }, b = c.prototype;
    b.complete = true;
    b._animations = null;
    b._frames = null;
    b._images = null;
    b._data = null;
    b._loadCount = 0;
    b._frameHeight = 0;
    b._frameWidth = 0;
    b._numFrames = 0;
    b._regX = 0;
    b._regY = 0;
    b.initialize = function( a ) {
        var b, c, e;
        if ( a != null ) {
            if ( a.images && (c = a.images.length) > 0 ) {
                e = this._images = [];
                for ( b = 0; b < c; b++ ) {
                    var d = a.images[b];
                    if ( !(d instanceof Image) ) {
                        var f = d, d = new Image;
                        d.src = f
                    }
                    e.push( d );
                    if ( !d.getContext && !d.complete ) {
                        this._loadCount++, this.complete = false, d.onload =
                                                                  this._handleImageLoad()
                    }
                }
            }
            if ( a.frames != null ) {
                if ( a.frames instanceof Array ) {
                    this._frames = [];
                    e = a.frames;
                    for ( b = 0, c = e.length; b < c; b++ ) {
                        d = e[b], this._frames.push( {image:this._images[d[4] ? d[4] : 0],rect:new Rectangle( d[0], d[1], d[2], d[3] ),regX:d[5] || 0,regY:d[6] || 0} )
                    }
                }
                else {
                    c = a.frames, this._frameWidth = c.width, this._frameHeight = c.height, this._regX = c.regX || 0, this._regY = c.regY || 0, this._numFrames = c.count, this._loadCount == 0 && this._calculateFrames();
                }
            }
            if ( (c = a.animations) != null ) {
                this._animations = [];
                this._data = {};
                for ( var g in c ) {
                    a =
                    {name:g};
                    d = c[g];
                    if ( isNaN( d ) ) {
                        if ( d instanceof Array ) {
                            a.frequency = d[3];
                            a.next = d[2];
                            e = a.frames = [];
                            for ( b = d[0]; b <= d[1]; b++ ) {
                                e.push( b )
                            }
                        }
                        else {
                            a.frequency = d.frequency, a.next = d.next, e = a.frames = d.frames.slice( 0 );
                        }
                    }
                    else {
                        e = a.frames = [d];
                    }
                    a.next = e.length < 2 || a.next == false ? null : a.next == true ? g : a.next;
                    if ( !a.frequency ) {
                        a.frequency = 1;
                    }
                    this._animations.push( g );
                    this._data[g] = a
                }
            }
        }
    };
    b.getNumFrames = function( a ) {
        return a == null ? this._frames ? this._frames.length : this._numFrames : (a = this._data[a], a == null ? 0 : a.frames.length)
    };
    b.getAnimations =
    function() {
        return this._animations.slice( 0 )
    };
    b.getAnimation = function( a ) {
        return this._data[a]
    };
    b.getFrame = function( a ) {
        return this.complete && this._frames && (frame = this._frames[a]) ? frame : null
    };
    b.toString = function() {
        return"[SpriteSheet]"
    };
    b.clone = function() {
        var a = new c;
        a.complete = this.complete;
        a._animations = this._animations;
        a._frames = this._frames;
        a._images = this._images;
        a._data = this._data;
        a._frameHeight = this._frameHeight;
        a._frameWidth = this._frameWidth;
        a._numFrames = this._numFrames;
        a._loadCount = this._loadCount;
        return a
    };
    b._handleImageLoad = function() {
        if ( --this._loadCount == 0 ) {
            this._calculateFrames(), this.complete = true
        }
    };
    b._calculateFrames = function() {
        if ( !(this._frames || this._frameWidth == 0) ) {
            this._frames = [];
            for ( var a = 0, b = this._frameWidth, c = this._frameHeight, e = 0, d = this._images; e < d.length; e++ ) {
                for ( var f = d[e], g = (f.width + 1) / b | 0, j = (f.height + 1) / c | 0, j = this._numFrames > 0 ? Math.min( this._numFrames - a, g * j ) : g * j, h = 0; h < j; h++ ) {
                    this._frames.push( {image:f,rect:new Rectangle( h % g * b, (h / g | 0) * c, b, c ),regX:this._regX,regY:this._regY} );
                }
                a += j
            }
            this._numFrames = a
        }
    };
    k.SpriteSheet = c
})( window );
(function( k ) {
    function c( a, b ) {
        this.f = a;
        this.params = b
    }

    c.prototype.exec = function( a ) {
        this.f.apply( a, this.params )
    };
    var b = function() {
        this.initialize()
    }, a = b.prototype;
    b.getRGB = function( a, b, c, d ) {
        a != null && c == null && (d = b, c = a & 255, b = a >> 8 & 255, a = a >> 16 & 255);
        return d == null ? "rgb(" + a + "," + b + "," + c + ")" : "rgba(" + a + "," + b + "," + c + "," + d + ")"
    };
    b.getHSL = function( a, b, c, d ) {
        return d == null ? "hsl(" + a % 360 + "," + b + "%," + c + "%)" : "hsla(" + a % 360 + "," + b + "%," + c + "%," + d + ")"
    };
    b.STROKE_CAPS_MAP = ["butt","round","square"];
    b.STROKE_JOINTS_MAP = ["miter","round",
        "bevel"];
    b._ctx = document.createElement( "canvas" ).getContext( "2d" );
    b.beginCmd = new c( b._ctx.beginPath, [] );
    b.fillCmd = new c( b._ctx.fill, [] );
    b.strokeCmd = new c( b._ctx.stroke, [] );
    a._strokeInstructions = null;
    a._strokeStyleInstructions = null;
    a._fillInstructions = null;
    a._instructions = null;
    a._oldInstructions = null;
    a._activeInstructions = null;
    a._active = false;
    a._dirty = false;
    a.initialize = function() {
        this.clear();
        this._ctx = b._ctx
    };
    a.draw = function( a ) {
        this._dirty && this._updateInstructions();
        for ( var b = this._instructions, c = 0, d = b.length; c < d; c++ ) {
            b[c].exec( a )
        }
    };
    a.moveTo = function( a, b ) {
        this._activeInstructions.push( new c( this._ctx.moveTo, [a,b] ) );
        return this
    };
    a.lineTo = function( a, b ) {
        this._dirty = this._active = true;
        this._activeInstructions.push( new c( this._ctx.lineTo, [a,b] ) );
        return this
    };
    a.arcTo = function( a, b, e, d, f ) {
        this._dirty = this._active = true;
        this._activeInstructions.push( new c( this._ctx.arcTo, [a,b,e,d,f] ) );
        return this
    };
    a.arc = function( a, b, e, d, f, g ) {
        this._dirty = this._active = true;
        g == null && (g = false);
        this._activeInstructions.push( new c( this._ctx.arc,
                                              [a,b,e,d,f,g] ) );
        return this
    };
    a.quadraticCurveTo = function( a, b, e, d ) {
        this._dirty = this._active = true;
        this._activeInstructions.push( new c( this._ctx.quadraticCurveTo, [a,b,e,d] ) );
        return this
    };
    a.bezierCurveTo = function( a, b, e, d, f, g ) {
        this._dirty = this._active = true;
        this._activeInstructions.push( new c( this._ctx.bezierCurveTo, [a,b,e,d,f,g] ) );
        return this
    };
    a.rect = function( a, b, e, d ) {
        this._dirty = this._active = true;
        this._activeInstructions.push( new c( this._ctx.rect, [a,b,e,d] ) );
        return this
    };
    a.closePath = function() {
        if ( this._active ) {
            this._dirty =
            true, this._activeInstructions.push( new c( this._ctx.closePath, [] ) );
        }
        return this
    };
    a.clear = function() {
        this._instructions = [];
        this._oldInstructions = [];
        this._activeInstructions = [];
        this._strokeStyleInstructions = this._strokeInstructions = this._fillInstructions = null;
        this._active = this._dirty = false;
        return this
    };
    a.beginFill = function( a ) {
        this._active && this._newPath();
        this._fillInstructions = a ? [new c( this._setProp, ["fillStyle",a] )] : null;
        return this
    };
    a.beginLinearGradientFill = function( a, b, e, d, f, g ) {
        this._active && this._newPath();
        e = this._ctx.createLinearGradient( e, d, f, g );
        d = 0;
        for ( f = a.length; d < f; d++ ) {
            e.addColorStop( b[d], a[d] );
        }
        this._fillInstructions = [new c( this._setProp, ["fillStyle",e] )];
        return this
    };
    a.beginRadialGradientFill = function( a, b, e, d, f, g, j, h ) {
        this._active && this._newPath();
        e = this._ctx.createRadialGradient( e, d, f, g, j, h );
        d = 0;
        for ( f = a.length; d < f; d++ ) {
            e.addColorStop( b[d], a[d] );
        }
        this._fillInstructions = [new c( this._setProp, ["fillStyle",e] )];
        return this
    };
    a.beginBitmapFill = function( a, b ) {
        this._active && this._newPath();
        var e = this._ctx.createPattern( a,
                                         b || "" );
        this._fillInstructions = [new c( this._setProp, ["fillStyle",e] )];
        return this
    };
    a.endFill = function() {
        this.beginFill( null );
        return this
    };
    a.setStrokeStyle = function( a, i, e, d ) {
        this._active && this._newPath();
        this._strokeStyleInstructions = [new c( this._setProp, ["lineWidth",a == null ? "1" : a] ),new c( this._setProp, ["lineCap",i == null ? "butt" : isNaN( i ) ? i : b.STROKE_CAPS_MAP[i]] ),new c( this._setProp, ["lineJoin",e == null ? "miter" : isNaN( e ) ? e : b.STROKE_JOINTS_MAP[e]] ),new c( this._setProp, ["miterLimit",d == null ? "10" : d] )];
        return this
    };
    a.beginStroke = function( a ) {
        this._active && this._newPath();
        this._strokeInstructions = a ? [new c( this._setProp, ["strokeStyle",a] )] : null;
        return this
    };
    a.beginLinearGradientStroke = function( a, b, e, d, f, g ) {
        this._active && this._newPath();
        e = this._ctx.createLinearGradient( e, d, f, g );
        d = 0;
        for ( f = a.length; d < f; d++ ) {
            e.addColorStop( b[d], a[d] );
        }
        this._strokeInstructions = [new c( this._setProp, ["strokeStyle",e] )];
        return this
    };
    a.beginRadialGradientStroke = function( a, b, e, d, f, g, j, h ) {
        this._active && this._newPath();
        e = this._ctx.createRadialGradient( e,
                                            d, f, g, j, h );
        d = 0;
        for ( f = a.length; d < f; d++ ) {
            e.addColorStop( b[d], a[d] );
        }
        this._strokeInstructions = [new c( this._setProp, ["strokeStyle",e] )];
        return this
    };
    a.beginBitmapStroke = function( a, b ) {
        this._active && this._newPath();
        var e = this._ctx.createPattern( a, b || "" );
        this._strokeInstructions = [new c( this._setProp, ["strokeStyle",e] )];
        return this
    };
    a.endStroke = function() {
        this.beginStroke( null );
        return this
    };
    a.curveTo = a.quadraticCurveTo;
    a.drawRect = a.rect;
    a.drawRoundRect = function( a, b, c, d, f ) {
        this.drawRoundRectComplex( a, b, c, d,
                                   f, f, f, f );
        return this
    };
    a.drawRoundRectComplex = function( a, b, e, d, f, g, j, h ) {
        this._dirty = this._active = true;
        this._activeInstructions.push( new c( this._ctx.moveTo, [a + f,b] ), new c( this._ctx.lineTo, [a + e - g,b] ), new c( this._ctx.arc, [a + e - g,b + g,g,-Math.PI / 2,0,false] ), new c( this._ctx.lineTo, [a + e,b + d - j] ), new c( this._ctx.arc, [a + e - j,b + d - j,j,0,Math.PI / 2,false] ), new c( this._ctx.lineTo, [a + h,b + d] ), new c( this._ctx.arc, [a + h,b + d - h,h,Math.PI / 2,Math.PI,false] ), new c( this._ctx.lineTo, [a,b + f] ), new c( this._ctx.arc, [a + f,b + f,f,Math.PI,
            Math.PI * 3 / 2,false] ) );
        return this
    };
    a.drawCircle = function( a, b, c ) {
        this.arc( a, b, c, 0, Math.PI * 2 );
        return this
    };
    a.drawEllipse = function( a, b, e, d ) {
        this._dirty = this._active = true;
        var f = e / 2 * 0.5522848, g = d / 2 * 0.5522848, j = a + e, h = b + d, e = a + e / 2, d = b + d / 2;
        this._activeInstructions.push( new c( this._ctx.moveTo, [a,d] ), new c( this._ctx.bezierCurveTo, [a,d - g,e - f,b,e,b] ), new c( this._ctx.bezierCurveTo, [e + f,b,j,d - g,j,d] ), new c( this._ctx.bezierCurveTo, [j,d + g,e + f,h,e,h] ), new c( this._ctx.bezierCurveTo, [e - f,h,a,d + g,a,d] ) );
        return this
    };
    a.drawPolyStar =
    function( a, b, e, d, f, g ) {
        this._dirty = this._active = true;
        f == null && (f = 0);
        f = 1 - f;
        g == null ? g = 0 : g /= 180 / Math.PI;
        var j = Math.PI / d;
        this._activeInstructions.push( new c( this._ctx.moveTo, [a + Math.cos( g ) * e,b + Math.sin( g ) * e] ) );
        for ( var h = 0; h < d; h++ ) {
            g += j, f != 1 && this._activeInstructions.push( new c( this._ctx.lineTo, [a + Math.cos( g ) * e * f,b + Math.sin( g ) * e * f] ) ), g += j, this._activeInstructions.push( new c( this._ctx.lineTo, [a + Math.cos( g ) * e,b + Math.sin( g ) * e] ) );
        }
        return this
    };
    a.clone = function() {
        var a = new b;
        a._instructions = this._instructions.slice();
        a._activeInstructions = this._activeInstructions.slice();
        a._oldInstructions = this._oldInstructions.slice();
        if ( this._fillInstructions ) {
            a._fillInstructions = this._fillInstructions.slice();
        }
        if ( this._strokeInstructions ) {
            a._strokeInstructions = this._strokeInstructions.slice();
        }
        if ( this._strokeStyleInstructions ) {
            a._strokeStyleInstructions = this._strokeStyleInstructions.slice();
        }
        a._active = this._active;
        a._dirty = this._dirty;
        return a
    };
    a.toString = function() {
        return"[Graphics]"
    };
    a.mt = a.moveTo;
    a.lt = a.lineTo;
    a.at = a.arcTo;
    a.bt =
    a.bezierCurveTo;
    a.qt = a.quadraticCurveTo;
    a.a = a.arc;
    a.r = a.rect;
    a.cp = a.closePath;
    a.c = a.clear;
    a.f = a.beginFill;
    a.lf = a.beginLinearGradientFill;
    a.rf = a.beginRadialGradientFill;
    a.bf = a.beginBitmapFill;
    a.ef = a.endFill;
    a.ss = a.setStrokeStyle;
    a.s = a.beginStroke;
    a.ls = a.beginLinearGradientStroke;
    a.rs = a.beginRadialGradientStroke;
    a.bs = a.beginBitmapStroke;
    a.es = a.endStroke;
    a.dr = a.drawRect;
    a.rr = a.drawRoundRect;
    a.rc = a.drawRoundRectComplex;
    a.dc = a.drawCircle;
    a.de = a.drawEllipse;
    a.dp = a.drawPolyStar;
    a._updateInstructions = function() {
        this._instructions =
        this._oldInstructions.slice();
        this._instructions.push( b.beginCmd );
        this._fillInstructions && this._instructions.push.apply( this._instructions, this._fillInstructions );
        this._strokeInstructions && (this._instructions.push.apply( this._instructions, this._strokeInstructions ), this._strokeStyleInstructions && this._instructions.push.apply( this._instructions, this._strokeStyleInstructions ));
        this._instructions.push.apply( this._instructions, this._activeInstructions );
        this._fillInstructions && this._instructions.push( b.fillCmd );
        this._strokeInstructions && this._instructions.push( b.strokeCmd )
    };
    a._newPath = function() {
        this._dirty && this._updateInstructions();
        this._oldInstructions = this._instructions;
        this._activeInstructions = [];
        this._active = this._dirty = false
    };
    a._setProp = function( a, b ) {
        this[a] = b
    };
    k.Graphics = b
})( window );
(function( k ) {
    var c = function() {
        this.initialize()
    }, b = c.prototype;
    c.suppressCrossDomainErrors = false;
    c._hitTestCanvas = document.createElement( "canvas" );
    c._hitTestCanvas.width = c._hitTestCanvas.height = 1;
    c._hitTestContext = c._hitTestCanvas.getContext( "2d" );
    b.alpha = 1;
    b.cacheCanvas = null;
    b.id = -1;
    b.mouseEnabled = true;
    b.name = null;
    b.parent = null;
    b.regX = 0;
    b.regY = 0;
    b.rotation = 0;
    b.scaleX = 1;
    b.scaleY = 1;
    b.skewX = 0;
    b.skewY = 0;
    b.shadow = null;
    b.visible = true;
    b.x = 0;
    b.y = 0;
    b.compositeOperation = null;
    b.snapToPixel = false;
    b.onPress =
    null;
    b.onClick = null;
    b.onDoubleClick = null;
    b.onMouseOver = null;
    b.onMouseOut = null;
    b.tick = null;
    b.filters = null;
    b.cacheID = 0;
    b._cacheOffsetX = 0;
    b._cacheOffsetY = 0;
    b._cacheDataURLID = 0;
    b._cacheDataURL = null;
    b._matrix = null;
    b.initialize = function() {
        this.id = UID.get();
        this._matrix = new Matrix2D
    };
    b.isVisible = function() {
        return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0
    };
    b.draw = function( a, b ) {
        if ( b || !this.cacheCanvas ) {
            return false;
        }
        a.drawImage( this.cacheCanvas, this._cacheOffsetX, this._cacheOffsetY );
        return true
    };
    b.cache = function( a, b, c, e ) {
        if ( this.cacheCanvas == null ) {
            this.cacheCanvas = document.createElement( "canvas" );
        }
        var d = this.cacheCanvas.getContext( "2d" );
        this.cacheCanvas.width = c;
        this.cacheCanvas.height = e;
        d.clearRect( 0, 0, c + 1, e + 1 );
        d.setTransform( 1, 0, 0, 1, -a, -b );
        this.draw( d, true, this._matrix.reinitialize( 1, 0, 0, 1, -a, -b ) );
        this._cacheOffsetX = a;
        this._cacheOffsetY = b;
        this._applyFilters();
        this.cacheID++
    };
    b.updateCache = function( a ) {
        if ( this.cacheCanvas == null ) {
            throw"cache() must be called before updateCache()";
        }
        var b = this.cacheCanvas.getContext( "2d" );
        b.setTransform( 1, 0, 0, 1, -this._cacheOffsetX, -this._cacheOffsetY );
        a ? b.globalCompositeOperation = a : b.clearRect( 0, 0, this.cacheCanvas.width + 1, this.cacheCanvas.height + 1 );
        this.draw( b, true );
        if ( a ) {
            b.globalCompositeOperation = "source-over";
        }
        this._applyFilters();
        this.cacheID++
    };
    b.uncache = function() {
        this._cacheDataURL = this.cacheCanvas = null;
        this._cacheOffsetX = this._cacheOffsetY = 0
    };
    b.getCacheDataURL = function() {
        if ( !this.cacheCanvas ) {
            return null;
        }
        if ( this.cacheID != this._cacheDataURLID ) {
            this._cacheDataURL = this.cacheCanvas.toDataURL();
        }
        return this._cacheDataURL
    };
    b.getStage = function() {
        for ( var a = this; a.parent; ) {
            a = a.parent;
        }
        return a instanceof Stage ? a : null
    };
    b.localToGlobal = function( a, b ) {
        var c = this.getConcatenatedMatrix( this._matrix );
        if ( c == null ) {
            return null;
        }
        c.append( 1, 0, 0, 1, a, b );
        return new Point( c.tx, c.ty )
    };
    b.globalToLocal = function( a, b ) {
        var c = this.getConcatenatedMatrix( this._matrix );
        if ( c == null ) {
            return null;
        }
        c.invert();
        c.append( 1, 0, 0, 1, a, b );
        return new Point( c.tx, c.ty )
    };
    b.localToLocal = function( a, b, c ) {
        a = this.localToGlobal( a, b );
        return c.globalToLocal( a.x,
                                a.y )
    };
    b.setTransform = function( a, b, c, e, d, f, g, j, h ) {
        this.x = a || 0;
        this.y = b || 0;
        this.scaleX = c == null ? 1 : c;
        this.scaleY = e == null ? 1 : e;
        this.rotation = d || 0;
        this.skewX = f || 0;
        this.skewY = g || 0;
        this.regX = j || 0;
        this.regY = h || 0
    };
    b.getConcatenatedMatrix = function( a ) {
        a ? a.identity() : a = new Matrix2D;
        for ( var b = this; b != null; ) {
            a.prependTransform( b.x, b.y, b.scaleX, b.scaleY, b.rotation, b.skewX, b.skewY, b.regX, b.regY ), a.prependProperties( b.alpha, b.shadow, b.compositeOperation ), b = b.parent;
        }
        return a
    };
    b.hitTest = function( a, b ) {
        var i = c._hitTestContext, e = c._hitTestCanvas;
        i.setTransform( 1, 0, 0, 1, -a, -b );
        this.draw( i );
        i = this._testHit( i );
        e.width = 0;
        e.width = 1;
        return i
    };
    b.clone = function() {
        var a = new c;
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[DisplayObject (name=" + this.name + ")]"
    };
    b.cloneProps = function( a ) {
        a.alpha = this.alpha;
        a.name = this.name;
        a.regX = this.regX;
        a.regY = this.regY;
        a.rotation = this.rotation;
        a.scaleX = this.scaleX;
        a.scaleY = this.scaleY;
        a.shadow = this.shadow;
        a.skewX = this.skewX;
        a.skewY = this.skewY;
        a.visible = this.visible;
        a.x = this.x;
        a.y = this.y;
        a.mouseEnabled = this.mouseEnabled;
        a.compositeOperation = this.compositeOperation;
        if ( this.cacheCanvas ) {
            a.cacheCanvas = this.cacheCanvas.cloneNode( true ), a.cacheCanvas.getContext( "2d" ).putImageData( this.cacheCanvas.getContext( "2d" ).getImageData( 0, 0, this.cacheCanvas.width, this.cacheCanvas.height ), 0, 0 )
        }
    };
    b.applyShadow = function( a, b ) {
        b = b || Shadow.identity;
        a.shadowColor = b.color;
        a.shadowOffsetX = b.offsetX;
        a.shadowOffsetY = b.offsetY;
        a.shadowBlur = b.blur
    };
    b._testHit = function( a ) {
        try {
            var b = a.getImageData( 0, 0, 1, 1 ).data[3] >
                    1
        }
        catch( i ) {
            if ( !c.suppressCrossDomainErrors ) {
                throw"An error has occured. This is most likely due to security restrictions on reading canvas pixel data with local or cross-domain images.";
            }
        }
        return b
    };
    b._applyFilters = function() {
        if ( this.filters && this.filters.length != 0 && this.cacheCanvas ) {
            for ( var a = this.filters.length, b = this.cacheCanvas.getContext( "2d" ), c = this.cacheCanvas.width, e = this.cacheCanvas.height, d = 0; d < a; d++ ) {
                this.filters[d].applyFilter( b, 0, 0, c, e )
            }
        }
    };
    k.DisplayObject = c
})( window );
(function( k ) {
    var c = function() {
        this.initialize()
    }, b = c.prototype = new DisplayObject;
    b.children = null;
    b.DisplayObject_initialize = b.initialize;
    b.initialize = function() {
        this.DisplayObject_initialize();
        this.children = []
    };
    b.isVisible = function() {
        return this.visible && this.alpha > 0 && this.children.length && this.scaleX != 0 && this.scaleY != 0
    };
    b.DisplayObject_draw = b.draw;
    b.draw = function( a, b, i ) {
        var e = Stage._snapToPixelEnabled;
        if ( this.DisplayObject_draw( a, b ) ) {
            return true;
        }
        for ( var i = i || this._matrix.reinitialize( 1, 0, 0, 1, 0, 0,
                                                      this.alpha, this.shadow, this.compositeOperation ), b = this.children.length, d = this.children.slice( 0 ), f = 0; f < b; f++ ) {
            var g = d[f];
            if ( g.isVisible() ) {
                var j = false, h = g._matrix.reinitialize( i.a, i.b, i.c, i.d, i.tx, i.ty, i.alpha, i.shadow, i.compositeOperation );
                h.appendTransform( g.x, g.y, g.scaleX, g.scaleY, g.rotation, g.skewX, g.skewY, g.regX, g.regY );
                h.appendProperties( g.alpha, g.shadow, g.compositeOperation );
                if ( !(g instanceof c && g.cacheCanvas == null) ) {
                    e && g.snapToPixel && h.a == 1 && h.b == 0 && h.c == 0 && h.d == 1 ? a.setTransform( h.a, h.b, h.c,
                                                                                                         h.d, h.tx + 0.5 | 0, h.ty + 0.5 | 0 ) : a.setTransform( h.a, h.b, h.c, h.d, h.tx, h.ty ), a.globalAlpha = h.alpha, a.globalCompositeOperation = h.compositeOperation || "source-over", (j = h.shadow) && this.applyShadow( a, j );
                }
                g.draw( a, false, h );
                j && this.applyShadow( a )
            }
        }
        return true
    };
    b.addChild = function( a ) {
        var b = arguments.length;
        if ( b > 1 ) {
            for ( var c = 0; c < b; c++ ) {
                this.addChild( arguments[c] );
            }
            return arguments[b - 1]
        }
        a.parent && a.parent.removeChild( a );
        a.parent = this;
        this.children.push( a );
        return a
    };
    b.addChildAt = function( a, b ) {
        var c = arguments.length;
        if ( c >
             2 ) {
            for ( var b = arguments[e - 1], e = 0; e < c - 1; e++ ) {
                this.addChildAt( arguments[e], b + e );
            }
            return arguments[c - 2]
        }
        a.parent && a.parent.removeChild( a );
        a.parent = this;
        this.children.splice( b, 0, a );
        return a
    };
    b.removeChild = function( a ) {
        var b = arguments.length;
        if ( b > 1 ) {
            for ( var c = true, e = 0; e < b; e++ ) {
                c = c && this.removeChild( arguments[e] );
            }
            return c
        }
        return this.removeChildAt( this.children.indexOf( a ) )
    };
    b.removeChildAt = function( a ) {
        var b = arguments.length;
        if ( b > 1 ) {
            for ( var c = [], e = 0; e < b; e++ ) {
                c[e] = arguments[e];
            }
            c.sort( function( a, b ) {
                return b - a
            } );
            for ( var d = true, e = 0; e < b; e++ ) {
                d = d && this.removeChildAt( c[e] );
            }
            return d
        }
        if ( a < 0 || a > this.children.length - 1 ) {
            return false;
        }
        b = this.children[a];
        if ( b != null ) {
            b.parent = null;
        }
        this.children.splice( a, 1 );
        return true
    };
    b.removeAllChildren = function() {
        for ( ; this.children.length; ) {
            this.removeChildAt( 0 )
        }
    };
    b.getChildAt = function( a ) {
        return this.children[a]
    };
    b.sortChildren = function( a ) {
        this.children.sort( a )
    };
    b.getChildIndex = function( a ) {
        return this.children.indexOf( a )
    };
    b.getNumChildren = function() {
        return this.children.length
    };
    b.contains =
    function( a ) {
        for ( ; a; ) {
            if ( a == this ) {
                return true;
            }
            a = a.parent
        }
        return false
    };
    b.hitTest = function( a, b ) {
        return this.getObjectUnderPoint( a, b ) != null
    };
    b.getObjectsUnderPoint = function( a, b ) {
        var c = [], e = this.localToGlobal( a, b );
        this._getObjectsUnderPoint( e.x, e.y, c );
        return c
    };
    b.getObjectUnderPoint = function( a, b ) {
        var c = this.localToGlobal( a, b );
        return this._getObjectsUnderPoint( c.x, c.y )
    };
    b.clone = function( a ) {
        var b = new c;
        this.cloneProps( b );
        if ( a ) {
            for ( var i = b.children = [], e = 0, d = this.children.length; e < d; e++ ) {
                var f = this.children[e].clone( a );
                f.parent = b;
                i.push( f )
            }
        }
        return b
    };
    b.toString = function() {
        return"[Container (name=" + this.name + ")]"
    };
    b._tick = function() {
        for ( var a = this.children.length - 1; a >= 0; a-- ) {
            var b = this.children[a];
            b._tick && b._tick();
            b.tick && b.tick()
        }
    };
    b._getObjectsUnderPoint = function( a, b, i, e ) {
        var d = DisplayObject._hitTestContext, f = DisplayObject._hitTestCanvas, g = this._matrix, j = e & 1 && (this.onPress || this.onClick || this.onDoubleClick) || e & 2 && (this.onMouseOver || this.onMouseOut);
        if ( this.cacheCanvas ) {
            if ( this.getConcatenatedMatrix( g ), d.setTransform( g.a,
                                                                  g.b, g.c, g.d, g.tx - a, g.ty - b ), d.globalAlpha = g.alpha, this.draw( d ), this._testHit( d ) ) {
                if ( f.width = 0, f.width = 1, j ) {
                    return this
                }
            }
            else {
                return null;
            }
        }
        for ( var h = this.children.length - 1; h >= 0; h-- ) {
            var l = this.children[h];
            if ( l.isVisible() && l.mouseEnabled ) {
                if ( l instanceof c ) {
                    if ( j ) {
                        if ( l = l._getObjectsUnderPoint( a, b ) ) {
                            return this
                        }
                    }
                    else {
                        if ( l = l._getObjectsUnderPoint( a, b, i, e ), !i && l ) {
                            return l
                        }
                    }
                } else if ( !e || j || e & 1 && (l.onPress || l.onClick || l.onDoubleClick) || e & 2 && (l.onMouseOver || l.onMouseOut) ) {
                    if ( l.getConcatenatedMatrix( g ), d.setTransform( g.a,
                                                                       g.b, g.c, g.d, g.tx - a, g.ty - b ), d.globalAlpha = g.alpha, l.draw( d ), this._testHit( d ) ) {
                        if ( f.width = 0, f.width = 1, j ) {
                            return this;
                        } else if ( i ) {
                            i.push( l );
                        }
                        else {
                            return l
                        }
                    }
                }
            }
        }
        return null
    };
    k.Container = c
})( window );
(function( k ) {
    var c = function( a ) {
        this.initialize( a )
    }, b = c.prototype = new Container;
    c._snapToPixelEnabled = false;
    b.autoClear = true;
    b.canvas = null;
    b.mouseX = null;
    b.mouseY = null;
    b.onMouseMove = null;
    b.onMouseUp = null;
    b.onMouseDown = null;
    b.snapToPixelEnabled = false;
    b.mouseInBounds = false;
    b.tickOnUpdate = true;
    b._activeMouseEvent = null;
    b._activeMouseTarget = null;
    b._mouseOverIntervalID = null;
    b._mouseOverX = 0;
    b._mouseOverY = 0;
    b._mouseOverTarget = null;
    b.Container_initialize = b.initialize;
    b.initialize = function( a ) {
        this.Container_initialize();
        this.canvas = a;
        this._enableMouseEvents( true )
    };
    b.update = function() {
        if ( this.canvas ) {
            this.autoClear && this.clear(), c._snapToPixelEnabled = this.snapToPixelEnabled, this.tickOnUpdate && this._tick(), this.draw( this.canvas.getContext( "2d" ), false, this.getConcatenatedMatrix( this._matrix ) )
        }
    };
    b.tick = b.update;
    b.clear = function() {
        if ( this.canvas ) {
            var a = this.canvas.getContext( "2d" );
            a.setTransform( 1, 0, 0, 1, 0, 0 );
            a.clearRect( 0, 0, this.canvas.width, this.canvas.height )
        }
    };
    b.toDataURL = function( a, b ) {
        b || (b = "image/png");
        var c = this.canvas.getContext( "2d" ), e = this.canvas.width, d = this.canvas.height, f;
        if ( a ) {
            f = c.getImageData( 0, 0, e, d );
            var g = c.globalCompositeOperation;
            c.globalCompositeOperation = "destination-over";
            c.fillStyle = a;
            c.fillRect( 0, 0, e, d )
        }
        var j = this.canvas.toDataURL( b );
        if ( a ) {
            c.clearRect( 0, 0, e, d ), c.putImageData( f, 0, 0 ), c.globalCompositeOperation = g;
        }
        return j
    };
    b.enableMouseOver = function( a ) {
        if ( this._mouseOverIntervalID ) {
            clearInterval( this._mouseOverIntervalID ), this._mouseOverIntervalID = null;
        }
        if ( !(a <= 0) ) {
            var b = this;
            this._mouseOverIntervalID = setInterval( function() {
                                                         b._testMouseOver()
                                                     },
                                                     1E3 / Math.min( 50, a ) );
            this._mouseOverX = NaN;
            this._mouseOverTarget = null
        }
    };
    b.clone = function() {
        var a = new c( null );
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[Stage (name=" + this.name + ")]"
    };
    b._enableMouseEvents = function() {
        var a = this, b = k.addEventListener ? k : document;
        b.addEventListener( "mouseup", function( b ) {
            a._handleMouseUp( b )
        }, false );
        b.addEventListener( "mousemove", function( b ) {
            a._handleMouseMove( b )
        }, false );
        b.addEventListener( "dblclick", function( b ) {
            a._handleDoubleClick( b )
        }, false );
        this.canvas && this.canvas.addEventListener( "mousedown",
                                                     function( b ) {
                                                         a._handleMouseDown( b )
                                                     }, false )
    };
    b._handleMouseMove = function( a ) {
        if ( this.canvas ) {
            if ( !a ) {
                a = k.event;
            }
            var b = this.mouseInBounds;
            this._updateMousePosition( a.pageX, a.pageY );
            if ( b || this.mouseInBounds ) {
                a = new MouseEvent( "onMouseMove", this.mouseX, this.mouseY, this, a );
                if ( this.onMouseMove ) {
                    this.onMouseMove( a );
                }
                if ( this._activeMouseEvent && this._activeMouseEvent.onMouseMove ) {
                    this._activeMouseEvent.onMouseMove( a )
                }
            }
        }
        else {
            this.mouseX = this.mouseY = null
        }
    };
    b._updateMousePosition = function( a, b ) {
        var c = this.canvas;
        do {
            a -= c.offsetLeft,
                    b -= c.offsetTop;
        } while ( c = c.offsetParent );
        if ( this.mouseInBounds = a >= 0 && b >= 0 && a < this.canvas.width && b < this.canvas.height ) {
            this.mouseX = a, this.mouseY = b
        }
    };
    b._handleMouseUp = function( a ) {
        var b = new MouseEvent( "onMouseUp", this.mouseX, this.mouseY, this, a );
        if ( this.onMouseUp ) {
            this.onMouseUp( b );
        }
        if ( this._activeMouseEvent && this._activeMouseEvent.onMouseUp ) {
            this._activeMouseEvent.onMouseUp( b );
        }
        if ( this._activeMouseTarget && this._activeMouseTarget.onClick && this._getObjectsUnderPoint( this.mouseX, this.mouseY, null, true, this._mouseOverIntervalID ?
                                                                                                                                             3 : 1 ) == this._activeMouseTarget ) {
            this._activeMouseTarget.onClick( new MouseEvent( "onClick", this.mouseX, this.mouseY, this._activeMouseTarget, a ) );
        }
        this._activeMouseEvent = this._activeMouseTarget = null
    };
    b._handleMouseDown = function( a ) {
        if ( this.onMouseDown ) {
            this.onMouseDown( new MouseEvent( "onMouseDown", this.mouseX, this.mouseY, this, a ) );
        }
        var b = this._getObjectsUnderPoint( this.mouseX, this.mouseY, null, this._mouseOverIntervalID ? 3 : 1 );
        if ( b ) {
            if ( b.onPress instanceof Function && (a = new MouseEvent( "onPress", this.mouseX, this.mouseY,
                                                                       b, a ), b.onPress( a ), a.onMouseMove || a.onMouseUp) ) {
                this._activeMouseEvent = a;
            }
            this._activeMouseTarget = b
        }
    };
    b._testMouseOver = function() {
        if ( !(this.mouseX == this._mouseOverX && this.mouseY == this._mouseOverY && this.mouseInBounds) ) {
            var a = null;
            if ( this.mouseInBounds ) {
                a = this._getObjectsUnderPoint( this.mouseX, this.mouseY, null, 3 ), this._mouseOverX = this.mouseX, this._mouseOverY = this.mouseY;
            }
            if ( this._mouseOverTarget != a ) {
                if ( this._mouseOverTarget && this._mouseOverTarget.onMouseOut ) {
                    this._mouseOverTarget.onMouseOut( new MouseEvent( "onMouseOut",
                                                                      this.mouseX, this.mouseY, this._mouseOverTarget ) );
                }
                if ( a && a.onMouseOver ) {
                    a.onMouseOver( new MouseEvent( "onMouseOver", this.mouseX, this.mouseY, a ) );
                }
                this._mouseOverTarget = a
            }
        }
    };
    b._handleDoubleClick = function( a ) {
        if ( this.onDoubleClick ) {
            this.onDoubleClick( new MouseEvent( "onDoubleClick", this.mouseX, this.mouseY, this, a ) );
        }
        var b = this._getObjectsUnderPoint( this.mouseX, this.mouseY, null, this._mouseOverIntervalID ? 3 : 1 );
        if ( b && b.onDoubleClick instanceof Function ) {
            b.onDoubleClick( new MouseEvent( "onPress", this.mouseX, this.mouseY,
                                             b, a ) )
        }
    };
    k.Stage = c
})( window );
(function( k ) {
    var c = function( a ) {
        this.initialize( a )
    }, b = c.prototype = new DisplayObject;
    b.image = null;
    b.snapToPixel = true;
    b.DisplayObject_initialize = b.initialize;
    b.initialize = function( a ) {
        this.DisplayObject_initialize();
        typeof a == "string" ? (this.image = new Image, this.image.src = a) : this.image = a
    };
    b.isVisible = function() {
        return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.image && (this.image.complete || this.image.getContext || this.image.readyState == 2)
    };
    b.DisplayObject_draw = b.draw;
    b.draw = function( a, b ) {
        if ( this.DisplayObject_draw( a, b ) ) {
            return true;
        }
        a.drawImage( this.image, 0, 0 );
        return true
    };
    b.clone = function() {
        var a = new c( this.image );
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[Bitmap (name=" + this.name + ")]"
    };
    k.Bitmap = c
})( window );
(function( k ) {
    var c = function( a ) {
        this.initialize( a )
    }, b = c.prototype = new DisplayObject;
    b.onAnimationEnd = null;
    b.currentFrame = -1;
    b.currentAnimation = null;
    b.paused = true;
    b.spriteSheet = null;
    b.snapToPixel = true;
    b.offset = 0;
    b.currentAnimationFrame = 0;
    b._advanceCount = 0;
    b._animation = null;
    b.DisplayObject_initialize = b.initialize;
    b.initialize = function( a ) {
        this.DisplayObject_initialize();
        this.spriteSheet = a
    };
    b.isVisible = function() {
        return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.spriteSheet.complete &&
               this.currentFrame >= 0
    };
    b.DisplayObject_draw = b.draw;
    b.draw = function( a, b ) {
        if ( this.DisplayObject_draw( a, b ) ) {
            return true;
        }
        this._normalizeFrame();
        var c = this.spriteSheet.getFrame( this.currentFrame );
        if ( c != null ) {
            var e = c.rect;
            a.drawImage( c.image, e.x, e.y, e.width, e.height, -c.regX, -c.regY, e.width, e.height );
            return true
        }
    };
    b.gotoAndPlay = function( a ) {
        this.paused = false;
        this._goto( a )
    };
    b.gotoAndStop = function( a ) {
        this.paused = true;
        this._goto( a )
    };
    b.advance = function() {
        this._animation ? this.currentAnimationFrame++ : this.currentFrame++;
        this._normalizeFrame()
    };
    b.clone = function() {
        var a = new c( this.spriteSheet );
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[BitmapAnimation (name=" + this.name + ")]"
    };
    b._tick = function() {
        var a = this._animation ? this._animation.frequency : 1;
        !this.paused && (++this._advanceCount + this.offset) % a == 0 && this.advance()
    };
    b._normalizeFrame = function() {
        var a = this._animation;
        if ( a ) {
            if ( this.currentAnimationFrame >= a.frames.length ) {
                if ( a.next ? this._goto( a.next ) : (this.paused = true, this.currentAnimationFrame = a.frames.length -
                                                                                                       1, this.currentFrame = a.frames[this.currentAnimationFrame]), this.onAnimationEnd ) {
                    this.onAnimationEnd( this, a.name )
                }
            }
            else {
                this.currentFrame = a.frames[this.currentAnimationFrame];
            }
        } else if ( this.currentFrame >= this.spriteSheet.getNumFrames() && (this.currentFrame = 0, this.onAnimationEnd) ) {
            this.onAnimationEnd( this, null )
        }
    };
    b.DisplayObject_cloneProps = b.cloneProps;
    b.cloneProps = function( a ) {
        this.DisplayObject_cloneProps( a );
        a.onAnimationEnd = this.onAnimationEnd;
        a.currentFrame = this.currentFrame;
        a.currentAnimation = this.currentAnimation;
        a.paused = this.paused;
        a.offset = this.offset;
        a._animation = this._animation;
        a.currentAnimationFrame = this.currentAnimationFrame
    };
    b._goto = function( a ) {
        if ( isNaN( a ) ) {
            var b = this.spriteSheet.getAnimation( a );
            if ( b ) {
                this.currentAnimationFrame = 0, this._animation = b, this.currentAnimation = a, this._normalizeFrame()
            }
        }
        else {
            this.currentAnimation = this._animation = null, this.currentFrame = a
        }
    };
    k.BitmapAnimation = c
})( window );
(function( k ) {
    var c = function( a ) {
        this.initialize( a )
    }, b = c.prototype = new DisplayObject;
    b.graphics = null;
    b.DisplayObject_initialize = b.initialize;
    b.initialize = function( a ) {
        this.DisplayObject_initialize();
        this.graphics = a ? a : new Graphics
    };
    b.isVisible = function() {
        return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.graphics
    };
    b.DisplayObject_draw = b.draw;
    b.draw = function( a, b ) {
        if ( this.DisplayObject_draw( a, b ) ) {
            return true;
        }
        this.graphics.draw( a );
        return true
    };
    b.clone = function( a ) {
        a = new c( a && this.graphics ?
                   this.graphics.clone() : this.graphics );
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[Shape (name=" + this.name + ")]"
    };
    k.Shape = c
})( window );
(function( k ) {
    var c = function( a, b, c ) {
        this.initialize( a, b, c )
    }, b = c.prototype = new DisplayObject;
    c._workingContext = document.createElement( "canvas" ).getContext( "2d" );
    b.text = "";
    b.font = null;
    b.color = null;
    b.textAlign = null;
    b.textBaseline = null;
    b.maxWidth = null;
    b.outline = false;
    b.lineHeight = null;
    b.lineWidth = null;
    b.DisplayObject_initialize = b.initialize;
    b.initialize = function( a, b, c ) {
        this.DisplayObject_initialize();
        this.text = a;
        this.font = b;
        this.color = c ? c : "#000"
    };
    b.isVisible = function() {
        return Boolean( this.visible && this.alpha >
                                        0 && this.scaleX != 0 && this.scaleY != 0 && this.text != null && this.text != "" )
    };
    b.DisplayObject_draw = b.draw;
    b.draw = function( a, b ) {
        if ( this.DisplayObject_draw( a, b ) ) {
            return true;
        }
        this.outline ? a.strokeStyle = this.color : a.fillStyle = this.color;
        a.font = this.font;
        a.textAlign = this.textAlign ? this.textAlign : "start";
        a.textBaseline = this.textBaseline ? this.textBaseline : "alphabetic";
        for ( var c = String( this.text ).split( /(?:\r\n|\r|\n)/ ), e = this.lineHeight == null ? this.getMeasuredLineHeight() : this.lineHeight, d = 0, f = 0, g = c.length; f < g; f++ ) {
            var j =
                    a.measureText( c[f] ).width;
            if ( this.lineWidth == null || j < this.lineWidth ) {
                this._drawTextLine( a, c[f], d );
            }
            else {
                for ( var j = c[f].split( /(\s)/ ), h = j[0], l = 1, k = j.length; l < k; l += 2 ) {
                    a.measureText( h + j[l] + j[l + 1] ).width > this.lineWidth ? (this._drawTextLine( a, h, d ), d += e, h = j[l + 1]) : h += j[l] + j[l + 1];
                }
                this._drawTextLine( a, h, d )
            }
            d += e
        }
        return true
    };
    b.getMeasuredWidth = function() {
        return this._getWorkingContext().measureText( this.text ).width
    };
    b.getMeasuredLineHeight = function() {
        return this._getWorkingContext().measureText( "M" ).width * 1.2
    };
    b.clone = function() {
        var a = new c( this.text, this.font, this.color );
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[Text (text=" + (this.text.length > 20 ? this.text.substr( 0, 17 ) + "..." : this.text) + ")]"
    };
    b.DisplayObject_cloneProps = b.cloneProps;
    b.cloneProps = function( a ) {
        this.DisplayObject_cloneProps( a );
        a.textAlign = this.textAlign;
        a.textBaseline = this.textBaseline;
        a.maxWidth = this.maxWidth;
        a.outline = this.outline;
        a.lineHeight = this.lineHeight;
        a.lineWidth = this.lineWidth
    };
    b._getWorkingContext = function() {
        var a =
                c._workingContext;
        a.font = this.font;
        a.textAlign = this.textAlign ? this.textAlign : "start";
        a.textBaseline = this.textBaseline ? this.textBaseline : "alphabetic";
        return a
    };
    b._drawTextLine = function( a, b, c ) {
        this.outline ? a.strokeText( b, 0, c, this.maxWidth ) : a.fillText( b, 0, c, this.maxWidth )
    };
    k.Text = c
})( window );
(function( k ) {
    var c = function() {
        throw"SpriteSheetUtils cannot be instantiated";
    };
    c._workingCanvas = document.createElement( "canvas" );
    c._workingContext = c._workingCanvas.getContext( "2d" );
    c.addFlippedFrames = function( b, a, k, i ) {
        if ( a || k || i ) {
            var e = 0;
            a && c._flip( b, ++e, true, false );
            k && c._flip( b, ++e, false, true );
            i && c._flip( b, ++e, true, true )
        }
    };
    c.extractFrame = function( b, a ) {
        isNaN( a ) && (a = b.getAnimation( a ).frames[0]);
        var k = b.getFrame( a );
        if ( !k ) {
            return null;
        }
        var i = k.rect, e = c._workingCanvas;
        e.width = i.width;
        e.height = i.height;
        c._workingContext.drawImage( k.image, i.x, i.y, i.width, i.height, 0, 0, i.width, i.height );
        k = new Image;
        k.src = e.toDataURL( "image/png" );
        return k
    };
    c._flip = function( b, a, k, i ) {
        for ( var e = b._images, d = c._workingCanvas, f = c._workingContext, g = e.length / a, j = 0; j < g; j++ ) {
            var h = e[j];
            h.__tmp = j;
            d.width = h.width;
            d.height = h.height;
            f.setTransform( k ? -1 : 1, 0, 0, i ? -1 : 1, k ? h.width : 0, i ? h.height : 0 );
            f.drawImage( h, 0, 0 );
            var l = new Image;
            l.src = d.toDataURL( "image/png" );
            e.push( l )
        }
        f = b._frames;
        d = f.length / a;
        for ( j = 0; j < d; j++ ) {
            var h = f[j], m = h.rect.clone(), l = e[h.image.__tmp + g * a], o = {image:l,rect:m,regX:h.regX,regY:h.regY};
            if ( k ) {
                m.x = l.width - m.x - m.width, o.regX = m.width - h.regX;
            }
            if ( i ) {
                m.y = l.height - m.y - m.height, o.regY = m.height - h.regY;
            }
            f.push( o )
        }
        k = "_" + (k ? "h" : "") + (i ? "v" : "");
        i = b._animations;
        b = b._data;
        e = i.length / a;
        for ( j = 0; j < e; j++ ) {
            f = i[j];
            h = b[f];
            g = {name:f + k,frequency:h.frequency,next:h.next,frames:[]};
            h.next && (g.next += k);
            f = h.frames;
            h = 0;
            for ( l = f.length; h < l; h++ ) {
                g.frames.push( f[h] + d * a );
            }
            b[g.name] = g;
            i.push( g.name )
        }
    };
    k.SpriteSheetUtils = c
})( window );
(function( k ) {
    var c = function( a ) {
        this.initialize( a )
    }, b = c.prototype = new DisplayObject;
    b.htmlElement = null;
    b._style = null;
    b.DisplayObject_initialize = b.initialize;
    b.initialize = function( a ) {
        typeof a == "string" && (a = document.getElementById( a ));
        this.DisplayObject_initialize();
        this.mouseEnabled = false;
        if ( this.htmlElement = a ) {
            this._style = a.style, this._style.position = "absolute", this._style.transformOrigin = this._style.webkitTransformOrigin = this._style.MozTransformOrigin = "0% 0%"
        }
    };
    b.isVisible = function() {
        return this.htmlElement !=
               null
    };
    b.draw = function() {
        if ( this.htmlElement != null ) {
            var a = this._matrix, b = this.htmlElement;
            b.style.opacity = "" + a.alpha;
            b.style.visibility = this.visible ? "visible" : "hidden";
            b.style.transform = b.style.webkitTransform = b.style.oTransform = ["matrix(" + a.a,a.b,a.c,a.d,a.tx,a.ty + ")"].join( "," );
            b.style.MozTransform = ["matrix(" + a.a,a.b,a.c,a.d,a.tx + "px",a.ty + "px)"].join( "," );
            return true
        }
    };
    b.cache = function() {
    };
    b.uncache = function() {
    };
    b.updateCache = function() {
    };
    b.hitTest = function() {
    };
    b.localToGlobal = function() {
    };
    b.globalToLocal =
    function() {
    };
    b.localToLocal = function() {
    };
    b.clone = function() {
        var a = new c;
        this.cloneProps( a );
        return a
    };
    b.toString = function() {
        return"[DOMElement (name=" + this.name + ")]"
    };
    b._tick = function() {
        if ( this.htmlElement != null )this.htmlElement.style.visibility = "hidden"
    };
    k.DOMElement = c
})( window );
