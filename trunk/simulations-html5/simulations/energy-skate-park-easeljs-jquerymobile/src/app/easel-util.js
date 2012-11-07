define( [], function () {

    return {changeHitDetection:function () {
        function newTestHit( x, y, displayObject, ctx ) {
            if ( displayObject.image !== undefined ) {
                //            console.log( "x = " + x + ", y=" + y );
                return x >= 0 && y >= 0 && x <= displayObject.image.width && y <= displayObject.image.height;
            }
            else {
                try {
                    var hit = ctx.getImageData( 0, 0, 1, 1 ).data[3] > 1;
                }
                catch ( e ) {
                    if ( !createjs.DisplayObject.suppressCrossDomainErrors ) {
                        throw "An error has occurred. This is most likely due to security restrictions on reading canvas pixel data with local or cross-domain images.";
                    }
                }
                return hit;
            }
        }

        createjs.Container.prototype._getObjectsUnderPoint = function ( x, y, arr, mouseEvents ) {
            var ctx = createjs.DisplayObject._hitTestContext;
            var canvas = createjs.DisplayObject._hitTestCanvas;
            var mtx = this._matrix;
            var hasHandler = (mouseEvents & 1 && (this.onPress || this.onClick || this.onDoubleClick)) || (mouseEvents & 2 &&
                                                                                                           (this.onMouseOver || this.onMouseOut));

            // if we have a cache handy & this has a handler, we can use it to do a quick check.
            // we can't use the cache for screening children, because they might have hitArea set.
            if ( this.cacheCanvas && hasHandler ) {
                this.getConcatenatedMatrix( mtx );
                ctx.setTransform( mtx.a, mtx.b, mtx.c, mtx.d, mtx.tx - x, mtx.ty - y );
                ctx.globalAlpha = mtx.alpha;
                this.draw( ctx );
                if ( this._testHit( ctx ) ) {
                    canvas.width = 0;
                    canvas.width = 1;
                    return this;
                }
            }

            // draw children one at a time, and check if we get a hit:
            var l = this.children.length;
            for ( var i = l - 1; i >= 0; i-- ) {
                var child = this.children[i];
                if ( !child.isVisible() || !child.mouseEnabled ) { continue; }

                if ( child instanceof createjs.Container ) {
                    var result;
                    if ( hasHandler ) {
                        // only concerned about the first hit, because this container is going to claim it anyway:
                        result = child._getObjectsUnderPoint( x, y );
                        if ( result ) { return this; }
                    }
                    else {
                        result = child._getObjectsUnderPoint( x, y, arr, mouseEvents );
                        if ( !arr && result ) { return result; }
                    }
                }
                else if ( !mouseEvents || hasHandler || (mouseEvents & 1 && (child.onPress || child.onClick || child.onDoubleClick)) || (mouseEvents & 2 && (child.onMouseOver || child.onMouseOut)) ) {
                    var hitArea = child.hitArea;
                    child.getConcatenatedMatrix( mtx );

                    if ( hitArea ) {
                        mtx.appendTransform( hitArea.x + child.regX, hitArea.y + child.regY, hitArea.scaleX, hitArea.scaleY, hitArea.rotation, hitArea.skewX, hitArea.skewY, hitArea.regX, hitArea.regY );
                        mtx.alpha *= hitArea.alpha / child.alpha;
                    }

                    ctx.globalAlpha = mtx.alpha;
                    ctx.setTransform( mtx.a, mtx.b, mtx.c, mtx.d, mtx.tx - x, mtx.ty - y );
                    (hitArea || child).draw( ctx );

                    //TODO: put x and y in the child coordinate frame
                    var transformed = child.globalToLocal( x, y );
                    if ( !newTestHit( transformed.x, transformed.y, child, ctx ) ) { continue;}
                    canvas.width = 0;
                    canvas.width = 1;
                    if ( hasHandler ) { return this; }
                    else if ( arr ) { arr.push( child ); }
                    else { return child; }
                }
            }
            return null;
        };
    }};

} );