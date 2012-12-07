define([
    'easel',
    'common/polyfills'
], function(
    Easel
) {
    // create()
    
    // Easel.DisplayObject.__proto__.create = function(params) {
    //        var object = new this();
    //        for(var key in params) {
    //            var value = params[key];
    //            var setter = 'set' + key.substring(0,1).toUpperCase() + key.substring(1);
    //            if(object[setter]) {
    //                object[setter](value);
    //            } else {
    //                if(!(key in object))
    //                    console.log('Warning in create(): no property or setter for', key, 'on', object, '; params are:', params);
    //                else
    //                object[key] = value;
    //            }
    //        }
    //        return object;
    //    }
    //    
    //    var Shape_superCreate = Easel.Shape.__proto__.create;
    //    Easel.Shape.__proto__.create = function(params) {
    //        var gkeys = ['graphics', 'fillColor', 'strokeColor', 'strokeWidth', 'strokeCap', 'strokeJoin', 'strokeMiterLimit'],
    //            gattrs = {};
    //        for(var i = 0; i < gkeys.length; i++) {
    //            var k = gkeys[i];
    //            gattrs[k] = params[k];
    //            delete params[k];
    //        }
    // 
    //        var object = Shape_superCreate.call(this, params);
    // 
    //        if(gattrs.strokeWidth || gattrs.strokeCap || gattrs.strokeJoin || gattrs.strokeMiterLimit)
    //            object.graphics.setStrokeStyle(gattrs.strokeWidth, gattrs.strokeCap, gattrs.strokeJoin, gattrs.strokeMiterLimit);
    //        if(gattrs.fillColor)
    //            object.graphics.beginFill(gattrs.fillColor);
    //        if(gattrs.strokeColor)
    //            object.graphics.beginStroke(gattrs.strokeColor);
    //        if(gattrs.graphics)
    //            gattrs.graphics(object.graphics);
    // 
    //        return object;
    //    }
    
    
    // Custom drag handling
    
    Easel.DisplayObject.prototype.makeDraggable = function(handler) {
        var obj = this;
        obj.onMouseOver = function() { document.body.style.cursor = "pointer"; };
        obj.onMouseOut  = function() { document.body.style.cursor = "default"; };
        obj.onPress = function(pressEvt) {
            //Make dragging relative to touch point
            var relativePressPoint;
            var dragHandler = function(event) {
                var transformed = obj.parent.globalToLocal( event.stageX, event.stageY );
                if ( !relativePressPoint ) {
                    relativePressPoint = {
                        x: obj.x - transformed.x,
                        y: obj.y - transformed.y
                    };
                } else {
                    handler(
                        transformed.x + relativePressPoint.x,
                        transformed.y + relativePressPoint.y);
                }
            };
            dragHandler(pressEvt);              // Set up initial pos...
            pressEvt.onMouseMove = dragHandler; // ...and handle subsequent motion
        };
    };
    
    
    
    // Convenience methods
    
    Easel.DisplayObject.prototype.setPosition = function(pos) {
        this.x = pos.x;
        this.y = pos.y;
    }
    
    Easel.DisplayObject.prototype.setScale = function(scale) {
        this.scaleX = this.scaleY = scale;
    }

    return Easel;
});
