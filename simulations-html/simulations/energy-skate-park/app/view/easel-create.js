define( ['easel', 'util/ie-support'], function ( createjs ) {

    createjs.DisplayObject.__proto__.create = function ( params ) {
        var object = new this();
        for ( var key in params ) {
            var value = params[key];
            var setter = 'set' + key.substring( 0, 1 ).toUpperCase() + key.substring( 1 );
            if ( object[setter] ) {
                object[setter]( value );
            }
            else {
                if ( !(key in object) ) {
                    console.log( 'Warning in create(): no property or setter for', key, 'on', object, '; params are:', params );
                }
                else {
                    object[key] = value;
                }
            }
        }
        return object;
    }

    var Shape_superCreate = createjs.Shape.__proto__.create;
    createjs.Shape.__proto__.create = function ( params ) {
        var gkeys = ['graphics', 'fillColor', 'strokeColor', 'strokeWidth', 'strokeCap', 'strokeJoin', 'strokeMiterLimit'],
                gattrs = {};
        for ( var i = 0; i < gkeys.length; i++ ) {
            var k = gkeys[i];
            gattrs[k] = params[k];
            delete params[k];
        }

        var object = Shape_superCreate.call( this, params );

        if ( gattrs.strokeWidth || gattrs.strokeCap || gattrs.strokeJoin || gattrs.strokeMiterLimit ) {
            object.graphics.setStrokeStyle( gattrs.strokeWidth, gattrs.strokeCap, gattrs.strokeJoin, gattrs.strokeMiterLimit );
        }
        if ( gattrs.fillColor ) {
            object.graphics.beginFill( gattrs.fillColor );
        }
        if ( gattrs.strokeColor ) {
            object.graphics.beginStroke( gattrs.strokeColor );
        }
        if ( gattrs.graphics ) {
            gattrs.graphics( object.graphics );
        }

        return object;
    }

} );


