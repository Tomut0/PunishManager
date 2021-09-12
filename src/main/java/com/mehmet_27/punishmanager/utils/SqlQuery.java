package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;

public enum SqlQuery {
    CREATE_PUNISHMENTS_TABLE(
            "CREATE TABLE IF NOT EXISTS `punishmanager_punishments` (" +
                    " `id` BIGINT(20) NOT NULL auto_increment," +
                    " `name` VARCHAR(16)," +
                    " `uuid` VARCHAR(72)," +
                    " `ip` VARCHAR(25)," +
                    " `reason` VARCHAR(255)," +
                    " `operator` VARCHAR(16)," +
                    " `type` VARCHAR(16)," +
                    " `start` LONGTEXT," +
                    " `end` LONGTEXT," +
                    " PRIMARY KEY (`id`))"
    ),
    CREATE_PLAYERS_TABLE(
            "CREATE TABLE IF NOT EXISTS `punishmanager_players` (" +
                    " `uuid` VARCHAR(72) NOT NULL," +
                    " `name` VARCHAR(16)," +
                    " `ip` VARCHAR(25)," +
                    " `language` VARCHAR(10)," +
                    " PRIMARY KEY (`uuid`))"
    ),
    ADD_PLAYER_TO_PLAYERS_TABLE(
            "INSERT INTO `punishmanager_players` (" +
                    " `uuid`, `name`, `ip`, `language`)" +
                    " VALUES (?,?,?,?)"
    ),
    ADD_PUNISH_TO_PUNISHMENTS(
            "INSERT INTO `punishmanager_punishments` (" +
                    "`name`, `uuid`, `ip`, `reason`, `operator`, `type`, `start`, `end`)" +
                    " VALUES (?,?,?,?,?,?,?,?)"
    ),
    GET_PUNISHMENT("SELECT * FROM `punishmanager_punishments` WHERE name = ?"),
    DELETE_PUNISHMENT("DELETE FROM `punishmanager_punishments` WHERE name = ?"),
    DELETE_PUNISHMENT_WITH_TYPE("DELETE FROM `punishmanager_punishments` WHERE name = ? and type = ?"),
    SELECT_ALL_PUNISHMENTS("SELECT * FROM `punishmanager_punishments`"),
    SELECT_ALL_PLAYERS("SELECT * FROM `punishmanager_players`"),
    SELECT_PLAYER_WITH_NAME("SELECT * FROM `punishmanager_players` WHERE name = ?"),
    UPDATE_PLAYER_LOCALE("UPDATE `punishmanager_players` SET `language` = ? WHERE `name` = ?"),
    SELECT_DISCORDSRV_WITH_UUID("SELECT * FROM `discordsrv_accounts` WHERE uuid = ?"),
    GET_ALL_LOGGED_NAMES("SELECT name FROM `punishmanager_players`");
    private final String query;

    SqlQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        if (!PunishManager.getInstance().getConfigManager().getConfig().getBoolean("mysql.enable")) {
            return query.replace("`", "");
        }
        return query;
    }
}
