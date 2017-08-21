package com.prokkypew.asciipanelview

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.manifest.AndroidManifest
import org.robolectric.res.FileFsFile


/**
 * Created by alexander.roman on 21.08.2017.
 */
class CustomTestRunner
constructor(klass: Class<*>) : RobolectricTestRunner(klass) {

    private val BUILD_OUTPUT = "build/intermediates";
    private val BUILD_OUTPUT_APP_LEVEL = "asciipanelview/build/intermediates"

    override fun getAppManifest(config: Config): AndroidManifest {
        var res = FileFsFile.from(BUILD_OUTPUT, "/res/merged", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE)
        var assets = FileFsFile.from(BUILD_OUTPUT, "bundles", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE, "assets")
        var manifest = FileFsFile.from(BUILD_OUTPUT, "manifests", "full", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE, "AndroidManifest.xml")

        if (!manifest.exists()) {
            manifest = FileFsFile.from(BUILD_OUTPUT_APP_LEVEL, "manifests", "full", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE, "AndroidManifest.xml")
        }

        if (!res.exists()) {
            res = FileFsFile.from(BUILD_OUTPUT_APP_LEVEL, "/res/merged", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE)
        }

        if (!assets.exists()) {
            assets = FileFsFile.from(BUILD_OUTPUT_APP_LEVEL, "bundles", BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE, "assets")
        }

        return AndroidManifest(manifest, res, assets)
    }
}