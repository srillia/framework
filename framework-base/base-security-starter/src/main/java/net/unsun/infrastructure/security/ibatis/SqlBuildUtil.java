package net.unsun.infrastructure.security.ibatis;

import net.unsun.infrastructure.security.util.SecurityKit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
    private final static String UPDATE_SET_FIELD_NAME = " set ";//add blank
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
        if (null == originalSql || "".equals(originalSql) || originalSql.isEmpty()) {
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
     * edit time 2020/01/13 10:28
     * @param originalSql
     * @return
     */
    public static String buildInsertSql(String originalSql) {
        List<Integer> indices = parse(originalSql);
        final StringBuilder builder = new StringBuilder();
        char[] src = originalSql.toCharArray();
        String addColumnSql = null;
        int begin = 0;
        for (int i = 0; i < indices.size(); i++) {
            Integer index = indices.get(i);
            int end = index;
            if (i == 0) {
                builder.append(src, begin, end - begin);
                //这里插入 字段 //传入builder.toString，方便排除value字段中存在需要添加的字段
                addColumnSql = addColumnForInsert(builder.toString());
                if (null != addColumnSql && !"".equals(addColumnSql)) {
                    builder.append(addColumnSql);
                }
            } else {
                builder.append(src, begin, end - begin);
                //这里插入值
                if (null != addColumnSql && !"".equals(addColumnSql)) {
                    builder.append(addValueForInsert(addColumnSql));
                }

            }
            begin = end;
            if (i == indices.size() - 1) {
                builder.append(src, begin, src.length - begin);
            }
        }
        return builder.toString();
    }

    /**
     * insert类型sql时，为新增加的字段赋值
     * 需要注意的是，字段顺序一定要保持一致，且需要sql中是否已有INSERT_ADD_COLUMN中的字段
     *
     * @param originalSql
     * @return
     */
    private static String addValueForInsert(String originalSql) {
        StringBuffer addColumnSql = new StringBuffer();
        for (int i = 0; i < INSERT_ADD_COLUMN.length; i++) {
            String sqlField = INSERT_ADD_COLUMN[i];
            if (originalSql.contains(sqlField)) {
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
                        addColumnSql.append(sqlField).append(" = '").append(LocalDateTime.now()).append("',");
                    }
                    break;
                case "update_by_id":
                    if (!originalSql.contains(sqlField)) {
                        addColumnSql.append(sqlField).append(" = '").append(SecurityKit.currentUser().getUserId()).append("',");
                    }
                    break;
                case "update_by_name":
                    if (!originalSql.contains(sqlField)) {
                        addColumnSql.append(sqlField).append(" = '").append(SecurityKit.currentUser().getUsername()).append("',");
                    }
                    break;
                default:
                    break;
            }
        }
        return addColumnSql.toString();
    }

    /**
     * 添加update sql (废弃)
     *
     * @param sb
     * @param sqlField
     * @param value
     * @return
     */
    private static StringBuffer appendSql(StringBuffer sb, String sqlField, Object value) {
        return sb.append(sqlField).append(" = '").append(value).append("',");
    }

    /**
     * 获取所有 真实）所在的索引
     * @author tokey
     * @param sql 输入sql
     * @return 所有 ）的索引
     */
    public static List<Integer> parse(String sql) {
        if (sql == null || sql.isEmpty()) {
            return new ArrayList<>();
        }
        List<Map.Entry<Character,Integer>> brackets = new ArrayList<>();

        //初始化相关参数
        Integer firstForeBracket = getRealQuoteOffset(sql,"(" ,0);
        Integer firstBackBracket = getRealQuoteOffset(sql,")" ,0);
        if(firstForeBracket  == -1 || firstBackBracket == -1) {
            throw new RuntimeException("insert into :sql 不正确");
        }

        brackets.add(Map.entry('(',firstForeBracket));
        brackets.add(Map.entry(')',firstBackBracket));


        //获取所有冒号
        List<int[]> quotesEntrys = quotesEntrys(sql, firstBackBracket);

        //获取 正反括号
        if(quotesEntrys.size() == 0) {
            brackets = findBrackets(sql,brackets,firstBackBracket);

        }
        for (int i = 0; i < quotesEntrys.size(); i++) { ;

            int[] quotesEntry =  quotesEntrys.get(i);
            if(i == 0) {
                String subSql = sql.substring(0,quotesEntry[0]);
                brackets = findBrackets(subSql,brackets,firstBackBracket);
            }

            if(i == quotesEntrys.size() -1) {
                if( quotesEntrys.size() == 1) {
                    brackets = findBrackets(sql,brackets,quotesEntry[1]);
                } else  {
                    int[] foreQuotesEntry =  quotesEntrys.get(i -1);
                    brackets = findBrackets(sql.substring(0,quotesEntry[0]),brackets,foreQuotesEntry[1]);
                    brackets = findBrackets(sql,brackets,quotesEntry[1]);
                }
            } else {
                if( i > 0) {
                    int[] foreQuotesEntry =  quotesEntrys.get(i -1);
                    brackets = findBrackets(sql.substring(0,quotesEntry[0]),brackets,foreQuotesEntry[1]);
                }
            }
        }

        //排序
        if(brackets.size() > 1) {
            //冒泡排序
            for (int i = 0; i < brackets.size() - 1; i++) {
                for (int j = 0; j < brackets.size() - 1 - i; j++) {
                    Map.Entry<Character,Integer> first =  brackets.get(j);
                    Map.Entry<Character,Integer> second =  brackets.get(j+1);
                    if(first.getValue() > second.getValue()) {
                        Map.Entry temp = first;
                        brackets.set(j,second);
                        brackets.set(j+1,first);
                    }
                }
            }
        }

        List<Integer> indices = new ArrayList<>();

        //查找正解的 结束反括号
        Map<Character,Character> map = new HashMap<>();
        map.put(')', '(');
        Stack<Character> stack =new Stack<>();

        for (Map.Entry<Character,Integer> bracket : brackets) {
            if(map.containsValue(bracket.getKey())) {//左括号入栈
                stack.push(bracket.getKey());
            }
            else if(map.containsKey(bracket.getKey())) {//右括号出栈匹配
                if(!stack.empty()) {
                    if (stack.peek() == map.get(bracket.getKey())) {
                        stack.pop();
                        if(stack.empty()) {
                            indices.add(bracket.getValue());
                        }
                    }
                }
            }
        }
        return indices;
    }

    private static List<Map.Entry<Character, Integer>> findBrackets(String sql, List<Map.Entry<Character, Integer>> brackets, int firstBackBracket) {
        int offset1 = firstBackBracket +1;
        while (true) {
            int foreBracket = sql.indexOf("(", offset1);
            if(foreBracket > -1) {
                brackets.add(Map.entry('(',foreBracket));
                offset1 = foreBracket +1;
            } else {
                break;
            }
        }
        int offset2 = firstBackBracket +1;
        while (true) {
            int backBracket = sql.indexOf(")", offset2);
            if(backBracket > -1) {
                brackets.add(Map.entry(')',backBracket));
                offset2 = backBracket +1;
            } else {
                break;
            }
        }

        return brackets;

    }
    /**
     *
     * @param text
     * @param offset
     * @return
     */
    private static List<int[]>   quotesEntrys(String text, int offset) {
        List<int[]> quotesEntry = new ArrayList<>();

        while (true) {
            int singleQuote = getRealQuoteOffset(text,"'" ,offset);

            int quote = getRealQuoteOffset(text,"\"" ,offset);
            String token;
            if(singleQuote > -1 && quote > -1) {
                if(singleQuote < quote) {//singleQuote first
                    offset = getQuoteEntry(offset,text, quotesEntry, singleQuote,"'");
                } else {
                    offset = getQuoteEntry(offset,text, quotesEntry, quote,"\"");
                }
            } else if(singleQuote > -1) {
                offset = getQuoteEntry(offset,text, quotesEntry, singleQuote,"'");
            } else if(quote > -1) {
                offset = getQuoteEntry(offset,text, quotesEntry, quote,"\"");
            } else {//两个token都没有找到,whatever ,arbitrary choose one , following statement gets nothing
                break;
            }
        }
        return quotesEntry;
    }

    private static int getQuoteEntry(int offset,String text, List<int[]> quotesEntry, int quote,String token) {
        int[] gapIndices = new int[2];
        gapIndices[0] = quote;
        offset = quote +1;
        gapIndices[1] = getRealQuoteOffset(text,token,offset);
        quotesEntry.add(gapIndices);
        return gapIndices[1] +1;
    }

    /**
     * 获取真实的引号，不包含转意的引号
     * @param text
     * @param token
     * @param offset
     * @return
     */
    private static int getRealQuoteOffset(String text,String token, int offset) {
        char[] src = text.toCharArray();
        int singleQuote = -1;
        while (true) {
            singleQuote = text.indexOf(token, offset);
            if (singleQuote > 0 && src[singleQuote - 1] == '\\') {
                offset = singleQuote + 1;
            } else {
                break;
            }
        }
        return singleQuote;
    }
}