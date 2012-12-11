require( ['util/WebsocketRefresh',
             '../../../common/phetcommon/js/analytics/Analytics',
             'i18n!../nls/energy-skate-park-strings',
             'view/Tab',
             'easel',
             'tpl!../tab.html',
             'phetcommon/model/property/Property',
             'phetcommon/view/PrototypeDialog'
         ], function ( WebsocketRefresh, Analytics, Strings, Tab, Easel, tabTemplate, Property, PrototypeDialog ) {

    WebsocketRefresh.listenForRefresh();
    var analytics = new Analytics();

    var tabs = ["introductionTab", "frictionTab", "trackPlaygroundTab"];

    var activeTab = new Property( tabs[0] );
    var $container = $( '#container' );

    for ( var i = 0; i < tabs.length; i++ ) {
        var tab = tabs[i];
        $container.append( tabTemplate( {id: tab,
                                            barGraph: Strings["plots.bar-graph"],
                                            pieChart: Strings["pieChart"],
                                            grid: Strings["controls.show-grid"],
                                            speed: Strings["properties.speed"]} ) );
        var $tab = $( "#" + tab );
        new Tab( $tab, Easel, Strings, analytics, tab, activeTab ).render();
        if ( i > 0 ) {
            $tab.hide();
        }
    }

    activeTab.addObserver( function ( newTab ) {
        for ( var j = 0; j < tabs.length; j++ ) {
            $( "#" + tabs[j] ).hide();
        }
        $( "#" + newTab ).show();
    } );

    PrototypeDialog.init( "Energy Skate Park: Basics" );
} );
