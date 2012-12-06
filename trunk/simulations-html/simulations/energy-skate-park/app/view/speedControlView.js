define([
  'underscore',
  'tpl!view/speed-control.html'
], function( _, speedControlTmpl){

  function SpeedControl( Strings, dt ){
    this.Strings = Strings;

    this.$el = $('<div/>', { 'class': 'speedControl' });

    var self = this;

    function updateInput( value ){
      // make sure that the unchecked checkbox is actually unchecked
      self.$el.find('input:not([value="'+ value +'"])')
        .attr('checked', false)
        .checkboxradio('refresh');

      // make sure the correct checkbox is checked
      self.$('[value="'+ value +'"]')
        .attr('checked', true)
        .checkboxradio( 'refresh' );
    }

    dt.addObserver( updateInput );

    this.$el.on('change', 'input', function(e){
      var speed = parseFloat( $(e.currentTarget).val(), 10);
      dt.set( speed );
    });

  }

  SpeedControl.prototype.$ = function( selector ){
    this.$el.find( selector );
  };

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
