package test;

import java.util.BitSet;

/**
 * @author song minghua
 * @date 2019/11/28
 */
public class setBit {
    public static void main(String[] args) {
        //代码用例
        BitSet bitSet = new BitSet();
        BitSet bitSet1 = new BitSet();
        BitSet bitSet2 = new BitSet();
        for (int i = 1; i <= 10; i++) {
            bitSet.set(i);
            if (i % 2 == 0) bitSet1.set(i);
            else bitSet2.set(i);
        }
        //and（并且 交集）
        System.out.println("bitSet and 前:" + bitSet.toString());
        System.out.println("bitSet1 and 前:" + bitSet1.toString());

        bitSet1.and(bitSet);
        System.out.println("bitSet1 and bitSet后:" + bitSet1.toString());

        System.out.println("bitSet2 and 前:" + bitSet2.toString());
        bitSet1.and(bitSet);
        System.out.println("bitSet2 and bitSet后:" + bitSet2.toString());
    }
}
