package com.zyh5games.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 */
public class IOSDesUtil {
    private final static Pattern pattern = Pattern.compile("\\d+");

    private final static String charset = "utf-8";

    public static String encode(String src, String key) {
        try {
            byte[] data = src.getBytes(charset);
            byte[] keys = key.getBytes();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                int n = (0xff & data[i]) + (0xff & keys[i % keys.length]);
                sb.append("@" + n);
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return src;
    }

    public static String decode(String src, String key) {
        if (src == null || src.length() == 0) {
            return src;
        }
        Matcher m = pattern.matcher(src);
        List<Integer> list = new ArrayList<Integer>();
        while (m.find()) {
            try {
                String group = m.group();
                list.add(Integer.valueOf(group));
            } catch (Exception e) {
                e.printStackTrace();
                return src;
            }
        }

        if (list.size() > 0) {
            try {
                byte[] data = new byte[list.size()];
                byte[] keys = key.getBytes();

                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (list.get(i) - (0xff & keys[i % keys.length]));
                }
                return new String(data, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return src;
        } else {
            return src;
        }
    }

    public static void main(String[] args) {
        String nt_data = "@109@118@168@163@160@81@171@157@162@172@156@167@161@115@85@97@103@97@84@86@154@161@152@159@148@160@163@153@111@89@133@135@119@100@104@88@84@164@169@153@158@157@148@164@162@164@152@109@91@159@161@88@116@113@113@161@165@160@152@157@165@155@155@146@158@156@163@169@149@152@154@118@108@166@152@171@166@151@154@149@119@109@155@169@148@167@154@163@164@117@101@110@97@160@163@146@165@156@163@170@114@109@152@160@145@167@161@157@159@116@103@100@107@104@110@101@152@155@150@158@158@156@161@112@110@154@152@148@159@165@149@162@147@159@150@165@149@119@111@103@150@158@148@158@167@150@158@149@163@148@162@149@110@115@152@154@147@165@158@152@157@150@165@159@152@111@173@151@97@110@108@104@105@107@102@99@109@104@165@174@108@101@113@95@147@159@150@160@160@156@156@146@166@160@148@116@112@148@157@153@158@167@152@164@146@165@165@148@158@163@112@104@101@101@101@96@101@105@109@99@106@103@98@101@104@108@102@106@102@105@107@116@95@156@155@153@161@164@152@156@152@160@164@154@154@165@115@108@151@152@162@151@145@166@162@151@150@169@110@153@109@148@108@153@98@106@151@101@152@105@148@102@102@101@104@111@103@96@109@105@146@152@98@149@151@156@148@148@105@111@100@102@101@150@150@116@95@160@148@165@152@149@162@162@157@150@164@116@113@162@167@148@149@169@148@160@161@117@100@103@99@110@98@102@102@97@101@109@98@113@100@112@99@104@101@96@114@97@98@111@106@107@104@101@98@115@100@161@164@155@149@165@144@165@159@116@112@161@150@177@143@173@156@165@152@116@101@96@107@97@95@102@106@96@103@104@80@104@109@108@98@105@106@101@104@115@95@166@149@170@148@172@153@166@152@118@111@151@160@159@174@159@166@116@102@99@99@96@96@115@100@147@159@166@165@161@165@117@108@169@168@146@169@173@163@119@99@116@98@169@167@145@173@166@165@116@113@152@173@164@162@152@168@145@162@152@162@148@158@170@110@153@109@148@108@153@98@106@151@101@152@105@148@102@102@101@104@111@103@96@109@105@146@152@98@149@151@156@148@148@105@111@100@102@101@150@150@116@95@158@171@172@165@151@166@143@169@146@164@151@162@166@115@108@95@164@154@165@165@152@151@152@111@115@95@167@169@154@152@163@163@157@158@151@160@155@166@163@154@152@151@116";
        String key = "17064158093836309126535007522703";
        String s = decode(nt_data, key);

        /*nt_data => '@108@119@174@165@158@82@167@152@171@166@161@162@159@115@90@106@94@103@83@85@154@166@156@161@155@160@166@155@118@90@139@141@118@101@110@90@82@165@165@148@167@151@153@159@160@164@157@118@82@165@160@87@116@118@117@165@162@176@165@163@168@166@169@152@157@157@169@171@147@153@150@113@117@160@157@166@164@151@159@158@110@115@166@158@153@118@109@110@102@172@161@152@119@116@162@168@151@161@164@151@160@147@158@152@119@164@169@164@162@167@169@117@95@163@160@156@158@166@152@160@152@164@157@114@117@167@171@173@143@167@168@156@151@164@144@161@168@113@116@98@160@171@172@152@159@169@149@154@167@151@167@161@117@115@167@166@157@157@168@152@158@167@116@104@99@98@97@101@105@100@108@100@98@102@109@106@101@106@100@101@102@105@106@100@111@103@107@103@117@103@165@171@148@157@168@151@160@161@111@111@169@148@177@146@165@159@165@158@110@105@97@102@105@101@106@99@100@103@109@84@106@109@112@108@99@114@103@108@110@97@161@148@178@146@172@156@158@155@118@117@145@164@160@170@163@172@119@98@101@103@105@112@104@153@163@168@165@166@170@118@110@165@165@148@173@168@171@113@97@114@103@172@164@152@165@170@168@118@117@151@175@171@170@149@172@151@166@154@162@153@163@171@112@170@169@171@117@98@157@171@165@168@153@172@143@167@146@167@150@165@172@112@115@102@165@153@172@171@151@160@149@118@114@103@165@157@170@160@168@162@166@166@144@163@157@172@163@152@152@154@115';
sign => '@104@155@109@158@105@104@104@106@111@103@112@103@148@152@104@158@149@111@101@151@102@111@156@149@155@112@113@105@154@110@108@106';
md5Sign => '07dae8d71374651c1f0ee4cc16a37f6e';
callback_key => '08682213938316890715589277849869';*/
//        nt_data = "@108@119@174@165@158@82@167@152@171@166@161@162@159@115@90@106@94@103@83@85@154@166@156@161@155@160@166@155@118@90@139@141@118@101@110@90@82@165@165@148@167@151@153@159@160@164@157@118@82@165@160@87@116@118@117@165@162@176@165@163@168@166@169@152@157@157@169@171@147@153@150@113@117@160@157@166@164@151@159@158@110@115@166@158@153@118@109@110@102@172@161@152@119@116@162@168@151@161@164@151@160@147@158@152@119@164@169@164@162@167@169@117@95@163@160@156@158@166@152@160@152@164@157@114@117@167@171@173@143@167@168@156@151@164@144@161@168@113@116@98@160@171@172@152@159@169@149@154@167@151@167@161@117@115@167@166@157@157@168@152@158@167@116@104@99@98@97@101@105@100@108@100@98@102@109@106@101@106@100@101@102@105@106@100@111@103@107@103@117@103@165@171@148@157@168@151@160@161@111@111@169@148@177@146@165@159@165@158@110@105@97@102@105@101@106@99@100@103@109@84@106@109@112@108@99@114@103@108@110@97@161@148@178@146@172@156@158@155@118@117@145@164@160@170@163@172@119@98@101@103@105@112@104@153@163@168@165@166@170@118@110@165@165@148@173@168@171@113@97@114@103@172@164@152@165@170@168@118@117@151@175@171@170@149@172@151@166@154@162@153@163@171@112@170@169@171@117@98@157@171@165@168@153@172@143@167@146@167@150@165@172@112@115@102@165@153@172@171@151@160@149@118@114@103@165@157@170@160@168@162@166@166@144@163@157@172@163@152@152@154@115";
//        key = "07dae8d71374651c1f0ee4cc16a37f6e";
//        String s = decode(nt_data, key);

        System.out.println(s);
    }
}