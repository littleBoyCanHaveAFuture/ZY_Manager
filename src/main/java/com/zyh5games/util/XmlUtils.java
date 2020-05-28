package com.zyh5games.util;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;


/**
 * @author song minghua
 * @date 2020/5/27
 */
public class XmlUtils {
    public static void main(String[] args) {
        StringBuilder xml = new StringBuilder();
        xml.append("<!--?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?-->");
        xml.append("<quicksdk_message>");
        xml.append("<message>");
        xml.append("<is_test>0</is_test>");
        xml.append("<channel>8888</channel>");
        xml.append("<channel_uid>231845</channel_uid>");
        xml.append("<game_order>123456789</game_order>");
        xml.append("<order_no>12520160612114220441168433</order_no>");
        xml.append("<pay_time>2016-06-12 11:42:20</pay_time>");
        xml.append("<amount>1.00</amount>");
        xml.append("<status>0</status>");
        xml.append("<extras_params>{1}_{2}</extras_params>");
        xml.append("<status>0</status>");
        xml.append("</message>");
        xml.append("</quicksdk_message>");

        JSONObject data = getQuickOrderXml(xml.toString());
        System.out.println("data = " + data);
    }


    public static JSONObject getQuickOrderXml(String xml) {
        JSONObject data = new JSONObject();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);

            Element rootElt = doc.getRootElement();
            Element message = rootElt.element("message");

            Iterator iter = message.elementIterator();

            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                String name = recordEle.getName();
                String textTrim = recordEle.getTextTrim();

                System.out.println("\n" + name + " = " + textTrim);
                data.put(name, textTrim);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }
}
