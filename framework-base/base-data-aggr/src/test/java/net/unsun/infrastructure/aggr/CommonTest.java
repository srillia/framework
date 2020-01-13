package net.unsun.infrastructure.aggr;

import com.alibaba.fastjson.JSON;
import net.unsun.infrastructure.aggr.core.Aggr;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: framework
 * @author: Tokey
 * @create: 2020-01-07 15:59
 */
public class CommonTest {

    public static class Entity1 {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Entity2 {
        private Long id;
        private String nickname;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }


        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Test
    public void test() {
        Entity1 entity1 = new Entity1();
        entity1.setId(1L);
        entity1.setName("ssss");
        Entity1 entity2 = new Entity1();
        entity2.setId(2L);
        entity2.setName("ssss");
        Entity1 entity3 = new Entity1();
        entity3.setId(3L);
        entity3.setName("ssss");


        Entity2 entity4 = new Entity2();
        entity4.setId(6L);
        entity4.setNickname("ssss");
        Entity2 entity5 = new Entity2();
        entity5.setId(2L);
        entity5.setNickname("ssss");
        Entity2 entity6 = new Entity2();
        entity6.setId(4L);
        entity6.setNickname("ssss");


        List<Entity1> list = new ArrayList<Entity1>();
        list.add(entity1);
        list.add(entity2);
        list.add(entity3);
        for (Entity1 entity11 : list) {

            System.out.println(entity1);
        }

        List list2 = new ArrayList();
        list2.add(entity4);
        list2.add(entity5);
        list2.add(entity6);




        List list3 =Aggr.main(list).connect(list2).build();
        System.out.println(JSON.toJSONString(list3));

    }
    @Test
    public void test03() {
        String str = "ssssssssssss; " +
                "";
        String[] split = str.split(";");
        System.out.println(split.length);
    }
}