define([
    'common/easel-custom',
    'common/util/object',
    'model/root',
    'model/crystal'
], function(
    Easel,
    ObjectUtil,
    ConcentrationModel,
    Crystal
) {
    console.log(new Easel.DisplayObject().set({ x: 100, y: 100, foo: 'bar' }))
    
    $canvas = $(canvas);
    var stage = new Easel.Stage($canvas[0]),
        mainContainer = new Easel.Container();
    stage.addChild(mainContainer);
    createjs.Touch.enable(stage, false, false);
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
    
    $(window).blur( function() { console.log('pause');  createjs.Ticker.setPaused(true);  });
    $(window).focus(function() { console.log('resume'); createjs.Ticker.setPaused(false); });
    
    var onResize = function(event) {
        var winW = $(window).width(),
            winH = $(window).height(),
            scale = Math.min(winW / model.width, winH / model.height),
            contentW = scale * model.width,
            contentH = scale * model.height;
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

