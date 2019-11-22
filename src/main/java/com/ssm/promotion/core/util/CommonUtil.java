package com.ssm.promotion.core.util;


import com.ssm.promotion.core.util.enums.OSType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class CommonUtil {
    public static final OSType OS_TYPE;
    private static final int BUFFER_SIZE = 2048;

    public CommonUtil() {
    }

    public static boolean isDebug() {
        return OS_TYPE != OSType.Linux;
    }

    public static final short getShortHigh(short s) {
        return (short) (s & '\uff00');
    }

    public static final short getHighLow(short s) {
        return (short) (s & 255);
    }

    public static byte[] getBytesFromObject(Object obj) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            (new ObjectOutputStream(os)).writeObject(obj);
            bytes = os.toByteArray();
            os.close();
        } catch (Exception var3) {
//            LogUtil.exception(var3);
        }

        return bytes;
    }

    public static Object getObjectFromBytes(byte[] bytes) {
        Object obj = null;

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            obj = (new ObjectInputStream(is)).readObject();
            is.close();
        } catch (Exception var3) {
//            LogUtil.exception(var3);
        }

        return obj;
    }

    private static int getObjectIndexOfCollection(Collection<?> collection, Object object) {
        int i = 0;

        for (Iterator var3 = collection.iterator(); var3.hasNext(); ++i) {
            Object obj = var3.next();
            if (equals(obj, object)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean equals(Object obj, Object otherObj) {
        if (obj != null && otherObj != null) {
            return obj.equals(otherObj);
        } else {
            return obj == otherObj;
        }
    }

    public static int getIndexOf(Object objects, Object object) {
        int index = -1;
        if (objects.getClass().isArray()) {
            int len = Array.getLength(objects);

            for (int i = 0; i < len; ++i) {
                Object obj = Array.get(objects, i);
                if (equals(obj, object)) {
                    index = i;
                    break;
                }
            }
        } else if (objects instanceof List) {
            List<?> list = (List) objects;
            index = list.indexOf(object);
        } else if (objects instanceof Set) {
            Set<?> set = (Set) objects;
            index = getObjectIndexOfCollection(set, object);
        } else if (objects instanceof Map) {
            Map<?, ?> map = (Map) objects;
            index = getObjectIndexOfCollection(map.values(), object);
        }

        return index;
    }

    public static boolean removeObjectOfList(List<?> collection, Object object) {
        boolean remove = false;
        if (collection != null && !collection.isEmpty() && object != null) {
            Iterator it = collection.iterator();

            while (it.hasNext()) {
                Object obj = it.next();
                if (obj.equals(object)) {
                    it.remove();
                    remove = true;
                }
            }

            return remove;
        } else {
            return remove;
        }
    }

    public static Object mergeArray(Class<?> elementClass, Object a, Object b) {
        int aLen = Array.getLength(a);
        int bLen = Array.getLength(b);
        Object m = Array.newInstance(elementClass, aLen + bLen);
        System.arraycopy(a, 0, m, 0, aLen);
        System.arraycopy(b, 0, m, aLen, bLen);
        return m;
    }

    @SafeVarargs
    public static <M> Set<M> getIntersection(Collection<M>... listArray) {
        Set<M> intersection = new TreeSet();
        Collection[] var2 = listArray;
        int var3 = listArray.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Collection<M> list = var2[var4];
            if (intersection.isEmpty()) {
                intersection.addAll(list);
            } else {
                intersection.retainAll(list);
            }
        }

        return intersection;
    }

    public static ByteBuffer byteBufferAdd(ByteBuffer buffer, byte[] byteArray, int unit) {
        int newCapacity = buffer.capacity();
        int residual = buffer.limit() - buffer.position();
        if (residual < byteArray.length) {
            if (unit == 0) {
                newCapacity += byteArray.length - residual;
            } else {
                newCapacity += unit * ((byteArray.length - residual) / unit + 1);
            }
        }

        ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
        newBuffer.put(buffer.array(), 0, buffer.position());
        newBuffer.put(byteArray);
        return newBuffer;
    }

    public static byte[] bufferRead(InputStream input) throws Exception {
        byte[] bytes = null;
        byte[] buffer = new byte[2048];
        BufferedInputStream bis = new BufferedInputStream(input);

        while (true) {
            Arrays.fill(buffer, (byte) 0);
            int size = bis.read(buffer);
            if (size <= 0) {
                return bytes;
            }

            if (bytes == null) {
                bytes = new byte[size];
                System.arraycopy(buffer, 0, bytes, 0, bytes.length);
            } else {
                byte[] temps = new byte[bytes.length + size];
                System.arraycopy(bytes, 0, temps, 0, bytes.length);
                System.arraycopy(buffer, 0, temps, bytes.length, size);
                byte[] bytess = null;
                bytess = temps;
            }
        }
    }

    public static boolean assertFailByLocalNotice(boolean Assert, int noticeId) throws Exception {
        if (Assert) {
            return true;
        } else {
            String notice = String.valueOf(noticeId);
            throw new Exception(notice);
        }
    }

    public static <K, V extends Comparable<V>> LinkedHashMap<K, V> sortMapValue(Map<K, V> oldMap) throws Exception {
        List<Entry<K, V>> entryList = new ArrayList(oldMap.entrySet());
        Collections.sort(entryList, new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return -((Comparable) o1.getValue()).compareTo(o2.getValue());
            }
        });
        LinkedHashMap<K, V> newMap = new LinkedHashMap();
        Iterator var3 = entryList.iterator();

        while (var3.hasNext()) {
            Entry<K, V> entry = (Entry) var3.next();
            newMap.put(entry.getKey(), entry.getValue());
        }

        return newMap;
    }

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            OS_TYPE = OSType.Windows;
        } else if (osName.contains("android")) {
            OS_TYPE = OSType.Android;
        } else if (osName.contains("linux")) {
            OS_TYPE = OSType.Linux;
        } else {
            OS_TYPE = OSType.IOS;
        }

    }
}
