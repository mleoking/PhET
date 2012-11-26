define([
    'lib/underscore',
    'common/util/object',
    'model/shaker',
    'model/crystal',
    'model/solutes'
], function(
    _,
    ObjectUtil,
    Shaker,
    Crystal,
    Solutes
) {
    return ObjectUtil.classdef('ConcentrationRoot', Object, {
        initialize: function(container) {
            this.crystals = [];
            this.tub = {
                left:    50,
                right:  350,
                top:    100,
                bottom: 250,
                fill:     0
            };
            this.solutes = Solutes;
            this.solute = Solutes[0];
            
            this.container = container;
            this.shaker = new Shaker(this, 50, 100);
            this.container.addChild(this.shaker.sprite);
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
