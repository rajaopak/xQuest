package id.rajaopak.xquest.util;

import id.rajaopak.xquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>.
 *
 * @author MrMicky, (edited by) rajaopak
 * @version 3.0.3
 */
public class GuiBuilder implements InventoryHolder {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();

    private final Inventory inventory;

    private Predicate<Player> closeFilter;

    private final AtomicBoolean unCloseable = new AtomicBoolean(false);

    private final AtomicBoolean unTakeAble = new AtomicBoolean(false);

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size The size of the inventory.
     */
    public GuiBuilder(int size) {
        this(size, InventoryType.CHEST.getDefaultTitle());
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  The size of the inventory.
     * @param title The title (name) of the inventory.
     */
    public GuiBuilder(int size, String title) {
        this(size, InventoryType.CHEST, Utils.colors(title));
    }

    /**
     * Create a new FastInv with a custom type.
     *
     * @param type The type of the inventory.
     */
    public GuiBuilder(InventoryType type) {
        this(Objects.requireNonNull(type, "type"), type.getDefaultTitle());
    }

    /**
     * Create a new FastInv with a custom type and title.
     *
     * @param type  The type of the inventory.
     * @param title The title of the inventory.
     */
    public GuiBuilder(InventoryType type, String title) {
        this(0, Objects.requireNonNull(type, "type"), title);
    }

    private GuiBuilder(int size, InventoryType type, String title) {
        if (type == InventoryType.CHEST && size > 0) {
            this.inventory = Bukkit.createInventory(this, size, title);
        } else {
            this.inventory = Bukkit.createInventory(this, type, title);
        }

        if (this.inventory.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + this.inventory.getHolder());
        }
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot.
     *
     * @param item The ItemStack to add
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
     *
     * @param item    The item to add.
     * @param handler The click handler for the item.
     */
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
     *
     * @param slot    The slot where to add the item.
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots.
     *
     * @param slotFrom Starting slot to add the item in.
     * @param slotTo   Ending slot to add the item in.
     * @param item     The item to add.
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
     *
     * @param slotFrom Starting slot to put the item in.
     * @param slotTo   Ending slot to put the item in.
     * @param item     The item to add.
     * @param handler  The click handler for the item
     */
    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots.
     *
     * @param slots The slots where to add the item
     * @param item  The item to add.
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   The slots where to add the item
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   The slots where to add the item
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItems(List<Integer> slots, ItemStack item, Consumer<InventoryClickEvent> handler){
        for(int slot : slots){
            setItem(slot, item, handler);
        }
    }

    public void setFilterItem(ItemStack item){
        for (int i = 0; i < this.inventory.getSize(); i++) {
            if (this.inventory.getItem(i) == null) {
                setItem(i, item);
            }
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   The slots where to add the item
     * @param item    The item to add.
     */
    public void setItems(List<Integer> slots, ItemStack item){
        for(int slot : slots){
            setItem(slot, item);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory.
     *
     * @param slot The slot where to remove the item
     */
    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * Remove multiples {@link ItemStack} from the inventory.
     *
     * @param slots The slots where to remove the items
     */
    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Add a close filter to prevent players from closing the inventory.
     * To prevent a player from closing the inventory the predicate should return {@code true}.
     *
     * @param closeFilter The close filter
     */
    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    /**
     * Add a handler to handle inventory open.
     *
     * @param openHandler The handler to add.
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    /**
     * Add a handler to handle inventory close.
     *
     * @param closeHandler The handler to add
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler to handle inventory click.
     *
     * @param clickHandler The handler to add.
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    /**
     * Open the inventory to a player.
     *
     * @param entity The player to open the menu.
     */
    public void open(HumanEntity entity) {
        entity.openInventory(this.inventory);
    }

    /**
     * Close the inventory to a player.
     *
     * @param entity player to check if he can open the inventory.
     */
    public void close(HumanEntity entity){
        entity.closeInventory();
    }

    /**
     * Get borders of the inventory. If the inventory size is under 27, all slots are returned.
     *
     * @return inventory borders
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get corners of the inventory.
     *
     * @return inventory corners
     */
    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10) || i == 17 || i == size - 18 || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    /**
     *
     * Set the inventory to unClosable.
     *
     * @param unCloseable If the inventory should be unCloseable
     */
    public void unCloseable(boolean unCloseable) {
        this.unCloseable.set(unCloseable);
    }

    public boolean isUnCloseable() {
        return this.unCloseable.get();
    }

    public void takeAble(boolean takeAble) {
        this.unTakeAble.set(takeAble);
    }

    public boolean isTakeAble() {
        return this.unTakeAble.get();
    }

    public void updateInventory() {
        for (HumanEntity entity : this.inventory.getViewers()) {
            if (entity instanceof Player) {
                ((Player) entity).updateInventory();
            }
        }
    }

    /**
     * Get the Bukkit inventory.
     *
     * @return The Bukkit inventory.
     */
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));

        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }

}
