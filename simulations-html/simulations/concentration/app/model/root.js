define([
    'underscore',
    'common/util/object',
    'model/shaker',
    'model/crystal',
    'model/solutes',
    'view/beaker'
], function(
    _,
    ObjectUtil,
    Shaker,
    Crystal,
    Solutes,
    BeakerView
) {
    return ObjectUtil.classdef('ConcentrationRoot', Object, {
        initialize: function(container) {
            this.crystals = [];
            this.width = 1024;
            this.height = 748;
            var beaker = this.beaker = {
                left:   100,
                right:  710,
                top:    292,
                bottom: 594,
                fill:     0
            };
            this.solutes = Solutes;
            this.solute = Solutes[0];
            
            this.container = container;
            container.addChild(BeakerView.build(beaker.left, beaker.top, beaker.right, beaker.bottom));
            this.shaker = new Shaker(this, (beaker.left + beaker.right) / 2, beaker.top - 20);
            container.addChild(this.shaker.sprite);
        },
        
        addCrystal: function(crystal) {
            if(this.crystals.length < 200) {
                this.crystals.push(crystal);
                this.container.addChild(crystal.sprite);
            }
        },
        
        removeCrystal: function(crystal) {
            this.container.removeChild(crystal.sprite);
            this.crystals = _.without(this.crystals, crystal);
        },
        
        tick: function(dt) {
            _.each(this.crystals, function(crystal) {
                crystal.tick(dt);
            }.bind(this));
        },
        
        updateView: function() {
            this.shaker.updateView();
            _.each(this.crystals, function(crystal) {
                crystal.updateView();
            });
        }
    });
});
