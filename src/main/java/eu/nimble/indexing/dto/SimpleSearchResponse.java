package eu.nimble.indexing.dto;

import java.util.ArrayList;
import java.util.List;

public class SimpleSearchResponse {

    List<SimpleItem> itemList = new ArrayList<>();

    public List<SimpleItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<SimpleItem> itemList) {
        this.itemList = itemList;
    }
}
