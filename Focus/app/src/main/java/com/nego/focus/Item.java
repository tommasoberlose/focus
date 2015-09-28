package com.nego.focus;

/**
 * Created by Tommaso on 27/03/2015.
 */
public class Item {
    private int type;
    private Reminder item;
    private String subtitle;
    private boolean selected = false;

    public Item(int type) {
        this.type = type;
    }
    public Item(int type, Reminder item) {
        this.type = type;
        this.item = item;
    }
    public Item(int type, String subtitle) {
        this.type = type;
        this.subtitle = subtitle;
    }
    public int getType() { return type; }

    public Reminder getItem() {
        return item;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isSelected() {
        return selected;
    }
    public void toggleSelected() {
        if(selected)
            selected = false;
        else
            selected = true;
    }
}
