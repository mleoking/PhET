define([
  'underscore',
  'common/AtomIdentifier',
  'tpl!templates/periodic-table.html'
], function( _, AtomIdentifier, periodicTableTemplate ){

  function PeriodicTablelView( atom ){
    this.atom = atom;

    if( !this.atom ){
      throw new Error('I need an atom!');
    }

    this.$el = $('#periodic-table');
    this.el = this.$el[0];

    var self = this;
    var numProtons = this.atom.getNumProtons();

    this.atom.events.on('configurationChanged', function(){
      var newNumProtons = self.atom.getNumProtons();

      if( numProtons !== newNumProtons ){
        numProtons = newNumProtons;
        self.render();
      }
    });

    this.$el.html( periodicTableTemplate({}) );
    this.render();
  }

  PeriodicTablelView.prototype.render = function(){
    var symbol = AtomIdentifier.getSymbol( this.atom.getNumProtons() );
    this.$el.find('.active').removeClass('active');
    this.$el.find('[data-symbol="'+ symbol +'"]').addClass('active');
  };

  return PeriodicTablelView;

});
