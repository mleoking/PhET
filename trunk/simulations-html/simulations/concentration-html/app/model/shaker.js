define([
    'common/util/object',
    'common/easel-custom',
    'common/model/positionable',
    'model/crystal',
    'image!res/images/shaker.png',
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
            
            this.sprite = new Easel.Bitmap().set({
                image: shakerImage,
                rotation: -44,
                scale: 0.75,
                regX: 12,
                regY: 72
            });
            this.sprite.makeDraggable(this.shakeTo.bind(this));
        },
        
        shakeTo: function(x1, y1) {
            var beaker = this.modelRoot.beaker;
            x1 = Math.max(beaker.left, Math.min(beaker.right,    x1));
            y1 = Math.max(         10, Math.min(beaker.top - 10, y1));
            
            var xdelta = Math.min(20, x1 - this.x),
                ydelta = Math.min(20, y1 - this.y),
                totalToAdd = this.crystalsPending += Math.sqrt(xdelta * xdelta + ydelta * ydelta) / 10;
            
            var limitSpeed = function(max, x) {
                return Math.min(max, Math.max(-max, x));
            }
            
            for(; this.crystalsPending > 0; this.crystalsPending--) {
                var crystal = new Crystal(this.modelRoot);
                crystal.x = this.x + xdelta * (this.crystalsPending / totalToAdd);
                crystal.y = this.y + ydelta * (this.crystalsPending / totalToAdd);
                crystal.dx = limitSpeed(50, Math.min(0.1, xdelta * Math.random() + Math.random() - 0.5));
                crystal.dy = limitSpeed(50, Math.min(0.1, ydelta));
                crystal.drotation = (Math.random() - 0.5) * 10000;
                
                this.modelRoot.addCrystal(crystal);
            }
            
            this.x = x1;
            this.y = y1;
        }
    });
});





