package io.github.thebusybiscuit.slimefun4.api.player;

import com.xzavier0722.mc.plugin.slimefun4.storage.callback.IAsyncReadCallback;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BackpackListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ren.natsuyuk1.slimefun4.inventoryholder.SlimefunBackpackHolder;
import ren.natsuyuk1.slimefun4.utils.InventoryUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represents the instance of a {@link SlimefunBackpack} that is ready to
 * be opened.
 * 
 * It holds an actual {@link Inventory} and represents the backpack on the
 * level of an individual {@link ItemStack} as opposed to the class {@link SlimefunBackpack}.
 * 
 * @author TheBusyBiscuit
 *
 * @see SlimefunBackpack
 * @see BackpackListener
 */
public class PlayerBackpack {
    private final OfflinePlayer owner;
    private final UUID uuid;
    private final int id;
    private String name;
    private Inventory inventory;
    private int size;

    public static void getAsync(ItemStack item, Consumer<PlayerBackpack> callback, boolean runCbOnMainThread) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        OptionalInt id = OptionalInt.empty();
        String uuid = "";

        for (String line : item.getItemMeta().getLore()) {
            if (line.startsWith(ChatColors.color("&7ID: ")) && line.indexOf('#') != -1) {
                String[] splitLine = CommonPatterns.HASH.split(line);

                if (CommonPatterns.NUMERIC.matcher(splitLine[1]).matches()) {
                    id = OptionalInt.of(Integer.parseInt(splitLine[1]));
                    uuid = splitLine[0].replace(ChatColors.color("&7ID: "), "");
                }
            }
        }

        if (id.isPresent()) {
            int number = id.getAsInt();
            Slimefun.getRegistry().getProfileDataController().getBackpackAsync(
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)),
                    number,
                    new IAsyncReadCallback<>() {
                        @Override
                        public boolean runOnMainThread() {
                            return runCbOnMainThread;
                        }

                        @Override
                        public void onResult(PlayerBackpack result) {
                            callback.accept(result);
                        }
                    }
            );
        }
    }

    @ParametersAreNonnullByDefault
    public PlayerBackpack(OfflinePlayer owner, UUID uuid, int id, int size, @Nullable ItemStack[] contents) {
        this(owner, uuid, "", id, size, contents);
    }

    @ParametersAreNonnullByDefault
    public PlayerBackpack(OfflinePlayer owner, UUID uuid, String name, int id, int size, @Nullable ItemStack[] contents) {
        if (size < 9 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("Invalid size! Size must be one of: [9, 18, 27, 36, 45, 54]");
        }

        this.owner = owner;
        this.uuid = uuid;
        this.name = name;
        this.id = id;
        this.size = size;

        var holder = new SlimefunBackpackHolder();
        inventory = Bukkit.createInventory(holder, size, "背包 [大小 " + size + "]");

        holder.setBackpack(this);
        holder.setInventory(inventory);

        if (contents == null) {
            return;
        }

        if (size != contents.length) {
            throw new IllegalArgumentException("Invalid contents: size mismatched!");
        }
        inventory.setContents(contents);
    }

    /**
     * This returns the id of this {@link PlayerBackpack}
     * 
     * @return The id of this {@link PlayerBackpack}
     */
    public int getId() {
        return id;
    }

    /**
     * This method returns the {@link PlayerProfile} this {@link PlayerBackpack} belongs to
     * 
     * @return The owning {@link PlayerProfile}
     */
    @Nonnull
    public OfflinePlayer getOwner() {
        return owner;
    }

    /**
     * This returns the size of this {@link PlayerBackpack}.
     * 
     * @return The size of this {@link PlayerBackpack}
     */
    public int getSize() {
        return size;
    }

    /**
     * This method returns the {@link Inventory} of this {@link PlayerBackpack}
     *
     * @return The {@link Inventory} of this {@link PlayerBackpack}
     */
    @Nonnull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * This will open the {@link Inventory} of this backpack to every {@link Player}
     * that was passed onto this method.
     * <p>
     * 二进制兼容
     *
     * @param players The player who this Backpack will be shown to
     */
    public void open(Player... players) {
        Slimefun.runSync(() -> {
            for (Player p : players) {
                p.openInventory(inventory);
            }
        });
    }

    /**
     * This will open the {@link Inventory} of this backpack to every {@link Player}
     * that was passed onto this method.
     *
     * @param player   The player who this Backpack will be shown to
     * @param callback The operation after backpack was open
     */
    public void open(Player player, Runnable callback) {
        Slimefun.runSync(() -> {
            player.openInventory(inventory);
            callback.run();
        });
    }

    /**
     * This will change the current size of this Backpack to the specified size.
     * 
     * @param size
     *            The new size for this Backpack
     */
    public void setSize(int size) {
        if (size < 9 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("Invalid size! Size must be one of: [9, 18, 27, 36, 45, 54]");
        }

        this.size = size;

        var holder = new SlimefunBackpackHolder();
        Inventory inv = Bukkit.createInventory(holder, size, "背包 [大小 " + size + "]");

        holder.setInventory(inv);
        holder.setBackpack(this);

        InventoryUtil.closeInventory(inventory);

        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            inv.setItem(slot, this.inventory.getItem(slot));
        }
        this.inventory = inv;
        Slimefun.getRegistry().getProfileDataController().saveBackpackInfo(this);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
        Slimefun.getRegistry().getProfileDataController().saveBackpackInfo(this);
    }

    public String getName() {
        return name;
    }

}
