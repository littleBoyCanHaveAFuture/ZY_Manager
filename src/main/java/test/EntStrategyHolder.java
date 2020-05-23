package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
@Component
public class EntStrategyHolder {

    // 关键功能 Spring 会自动将 EntStrategy 接口的类注入到这个Map中
    @Autowired
    private Map<String, EntStrategy> entStrategyMap;

    @Autowired
    private List<EntStrategy> entStrategyList;


    public EntStrategy getBy(String entNum) {
        return entStrategyMap.get(entNum);
    }

    public void print() {
        System.out.println("===== implementation Map =====");
        System.out.println(entStrategyMap);
        entStrategyMap.forEach((name, impl) -> {
            System.out.println(name + ":" + impl.getStuff());
        });
        System.out.println("===== implementation List =====");
        System.out.println(entStrategyList);
        entStrategyList.forEach(impl -> System.out.println(impl.getStuff()));
    }

}
