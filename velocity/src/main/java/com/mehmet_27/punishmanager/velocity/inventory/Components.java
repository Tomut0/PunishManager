package com.mehmet_27.punishmanager.velocity.inventory;

import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.Paginator;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;

public class Components {
    public static UIComponent getBackComponent(UIFrame parent, int slot, Player viewer) {
        UIComponent back = new UIComponentImpl.Builder(ItemType.ARROW)
                .name(Messages.GUI_BACKBUTTON_NAME.getString(viewer.getUniqueId()))
                .slot(slot)
                .build();
        back.setListener(ClickType.LEFT_CLICK, () -> InventoryDrawer.open(parent));
        return back;
    }

    public static UIComponent getAirComponent(int slot) {
        return new UIComponentImpl.Builder(ItemType.AIR)
                .name("")
                .slot(slot)
                .build();
    }

    public static UIComponent getPreviousPageComponent(int slot, Runnable listener, Paginator paginator, Player viewer) {
        if (!paginator.hasPreviousPage()) {
            return getAirComponent(slot);
        }
        UIComponent c = new UIComponentImpl.Builder(ItemType.FEATHER)
                .name(Messages.GUI_MANAGEPUNISHMENTS_PREVIOUS_NAME.getString(viewer.getUniqueId()))
                .slot(slot)
                .build();
        setOneTimeUseListener(c, listener);
        return c;
    }

    public static UIComponent getNextPageComponent(int slot, Runnable listener, Paginator paginator, Player viewer) {
        if (!paginator.hasNextPage()) {
            return getAirComponent(slot);
        }
        UIComponent c = new UIComponentImpl.Builder(ItemType.FEATHER)
                .name(Messages.GUI_MANAGEPUNISHMENTS_NEXT_NAME.getString(viewer.getUniqueId()))
                .slot(slot)
                .build();
        setOneTimeUseListener(c, listener);
        return c;
    }

    private static void setOneTimeUseListener(UIComponent c, Runnable listener) {
        c.setListener(ClickType.LEFT_CLICK, () -> {
            if (listener != null) {
                listener.run();
            }
            c.setListener(ClickType.LEFT_CLICK, null);
        });
    }
}
