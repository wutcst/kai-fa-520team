package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;

/**
 * 物品命令处理器。
 * 处理"items"命令，显示玩家背包内容。
 */
public class ItemsCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        game.getPlayer().showInventory();
        return false;
    }
}