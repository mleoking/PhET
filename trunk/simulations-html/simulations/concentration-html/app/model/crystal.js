define([
    'common/util/object',
    'common/easel-custom',
    'common/model/positionable'
], function(
    ObjectUtil,
    Easel,
    PositionableModel
) {
    return ObjectUtil.classdef('Crystal', PositionableModel, {
        initialize: function(modelRoot) {
            this.modelRoot = modelRoot;
            this.dx = this.dy = 0;
            this.rotation = Math.random() * 360;
            
            var size = this.size = 5;
            this.sprite = new Easel.Shape().set({
                fillColor: modelRoot.solute.saturatedColor,
                strokeColor: 'rgba(0, 0, 0, 0.7)',
                strokeWidth: 0.5,
                graphics: function(g) { g.rect(-size/2, -size/2, size, size) }
            });
        },
        
        tick: function(dt) {
            var beaker = this.modelRoot.beaker;
            
            if(this.x >= beaker.left && this.x <= beaker.right) {
                if(this.y > beaker.bottom) {
                    this.y = beaker.bottom;
                    this.stopMoving();
                } else if(this.y > beaker.top) {
                    var nextX = this.x + this.dx;
                    if(nextX < beaker.left) {
                        this.x = beaker.left;
                        this.dx *= -1;
                    } else if(nextX > beaker.right) {
                        this.x = beaker.right;
                        this.dx *= -1;
                    }
                }
            }
            
            this.Crystal_super_tick(dt);
            this.dy += 3 * dt;
            
            if(this.y > this.modelRoot.height + this.size)
                this.modelRoot.removeCrystal(this);
        },
    })
});
