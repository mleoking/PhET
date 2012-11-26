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
            
            var size = 2.1;
            this.sprite = Easel.Shape.create({
                fillColor: modelRoot.solute.saturatedColor,
                strokeColor: 'rgba(0, 0, 0, 0.6)',
                strokeWidth: 0.3,
                graphics: function(g) { g.rect(-size/2, -size/2, size, size) }
            });
        },
        
        tick: function(dt) {
            var tub = this.modelRoot.tub;
            
            if(this.x >= tub.left && this.x <= tub.right) {
                if(this.y > tub.bottom) {
                    this.y = tub.bottom;
                    this.stopMoving();
                } else if(this.y > tub.top) {
                    var nextX = this.x + this.dx;
                    if(nextX < tub.left) {
                        this.x = tub.left;
                        this.dx *= -1;
                    } else if(nextX > tub.right) {
                        this.x = tub.right;
                        this.dx *= -1;
                    }
                }
            }
            
            this.Crystal_super_tick(dt);
            this.dy += 3 * dt;
            
            if(this.y > 300)
                this.modelRoot.removeCrystal(this);
        },
    })
});
