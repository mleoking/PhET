require( ['util/WebsocketRefresh',
             '../../../common/phet/app/analytics/Analytics',
             'i18n!../nls/energy-skate-park-strings',
             'view/Tab',
             'easel',
             'tpl!../tab.html'
         ], function ( WebsocketRefresh, Analytics, Strings, Tab, Easel, tabTemplate ) {

    WebsocketRefresh.listenForRefresh();
    var analytics = new Analytics();

    var $container = $( '#container' );
    var tabs = [
        {id: "tab1", name: "introductionTabButton"},
        {id: "tab2", name: "frictionTabButton"},
        {id: "tab3", name: "trackPlaygroundTabButton"}
    ];

    for ( var i = 0; i < tabs.length; i++ ) {
        var tab = tabs[i];
        $container.append( tabTemplate( {id: tab.id,
                                            barGraph: Strings["plots.bar-graph"],
                                            pieChart: Strings["pieChart"],
                                            grid: Strings["controls.show-grid"],
                                            speed: Strings["properties.speed"]} ) );
        var $tab = $( "#" + tab.id );
        new Tab( $tab, Easel, Strings, analytics, function ( newTab ) {
            for ( var j = 0; j < tabs.length; j++ ) {
                var t = tabs[j];
                $( "#" + t.id ).hide();
            }
            $( "#" + newTab ).show();
        }, tab.name ).render();
        if ( i > 0 ) {
            $tab.hide();
        }
    }
} );
