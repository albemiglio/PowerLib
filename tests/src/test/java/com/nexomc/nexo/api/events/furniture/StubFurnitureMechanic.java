package com.nexomc.nexo.api.events.furniture;

/**
 * Stand-in for Nexo's furniture mechanic: the bridge only ever asks it for {@code getItemID()}.
 */
public class StubFurnitureMechanic {

    private final String itemId;

    public StubFurnitureMechanic(String itemId) {
        this.itemId = itemId;
    }

    public String getItemID() {
        return itemId;
    }
}
