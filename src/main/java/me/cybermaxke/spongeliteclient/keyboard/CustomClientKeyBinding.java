package me.cybermaxke.spongeliteclient.keyboard;

import me.cybermaxke.spongeliteclient.interfaces.IMixinKeyBinding;
import net.minecraft.util.text.ITextComponent;

public class CustomClientKeyBinding extends net.minecraft.client.settings.KeyBinding implements IClientKeyBinding {

    private final String id;

    private ITextComponent displayName;
    private ITextComponent categoryTitle;

    /**
     * Creates a new custom key binding for the
     * specified key binding settings.
     *
     * @param keyBinding The key binding
     */
    public CustomClientKeyBinding(KeyBinding keyBinding) {
        super(keyBinding.getDisplayName().getUnformattedText(), 0, keyBinding.getKeyCategory().getTitle().getUnformattedText());
        ((IMixinKeyBinding) this).setInternalId(keyBinding.getInternalId());
        this.id = keyBinding.getId();
        this.displayName = keyBinding.getDisplayName();
        this.categoryTitle = keyBinding.getKeyCategory().getTitle();
    }

    /**
     * Gets the identifier of the custom key binding.
     *
     * @return The identifier
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the display name of the key binding.
     *
     * @return The display name
     */
    @Override
    public String getKeyDescription() {
        return this.displayName.getUnformattedText();
    }

    @Override
    public String getCategory() {
        return this.categoryTitle.getUnformattedText();
    }

    @Override
    public String getFormattedCategory() {
        return this.categoryTitle.getUnformattedText();
    }

    @Override
    public String getDisplayName() {
        return this.displayName.getUnformattedText();
    }

    @Override
    public String getFormattedDisplayName() {
        return this.displayName.getUnformattedText();
    }
}
