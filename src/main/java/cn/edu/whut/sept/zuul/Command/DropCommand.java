package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Item;
import cn.edu.whut.sept.zuul.Player;

/**
 * 丢弃命令处理器。
 * 处理"drop"命令，丢弃背包中的物品。
 */
public class DropCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Drop what?");
            System.out.println("请指定要丢弃的物品，如: drop key");
            System.out.println("你的物品:");
            game.getPlayer().showInventory();
            return false;
        }

        String itemName = command.getSecondWord();
        Player player = game.getPlayer();

        // 检查玩家是否有该物品
        if (!player.hasItem(itemName)) {
            System.out.println("你没有 '" + itemName + "' 这个物品！");
            player.showInventory();
            return false;
        }

        // 丢弃物品
        Item droppedItem = player.dropItem(itemName);
        if (droppedItem != null) {
            // 将物品放回房间
            game.getCurrentRoom().addItem(droppedItem);
            System.out.println(itemName + " 已放回房间");
        }

        return false;
    }
}