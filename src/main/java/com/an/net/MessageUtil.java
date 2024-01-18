package com.an.net;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxk
 * @date 2019-08-18 9:43
 */
public class MessageUtil {
    private static final Logger log = LoggerFactory.getLogger(MessageUtil.class);

    public static String toHexCard(String card) {
        card = Long.toHexString(Long.parseLong(card));
        int len = card.length();
        for (int i = 0; i < 8 - len; i++) {
            card = "0" + card;
        }
        return card;
    }

    public static int split(String str) {
        int len = 2;
        int sum = 0;
        if (str.length() % 2 == 1) str = "0" + str;
        for (int i = 0; i < str.length(); i = i + 2) {
            sum = sum + Integer.parseInt(str.substring(i, i + len), 16);
        }
        return sum;
    }

    public static String get(int num, int len) {
        String num_ = Long.toHexString(num);
        if (num_.length() > len) {
            num_ = num_.substring(1, 5);
        }
        int nlen = num_.length();
        for (int i = 0; i < len - nlen; i++) {
            num_ = "0" + num_;
        }
        return reversal(num_);
    }

    /**
     * 字符串转16进制unicode
     *
     * @param str 字符串
     * @param len 长度
     * @return
     */
    public static String get2(String str, int len) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            stringBuffer.append(Integer.toHexString(str.charAt(i)));
        }

        for (int i = stringBuffer.length(); i < len; i++) {
            stringBuffer.append("0");
        }
        return stringBuffer.toString();
    }

    /**
     * 字符串转化成为16进制字符串
     *
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        StringBuffer hexStr = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            hexStr.append(s4);
        }
        System.out.println("strTo16:" + hexStr.toString());
        return hexStr.toString();
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        System.out.println("string2Unicode:" + unicode.toString());
        return unicode.toString();
    }

    public static String reversal(String str) {
        String result = "";
        List<String> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i = i + 2) {
            list.add(str.substring(i, i + 2));
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            result = result + list.get(i);
        }
        return result;
    }

    public static String getCheckCode(String str, int length) {
        if (str.indexOf("null") != -1 || str.length() % 2 != 0) {
            return get(0, 4);
        }
        int len = 0;
        for (int i = 0; i < str.length(); i = i + 2) {
            len = len + Integer.parseInt(str.substring(i, i + 2), 16);
        }

        int mode = 65535;
        if (length == 2) {
            mode = 256;
        }
        return get(len % mode, length);
    }

    public static String getCheckCode(String str) {
        if (str.indexOf("null") != -1) {
            return get(0, 4);
        }
        int len = 0;
        while (str.length() > 0) {
            len = len + Integer.parseInt(str.substring(0, 2), 16);
            str = str.substring(2);
        }
        return get(len, 4);
    }

    /**
     * 累加和校验
     *
     * @param str 16进制字符串
     **/
    public static String CS(String str) {
        if (str.indexOf("null") != -1 || str.length() % 2 != 0) {
            return get(0, 4);
        }
        int length = str.length();
        Integer j = 0;
        for (int i = 0; i < length / 2; i++) {
            j += new BigInteger(str.substring((i * 2), (i + 1) * 2), 16).intValue();
        }
        String s = Integer.toHexString(j);
        while (s.length() < 4) {
            s = "0" + s;
        }
        return s.substring(s.length() - 2, s.length()) + s.substring(s.length() - 4, s.length() - 2);
    }

    public static void main(String args[]) {
//		   System.out.println(CS("444E590D01FFFFFFFF0E00F839000E00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF444E590D01FFFFFFFF0E00F839000E00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
//		   System.out.println(summationCheak("444E596300CC70C606E70001650098081000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000038200005B005"));
        System.out.println(get2("sds", 10));
    }

    /**
     * 根据设备类型返回适合语音
     * @param accountState 账户状态
     * @param deviceId  16进制物理编号
     * @return
     */
//	    public static String getVoice(String deviceId, String accountState){
//
//	        if ("0A".equals(accountState) || "0B".equals(accountState) ||"0C".equals(accountState) ||"0D".equals(accountState) ||
//                    "0E".equals(accountState) ||"0F".equals(accountState) ||"10".equals(accountState) ||"11".equals(accountState) ||
//                    "12".equals(accountState)){
//                return isNewEq(deviceId,accountState);
//            }
//	        return accountState;
//	    }

    /**判断是否新设备**/
//	    public static String isNewEq(String deviceId,String accountState) {
//	        RedisTemplate redisTemplate = SpringUtil.getBean("redisTemplate");
//	        Object obj = redisTemplate.opsForValue().get("S_"+deviceId);
//	        if(obj == null ) {return "08";}
//	        JSONObject json = JSONObject.parseObject(obj.toString());
//	        Object deviceType = json.get("deviceType");
//	        if(deviceType == null ) {return "08";}
//
//	        if(deviceType.equals("11") || deviceType.equals("12")|| deviceType.equals("13")|| deviceType.equals("15")|| deviceType.equals("16")
//	         || deviceType.equals("17")|| deviceType.equals("21")|| deviceType.equals("22")|| deviceType.equals("23")|| deviceType.equals("31")
//                || deviceType.equals("30") || deviceType.equals("29")) {
//	        	return accountState;
//	        }else {
//	        	return "08";
//	        }
//	    }

    /**
     * 转化数据的大小端模式
     *
     * @param hex
     * @return
     */
    public static String hexReverse(String hex) {
        StringBuilder stringBuilder = new StringBuilder();
        int size = hex.length() / 2;

        for (int i = size; i > 0; i--) {
            stringBuilder.append(hex.substring((i - 1) * 2, (i) * 2));
        }
        return stringBuilder.toString();
    }

    /**
     * 16进度转化为10进度
     *
     * @param hex
     * @return
     */
    public static int hex2Int(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static int reverseHex2Int(String hex) {
        return hex2Int(hexReverse(hex));
    }

    public static int getShortCode(String code) {
        code = code.substring(4, 6) + code.substring(2, 4) + code.substring(0, 2);
        int intCode = Integer.parseInt(code, 16);
        return intCode;
    }

    public static String getShortCode(String code, int version, String port) {
        int shortCode = getShortCode(code);
        return getShortCode(shortCode, version, port);
    }

    public static String getShortCode(int shortCode, int version, String port) {
        if (version == 1) {
            return shortCode + "" + (Integer.parseInt(port) + 1);
        } else {
            int prt = Integer.parseInt(port) + 1;
            if (prt < 10) return shortCode + "0" + (prt);
            else return shortCode + "" + (prt);
        }
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 累加和校验
     *
     * @param message 16进制消息
     * @return
     */
    public static boolean summationCheak(String message) {
        try {
            String he = message.substring(message.length() - 2, message.length() - 0) + message.substring(message.length() - 4, message.length() - 2);
            int heNum = Integer.parseInt(he, 16);
            String str = message.substring(0, message.length() - 4);
            int sumNum = 0;
            for (int i = 0; i <= str.length(); i++) {
                if (i != 0 && i % 2 == 0) {
                    String substring = str.substring(i - 2, i);
                    int i1 = Integer.parseInt(substring, 16);
                    log.debug("summationCheak:str = [{}] [{}]", substring,i1);
                    sumNum += i1;
                }
            }
            log.debug("summationCheak:message = [{}] checkSum:[{}]", message,sumNum);
//		   System.out.println("he:"+he+"___size:"+str.length()+"___heNum:"+heNum+"___sumNum:"+sumNum);
            if (heNum == sumNum) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
