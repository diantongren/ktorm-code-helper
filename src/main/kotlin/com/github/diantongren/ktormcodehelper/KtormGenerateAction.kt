package com.github.diantongren.ktormcodehelper

import com.intellij.database.dialects.DatabaseDialect
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasObject
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbSqlUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import org.javalite.common.Inflector
import org.javalite.common.JsonHelper
import java.math.BigDecimal
import java.sql.JDBCType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class KtormGenerateAction : AnAction() {

    val generator = Generator()
    override fun update(e: AnActionEvent) {
        val element = e.getData(LangDataKeys.PSI_ELEMENT)
        e.presentation.isEnabledAndVisible = element is DbTable
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        ShowSettingsUtil.getInstance().editConfigurable(project, ORMSettingsUI())

        val tables = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY).orEmpty()
            .filterIsInstance<DbTable>().sortedBy { it.name }
        if (tables.isEmpty()) return
        val dialect = DbSqlUtil.getSqlDialect(tables.first()).databaseDialect
        val myTables = tables.map {
            val tableName = it.name
            val className = Inflector.camelize(tableName.removePrefix("t_"))
            val classNamePlural = Inflector.pluralize(className)

            MyTable(
                tableName,
                it.comment,
                className,
                classNamePlural,
                getColumns(it, dialect)
            )
        }.toList()
        println(JsonHelper.toJsonString(myTables))
        generate(myTables, project)
    }

    private fun getColumns(table: DasObject, dialect: DatabaseDialect): List<MyColumn> {
        return DasUtil.getColumns(table).map {
            it.isNotNull
            MyColumn(
                it.name,
                it.dataType.typeName,
                it.comment,
                Inflector.camelize(it.name, false),
                it.getType(dialect),
                it.getKtormType(dialect),
                DasUtil.isPrimary(it)
            )
        }.toList()
    }

    private fun DasColumn.getType(dialect: DatabaseDialect): String {
        val jdbcType = dialect.getJavaTypeForNativeType(dataType.typeName)
        when (JDBCType.valueOf(jdbcType)) {
            JDBCType.CHAR,
            JDBCType.VARCHAR,
            JDBCType.LONGVARCHAR,
            JDBCType.NCHAR,
            JDBCType.NVARCHAR,
            JDBCType.LONGNVARCHAR,
            JDBCType.CLOB,
            JDBCType.NCLOB -> String::class

            JDBCType.TINYINT,
            JDBCType.SMALLINT -> Short::class

            JDBCType.INTEGER -> Int::class
            JDBCType.BIGINT -> Long::class
            JDBCType.REAL,
            JDBCType.FLOAT,
            JDBCType.DOUBLE,
            JDBCType.NUMERIC,
            JDBCType.DECIMAL -> BigDecimal::class

            JDBCType.BIT,
            JDBCType.BOOLEAN -> Boolean::class

            JDBCType.DATE -> LocalDate::class
            JDBCType.TIME -> LocalTime::class
            JDBCType.TIMESTAMP -> LocalDateTime::class
            JDBCType.BINARY,
            JDBCType.VARBINARY,
            JDBCType.LONGVARBINARY,
            JDBCType.BLOB -> ByteArray::class

            else -> Any::class
        }.simpleName.let { return it ?: "" }
    }

    private fun DasColumn.getKtormType(dialect: DatabaseDialect): String {
        val jdbcType = dialect.getJavaTypeForNativeType(dataType.typeName)
        return when (JDBCType.valueOf(jdbcType)) {
            JDBCType.VARCHAR -> "varchar"
            JDBCType.LONGVARCHAR -> "text"
            JDBCType.TINYINT,
            JDBCType.SMALLINT -> "short"

            JDBCType.INTEGER -> "int"
            JDBCType.BIGINT -> "long"
            JDBCType.REAL,
            JDBCType.FLOAT,
            JDBCType.DOUBLE,
            JDBCType.NUMERIC,
            JDBCType.DECIMAL -> "decimal"

            JDBCType.BOOLEAN -> "boolean"
            JDBCType.DATE -> "date"
            JDBCType.TIME -> "time"
            JDBCType.TIMESTAMP -> "datetime"
            JDBCType.BINARY,
            JDBCType.VARBINARY,
            JDBCType.LONGVARBINARY -> "bytes"

            JDBCType.BLOB -> "blob"
            else -> ""
        }
    }

    private fun generate(tables: List<MyTable>, project: Project): Unit {

        var hasDate = false
        var hasDateTime = false
        run breaking@{
            tables.forEach {
                val columns = it.columns
                for (column in columns) {
                    if (hasDate && hasDateTime) {
                        return@breaking
                    }
                    if (column.attrType == "LocalDate") {
                        hasDate = true
                    }
                    if (column.attrType == "LocalDateTime") {
                        hasDateTime = true
                    }
                }
            }
        }
        generator.writeFile(
            MyModel(
                packageName = "com.diantongren.model",
                hasDate = hasDate,
                hasDateTime = hasDateTime,
                table = tables.first()
            ),
            project
        )

    }
}