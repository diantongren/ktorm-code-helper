package ${daoPackageName}

import ${modelPackageName}.${className}
import ${modelPackageName}.${classNamePlural}
import org.springframework.stereotype.Component

@Component
class ${className}Dao : BaseDao<${className}, ${classNamePlural}>(${classNamePlural}) {
}