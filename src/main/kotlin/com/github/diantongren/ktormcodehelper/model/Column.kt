package com.github.diantongren.ktormcodehelper.model

data class Column(
    val columnName: String,
    val dataType: String,
    val comments: String?,
    val attrName: String,
    val attrType: String,
    val ktormType: String,
    val isPrimary: Boolean
)
