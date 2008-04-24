<?php

    include_once("../admin/global.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

    define("WEBPAGES_CACHE",             "webpages");
    define("HOURS_TO_CACHE_WEBPAGES",     1);

    // Disable all caching when run on developer's machine:
    //$g_disable_all_caching = ((isset($_SERVER['SERVER_NAME'])) && ($_SERVER['SERVER_NAME'] == 'localhost')) ? true : false;

    function cache_enabled() {
        if ((isset($_SERVER['SERVER_NAME'])) &&
            ($_SERVER['SERVER_NAME'] == 'localhost')) {
            return true;
        }
        else {
            return true;
        }
    }

    /**
     * Set the group for the file (recursive if file is a directory)
     *
     *
     * @param string $file - name of the file to change the permissions on
     */
    function create_proper_ownership($file) {
        exec('chmod 775 '.$file);

        if (is_dir($file)) {
            exec('chgrp --recursive phet '.$file);
        }
        else {
            exec('chgrp phet '.$file);
        }
    }

    /**
     * TODO: Update this comment when I understand cache fully
     * ?? return './cache-$cache_name'
     *
     * @param string $cache_name - name of the file in cache?
     * @return string './cached-$cache_name'
     */
    function cache_get_location($cache_name) {
        return "./cached-$cache_name";
    }

    function cache_get_file_location($cache_name, $resource_name) {
        return cache_get_location($cache_name)."/$resource_name";
    }

    function cache_clear($cache_name) {
        exec('rm -rf '.cache_get_location($cache_name));
    }

    function cache_clear_simulations() {
        exec('rm -rf '.SITE_ROOT."/simulations/cached-webpages");
    }

    function cache_clear_admin() {
        exec('rm -rf '.SITE_ROOT."/admin/cached-*");
    }

    function cache_clear_teacher_ideas() {
        exec('rm -rf '.SITE_ROOT."/teacher_ideas/cached-*");
    }

    /**
     * Cache the given resource
     *
     * @param string $cache_name - cache name of the resource
     * @param string $resource_name - name of the resource
     * @param unknown_type $resource_contents - contents of the resource (like a web page)
     * @return unknown - same return value as flock_put_contents
     */
    function cache_put($cache_name, $resource_name, $resource_contents) {
        $cache_dir = cache_get_location($cache_name);

        if (!file_exists($cache_dir)) {
            mkdir($cache_dir);
            create_proper_ownership($cache_dir);
        }

        $resource_location = cache_get_file_location($cache_name, $resource_name);

        $return_value = flock_put_contents($resource_location, $resource_contents);


        create_proper_ownership($resource_location);

        return $return_value;
    }

    function cache_get($cache_name, $resource_name, $expiration_hours = false) {
        if (!cache_enabled()) return false;

        $resource_location = cache_get_file_location($cache_name, $resource_name);

        if (!file_exists($resource_location)) return false;

        if (is_numeric($expiration_hours)) {
            $time = filemtime($resource_location);

            $diff = time() - $time;

            // Refresh the cache every 24 hours:
            if ($diff > $expiration_hours * 60 * 60) {
                return false;
            }
        }

        return flock_get_contents($resource_location);
    }

    function cache_auto_get_page_name() {
        $hash_contents = $_SERVER['REQUEST_URI'];

        foreach ($_SESSION as $key => $value) {
            $hash_contents .= "$key=>$value";
        }

        return md5($hash_contents).'.html';
    }

    /**
     * Starts caching the current webpage. Must be called before any content printed.
     */
    function cache_auto_start() {
        if (!cache_enabled()) return;

        $page_name = cache_auto_get_page_name();

        $cached_page = cache_get(WEBPAGES_CACHE, $page_name, HOURS_TO_CACHE_WEBPAGES);

        if ($cached_page) {
            print $cached_page;

            exit;
        }
        else {
            ob_start();
        }
    }

    /**
     * Ends caching the current webpage. Must be called after all content printed.
     */
    function cache_auto_end() {
        if (!cache_enabled()) return;

        $page_name = cache_auto_get_page_name();

        $page_contents = ob_get_contents();
        // TODO: printing scheme is slow, change to get_flush and printing the result
        //$page_contents = ob_get_flush();

        $page_contents = preg_replace('/^ +/',       '',   $page_contents);
        $page_contents = preg_replace('/[ \t]{2,}/', ' ',  $page_contents);

        cache_put(WEBPAGES_CACHE, $page_name, $page_contents);

        ob_end_flush();
    }

?>