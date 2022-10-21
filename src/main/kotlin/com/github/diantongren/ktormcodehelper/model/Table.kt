package com.github.diantongren.ktormcodehelper.model

data class Table(
    val tableName: String,
    val comments: String?,
    val className: String,
    val classNamePlural: String,
    val columns: List<Column>,
    val ktormSequence: String = classNamePlural.lowercase(),
    val hasDate: Boolean,
    val hasDateTime: Boolean,
    val modelPackageName: String,
    val daoPackageName: String
)
