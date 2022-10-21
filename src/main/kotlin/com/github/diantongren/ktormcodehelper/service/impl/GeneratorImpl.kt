package com.github.diantongren.ktormcodehelper.service.impl

import com.github.diantongren.ktormcodehelper.MyColumn
import com.github.diantongren.ktormcodehelper.MyTable
import com.github.diantongren.ktormcodehelper.model.CodeGenContext
import com.github.diantongren.ktormcodehelper.model.Column
import com.github.diantongren.ktormcodehelper.model.Table
import com.github.diantongren.ktormcodehelper.service.AbstractGenerator
import com.google.common.base.CaseFormat
import com.intellij.database.dialects.DatabaseDialect
import com.intellij.database.dialects.mysqlbase.introspector.MysqlBaseIntroQueries
import com.intellij.database.model.DasColumn
import com.intellij.database.model.DasObject
import com.intellij.database.psi.DbTable
import com.intellij.database.util.DasUtil
import com.intellij.database.util.DbSqlUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import org.javalite.common.Inflector
import java.math.BigDecimal
import java.sql.JDBCType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GeneratorImpl : AbstractGenerator() {
    override lateinit var event: AnActionEvent
    override fun generateORM(project: Project, context: CodeGenContext) {
        val modelPackageName = context.modelPath?.split("/kotlin/")?.get(1)?.replace("/", ".") ?: return
        val daoPackageName = context.daoPath?.split("/kotlin/")?.get(1)?.replace("/", ".") ?: return
        getTables(context.daoPath, modelPackageName, daoPackageName).forEach {
            // model
            writeFile(
                project,
                context.modelPath,
                "${it.className}.kt",
                "Entity.ftl",
                it
            )

            // dao
            writeFile(
                project,
                context.daoPath,
                "${it.className}Dao.kt",
                "Dao.ftl",
                it
            )
        }
        writeFile(
            project,
            context.daoPath,
            "BaseDao.kt",
            "BaseDao.ftl",
            Any()
        )
    }

    private fun getTables(prefix: String?, modelPackageName: String, daoPackageName: String): List<Table> {
        val tables = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY).orEmpty()
            .filterIsInstance<DbTable>().sortedBy { it.name }
        if (tables.isEmpty()) return listOf()
        val dialect = DbSqlUtil.getSqlDialect(tables.first()).databaseDialect
        tables.map {
            val tableName = it.name
            val className = Inflector.camelize(tableName.removePrefix(prefix ?: ""))
            val classNamePlural = Inflector.pluralize(className)
            val columns = getColumns(it, dialect)
            var hasDate = false
            var hasDateTime = false
            columns.forEach {
                if (hasDate && hasDateTime) return@forEach
                if ("LocalDateTime".equals(it.attrType)) hasDateTime = true
                if ("LocalDate".equals(it.attrType)) hasDate = true
            }

            Table(
                tableName,
                it.comment,
                className,
                classNamePlural,
                columns,
                hasDate = hasDate,
                hasDateTime = hasDateTime,
                modelPackageName = modelPackageName,
                daoPackageName = daoPackageName
            )
        }.toList().let {
            return it
        }
    }

    private fun getColumns(table: DasObject, dialect: DatabaseDialect): List<Column> {
        return DasUtil.getColumns(table).map {
            it.isNotNull
            Column(
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
}