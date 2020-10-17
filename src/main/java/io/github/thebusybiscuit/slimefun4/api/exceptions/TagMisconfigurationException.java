package io.github.thebusybiscuit.slimefun4.api.exceptions;

import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import org.bukkit.NamespacedKey;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An {@link TagMisconfigurationException} is thrown whenever a {@link SlimefunTag}
 * contains illegal, invalid or unknown values.
 *
 * @author TheBusyBiscuit
 */
public class TagMisconfigurationException extends Exception {

    private static final long serialVersionUID = 5412127960821774280L;

    /**
     * This constructs a new {@link TagMisconfigurationException} for the given
     * {@link SlimefunTag}'s {@link NamespacedKey} with the provided context.
     *
     * @param key     The {@link NamespacedKey} of our {@link SlimefunTag}
     * @param message The message to display
     */
    @ParametersAreNonnullByDefault
    public TagMisconfigurationException(NamespacedKey key, String message) {
        super("Tag '" + key + "' has been misconfigured: " + message);
    }

}