package io.github.thebusybiscuit.slimefun4.api.items.settings;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import org.bukkit.Material;
import org.bukkit.Tag;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This variation of {@link ItemSetting} allows you to define a default {@link Tag}.
 * The {@link Tag} will be translated into a {@link String} {@link List} which the user
 * can then configure as they wish.
 * <p>
 * It also validates all inputs to be a valid {@link Material}.
 *
 * @author TheBusyBiscuit
 * @see ItemSetting
 */
public class MaterialTagSetting extends ItemSetting<List<String>> {

    private final Tag<Material> defaultTag;

    @ParametersAreNonnullByDefault
    public MaterialTagSetting(String key, Tag<Material> defaultTag) {
        super(key, getAsStringList(defaultTag));

        this.defaultTag = defaultTag;
    }

    /**
     * This {@link Tag} holds the default values for this {@link MaterialTagSetting}.
     *
     * @return The default {@link Tag}
     */
    @Nonnull
    public Tag<Material> getDefaultTag() {
        return defaultTag;
    }

    @Override
    public boolean validateInput(List<String> input) {
        if (input != null) {
            for (String value : input) {
                Material material = Material.matchMaterial(value);

                // This value is not a valid material, the setting is not valid.
                if (material == null) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Internal method to turn a {@link Tag} into a {@link List} of {@link String Strings}.
     *
     * @param tag Our {@link Tag}
     * @return The {@link String} {@link List}
     */
    @Nonnull
    private static List<String> getAsStringList(@Nonnull Tag<Material> tag) {
        return tag.getValues().stream().map(Material::name).collect(Collectors.toList());
    }

}