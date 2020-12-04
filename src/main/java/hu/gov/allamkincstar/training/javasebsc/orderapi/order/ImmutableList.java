package hu.gov.allamkincstar.training.javasebsc.orderapi.order;

import hu.gov.allamkincstar.training.javasebsc.orderapi.baseclasses.ProductItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImmutableList {
    private List<OrderItem> list;

    public ImmutableList(List<ProductItem> orderItemList) {
        this.list = new ArrayList<>();
        orderItemList.forEach(item -> this.list.add(new OrderItem(item)));
    }

    public OrderItem get(int index){
        return list.get(index);
    }

    public int size(){
        return list.size();
    }
}
