define([
  'underscore',
  'tpl!view/speed-control.html'
], function( _, speedControlTmpl){

  function SpeedControl( Strings ){
    this.Strings = Strings;

    this.$el = $('<div/>', { 'class': 'speedControl' });
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

  return SpeedControl;

});
