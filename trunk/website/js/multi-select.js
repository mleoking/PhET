// TODO: Finish JS validation of multi-select elements

var child_id_index = 1;

function ms_get_parent_table_row(node) {
    while (node && node.tagName != 'TR') {
        node = node.parentNode;
    }
    
    return node;
}

function ms_get_first_td(node) {
    var parentNode = ms_get_parent_table_row(node);
    
    for (var i = 0; i < parentNode.childNodes.length; i++) {
        if (parentNode.childNodes[i].tagName == 'TD') {
            return parentNode.childNodes[i];
        }
    }
}

function ms_mark_as_valid(node) {
    var label_td = ms_get_first_td(node);
    
    if (label_td) { 
        label_td.className = 'valid';
    }
}

function ms_mark_as_invalid(node) {
    var label_td = ms_get_first_td(node);
    
    if (label_td) {
        label_td.className = 'invalid';
    }
}

function ms_has_any_li(node) {
    for (var i = 0; i < node.childNodes.length; i++) {
        if (node.childNodes[i].tagName == 'LI') {
            return true;
        }
    }
    
    return false;
}

function ms_remove_li(id, child_id, invalidate_on_empty) {                  
    var Parent = document.getElementById(id);
    var Child  = document.getElementById(child_id);

    Parent.removeChild(Child);

    if (invalidate_on_empty == true) {
        if (!ms_has_any_li(Parent)) {
            ms_mark_as_invalid(Parent);
        }
    }

    return false;
}

function ms_add_li(basename, list_id, text, name, invalidate_on_empty) {
    if (invalidate_on_empty == undefined) invalidate_on_empty = true;

    var Parent = document.getElementById(list_id);

    ms_mark_as_valid(Parent);

    var li_children = Parent.getElementsByTagName("li");

    for(var i = 0, li_child; li_child = li_children[i]; i++) {
        var input_child = li_child.getElementsByTagName("input")[0];

        if (input_child.name == name) {
            return false;
        }
    }

    var NewLI = document.createElement("li");

    NewLI.id        = "child_" + basename + "_" + child_id_index;                    
    NewLI.innerHTML = "[<a href=\"javascript:void(0)\" onclick=\"ms_remove_li('" + list_id + "','" + NewLI.id + "'," + invalidate_on_empty + ")\">remove</a>] " + text +
                      "<input type=\"hidden\" name=\"" + name + "\" value=\"" + text + "\" />";

    Parent.appendChild(NewLI);

    child_id_index++;
}

function ms_on_change(basename, list_id, dropdown, invalidate_on_empty) {
    var index  = dropdown.selectedIndex;

    var text   = dropdown.options[index].text;
    var value  = dropdown.options[index].value;

    if (value && value != '') {
        alert("here "+ basename + " " + list_id + " " + dropdown + " " + invalidate_on_empty);
        alert(index + " - " + text + " - " + value);
        ms_add_li(basename, list_id, text, value, invalidate_on_empty);
    }

    return false;
}

