package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImmutableList {
    private List<OrderItem> list;

    public ImmutableList(Map<String, ProductItem> orderItemList) {
        this.list = new ArrayList<>();
        for (Map.Entry<String, ProductItem> element : orderItemList.entrySet()) {
            this.list.add(new OrderItem(element.getValue()));
        }
    }

    public OrderItem get(int index){
        return list.get(index);
    }

    public int size(){
        return list.size();
    }
}
