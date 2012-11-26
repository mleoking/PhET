define([
    'common/easel-custom',
    'lib/jquery',
    'common/util/object',
    'model/root',
    'model/crystal'
], function(
    Easel,
    $,
    ObjectUtil,
    ConcentrationModel,
    Crystal
) {
    $canvas = $(canvas);
    var stage = new Easel.Stage($canvas[0]),
        mainContainer = new Easel.Container();
    stage.addChild(mainContainer);
    stage.enableMouseOver();
    
    window.model = new ConcentrationModel(mainContainer);
    
    var fps = 60;
    Easel.Ticker.setFPS(fps);
    
    var modelTimeResolution = 1 / fps,  // set smaller if model requires extra precision
        futureRemaining = 0;
    Easel.Ticker.addListener(function(dt) {
        futureRemaining += Math.max(0.2, dt / 1000.0);
        for(; futureRemaining > 0; futureRemaining -= modelTimeResolution)
            model.tick(modelTimeResolution);
        
        model.updateView();
        stage.update();
    });
    
    $(window).blur( function() { console.log('pause');  createjs.Ticker.setPaused(true); });
    $(window).focus(function() { console.log('resume'); createjs.Ticker.setPaused(false); });
    
    var onResize = function(event) {
        var winW = $(window).width(),
            winH = $(window).height(),
            scale = Math.min(winW / 400, winH / 300),
            contentW = scale * 400,
            contentH = scale * 300;
        $canvas.attr('width',  winW);
        $canvas.attr('height', winH);
        mainContainer.x = (winW - contentW) / 2;
        mainContainer.y = (winH - contentH) / 2;
        mainContainer.setScale(scale);
        
        model.updateView();
        stage.update();
    }
    $(window).resize(onResize)
    onResize() // initial position
    
    $('#sim-loading').remove();
});
