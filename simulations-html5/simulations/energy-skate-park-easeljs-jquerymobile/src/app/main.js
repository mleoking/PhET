require( ['websocket-refresh',
             'introduction-tab',
             'i18n!nls/energy-skate-park-strings'
         ], function ( WebsocketRefresh, IntroductionTab, Strings ) {
    console.log( Strings.large );
    console.log( Strings["plots.position.meters"] );
    console.log( Strings["plots.energy-vs-position"] );
    console.log( Strings["energy-skate-park.description"] );
    WebsocketRefresh.listenForRefresh();
    var tab1 = new IntroductionTab();
} );