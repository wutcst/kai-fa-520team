package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Player;

/**
 * 吃命令处理器。
 * 处理"eat"命令，吃魔法饼干增加负重能力。
 */
public class EatCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("要吃什么？");
            System.out.println("请指定要吃的物品，如: eat cookie");
            return false;
        }

        String itemName = command.getSecondWord();

        if (!itemName.equalsIgnoreCase("cookie")) {
            System.out.println("你只能吃魔法饼干！");
            System.out.println("使用 'eat cookie' 吃魔法饼干增加负重能力");
            return false;
        }

        Player player = game.getPlayer();

        // 检查是否有魔法饼干
        if (!player.hasItem("cookie")) {
            System.out.println("你没有魔法饼干！");
            System.out.println("可以在酒吧(pub)找到魔法饼干");
            return false;
        }

        player.eatCookie();
        return false;
    }
}