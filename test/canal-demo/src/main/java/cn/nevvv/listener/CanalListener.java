package cn.nevvv.listener;

import cn.nevvv.pojo.Item;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@Component
@CanalTable(value = "tb_item")
public class CanalListener implements EntryHandler<Item> {
    @Override
    public void insert(Item item) {
        System.out.println(item);
    }

    @Override
    public void update(Item before, Item after) {
        System.out.println(before);
        System.out.println(after);
    }

    @Override
    public void delete(Item item) {
        System.out.println(item);
    }
}
