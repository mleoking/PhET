define([
  'underscore',
  'tpl!view/speed-control.html'
], function( _, speedControlTmpl){

  function SpeedControl( Strings, parentTab ){
    this.Strings = Strings;

    this.$el = $('<div/>', { 'class': 'speedControl' });

    this.$el.on('change', 'input', function(e){
      var speed = parseFloat( $(e.currentTarget).val(), 10);
      parentTab.dt = speed;
    });
  }

  SpeedControl.prototype.render = function(){

    var slowMotionString = this.Strings["slow.motion"];
    var normalString = this.Strings.normal;

    this.$el.html( speedControlTmpl({
      slowMotion: slowMotionString,
      normal: normalString
    }) );

    return this.$el;
  };

  SpeedControl.prototype.getValue = function(){
    return parseFloat( this.$el.find(':checked').val(), 10);
  };

  return SpeedControl;

});
