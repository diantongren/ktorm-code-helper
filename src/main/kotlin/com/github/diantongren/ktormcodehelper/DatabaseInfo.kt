package com.github.diantongren.ktormcodehelper

data class MyTable(
    val tableName: String,
    val comments: String?,
    val className: String,
    val classNamePlural: String,
    val columns: List<MyColumn>,
    val seq: String = classNamePlural.lowercase()
)

data class MyColumn(
    val columnName: String,
    val dataType: String,
    val comments: String?,
    val attrName: String,
    val attrType: String,
    val ktormType: String,
    val isPrimary: Boolean
)

data class MyModel(
    val packageName: String,
    val hasDate: Boolean,
    val hasDateTime: Boolean,
    val table: MyTable
)