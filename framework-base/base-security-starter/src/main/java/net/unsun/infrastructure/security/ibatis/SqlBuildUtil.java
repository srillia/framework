package net.unsun.infrastructure.security.ibatis;

import net.unsun.infrastructure.security.util.SecurityKit;

import java.time.LocalDateTime;

/**
 * sql 构建工具类
 *
 * @author loken
 * @since 2020-01-07
 */
public class SqlBuildUtil {
    /**
     * 更新添加的字段
     */
    private final static String[] UPDATE_ADD_COLUMN = {"update_datetime", "update_by_id", "update_by_name"};
    /**
     * 新增添加的字段
     */
    private final static String[] INSERT_ADD_COLUMN = {"create_datetime", "create_by_id", "create_by_name", "update_datetime", "update_by_id", "update_by_name"};
    /**
     * update  split key
     */
    private final static String UPDATE_SET_FIELD_NAME = "set";
    /**
     * insert split key
     */
    private final static String INSERT_VALUES_FIELD_NAME = "values";
    /**
     * insert split key
     */
    private final static String INSERT_VALUE_FIELD_NAME = "value";

    /**
     * 构建sql
     *
     * @param originalSql
     * @return
     */
    public static String buildSql(String originalSql, String type) {
        if (null == originalSql || "".equals(originalSql)) {
            return originalSql;
        }
        switch (type) {
            case "UPDATE":
                return buildUpdateSql(originalSql.toLowerCase());
            case "INSERT":
                return buildInsertSql(originalSql.toLowerCase());
            default:
                //如果不是update或者insert，直接不处理sql
                return originalSql;
        }
    }

    /**
     * 构建新增sql
     *
     * @param originalSql
     * @return
     */
    public static String buildInsertSql(String originalSql) {
        StringBuffer finalSql = new StringBuffer();
        //这里先通过”;“截取，防止为批量添加，即使没有”;“，split（）不会报错，会将原来的数据放置在数组的第一位
        for (String sqlArray : originalSql.split(";")) {
            //通过values或者value进行截取，这里保证的是，数组的长度为2，所以可以直接用下标来处理
            String[] sql = originalSql.split(checkValuesForInsertSql(originalSql));
            //如果insert into tableNAME 没有带括号“)'，则表示为全表字段插入，所以我们无需处理，直接返回即可
            if (!sql[0].contains(")")) {
                return originalSql;
            }
            finalSql.append(sql[0]);
            //将我们需要新增的字段插入到最后一个)的前面
            finalSql.insert(finalSql.lastIndexOf(")"), addColumnForInsert(originalSql)).append(" ").append(INSERT_VALUES_FIELD_NAME);
            //后半段的sql,我们通过)进行截取，然后顺序加入我们需要的value和）即可，注意，这里别添加,否则会出现错误
            String[] valuesSql = sql[1].split("\\)");
            for (String values : valuesSql) {
                finalSql.append(values).append(addValueForInsert(originalSql)).append("    )");
            }
            //最后加上;，即使是单个的insert,也运行添加
            finalSql.append(";");
        }
        return finalSql.toString();
    }

    /**
     * insert类型sql时，为新增加的字段赋值
     * 需要注意的是，字段顺序一定要保持一致，且需要判定原先的sql中是否已有INSERT_ADD_COLUMN中的字段
     *
     * @param originalSql
     * @return
     */
    private static String addValueForInsert(String originalSql) {
        StringBuffer addColumnSql = new StringBuffer();
        for (int i = 0; i < INSERT_ADD_COLUMN.length; i++) {
            String sqlField = INSERT_ADD_COLUMN[i];
            if (!originalSql.contains(sqlField)) {
                if (sqlField.endsWith("datetime")) {
                    addColumnSql.append(",'").append(LocalDateTime.now()).append("'");
                } else if (sqlField.endsWith("id")) {
                    addColumnSql.append(",'").append(SecurityKit.currentUser().getUserId()).append("'");
                } else if (sqlField.endsWith("name")) {
                    addColumnSql.append(",'").append(SecurityKit.currentUser().getUsername()).append("'");
                }
            }
        }
        return addColumnSql.toString();
    }

    /**
     * insert 时，添加新的字段
     * 需要注意的是，原先的sql中如果出现INSERT_ADD_COLUMN中的字段，就不添加，否则会报错
     *
     * @param originalSql
     * @return
     */
    private static String addColumnForInsert(String originalSql) {
        StringBuffer addColumnSql = new StringBuffer();
        for (int i = 0; i < INSERT_ADD_COLUMN.length; i++) {
            String sqlField = INSERT_ADD_COLUMN[i];
            if (!originalSql.contains(sqlField)) {
                addColumnSql.append(",").append(sqlField);
            }
        }
        return addColumnSql.toString();
    }


    /**
     * 生成update语句
     * 不管是批量还是单个更新，只需要根据set字段进行截取，然后将数据进行拼接成column =value即可
     * 需要注意的是，通过set进行截取后，我们应该循环遍历截取出来的字符数组length-1,因为最后一个里面我们是不需要任何操作的，
     * 如果不length-1,会导致最后多添加我们需要的字段，从而导致sql不可执行
     *
     * @param originalSql
     * @return
     */
    private static String buildUpdateSql(String originalSql) {
        StringBuffer finalSql = new StringBuffer();
        String[] sqlArray = originalSql.split(UPDATE_SET_FIELD_NAME);
        for (int i = 0; i < sqlArray.length - 1; i++) {
            finalSql.append(sqlArray[i]).append(" set ").append(addUpdateField(originalSql));
        }
        finalSql.append(sqlArray[sqlArray.length - 1]);
        return finalSql.toString();
    }

    /**
     * 判断insert语句的value/values
     * <p>
     * 顺序必须先从values 开始配对
     * 如果从value开始，则始终都会配对成功
     *
     * @param originalSql
     * @return
     */
    private static String checkValuesForInsertSql(String originalSql) {
        if (originalSql.contains(INSERT_VALUES_FIELD_NAME)) {
            return INSERT_VALUES_FIELD_NAME;
        }
        return INSERT_VALUE_FIELD_NAME;
    }


    /**
     * 拼接更新sql(添加字段）
     * 格式为 columnName = value,
     * 记得最后一定要加, 因为我们是在最前面加新的字段
     *
     * @param originalSql
     * @return
     */
    private static String addUpdateField(String originalSql) {
        StringBuffer addColumnSql = new StringBuffer();
        for (String sqlField : UPDATE_ADD_COLUMN) {
            switch (sqlField) {
                case "update_datetime":
                    if (!originalSql.contains(sqlField)) {
                        appendSql(addColumnSql, sqlField, LocalDateTime.now());
                    }
                    break;
                case "update_by_id":
                    if (!originalSql.contains(sqlField)) {
                        appendSql(addColumnSql, sqlField, SecurityKit.currentUser().getUserId());
                    }
                    break;
                case "update_by_name":
                    if (!originalSql.contains(sqlField)) {
                        appendSql(addColumnSql, sqlField, SecurityKit.currentUser().getUsername());
                    }
                    break;
                default:
                    break;
            }
        }
        return addColumnSql.toString();
    }

    private static StringBuffer appendSql(StringBuffer sb, String sqlField, Object value) {
        return sb.append(sqlField).append(" = '").append(value).append("',");
    }
}

