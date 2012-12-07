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
    var tabs = ["tab1", "tab2", "tab3"];
    for ( var i = 0; i < tabs.length; i++ ) {
        var tab = tabs[i];
        $container.append( tabTemplate( {id: tab,
                                            barGraph: Strings["plots.bar-graph"],
                                            pieChart: Strings["pieChart"],
                                            grid: Strings["controls.show-grid"],
                                            speed: Strings["properties.speed"]} ) );
        var $tab = $( "#" + tab );
        new Tab( $tab, Easel, Strings, analytics ).render();
    }
} );
