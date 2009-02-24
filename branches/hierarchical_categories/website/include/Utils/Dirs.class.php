<?php

class Dirs {
    private static $instance;

    private $siteRoot;
    private $portalRoot;
    private $cacheRoot;
    private $simsRoot;
    private $phetDistRoot;

    // $immutable allows tying in test harness.  Set it to TRUE in production code.
    private function __construct($immutable = true) {
        $this->immutable = $immutable;

        assert(defined('SITE_ROOT'));
        $this->siteRoot = SITE_ROOT;

        assert(defined('PORTAL_ROOT'));
        $this->portalRoot = PORTAL_ROOT;

        assert(defined('CACHE_ROOT'));
        $this->cacheRoot = CACHE_ROOT;

        assert(defined('SIMS_ROOT'));
        $this->simsRoot = SIMS_ROOT;

        assert(defined('PHET_DIST_ROOT'));
        $this->phetDistRoot = PHET_DIST_ROOT;
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

    public function cacheRoot() {
        return $this->cacheRoot;
    }

    public function simsRoot() {
        return $this->simsRoot;
    }

    public function phetDistRoot() {
        return $this->phetDistRoot;
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