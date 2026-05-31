package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存档命令，保存玩家当前状态到数据库。
 * 用法: save
 */
public class SaveCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        Player player = game.getPlayer();
        List<Item> inv = player.getInventory();
        // 使用简单的分隔格式保存每个物品: 名称||描述||重量  使用 ;; 分隔不同物品
        String invStr = inv.stream()
                .map(i -> i.getName() + "||" + i.getDescription() + "||" + i.getWeight())
                .collect(Collectors.joining(";;"));

        // 序列化地图上每个房间的物品: 房间描述:::item1name||desc||weight;;item2...  多个房间用 ### 分隔
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Room> e : game.getRoomsMap().entrySet()) {
            String roomDesc = e.getKey();
            List<Item> items = e.getValue().getItems();
            sb.append(roomDesc).append(":::");
            if (!items.isEmpty()) {
                String itemsStr = items.stream()
                        .map(i -> i.getName() + "||" + i.getDescription() + "||" + i.getWeight())
                        .collect(Collectors.joining(";;"));
                sb.append(itemsStr);
            }
            sb.append("###");
        }
        String mapState = sb.length() > 0 ? sb.substring(0, sb.length() - 3) : "";

        try {
            if (command.hasSecondWord()) {
                // 允许 save <id> 覆盖指定存档
                String idStr = command.getSecondWord();
                try {
                    int id = Integer.parseInt(idStr);
                    DBUtil.saveGameWithId(id, player.getName(), game.getCurrentRoom().getShortDescription(), player.getMaxWeight(), player.getCurrentWeight(), invStr, mapState);
                    System.out.println("已保存并覆盖存档 id=" + id);
                } catch (NumberFormatException nfe) {
                    System.out.println("save 命令的参数应为数字 id，例如: save 3 。若不带参数则创建新存档。");
                }
            } else {
                DBUtil.saveGame(player.getName(), game.getCurrentRoom().getShortDescription(), player.getMaxWeight(), player.getCurrentWeight(), invStr, mapState);
                System.out.println("存档成功。可以使用 'saves' 查看存档，'load <id>' 载入指定存档。");
            }
        } catch (Exception e) {
            System.out.println("存档失败: " + e.getMessage());
        }
        return false;
    }
}
