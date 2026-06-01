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

        // 尝试拾取物品：先判断是否超重，再调用模型方法更新状态
        if (player.getCurrentWeight() + itemToTake.getWeight() > player.getMaxWeight()) {
            System.out.println("拿不动了！当前负重：" + String.format("%.1f", player.getCurrentWeight()) +
                    "/" + String.format("%.1f", player.getMaxWeight()) +
                    "，物品重量：" + String.format("%.1f", itemToTake.getWeight()));
        } else {
            if (player.takeItem(itemToTake)) {
                currentRoom.getItems().remove(itemToTake);
                System.out.println("拾取了：" + itemToTake.getName());
                System.out.println("当前负重：" + String.format("%.1f", player.getCurrentWeight()) +
                        "/" + String.format("%.1f", player.getMaxWeight()));
            } else {
                System.out.println("无法拾取物品：" + itemToTake.getName());
            }
        }

        return false;
    }
}