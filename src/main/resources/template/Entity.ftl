package ${modelPackageName}

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

interface ${className} : Entity<${className}> {

    companion object : Entity.Factory<${className}>()

<#list columns as column>
    <#if column.comments??>
    /**
    * ${column.comments}
    */
    </#if>
    var ${column.attrName}: ${column.attrType}

</#list>
}

object ${classNamePlural} : Table<${className}>("${tableName}") {

<#list columns as column>
    val ${column.attrName} = ${column.ktormType}("${column.columnName}")<#if column.primary>.primaryKey()</#if>.bindTo { it.${column.attrName} }

</#list>
}

val Database.${ktormSequence} get() = this.sequenceOf(${classNamePlural})