package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;

/**
 * 退出命令处理器。
 * 处理"quit"命令，退出游戏。
 */
public class QuitCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        if (command.hasSecondWord()) {
            System.out.println("退出命令不需要参数。");
            System.out.println("请输入 'quit' 退出游戏");
            return false;
        }

        if (game.isGuiMode()) {
            System.out.println("感谢游玩祖尔世界！");
            return true;
        }

        System.out.println("确定要退出游戏吗？(yes/no)");
        System.out.print("> ");
        String confirmation = game.getParser().getReader().nextLine().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            System.out.println("感谢游玩祖尔世界！");
            return true;
        } else {
            System.out.println("继续游戏...");
            return false;
        }
    }
}