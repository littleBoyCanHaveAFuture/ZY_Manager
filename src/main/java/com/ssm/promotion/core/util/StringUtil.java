package com.ssm.promotion.core.util;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author song minghua
 */
public class StringUtil {
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String ENTER = "\n";
    public static final String TABLE = "\t";
    public static final String QUESTION = "?";
    public static final String SLASH_TOLEFT = "/";
    public static final String SLASH_TORIGHT = "\\";
    public static final String COMMA = ",";
    public static final String POINT = ".";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";
    public static final String MINUS = "-";
    public static final String UNDERLINE = "_";
    public static final String MONEY = "$";
    public static final String AND = "&";
    public static final String PERCENT = "%";
    public static final String STAR = "*";
    public static final String WAVE = "~";
    public static final String TOP_POINT = "`";
    public static final String QUOTE = "\"";
    public static final String NUMBER_SIGN = "#";
    public static final String BRACKETS1_LEFT = "(";
    public static final String BRACKETS1_RIGHT = ")";
    public static final String BRACKETS2_LEFT = "[";
    public static final String BRACKETS2_RIGHT = "]";
    public static final String BRACKETS3_LEFT = "{";
    public static final String BRACKETS3_RIGHT = "}";
    public static final Character[] CHINESE_DIGIT = new Character[]{'零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'};
    public static final String CHARSET_UTF8 = "UTF-8";
    private static final Logger log = Logger.getLogger(StringUtil.class);// 日志文件
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static Pattern intPattern = Pattern.compile("^[-\\+]?[\\d]*$");

    public StringUtil() {
    }

    public static boolean isChinese(char c) {
        UnicodeBlock ub = UnicodeBlock.of(c);
        return ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub == UnicodeBlock.GENERAL_PUNCTUATION;
    }

    public static boolean isChinese(String string) {
        char[] var1 = string.toCharArray();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            char c = var1[var3];
            if (isChinese(c)) {
                return true;
            }
        }

        return false;
    }

    public static Character getChineseDigit(int sigleInt) {
        return sigleInt >= 0 && sigleInt < CHINESE_DIGIT.length ? CHINESE_DIGIT[sigleInt] : ' ';
    }

    public static boolean isSymmetry(String str) {
        int len = str.length();

        for (int i = 0; i < len / 2; ++i) {
            if (str.charAt(i) != str.charAt(len - 1 - i)) {
                return false;
            }
        }

        return true;
    }

    public static int getSingleCharLengthCount(String... strs) {
        int count = 0;
        String[] var2 = strs;
        int var3 = strs.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String str = var2[var4];
            char[] var6 = str.toCharArray();
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                char c = var6[var8];
                if (isChinese(c)) {
                    count += 2;
                } else {
                    ++count;
                }
            }
        }

        return count;
    }

    public static String firstChangeCase(String str, boolean isUpper) {
        if (!isValid(str)) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            if (isUpper) {
                sb.append(Character.toUpperCase(str.charAt(0)));
            } else {
                sb.append(Character.toLowerCase(str.charAt(0)));
            }

            if (str.length() > 1) {
                sb.append(str.substring(1, str.length()));
            }

            return sb.toString();
        }
    }

    public static String getArrayStr(Collection<?> objList) {
        StringBuilder sb = new StringBuilder();

        Object obj;
        for (Iterator var2 = objList.iterator(); var2.hasNext(); sb.append(obj.toString())) {
            obj = var2.next();
            if (sb.length() > 0) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    public static String toHexString(byte[] md) {
        char[] str = new char[md.length << 1];

        for (int i = 0; i < md.length; ++i) {
            byte b = md[i];
            str[i << 1] = HEX_DIGITS[b >>> 4 & 15];
            str[i << 1 | 1] = HEX_DIGITS[b & 15];
        }

        return new String(str);
    }

    public static String replaceParams(String orgStr, String paramFlag, Object... paramValArray) {
        StringBuilder sb = new StringBuilder(orgStr);
        int valIdx = 0;

        do {
            int regIdx = sb.indexOf(paramFlag);
            if (regIdx < 0) {
                break;
            }

            sb = sb.replace(regIdx, regIdx + paramFlag.length(), paramValArray[valIdx++].toString());
        } while (valIdx < paramValArray.length);

        return sb.toString();
    }

    public static String madeRndString(int lenMin, int lenMax) {
        int len = RandomUtil.rndInt(lenMin, lenMax);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; ++i) {
            if (RandomUtil.rndBool100(38)) {
                sb.append(RandomUtil.rndInt(10));
            } else {
                sb.append((char) (65 + RandomUtil.rndInt(26)));
            }
        }

        return sb.toString();
    }

    public static String underlineToHump(String underlineWord) {
        StringBuilder sb = new StringBuilder();
        int wordIndex = 0;
        String[] words = underlineWord.split("_");
        String[] var4 = words;
        int var5 = words.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            String oldWord = var4[var6];
            if (oldWord.length() != 0) {
                for (int i = 0; i < oldWord.length(); ++i) {
                    if (wordIndex > 0 && i == 0) {
                        sb.append(Character.toUpperCase(oldWord.charAt(i)));
                    } else {
                        sb.append(Character.toLowerCase(oldWord.charAt(i)));
                    }
                }

                ++wordIndex;
            }
        }

        return sb.toString();
    }

    public static String humpToUnderline(String humpWord) {
        boolean isMissUnderLineIfNextUpper = true;
        StringBuilder sb = new StringBuilder();
        char[] var3 = humpWord.toCharArray();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (Character.isUpperCase(c)) {
                if (isMissUnderLineIfNextUpper) {
                    isMissUnderLineIfNextUpper = false;
                } else {
                    sb.append("_");
                }

                sb.append(Character.toLowerCase(c));
            } else {
                if (isMissUnderLineIfNextUpper) {
                    isMissUnderLineIfNextUpper = false;
                }

                if (c == ".".charAt(0)) {
                    isMissUnderLineIfNextUpper = true;
                }

                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String humpToUnderlineAdvanced(String str, String prefix, String suffix) {
        StringBuilder sb = new StringBuilder(str);
        if (isValid(suffix)) {
            int suffixStart = sb.indexOf(suffix);
            if (suffixStart >= 0) {
                sb.delete(suffixStart, sb.length());
            }
        }

        if (isValid(prefix)) {
            sb.insert(0, prefix);
        }

        String cacheWord = humpToUnderline(sb.toString());
        return cacheWord;
    }

    public static boolean isValid(String str) {
        if (str == null) {
            return false;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isValidUsername(String str) {
        if (!isValid(str)) {
            return false;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                boolean res = !Character.isLetter(ch) && ch != '_' && !Character.isDigit(ch);
                if (res || isChinese(ch)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static String getValidLenStr(String name, int lenMin, int lenMax) {
        if (name != null) {
            name = name.replace("　", " ").trim();
            if (name.length() >= lenMin && name.length() <= lenMax) {
                return name;
            }
        }

        return null;
    }

    public static String readInputStream(InputStream is) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            for (String line = null; (line = reader.readLine()) != null; sb.append(line)) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
            }

            reader.close();
        } catch (Exception var12) {
            log.info(var12);
        } finally {
            try {
                is.close();
            } catch (Exception var11) {
                log.info(var11);
            }

        }

        return sb.toString();
    }

    public static Map<Character, Integer> getCharNumMap(String str) throws Exception {
        Map<Character, Integer> charMap = new HashMap();
        char[] var2 = str.toCharArray();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Character c = var2[var4];
            Integer num = (Integer) charMap.get(c);
            if (num == null) {
                num = 0;
            }

            num = num + 1;
            charMap.put(c, num);
        }

        Map<Character, Integer> numMap = CommonUtil.sortMapValue(charMap);
        return numMap;
    }

    public static void paddingChar(StringBuilder sb, char c, int len, boolean isBefore) {
        while (sb.length() < len) {
            if (isBefore) {
                sb.insert(0, c);
            } else {
                sb.append(c);
            }
        }

    }

    public static boolean isNumeric(String str) {
        int i = str.length();

        do {
            --i;
            if (i < 0) {
                return true;
            }
        } while (Character.isDigit(str.charAt(i)));

        return false;
    }

    /**
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        if ((str != null) && !"".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param str
     * @return
     */
    public static String formatLike(String str) {
        if (isNotEmpty(str)) {
            return "%" + str + "%";
        } else {
            return null;
        }
    }

    public static boolean isInteger(String str) {
        return intPattern.matcher(str).matches();
    }

    public static List getCode(Map map) {
        List list = new ArrayList();
        Iterator iter = map.entrySet().iterator();  //获得map的Iterator
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            list.add(entry.getKey());
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 字典升序排列
     */
    public static void sort(ArrayList arr) {

        Collections.sort(arr, (Comparator<String>) (o1, o2) -> {
            try {
                String str1 = new String(o1.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                String str2 = new String(o2.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                return str1.compareTo(str2);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return 0;
        });

    }

    public String getValidNameThrowMsg(String name, int lenMin, int lenMax, Collection<String> badWordList) throws Exception {
        String valid = getValidLenStr(name, lenMin, lenMax);
        if (valid == null) {
            throw new Exception("名称长度范围必须在" + lenMin + "~" + lenMax);
        } else {
            Iterator var6 = badWordList.iterator();

            String badWord;
            do {
                if (!var6.hasNext()) {
                    return valid;
                }

                badWord = (String) var6.next();
            } while (!valid.contains(badWord));

            throw new Exception("名称中含有禁用词汇");
        }
    }
}
