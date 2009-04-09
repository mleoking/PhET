// Copyright: (c) 2006, Michal Tatarynowicz (tatarynowicz@gmail.com)
// Licenced as Public Domain (http://creativecommons.org/licenses/publicdomain/)
// $Id: jquery_std.js 568 2006-06-05 09:40:53Z Michał $


// Additions to the wonderful jQuery Javascript library
//
//
// // basics
//
// $.fn.first = function() {
// 	return this.cur[0];
// }
//
// $.fn.last = function() {
// 	return this.cur[this.size()-1];
// }
//
//
// // form handling
//
// $.fn.focus = function() {
// 	if (this.size() && this.get(0).focus) try { this.get(0).focus() } catch(e) {};
// 	return this;
// }
//
// $.fn.blur = function() {
// 	if (this.size() && this.get(0).blur) this.get(0).blur();
// 	return this;
// }

$.fn.enable = function() {
	return this.each(function(){
		this.disabled = false;
	});
}

$.fn.disable = function() {
	return this.each(function(){
		this.disabled = true;
	});
}
//
//
// // timed events
//
// $.fn.time = function (t, f, id) {
// 	id = id || 1;
// 	return this.each(function(){
// 		this['timer'+id] = setTimeout(f,t*1000);
// 	});
// }
//
// $.fn.untime = function (id) {
// 	id = id || 1;
// 	return this.each(function(){
// 		clearTimeout(this['timer'+id]);
// 	});
// }
//
//
// // visual DOM manipulation
//
//
// $.pos = function (n) {
// 	var t = 0, l = 0;
//
// 	var rev = false;
// 	if (n.style && (n.style.position == 'static')) {
// 		n.style.position = 'absolute';
// 		rev = true;
// 	}
//
// 	do {
// 		t += n.offsetTop || 0;
// 		l += n.offsetLeft || 0;
// 		// n = n.offsetParent;
// 		try { n = n.offsetParent; } catch(e){ return { x: 0, y: 0}; }
// 	} while (n);
//
// 	if (rev) n.style.position = 'static';
//
// 	return { x: l, y: t };
// }
//
// $.place = function (n, p) {
// 	var s = n.style;
// 	s.position = 'absolute';
// 	s.left = p.x + 'px';
// 	s.top  = p.y + 'px';
// }
//
// $.scrollOffset = function(pos) {
// 	if (pos) {
// 		if (window.pageXOffset) {
// 			window.pageXOffset = pos.x;
// 			window.pageYOffset = pos.y;
// 		}
// 		else if (document.documentElement.scrollLeft) {
// 			document.documentElement.scrollLeft = pos.x;
// 			document.documentElement.scrollTop = pos.y;
// 		}
// 		else if (document.body.scrollTop) {
// 			document.body.scrollLeft = pos.x;
// 			document.body.scrollTop = pos.y;
// 		}
// 	}
// 	else return {
// 		x: window.pageXOffset
//            || document.documentElement.scrollLeft
//            || document.body.scrollLeft
//            || 0,
// 		y: window.pageYOffset
// 		   || document.documentElement.scrollTop
// 		   || document.body.scrollTop
// 		   || 0
// 	}
// }
//
// $.fn.pos = function(p) {
// 	if (p)
// 		return this.each(function(){
// 			$.place(this, p);
// 		});
// 	else
// 		return $.pos(this.get(0));
// }
//
// $.fn.center = function() {
// 	var w = $.windowSize();
// 	var so = $.scrollOffset();
//
// 	return this.each(function(){
// 		var s = $.size(this);
// 		$.place(this, {
// 			x: so.x + Math.round((w.w - s.w)/2),
// 			y: so.y + Math.round((w.h - s.h)/2) - 10  // -10 compensates for optical illusion
// 		});
// 	});
// }
//
// $.fn.centerOver = function(rel) {
// 	var s = $.size(rel);
// 	var p = $.pos(rel);
// 	var so = $.scrollOffset();
// 	var x = so.x + Math.round((p.x + s.w)/2);
// 	var y = so.y + Math.round((p.y + s.h)/2);
//
// 	return this.each(function(){
// 		var s = $.size(this);
// 		$.place(this, {
// 			x: x - Math.round(s.w/2),
// 			y: y - Math.round(s.h/2) - 10  // -10 compensates for optical illusion
// 		});
// 	});
// }
//
// $.fn.centerBelow = function(rel, dist) {
// 	var p = $.pos(rel);
// 	var s = $.size(rel);
// 	var so = $.scrollOffset();
// 	var x = so.x + Math.round(p.x + s.w/2);
// 	var y = p.y + so.y + s.h + (dist || 0);
//
// 	return this.each(function(){
// 		var s = $.size(this);
// 		$.place(this, {
// 			x: Math.round(x - s.w/2),
// 			y: y
// 		});
// 	});
// }
//
// $.fn.limitToWindow = function() {
// 	var w = $.windowSize();
// 	return this.each(function(){
// 		var s = $.size(this);
// 		var p = $.pos(this);
// 		if (p.x + s.w > w.w) p.x = max(0, w.w - s.w - 5);
// 		if (p.y + s.h > w.h) p.y = max(0, w.h - s.h - 5);
// 		$.place(this, p);
// 	});
// }
//
// $.fn.resizeTo = function(size) {
// 	return this.each(function(){
// 		this.style.height = size.h + 'px';
// 		this.style.width  = size.w + 'px';
// 	});
// }
//
//
// // DOM measurements
//
// $.size = function (n) {
// 	n = $(n).get(0);
//
// 	if (n.style.display == 'none') {
// 		var v = n.style.visibility;
// 		n.style.visibility = 'hidden';
// 		n.style.display = '';
// 		var s = { w: n.offsetWidth, h: n.offsetHeight };
// 		n.style.display = 'none';
// 		n.style.visibility = v;
// 	}
// 	else {
// 		var s = { w: n.offsetWidth, h: n.offsetHeight };
// 	}
//
// 	return s;
// }
//
// $.windowSize = function() {
// 	var de = document.documentElement;
// 	var db = document.body;
// 	return {
// 		w: window.innerWidth || (de && de.clientWidth) || (db && db.clientWidth),
// 		h: window.innerHeight || (de && de.clientHeight) || (db && db.clientHeight)
// 	}
// }
//
//
// // DOM properties
//
// $.fn.prop = function(a,b) {
// 	if (b == null && this.size())
// 		return this.get(0)[a];
// 	else
// 		return this.set(a,b);
// }
//
// $.fn.hasClass = function(c) {
// 	if (this.size()) return $.hasWord(this.get(0),c);
// 	else return false;
// }
//
//
// // DOM builders
//
// $.elem = function(tag) {
// 	var fix = { 'class':'className', 'Class':'className' };
// 	try {
// 		var e = document.createElement(tag);
//
// 		var attrs = arguments[1] || {};
// 		for (var attr in attrs) {
// 			var a = fix[attr] || attr;
// 			e[a] = attrs[attr];
// 		};
// 	}
// 	catch( ex ) {
// 		alert( 'Cannot create <' + tag + '> element:\n' + args.toSource() + '\n' + args );
// 		var e = null;
// 	};
// 	return e;
// };
//
// $.text = function(content) {
// 	return document.createTextNode(content);
// };
//
//
// // patch for jquery ajax to handle json natively
// // Opera 9 doesn't like text/json header, I use text/javascript
//
// $.httpData = function(r,type) {
// 	var h = r.getResponseHeader("content-type");
// 	return h.indexOf("xml") > 0 || type == "xml"
// 		? r.responseXML
// 		: h.indexOf("json") > 0 || h.indexOf("javascript") > 0 || type == "json"
// 			? eval('('+r.responseText+')')
// 			: r.responseText;
// };
//
//
// // some useful extensions to Array (from Prototype)
//
// Array.prototype.each = function(iterator) {
// 	for (var i = 0; i < this.length; i++)
// 		iterator(this[i]);
// };
//
// Array.prototype.last = function() {
// 	return this[this.length - 1];
// };
//
//
// // minor utility functions
//
// function min(a,b) {
// 	return a <= b? a: b;
// }
//
// function max(a,b) {
// 	return a >= b? a: b;
// }
