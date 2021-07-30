package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mehmet_27.punishmanager.Punishment.PunishType;
import static com.mehmet_27.punishmanager.Punishment.PunishType.BAN;

public class PunishmentManager {

    private final Connection connection;

    public PunishmentManager(PunishManager plugin) {
        connection = plugin.getMySQLManager().getConnection();
    }

    public void AddPunish(Punishment punishment) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO `punishmanager_punishments` (`name`, `uuid`, `reason`, `operator`, `type`, `start`, `end`) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1, punishment.getPlayerName());
            ps.setString(2, punishment.getUuid());
            ps.setString(3, punishment.getReason());
            ps.setString(4, punishment.getOperator());
            ps.setString(5, punishment.getPunishType().toString());
            ps.setString(6, String.valueOf(punishment.getStart()));
            ps.setString(7, String.valueOf(punishment.getEnd()));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unPunishPlayer(Punishment punishment) {
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement("DELETE FROM `punishmanager_punishments` WHERE name = ? and type = ?");
            ps.setString(1, punishment.getPlayerName());
            ps.setString(2, punishment.getPunishType().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAllPunishes(Punishment punishment) {
        try {
            PreparedStatement ps;
            ps = connection.prepareStatement("DELETE FROM `punishmanager_punishments` WHERE name = ?");
            ps.setString(1, punishment.getPlayerName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerIsBanned(String wantedPlayer) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE `name` = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getString("type").contains(BAN.name());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Punishment getPunishment(String wantedPlayer) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE name = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                String playerName = result.getString("name");
                String uuid = result.getString("uuid");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                long start = result.getLong("start");
                long end = result.getLong("end");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                return new Punishment(playerName, uuid, punishType, reason, operator, start, end);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Punishment getBan(String wantedPlayer) {
        return getPunishment(wantedPlayer, "BAN");
    }
    public Punishment getMute(String wantedPlayer) {
        return getPunishment(wantedPlayer, "MUTE");
    }

    public Punishment getPunishment(String wantedPlayer, String type) {
        List<Punishment> punishments = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments` WHERE name = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("name");
                String uuid = result.getString("uuid");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                long start = result.getLong("start");
                long end = result.getLong("end");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                punishments.add(new Punishment(playerName, uuid, punishType, reason, operator, start, end));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Punishment punishment : punishments) {
            if (punishment.getPunishType().toString().contains(type.toUpperCase())) {
                return punishment;
            }
        }
        return null;
    }
    public String getOfflineIp(String wantedPlayer) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_players` WHERE `name` = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getString("ip");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isLoggedServer(String wantedPlayer) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_players` WHERE name = ?");
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void removeAllOutdatedPunishes() {
        try {
            int deleted = 0;
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `punishmanager_punishments`");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("name");
                Punishment punishment = getPunishment(playerName);
                if (punishment.getPunishType().isTemp() && !punishment.isStillPunished()) {
                    unPunishPlayer(punishment);
                    deleted++;
                }
            }
            PunishManager.getInstance().getLogger().info(deleted + " expiring punish deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
