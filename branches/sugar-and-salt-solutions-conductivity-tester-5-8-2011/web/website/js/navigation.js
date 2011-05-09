function select_current_navbar_category(request_uri) {
    $("li.subnav a").each(function(i) {
        var re = /^.+(\.com|\.edu|\.net|\.org|(localhost[:0-9]*))(\/.+)$/i;

        var result = re.exec(this.href);

        var relative_url = this.href;

        if (result) {
            relative_url = result[3];
        }
        if (string_starts_with(request_uri, relative_url)) {
            this.className            = 'subnav-selected';
            this.parentNode.className = 'subnav-selected';
        }
    });
}
