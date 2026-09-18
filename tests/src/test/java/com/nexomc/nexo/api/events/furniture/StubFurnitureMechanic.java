package com.nexomc.nexo.api.events.furniture;

import com.nexomc.nexo.mechanics.Mechanic;

/**
 * Stand-in for Nexo's furniture mechanic: the bridge only ever asks it for {@code getItemID()}. It
 * extends the {@link Mechanic} stand-in because Nexo's own furniture mechanic does, which is what makes
 * the single handle PowerLib resolves on the base class usable on every subclass.
 */
public class StubFurnitureMechanic extends Mechanic {

    private final String itemId;
    private final RuntimeException failure;

    public StubFurnitureMechanic(String itemId) {
        this(itemId, null);
    }

    /**
     * A mechanic whose id lookup blows up, as Nexo's own Kotlin code does when the furniture it describes
     * has already been removed.
     *
     * @param failure what the id lookup throws
     * @return a mechanic that never yields an id
     */
    public static StubFurnitureMechanic failing(RuntimeException failure) {
        return new StubFurnitureMechanic(null, failure);
    }

    private StubFurnitureMechanic(String itemId, RuntimeException failure) {
        this.itemId = itemId;
        this.failure = failure;
    }

    @Override
    public String getItemID() {
        if (failure != null) {
            throw failure;
        }
        return itemId;
    }
}
