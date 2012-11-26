define([
    'underscore',
    'common/easel-custom'
], function(
    _,
    Easel
) {
    return {
        rgbArrayToColor: function(a) {
            return Easel.Graphics.getRGB.apply(null, a);
        },
        
        blendArrays: function(c0, c1, alpha) {
            return _.map(
                _.zip(c0, c1),
                function(c) {
                    return c[0] * (1-alpha) + c[1] * alpha;
                }
            );
        }
    }
});
