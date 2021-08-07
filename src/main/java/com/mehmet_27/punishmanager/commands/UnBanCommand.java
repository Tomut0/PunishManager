package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("unban")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players")
    public void unBan(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = punishmentManager.getPunishment(playerName, "ban");
        /* fixme: Small advice
           Replace it with ACF conditions
        */
        if (punishment == null || !punishment.playerIsBanned()) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("unban.notPunished", sender.getName()).
                    replace("%name%", playerName)));
            return;
        }
        punishmentManager.unPunishPlayer(punishment);
        sender.sendMessage(new TextComponent(messageManager.getMessage("unban.done", sender.getName()).
                replace("%name%", playerName)));

    }
}
