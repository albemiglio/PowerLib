package it.mycraft.powerlib.bukkit.inventory;

public final class NavigationLayout {

    private final int prevSlot;
    private final int nextSlot;

    private NavigationLayout(int prev, int next) {
        this.prevSlot = prev;
        this.nextSlot = next;
    }

    public static NavigationLayout bottomRow(int rows) {
        int last = rows * 9;
        return new NavigationLayout(last - 9, last - 1);
    }

    public static NavigationLayout custom(int prev, int next) {
        return new NavigationLayout(prev, next);
    }

    public int getPrevSlot() { return prevSlot; }
    public int getNextSlot() { return nextSlot; }
}
