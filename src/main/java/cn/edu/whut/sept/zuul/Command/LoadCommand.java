package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.*;

import java.util.Map;

/**
 * 载入最近一次存档命令。
 * 用法: load
 */
public class LoadCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        try {
            Map<String, String> m;
            if (command.hasSecondWord()) {
                // 通过 id 载入
                String idStr = command.getSecondWord();
                try {
                    int id = Integer.parseInt(idStr);
                    m = DBUtil.loadById(id);
                    if (m == null) {
                        System.out.println("未找到 id=" + id + " 的存档。");
                        return false;
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("load 命令需要一个数字 id，示例: load 3 或不带参数载入最近存档");
                    return false;
                }
            } else {
                m = DBUtil.loadLatest();
                if (m == null) {
                    System.out.println("没有可用的存档。");
                    return false;
                }
            }

            String roomDesc = m.get("current_room");
            Room room = game.findRoomByDescription(roomDesc);
            if (room != null) {
                game.setCurrentRoom(room);
            } else {
                System.out.println("无法找到原先的房间，保留当前房间。");
            }

            Player player = game.getPlayer();
            player.setMaxWeight(Double.parseDouble(m.get("player_max_weight")));

            // 清空并重建背包
            player.getInventory().clear();
            String inv = m.get("inventory");
            if (inv != null && !inv.isEmpty()) {
                String[] items = inv.split(";;");
                for (String s : items) {
                    String[] parts = s.split("\\|\\|");
                    if (parts.length == 3) {
                        String name = parts[0];
                        String desc = parts[1];
                        double w = Double.parseDouble(parts[2]);
                        player.getInventory().add(new Item(name, desc, w));
                    }
                }
                // 重新计算当前负重
                double cw = player.getInventory().stream().mapToDouble(Item::getWeight).sum();
                player.setCurrentWeight(cw);
            }

            // 恢复地图物品状态
            String mapState = m.get("map_state");
            if (mapState != null && !mapState.isEmpty()) {
                // 格式: roomDesc:::item1name||desc||weight;;item2...###room2:::
                String[] roomEntries = mapState.split("###");
                for (String re : roomEntries) {
                    String[] parts = re.split(":::");
                    if (parts.length >= 1) {
                        String desc = parts[0];
                        Room r = game.findRoomByDescription(desc);
                        if (r == null) continue;
                        // 清空房间现有物品
                        r.getItems().clear();
                        if (parts.length == 2 && parts[1] != null && !parts[1].isEmpty()) {
                            String[] its = parts[1].split(";;");
                            for (String is : its) {
                                String[] p = is.split("\\|\\|");
                                if (p.length == 3) {
                                    String in = p[0];
                                    String id = p[1];
                                    double iw = Double.parseDouble(p[2]);
                                    r.getItems().add(new Item(in, id, iw));
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("载入成功。");
        } catch (Exception e) {
            System.out.println("载入失败: " + e.getMessage());
        }
        return false;
    }
}
