package com.mehmet_27.punishmanager.velocity.inventory.inventories;

import co.aikar.commands.InvalidCommandArgument;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.configuration.Configuration;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Messages;
import com.mehmet_27.punishmanager.utils.Utils;
import com.mehmet_27.punishmanager.velocity.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.velocity.inventory.UIComponent;
import com.mehmet_27.punishmanager.velocity.inventory.UIComponentImpl;
import com.mehmet_27.punishmanager.velocity.inventory.UIFrame;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.data.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class PunishFrame extends UIFrame {

    private final StorageManager storageManager = PunishManager.getInstance().getStorageManager();
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();

    private final OfflinePlayer target;
    private final UUID viewerUuid = getViewer().getUniqueId();

    public PunishFrame(Player viewer, OfflinePlayer target) {
        super(null, viewer);
        this.target = target;
    }

    @Override
    public String getTitle() {
        return Messages.GUI_PUNISH_TITLE.getString(getViewer().getUniqueId()).replace("%player%", target.getName());
    }

    @Override
    public int getSize() {
        return 9 * 6;
    }

    @Override
    public void createComponents() {
        List<String> lore = new ArrayList<>();
        UUID uuid = target.getUniqueId();
        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid).replaceAll("\\s+", "");
        Punishment ban = storageManager.getBan(uuid);
        Punishment mute = storageManager.getMute(uuid);

        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", viewerUuid);
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", viewerUuid);

        lore.add(configManager.getMessage("check.uuid", viewerUuid, message -> message.replace("%uuid%", uuid.toString())));
        if (PunishManager.getInstance().getConfigManager().getConfig().getBoolean("check-command-show-ip-require-perm", false)) {
            if (getViewer().hasPermission("punishmanager.command.check.ip")) {
                lore.add(configManager.getMessage("check.ip", viewerUuid, message -> message.replace("%ip%", ip)));
            }
        } else {
            lore.add(configManager.getMessage("check.ip", viewerUuid, message -> message.replace("%ip%", ip)));
        }
        String country = Utils.getValueFromUrlJson(ip);
        lore.add(configManager.getMessage("check.country", viewerUuid, message -> message.replace("%country%", country)));
        String language = storageManager.getOfflinePlayer(uuid).getLocale().toString();
        lore.add(configManager.getMessage("check.language", viewerUuid, message -> message.replace("%language%", language)));
        lore.add(configManager.getMessage("check.banStatus", viewerUuid, message -> message.replace("%status%", banStatus)));

        if (ban != null && ban.isBanned()) {
            lore.add(configManager.getMessage("check.punishId", viewerUuid, message -> message.replace("%id%", ban.getId() + "")));
            lore.add(configManager.getMessage("check.banReason", viewerUuid, message -> message.replace("%reason%", ban.getReason())));
            lore.add(configManager.getMessage("check.banOperator", viewerUuid, message -> message.replace("%operator%", ban.getOperator())));
            lore.add(configManager.getMessage("check.banServer", viewerUuid, message -> message.replace("%server%", ban.getServer())));
        }

        lore.add(configManager.getMessage("check.muteStatus", viewerUuid, message -> message.replace("%status%", muteStatus)));
        if (mute != null && mute.isMuted()) {
            lore.add(configManager.getMessage("check.punishId", viewerUuid, message -> message.replace("%id%", mute.getId() + "")));
            lore.add(configManager.getMessage("check.muteReason", viewerUuid, message -> message.replace("%reason%", mute.getReason())));
            lore.add(configManager.getMessage("check.muteOperator", viewerUuid, message -> message.replace("%operator%", mute.getOperator())));
            lore.add(configManager.getMessage("check.muteServer", viewerUuid, message -> message.replace("%server%", mute.getServer())));
        }
        UIComponent infoComponent = new UIComponentImpl.Builder(ItemType.PLAYER_HEAD)
                .name(Messages.GUI_PUNISH_INFO_NAME.getString(viewerUuid).replace("%player%", target.getName()))
                .lore(lore)
                .slot(4).build();
        add(infoComponent);
        addTemplates();
    }

    private void addTemplates() {
        Configuration templates = PunishManager.getInstance().getConfigManager().getConfig().getSection("templates");
        for (int i = 0; i < templates.getKeys().size() && i < 18; i++) {
            Configuration template = templates.getSection(String.valueOf(i + 1));
            String duration = template.getString("duration", "permanent");
            String reason = template.getString("reason");
            String permission = template.getString("permission", null);
            Punishment.PunishType type = Punishment.PunishType.valueOf(template.getString("type").toUpperCase(Locale.ENGLISH));

            UIComponent component = new UIComponentImpl.Builder(ItemType.PAPER)
                    .name(Messages.GUI_PUNISH_TEMPLATE_NAME.getString(viewerUuid).replace("%template%", String.valueOf(i + 1)))
                    .lore(Messages.GUI_PUNISH_TEMPLATE_LORE.getStringList(viewerUuid)
                            .stream().map(s -> s.replace("%type%", type.name())
                                    .replace("%reason%", reason)
                                    .replace("%duration%", duration)).collect(Collectors.toList()))
                    .slot(i + 18).build();
            if (permission != null) {
                component.setPermission(ClickType.LEFT_CLICK, permission);
            }
            component.setListener(ClickType.LEFT_CLICK, () -> {
                String ip = PunishManager.getInstance().getMethods().getPlayerIp(target.getUniqueId());
                Punishment punishment;
                if (type.isTemp()) {
                    Matcher matcher = Utils.NumberAndUnit.matcher(duration.toLowerCase());
                    if (!matcher.find()) {
                        throw new InvalidCommandArgument();
                    }
                    int number = Integer.parseInt(matcher.group("number"));
                    String unit = matcher.group("unit");
                    long start = System.currentTimeMillis();
                    long end = start + Utils.convertToMillis(number, unit);

                    punishment = new Punishment(target.getName(), target.getUniqueId(), ip, type, reason, getViewer().getUsername(), getViewer().getUniqueId(), "ALL", start, end, -1);
                } else {
                    punishment = new Punishment(target.getName(), target.getUniqueId(), ip, type, reason, getViewer().getUsername(), getViewer().getUniqueId(), "ALL", -1);
                }
                PunishManager.getInstance().getMethods().callPunishEvent(punishment);
                if (!punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
                    updateFrame();
                }
            });
            add(component);
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }
}
