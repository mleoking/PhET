define([
    'common/util/object',
    'common/easel-custom',
    'common/model/positionable',
    'model/crystal',
    'lib/image!res/images/shaker.png',
], function(
    ObjectUtil,
    Easel,
    PositionableModel,
    Crystal,
    shakerImage
) {
    return ObjectUtil.classdef('Shaker', PositionableModel, {
        initialize: function(modelRoot, x, y) {
            this.modelRoot = modelRoot;
            this.x = x;
            this.y = y;
            this.crystalsPending = 0;
            
            this.sprite = Easel.Bitmap.create({
                image: shakerImage,
                rotation: -54,
                scale: 0.3,
                regX: 12,
                regY: 72
            });
            this.sprite.makeDraggable(this.shakeTo.bind(this));
        },
        
        shakeTo: function(x1, y1) {
            x1 = Math.max(50, Math.min(350, x1));
            y1 = Math.max( 5, Math.min(100, y1));
            
            var xdelta = Math.min(20, x1 - this.x),
                ydelta = Math.min(20, y1 - this.y),
                totalToAdd = this.crystalsPending += Math.sqrt(xdelta * xdelta + ydelta * ydelta) / 10;

            for(; this.crystalsPending > 0; this.crystalsPending--) {
                var crystal = new Crystal(this.modelRoot);
                crystal.x = this.x + xdelta * (this.crystalsPending / totalToAdd);
                crystal.y = this.y + ydelta * (this.crystalsPending / totalToAdd);
                crystal.dx = xdelta * Math.random() + Math.random() - 0.5;
                crystal.dy = ydelta;
                crystal.drotation = (Math.random() - 0.5) * 10000;
                
                this.modelRoot.addCrystal(crystal);
            }
            
            this.x = x1;
            this.y = y1;
        }
    });
});
