package com.github.diantongren.ktormcodehelper

import freemarker.template.Configuration
import freemarker.template.Template
import java.io.IOException

open class GeneratorConfig {
    @Throws(IOException::class)
    protected fun getTemplate(ftl: String?): Template {
        return freemarker.getTemplate(ftl, ENCODING)
    }

    internal class FreemarkerConfiguration(basePackagePath: String?) :
        Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS) {
        init {
            defaultEncoding = ENCODING
            setClassForTemplateLoading(javaClass, basePackagePath)
        }
    }

    companion object {
        private const val ENCODING = "UTF-8"
        private val freemarker = FreemarkerConfiguration("/template")
    }
}