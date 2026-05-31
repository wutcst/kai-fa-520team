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
        System.out.println("你迷失在大学里，孤身一人，在校园中探险。支持中英文指令输入。");
        System.out.println();
        System.out.println("常用命令示例：");
        System.out.println("- 移动：'前进 东' 或 'go east'");
        System.out.println("- 查看房间：'查看' 或 'look'");
        System.out.println("- 拾取物品：'拾取 钥匙' 或 'take key'");
        System.out.println("- 丢弃物品：'丢弃 钥匙' 或 'drop key'");
        System.out.println("- 查看背包：'背包' 或 'items'");
        System.out.println("- 返回上一个房间：'返回' 或 'back'");
        System.out.println("- 吃饼干：'吃 饼干' 或 'eat cookie'");
        System.out.println("- 存档/载入：'保存'/'载入' 或 'save'/'load'");
        System.out.println("- 退出游戏：'退出' 或 'quit'");
        game.getParser().showCommands();
        System.out.println("祝你游戏愉快！");
        return false;
    }
}