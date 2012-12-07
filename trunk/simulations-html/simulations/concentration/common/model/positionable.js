define([
    'common/util/object',
    'common/model/model'
], function(
    ObjectUtil,
    Model
) {
    return ObjectUtil.classdef('PositionableModel', Model, {
        stopMoving: function() {
            this.dx = this.dy = this.drotation = 0;
        },
        
        tick: function(dt) {
            if(this.dx)        this.x += this.dx * dt;
            if(this.dy)        this.y += this.dy * dt;
            if(this.drotation) this.rotation = (this.rotation + this.drotation) % 360;
        },
        
        updateView: function() {
            if(this.sprite) {
                if(this.x)        this.sprite.x = this.x;
                if(this.y)        this.sprite.y = this.y;
                if(this.rotation) this.sprite.rotation = this.rotation;
            }
        },
    })
});
