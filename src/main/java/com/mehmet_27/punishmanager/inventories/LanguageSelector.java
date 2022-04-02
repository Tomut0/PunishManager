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

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

//TODO add multi-page support
public class LanguageSelector extends UIFrame{

    public LanguageSelector(UIFrame parent, InventoryType type, @NotNull ProxiedPlayer viewer) {
        super(parent, type, viewer);
        PunishManager plugin = PunishManager.getInstance();
        ConfigManager configManager = plugin.getConfigManager();

        Locale viewerLocale = plugin.getOfflinePlayers().get(viewer.getName()).getLocale();
        title(Messages.GUI_LANGUAGESELECTOR_TITLE.getString(viewer.getName())
                .replace("{0}", viewerLocale.toString()));
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
            String name = click.clickedItem().displayName(true).toString();
            Locale newLocale = Utils.stringToLocale(name.substring(name.lastIndexOf("_") - 2, name.lastIndexOf("_") + 3));
            plugin.getStorageManager().updateLanguage(viewer, newLocale);
            plugin.getOfflinePlayers().get(viewer.getName()).setLocale(newLocale);
            Utils.sendTextComponent(viewer, "main.setlanguage", message -> message.replace("{0}", newLocale.toString()));
            ProtocolizeUtils.openMainInventory(viewer);
        });
    }
}
