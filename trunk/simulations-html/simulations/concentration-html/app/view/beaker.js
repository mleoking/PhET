define([
    'common/easel-custom',
], function(
    Easel
) {
    return {
        build: function(left, top, right, bottom) {
            var width = right - left,
                height = bottom - top;
            
            var beakerView = new Easel.Container();
            beakerView.x = left;
            beakerView.y = top;
            
            // beaker boundary
            var thickness = 5,
                lip = height * 0.075;
            beakerView.addChild(
                new Easel.Shape().set({
                    strokeColor: 'black',
                    strokeWidth: thickness,
                    strokeCap: 'round',
                    graphics: function(g) {
                        g.moveTo(-thickness - lip, -lip)
                         .lineTo(-thickness, 0)
                         .lineTo(-thickness, height + thickness)
                         .lineTo(width + thickness, height + thickness)
                         .lineTo(width + thickness, 0)
                         .lineTo(width + thickness + lip, -lip);
                    }
                })
            );
            
            return beakerView;
        }
    };
});
