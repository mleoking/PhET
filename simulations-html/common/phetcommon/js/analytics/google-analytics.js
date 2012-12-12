// Copyright 2002-2012, University of Colorado

/**
 * Google analytics collection for HTML5 sims.
 * Include this script as the last thing in your DOM's head.
 * Code provided by Google, possibly doctored by PhET.
 */

var _gaq = _gaq || [];
_gaq.push( ["_setAccount", "UA-5033201-1"] );
_gaq.push( ["_setDomainName", "phet.colorado.edu"] );
_gaq.push( ["_trackPageview"] );
_gaq.push( ['_trackPageLoadTime'] );
_gaq.push( ["b._setAccount", "UA-5033010-1"] );
_gaq.push( ["b._setDomainName", "phet.colorado.edu"] );
_gaq.push( ["b._trackPageview"] );

(function () {
    var ga = document.createElement( 'script' );
    ga.type = 'text/javascript';
    ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName( 'script' )[0];
    s.parentNode.insertBefore( ga, s );
})();
