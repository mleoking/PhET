<?php

class JavaSimulation extends BaseSimulation {
    public function runsInNewBrowserWindow() {
        return FALSE;
    }

    public function getSize() {
        $file = self::sim_root."{$this->project_name}/{$this->project_name}_all.jar";
        if (!file_exists($file)) {
            throw new RuntimeException("Cannot get size, project file '{$file}' does not exist");
        }

        return (int) (filesize($file) / 1024);
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        if (!Locale::inst()->isValid($locale)) {
            return '';
        }

        $url = self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jnlp";

        return $url;
    }

    protected function getTranslationGlob() {
        $base = self::sim_root."{$this->project_name}/{$this->sim_name}";
        $base_glob = $base."*.jnlp";
        $base_regex = $base."(_)([A-Za-z]{2})?(_([A-Za-z]{2}))?.jnlp";
        return array($base_glob, $base_regex);
    }
}

?>