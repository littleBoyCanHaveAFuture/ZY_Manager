package test;

import org.springframework.stereotype.Component;

/**
 * @author song minghua
 * @date 2020/5/21
 */
@Component
public class EntBStrategy implements EntStrategy {
    @Override
    public String getStuff() {
        return "企业B";
    }

    @Override
    public void send() {
        System.out.println("发送B标准的报文给对应企业");
    }

    @Override
    public String toString() {
        return getStuff();
    }
}
