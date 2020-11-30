package hu.gov.allamkincstar.training.javasebsc.order;

import hu.gov.allamkincstar.training.javasebsc.baseclasses.Lot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImmutableList {
    private List<OrderItem> list;

    public ImmutableList(Map<String, OrderItem> orderItemList) {
        this.list = new ArrayList<>();
        for (Map.Entry<String, OrderItem> element : orderItemList.entrySet()) {
            OrderItem item = (OrderItem) element.getValue();
            this.list.add(item);
        }
    }

    public OrderItem get(int index){
        return list.get(index);
    }

    public int size(){
        return list.size();
    }
}
