package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;

/**
 * 帮助命令处理器。
 * 处理"help"命令，显示游戏帮助信息。
 */
public class HelpCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        System.out.println("=== 祖尔世界冒险游戏 ===");
        System.out.println("你迷失在大学里。你孤身一人。你在大学里四处游荡。");
        System.out.println();
        game.getParser().showCommands();
        System.out.println("祝你游戏愉快！");
        return false;
    }
}