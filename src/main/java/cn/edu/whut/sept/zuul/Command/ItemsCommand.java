package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Item;
import cn.edu.whut.sept.zuul.Room;

/**
 * 物品命令处理器。
 * 处理"items"命令，显示当前房间内所有物件及玩家背包内容。
 */
public class ItemsCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        // 显示当前房间内的物品
        Room currentRoom = game.getCurrentRoom();
        System.out.println("=== 当前房间物品 ===");
        if (currentRoom.getItems().isEmpty()) {
            System.out.println("房间里没有物品");
        } else {
            double roomTotalWeight = 0;
            for (Item item : currentRoom.getItems()) {
                System.out.println("  - " + item);
                roomTotalWeight += item.getWeight();
            }
            System.out.println("房间物品总重量：" + String.format("%.1f", roomTotalWeight) + "kg");
        }

        System.out.println();

        // 显示玩家背包物品
        game.getPlayer().showInventory();
        return false;
    }
}