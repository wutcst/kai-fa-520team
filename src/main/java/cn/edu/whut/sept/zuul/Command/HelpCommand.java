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
        System.out.println("游戏命令:");
        game.getParser().showCommands();
        System.out.println();
        System.out.println("游戏提示:");
        System.out.println("- 使用 'go <方向>' 移动到其他房间");
        System.out.println("- 使用 'look' 查看当前房间详情");
        System.out.println("- 使用 'take <物品>' 拾取物品");
        System.out.println("- 使用 'drop <物品>' 丢弃物品");
        System.out.println("- 使用 'items' 查看背包");
        System.out.println("- 使用 'back' 返回上一个房间");
        System.out.println("- 使用 'eat cookie' 吃魔法饼干增加负重");
        System.out.println("- 使用 'quit' 退出游戏");
        return false;
    }
}