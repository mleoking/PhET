<?php

class Dirs {
    private static $instance;

    private $siteRoot;
    private $portalRoot;
    private $cacheDir;
    private $simsRoot;
    private $phetDistRoot;
    private $newslettersRoot;

    // $immutable allows tying in test harness.  Must be set to TRUE in production code.
    private function __construct($immutable = true) {
        $this->immutable = $immutable;

        // SITE_ROOT *MUST* be defined
        assert(defined('SITE_ROOT'));
        $this->siteRoot = SITE_ROOT;

        $this->portalRoot = (defined('PORTAL_ROOT')) ? PORTAL_ROOT : $this->siteRoot;

        $cacheRoot = (defined('CACHE_ROOT')) ? CACHE_ROOT : $this->portalRoot;
        $cacheDirName = (defined('CACHE_DIRNAME')) ? CACHE_DIRNAME : "webcache";
        $this->cacheDir = $cacheRoot . DIRECTORY_SEPARATOR . $cacheDirName . DIRECTORY_SEPARATOR;

        $this->simsRoot = (defined('SIMS_ROOT')) ? SIMS_ROOT : $this->portalRoot.'sims';
        $this->phetDistRoot = (defined('PHET_DIST_ROOT')) ? PHET_DIST_ROOT : $this->portalRoot.'phet-dist'.DIRECTORY_SEPARATOR;
        $this->newslettersRoot = (defined('NEWSLETTERS_ROOT')) ? NEWSLETTERS_ROOT : $this->portalRoot.'newsletters'.DIRECTORY_SEPARATOR;
    }

    public static function inst($immutable = true) {
        if (!isset(self::$instance)) {
            $class = __CLASS__;
            self::$instance = new $class($immutable);
        }
        return self::$instance;
    }

    // For testing, allow unsetting of singleton
    public static function clear() {
        if (!debug_is_on()) {
            throw new PhetException("class Dirs() is immutable");
        }

        self::$instance = NULL;
    }

    public function siteRoot() {
        return $this->siteRoot;
    }

    public function portalRoot() {
        return $this->portalRoot;
    }

    public function cacheDir() {
        return $this->cacheRoot;
    }

    public function simsRoot() {
        return $this->simsRoot;
    }

    public function phetDistRoot() {
        return $this->phetDistRoot;
    }

    public function newslettersRoot() {
        return $this->newslettersRoot;
    }

    // If $this->immutable is set, these functions throw exceptions
    // NOT to be used in production code

    public function setSiteRoot($siteRoot) {
        if ($this->immutable) {
            throw new PhetException("class Dirs() is immutable");
        }

        $this->siteRoot = $siteRoot;
    }

    public function setPortalRoot($portalRoot) {
        if ($this->immutable) {
            throw new PhetException("class Dirs() is immutable");
        }

        $this->portalRoot = $portalRoot;
    }

    public function setCacheRoot($cacheRoot) {
        if ($this->immutable) {
            throw new PhetException("class Dirs() is immutable");
        }

        $this->cacheRoot = $cacheRoot;
    }

    public function setSimsRoot($simsRoot) {
        if ($this->immutable) {
            throw new PhetException("class Dirs() is immutable");
        }

        $this->simsRoot = $simsRoot;
    }

    public function setPhetDistRoot($phetDistRoot) {
        if ($this->immutable) {
            throw new PhetException("class Dirs() is immutable");
        }

        $this->phetDistRoot = $phetDistRoot;
    }
}

?>