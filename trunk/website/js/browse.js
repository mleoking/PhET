function browse_build_update_query(select_prefix) {
    var select_id = select_prefix + '_uid';

    var select_element = document.getElementById(select_id);

    var option_nodes = select_element.getElementsByTagName('option');

    var selected_options = '';

    for (var i = 0; i < option_nodes.length; i++) {
        var option_node = option_nodes[i];

        if (option_node.selected) {
            selected_options += '&' + select_prefix + '[]=';

            selected_options += encodeURIComponent(option_node.value);
        }
    }

    return selected_options;
}

function browse_update_browser_for_select_element() {
    var sims_query   = browse_build_update_query('Simulations');
    var types_query  = browse_build_update_query('Types');
    var levels_query = browse_build_update_query('Levels');

    var url = 'browse.php?content_only=true&order={$this->order}&sort_by={$this->sort_by}' + sims_query + types_query + levels_query;

    HTTP.updateElementWithGet(url, null, 'browseresults');

    return false;
}
