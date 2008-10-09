function si(object) {
	if (document.getElementById){
		document.getElementById(object).style.display = 'inline';
	} 
}

function h(object) { 
	if (document.getElementById){
		document.getElementById(object).style.display = 'none'; 
	} 
}

function sb(object) { 
	if (document.getElementById){
		document.getElementById(object).style.display = 'block'; 
	}
}

function hl(linav) { 
	if (document.getElementById){
		mainnav=document.getElementById("nav").childNodes; 
		i=0; while(mainnav[i]){ mainnav[i].id=""; i++;} 
		linav.id="high"; 
	}	
}

function jump(targ,selObj,restore){ //v3.0
	if (selObj.options[selObj.selectedIndex].value) 
		eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
	if (restore) selObj.selectedIndex=0;
}

function report_expand(id, newC) {
	var i=0;
	var obj;
	while (obj = document.getElementById(id+"_"+i)) {
		if (newC) { 
			obj.className = newC; 
		} else if (obj.className == 'show') { 
			obj.className = 'hide'; 
		} else if (obj.className == 'hide') { 
			obj.className = 'show';
		} else if (obj.className == 'tdwn') { 
			obj.className = 'tup'; 
		} else if (obj.className == 'tup') { 
			obj.className = 'tdwn'; 
		} else if (obj.className == 'sel') { 
			obj.className = 'nosel'; 
		}  else if (obj.className == 'nosel') { 
			obj.className = 'sel'; 
		}
		i++;
	}
}

function report_setpref(id, cookiename) {
	var obj;
	var prefvalue; 
	if (obj = document.getElementById(id+"_1")) {
		if (obj.className == 'hide') {
			prefvalue = 1;
		} else {
			prefvalue = 0;
		}
		document.cookie = cookiename+"="+prefvalue;
	}

	return void(0);
}

function tab(o) { 
	if (document.getElementById) {
		t = document.getElementById("secondwave").childNodes; 
		var i = 0; 
			while(t[i]) { 
				t[i].className="";  
				i++;
			} 
		o.className="selected"; 
	}
}

function people_select(object) {
	var name = object.name+'_text';
	if (object.options[object.selectedIndex].value == 3) {
		document.getElementById(name).style.display = 'inline';
	} else {
		document.getElementById(name).style.display = 'none';

	}
}

function display_without_check() {
	var found = 0;
	if(document.advsearch.all_words.value != '') {
		found = 1;
	}
	if(document.advsearch.exact_phrase.value != '') {
		found = 1;
	}
	if(document.advsearch.some_word.value != '') {
		found = 1;
	}

	if (found == 1) {
	       document.advsearch.without_words.disabled = false;
        } else {
	       document.advsearch.without_words.disabled = true;
        }
}

function checkoff_sub_troves(obj,index) {
	var name;
	var state;
	var sub_list;

	if (index == null) {
		name = 'stc_' + obj.value;
		state = obj.checked;
	} else {
		name = 'stc_' + obj[index].value;
		state = obj[index].checked;
	}

	if (document.getElementById) {
		sub_list = document.getElementById(name).value;
	} else {
		sub_list = document.advsearch[name].value;
	}

	var subs = sub_list.split(",");
	var cboxes = document.advsearch["trove_cat[]"];
	for (x in subs) {
		for(var i=0; i< cboxes.length; ++i) {
			if (cboxes[i].value == subs[x]) {
				cboxes[i].checked = state;
				var sub_name = 'stc_' + cboxes[i].value;
				if (document.getElementById) {
					if (document.getElementById(sub_name)) {
						checkoff_sub_troves(cboxes, i);
					}
				} else {	
					if (document.advsearch[sub_name]) {
						checkoff_sub_troves(cboxes, i);
					}
				}
			}
		}
	}
}

function form_check() {
        var valid = true;
	var message = "";
        for (var i=0; i<document.advsearch.elements.length; i++) {
                var field = document.advsearch.elements[i];

		// Check dates
                if (field.name.match(/date/)) {
			if (field.value) {
				var field_name = field.name.replace(new RegExp(/_date_/)," ");
				if (field.value.match(/(19|20)(\d{2})[- \/.](0[1-9]|1[012])[- \/.](0[1-9]|[12][0-9]|3[01])/)) {
					var year = RegExp.$1 + RegExp.$2;
					year = parseInt(year);
					var month = parseInt(RegExp.$3);
					var day = parseInt(RegExp.$4);
					if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
						message += "The " + field_name + " date is invalid. <br />";
					} else if (day >= 30 && month == 2) {
						message += "The " + field_name + " date is invalid. <br />";
					} else if (day == 29 && month == 2 && !(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) {
						message += "The " + field_name + " date is invalid. <br />";
					} else {
						if (field.name.match(/date_start/)) {
							var end;
							var end_name = field.name.replace(new RegExp(/date_start/),"");
							end_name += 'date_end';
							if (document.getElementById(end_name)) {
								end = document.getElementById(end_name).value;
							} else {
								end = document.advsearch[end_name].value;
							}
							if (end && end.match(/(19|20)(\d{2})[- \/.](0[1-9]|1[012])[- \/.](0[1-9]|[12][0-9]|3[01])/)) {
								var end_year = RegExp.$1 + RegExp.$2;
								end_year = parseInt(end_year);
								var end_month = parseInt(RegExp.$3);
								var end_day = parseInt(RegExp.$4);
								var end_date = (end_year * 365) + (end_month * 32) + end_day;
								var start_date = (year * 365) + (month * 32) + day;
								if (end_date < start_date) {
									message += "The start date falls after the end date. <br />";
								}
							}
                                                }
					}
				} else {
					message += "The format for the " + field_name + " date field is yyyy-mm-dd. <br />";
				}
			}
                }

		// Check percentages
		if (field.name.match(/percentile/)) {
			if (field.value) {
				if (field.value.match(/^\d{1,3}\.?\d*$/)) {
					if (field.value < 0 || field.value > 100) {
						message += "Percentages must be positive numeric values between 0 and 100. <br />";
					} else if (field.name.match(/start/)) {
						var end;
						if (document.getElementById("percentile_end")) {
							end = document.getElementById("percentile_end").value;
						} else {
							end = document.advsearch["percentile_end"].value;
						}
						if (end) {
							end = parseInt(end);
							var start = parseInt(field.value);

							if (end < start) {
								message += "The starting percentage must be smaller than the end value. <br />";
							}
						}
					}
					
				} else {
					message += "Percentages must be positive numeric values only. <br />";
				}
			}
		}
        }
	if (message) {
		var error = document.getElementById("error");
		error.innerHTML = message;
		valid = false;
	}
	return valid;
}

function clear_form() {
        for (var i=0; i<document.advsearch.elements.length; i++) {
		var element = document.advsearch.elements[i];
		if (element.type == 'submit') { continue; }
		if (element.type == 'reset') { continue; }
		if (element.type == 'button') { continue; }
		if (element.type == 'hidden') { continue; }
		if (element.type == 'text') { element.value = ''; }
		if (element.type == 'textarea') { element.value = ''; }
		if (element.type == 'checkbox') { 
			if (element.name.match(/search_/)) {
				if (element.name.match(/search_comments/)) {
					element.checked = false; 
				} else {
					element.checked = true; 
				}
			} else {
				element.checked = false; 
			}
		}
		if (element.type == 'radio') { element.checked = false; }
		if (element.type == 'select-multiple') { element.selectedIndex = -1; }
		if (element.type == 'select-one') { element.selectedIndex = -1; }
	}

	document.advsearch.without_words.disabled = true;
	return false;
}
