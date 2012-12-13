/**
 * svg parser by Mobile Lab
 * @ http://mlearner.com
**/

(function($, object){
  // create util object, if not exist
  if (!window[object].util) window[object].util = {};

  // main function - parsing svg
  var parseSVG = function(id) {
    var svgContainer = $('#' + id);
    var svgXML = $(svgContainer[0].innerHTML);

    // create svg element with attributes and styles
    var svgElement = createSVGelement('svg');
    svgElement.setAttribute("version", "1.1");
    svgElement.setAttribute("xmlns", "http://www.w3.org/2000/svg");
    svgElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");

    var style = $.trim( svgXML.attr('style') ).split(';'), st;
    for (var i = 0, l = style.length; i < l; i++) {
      if ((style[i] = $.trim( style[i] ).split(':')).length == 2) {
         svgElement.style[ $.trim( style[i][0] ) ] = $.trim( style[i][1] );
      }
    }

    // create recursively all elements
    svgXML.children().each( parseSVGelement(svgElement) );

    // replace SVG DOM
    svgContainer.html('')[0].appendChild(svgElement);
  };

  var parseSVGelement = function(parent) {
    return function() {
      // create element
      var name = this.nodeName.toLowerCase(), jelem = $(this),
          elem = createSVGelement( name ), att, i, l;
      // set attributes
      for (i = 0, att = this.attributes, l = att.length; i < l; i++) {
        elem.setAttribute(att.item(i).nodeName, att.item(i).nodeValue)
      }
      // if text - add text
      if (name == 'text') {
        elem.appendChild(document.createTextNode( jelem.text() ));
      }
      // parse childrens
      jelem.children().each( parseSVGelement(elem) );
      // add to parent
      parent.appendChild(elem);
    }
  };

  var createSVGelement = function(name) {
    name = name.replace(/clippath/,'clipPath');
    name = name.replace(/gradient/,'Gradient');
    return document.createElementNS("http://www.w3.org/2000/svg", name);
  };

  window[object].util.parseSVG = function(id) {
    parseSVG(id);
  };

})(jQuery, "phet");
 