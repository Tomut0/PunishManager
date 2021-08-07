package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.Reason;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.IPBAN;

@CommandAlias("banip")
@CommandPermission("punishmanager.command.banip")
public class IpBanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players Reason")
    public void banIp(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getPunishment(playerName, "ban");
        if (punishment != null && punishment.playerIsBanned()) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("ipban.alreadyPunished", sender.getName()).
                    replace("%name%", playerName)));
            return;
        }
        String ip = new Ip(playerName).getPlayerIp();
        punishment = new Punishment(playerName, uuid, ip, IPBAN, new Reason(reason, playerName).getReason(), sender.getName());
        punishmentManager.AddPunish(punishment);
        sender.sendMessage(new TextComponent(messageManager.getMessage("ipban.punished", sender.getName()).
                replace("%name%", playerName)));
        Utils.disconnectPlayer(punishment);
    }
}
