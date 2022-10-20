package ${packageName}

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
<#if hasDate>
import java.time.LocalDate
</#if>
<#if hasDateTime>
import java.time.LocalDateTime
</#if>

<#assign columns = table.columns>
interface ${table.className} : Entity<${table.className}> {

    companion object : Entity.Factory<${table.className}>()

<#list columns as column>
    <#if column.comments??>
    /**
    * ${column.comments}
    */
    </#if>
    var ${column.attrName}: ${column.attrType}

</#list>
}

object ${table.classNamePlural} : Table<${table.className}>("${table.tableName}") {

<#list columns as column>
    val ${column.attrName} = ${column.ktormType}("${column.columnName}")<#if column.primary>.primaryKey()</#if>.bindTo { it.${column.attrName} }

</#list>
}

val Database.${table.seq} get() = this.sequenceOf(${table.classNamePlural})