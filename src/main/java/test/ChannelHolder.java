package test;

import com.zyh5games.sdk.channel.BaseChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
@Component
public class ChannelHolder {

    // 关键功能 Spring 会自动将 EntStrategy 接口的类注入到这个Map中
    @Autowired
    private Map<String, BaseChannel> entStrategyMap;

    @Autowired
    private List<BaseChannel> entStrategyList;


    public BaseChannel getBy(String entNum) {
        return entStrategyMap.get(entNum);
    }

    public void print() {
        System.out.println("===== implementation Map =====");
        System.out.println(entStrategyMap);
        entStrategyMap.forEach((name, impl) -> {
            System.out.println(name + ":" + impl.getChannelId());
        });
        System.out.println("===== implementation List =====");
        System.out.println(entStrategyList);
        entStrategyList.forEach(impl -> System.out.println(impl.getChannelId()));
    }

}
