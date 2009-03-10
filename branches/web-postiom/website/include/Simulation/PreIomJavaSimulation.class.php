<?php

class PreIomJavaSimulation extends JavaSimulation {
    public function getSize() {
        $size = parent::getSize();
        if ($size != 0) {
            return $size;
        }

        $file = self::sim_root."{$this->project_name}/{$this->sim_name}.jar";
        if (!file_exists($file)) {
            return 0;
        }
        /*
        if (!file_exists($file)) {
            throw new RuntimeException("Cannot get size, project file '{$file}' does not exist");
        }
        */
        return (int) (filesize($file) / 1024);
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        $url = parent::getLaunchUrl();
        if (file_exists($url)) {
            return $url;
        }

        if (!Locale::inst()->isValid($locale)) {
            return '';
        }

        if (Locale::inst()->isDefault($locale)) {
            $url = self::sim_root."{$this->project_name}/{$this->sim_name}.jnlp";
        }
        else {
            $url = self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jnlp";
        }

        return $url;        
    }

    public function getDownloadUrl($requested_locale = Locale::DEFAULT_LOCALE) {
        $url = parent::getDownloadUrl();
        if (!empty($url)) {
            return $url;
        }

        if (Locale::inst()->isDefault($requested_locale)) {
            $url = self::sim_root."{$this->project_name}/{$this->sim_name}.jar";
        }
        else {
            $url = self::sim_root."{$this->project_name}/{$this->sim_name}_{$requested_locale}.jar";
        }

        if (!file_exists($url)) {
            return '';
        }
        return SITE_ROOT."admin/get-run-offline.php?sim_id={$this->getId()}&locale={$requested_locale}";
;
    }
}

?>