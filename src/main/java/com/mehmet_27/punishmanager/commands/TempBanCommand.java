package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.events.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.regex.Matcher;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.TEMPBAN;
import static com.mehmet_27.punishmanager.utils.Utils.NumberAndUnit;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@command.tempban.description}")
    @CommandAlias("tempban")
    public void tempBan(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = dataBaseManager.getBan(playerName);
        if (punishment != null && punishment.isBanned()) {
            sender.sendMessage(new TextComponent(configManager.getMessage("tempban.alreadyPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        Matcher matcher = NumberAndUnit.matcher(time.toLowerCase());
        if (!matcher.find()) {
            throw new InvalidCommandArgument();
        }
        int number = Integer.parseInt(matcher.group("number"));
        String unit = matcher.group("unit");
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(number, unit);
        String ip = Utils.getPlayerIp(playerName);
        punishment = new Punishment(playerName, uuid, ip, TEMPBAN, reason, sender.getName(), start, end);
        PunishManager.getInstance().getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
