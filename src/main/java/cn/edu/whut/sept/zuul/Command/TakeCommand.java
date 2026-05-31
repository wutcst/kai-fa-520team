package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Item;
import cn.edu.whut.sept.zuul.Player;
import cn.edu.whut.sept.zuul.Room;

/**
 * 拾取命令处理器。
 * 处理"take"命令，拾取房间内的物品。
 */
public class TakeCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("要拾取什么？");
            System.out.println("请指定要拾取的物品，如: take key");
            return false;
        }

        String itemName = command.getSecondWord();
        Room currentRoom = game.getCurrentRoom();
        Player player = game.getPlayer();

        // 检查房间是否有该物品
        Item itemToTake = null;
        for (Item item : currentRoom.getItems()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                itemToTake = item;
                break;
            }
        }

        if (itemToTake == null) {
            System.out.println("这里没有这个物品！");
            System.out.println("房间内的物品：");
            if (currentRoom.getItems().isEmpty()) {
                System.out.println("  没有物品");
            } else {
                for (Item item : currentRoom.getItems()) {
                    System.out.println("  - " + item);
                }
            }
            return false;
        }

        // 尝试拾取物品
        if (player.takeItem(itemToTake)) {
            // 如果拾取成功，从房间移除物品
            currentRoom.getItems().remove(itemToTake);
        }
        // 如果拾取失败（比如超重），takeItem方法中已经给出提示，物品还在房间

        return false;
    }
}