define( [], function () {
    var Slider = function ( opts ) {
        this.initialize( opts );
    };

    //REVIEW: Why use inheritance here?
    Slider.prototype = $.extend( new createjs.Container(), {
        initialize:function ( opts ) {
            opts = opts || {};
            this.min = opts.min;
            this.max = opts.max;
            this.size = opts.size;
            this.thumbSize = this.size.y / 2;
            this.trackWidth = this.size.x - this.size.y;
            this.onChange = opts.onChange || function () {};
            this.x = opts.pos.x;
            this.y = opts.pos.y;

            this.track = new createjs.Shape();
            this.track.graphics
                    .setStrokeStyle( this.size.y / 4, 1 /*round*/ )
                    .beginStroke( createjs.Graphics.getHSL( 192, 10, 80 ) )
                    .moveTo( this.thumbSize, 0 )
                    .lineTo( this.thumbSize + this.trackWidth, 0 );
            this.addChild( this.track );

            this.thumb = new createjs.Shape();
            this.thumb.graphics
                    .setStrokeStyle( this.size.y / 12 )
                    .beginStroke( createjs.Graphics.getHSL( 18, 50, 30 ) )
                    .beginFill( createjs.Graphics.getHSL( 18, 30, 90 ) )
                    .drawCircle( 0, 0, this.thumbSize );
            this.addChild( this.thumb );

            this.thumb.onPress = this.drag.bind( this );
            this.thumb.onMouseDrag = this.drag.bind( this );

            if ( opts.value ) {
                this.setValue( opts.value )
            }
        },

        getValue:function () {
            return this._value
        },

        setValue:function ( value ) {
            if ( this.max && !(value && value < this.max) )  // This phrasing deals with NaN
            {
                value = this.max;
            }
            if ( this.min && !(value && value > this.min) )  // max === null / undefined / NaN defaults to min
            {
                value = this.min;
            }
            this._value = value;

            this.thumb.x = this.thumbSize + this.trackWidth * (value - this.min) / (this.max - this.min);

            this.onChange( value )
        },

        drag:function ( event ) {
            event.onMouseMove = this.drag.bind( this );
            var dragAtX = this.globalToLocal( event.stageX, event.stageY ).x;
            if ( event.type === 'onPress' ) {
                this.dragOffset = this.thumb.x - this.thumbSize - dragAtX;
            }
            else {
                this.setValue( (dragAtX + this.dragOffset) / this.trackWidth * (this.max - this.min) + this.min )
            }
        }
    } );

    return Slider;
} );