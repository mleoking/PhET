define([
    'underscore',
    'common/easel-custom',
    'common/util/object',
    'common/util/color',
    'common/model/model'
], function(
    _,
    Easel,
    ObjectUtil,
    ColorUtil,
    Model
) {
    var Solute = ObjectUtil.classdef('Solute', Model, {
        initialize: function(opts) {
            _.extend(this, opts);
            this.saturatedColor = ColorUtil.rgbArrayToColor(_.last(this.colors).color);
        },
        
        colorForConcentration: function(conc) {
            var prev = { conc: 0, color: [224, 255, 255] }; // water color
            
            for(var n = 0; n < this.colors.length; n++) {
                var cur = this.colors[n];
                if(conc < cur.conc)
                    return ColorUtil.rgbArrayToColor(
                        ColorUtil.blendArrays(prev.color, cur.color, (conc - prev.conc) / (cur.conc - prev.conc)));
                prev = cur;
            }
            
            return this.saturatedColor;
        }
    });
    
    return [
        new Solute({
            name:       'drinkMix',
            saturation: 5.96,
            colors:     [{ conc: 0.05, color: [255, 225, 225] },
                         { conc: 5.96, color: [255,   0,   0] }]
        }),
        new Solute({
            name:       'cobaltIINitrate',
            saturation: 5.64,
            colors:     [{ conc: 0.05, color: [255, 225, 225] },
                         { conc: 5.64, color: [255,   0,   0] }]
        }),
        new Solute({
            name:       'cobaltChloride',
            saturation: 4.33,
            colors:     [{ conc: 0.05, color: [255, 242, 242] },
                         { conc: 4.33, color: [255, 106, 106] }]
        }),
        new Solute({
            name:       'potassiumDichromate',
            saturation: 0.51,
            colors:     [{ conc: 0.01, color: [255, 204, 153] },
                         { conc: 0.51, color: [255, 127,   0] }]
        }),
        new Solute({
            name:       'potassiumChromate',
            saturation: 3.35,
            colors:     [{ conc: 0.05, color: [255, 255, 153] },
                         { conc: 3.35, color: [255, 255,   0] }]
        }),
        new Solute({
            name:       'nickelIIChloride',
            saturation: 5.21,
            colors:     [{ conc: 0.20, color: [170, 255, 170] },
                         { conc: 5.21, color: [  0, 128,   0] }]
        }),
        new Solute({
            name:       'copperSulfate',
            saturation: 1.38,
            colors:     [{ conc: 0.20, color: [200, 225, 255] },
                         { conc: 1.38, color: [ 30, 144, 255] }]
        }),
        new Solute({
            name:       'potassiumPermanganate',
            saturation: 0.48,
            colors:     [{ conc: 0.01, color: [255,   0, 255] },
                         { conc: 0.48, color: [ 80,   0, 120] }]
        }),
    ];
});

