package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.ProtocolizeUtils;
import com.mehmet_27.punishmanager.utils.Utils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DefaultLanguageSelector extends UIFrame {
    public DefaultLanguageSelector(@Nullable UIFrame parent, InventoryType type, @NotNull ProxiedPlayer viewer) {
        super(parent, type, viewer);
        PunishManager plugin = PunishManager.getInstance();
        ConfigManager configManager = plugin.getConfigManager();

        Locale defaultLocale = configManager.getDefaultLocale();
        title(Messages.GUI_DEFAULTLANGUAGESELECTOR_TITLE.getString(viewer.getName())
                .replace("{0}", defaultLocale.toString()));
        List<String> localeNames = configManager.getAvailableLocales().stream().map(Locale::toString).sorted().collect(Collectors.toList());
        for (int i = 0; i < localeNames.size(); i++) {
            ItemStack itemStack = new ItemStack(ItemType.PAPER);
            itemStack.displayName(localeNames.get(i));
            item(i, itemStack);
        }
        ItemStack backButton = new Item().back(viewer.getName());
        item(53, backButton);

        onClick(click -> {
            click.cancelled(true);
            ItemStack clickedItem = click.clickedItem();
            if (clickedItem == null) return;
            if (clickedItem.equals(backButton)){
                ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(viewer.getUniqueId());
                ProtocolizeUtils.openInventory(getParent(), protocolizePlayer);
                return;
            }
            String name = clickedItem.displayName(true).toString();
            Locale newLocale = Utils.stringToLocale(name.substring(name.lastIndexOf("_") - 2, name.lastIndexOf("_") + 3));
            configManager.setDefaultLocale(newLocale);
            Utils.sendTextComponent(viewer, "main.setdefaultlanguage", message -> message.replace("{0}", newLocale.toString()));
            ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(viewer.getUniqueId());
            ProtocolizeUtils.openInventory(getParent(), protocolizePlayer);
        });
    }
}
