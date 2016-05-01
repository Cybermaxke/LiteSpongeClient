package me.cybermaxke.spongeliteclient.keyboard;

public interface IClientKeyBinding {

    /**
     * Gets the raw category title.
     *
     * @return The category title
     */
    String getCategory();

    /**
     * Gets the formatted category title.
     *
     * @return The category title
     */
    String getFormattedCategory();

    /**
     * Gets the raw display name.
     *
     * @return The display name
     */
    String getDisplayName();

    /**
     * Gets the formatted display name.
     *
     * @return The display name
     */
    String getFormattedDisplayName();
}
