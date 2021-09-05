package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("punishmanager")
public class PunishManagerCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @Default
    @Description("The main command of the plugin.")
    public void punishManager(CommandSender sender) {
        sender.sendMessage(new TextComponent(Utils.color("&a&m+                                                        +")));
        sender.sendMessage(new TextComponent(Utils.color("&6&lPunishManager")));
        sender.sendMessage(new TextComponent(Utils.color("&eThe best punishment plugin for your server.")));
        sender.sendMessage(new TextComponent(Utils.color("&eMade by &bMehmet_27")));
        sender.sendMessage(new TextComponent(Utils.color("&eContributors: &7Minat0_, RoinujNosde")));
        sender.sendMessage(new TextComponent(Utils.color("&6Contact")));
        sender.sendMessage(new TextComponent(Utils.color("&eDiscord: &7Mehmet#5073")));
        sender.sendMessage(new TextComponent(Utils.color("&a&m+                                                        +")));
        sender.sendMessage(new TextComponent(Utils.color("&eUse &a/punishmanager help &efor help.")));
    }

    @Subcommand("reload")
    @Description("Reloads the plugin.")
    public void reload(CommandSender sender) {
        punishManager.getConfigManager().setup();
        sender.sendMessage(new TextComponent(Utils.color("&6" + punishManager.getDescription().getName() + " &7» &aAll configuration files have been reloaded.")));
    }

    @Subcommand("help")
    @Description("Help command.")
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
