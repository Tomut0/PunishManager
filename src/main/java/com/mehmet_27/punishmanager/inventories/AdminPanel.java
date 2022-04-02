package com.mehmet_27.punishmanager.inventories;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.ProtocolizeUtils;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AdminPanel extends UIFrame {

    public AdminPanel(UIFrame parent, InventoryType type, ProxiedPlayer viewer) {
        super(parent, type, viewer);
        PunishManager plugin = PunishManager.getInstance();
        title(Messages.GUI_ADMINPANEL_TITLE.getString(viewer.getName()));
        ItemStack reloadButton = new ItemStack(ItemType.LIME_DYE)
                .displayName(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(viewer.getName()));
        item(11, reloadButton);

        ItemStack backButton = new Item().back(viewer.getName());
        item(26, backButton);

        onClick(click -> {
            click.cancelled(true);
            ItemStack clickedItem = click.clickedItem();
            if (clickedItem == null) return;
            String itemName = clickedItem.displayName(true).toString();
            if (itemName.contains(Messages.GUI_ADMINPANEL_RELOAD_NAME.getString(viewer.getName()))) {
                plugin.getProxy().getPluginManager().dispatchCommand(viewer, "punishmanager reload");
            }
            if (clickedItem.equals(backButton)){
                ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(viewer.getUniqueId());
                ProtocolizeUtils.openInventory(getParent(), protocolizePlayer);
            }
        });
    }
}
