// CommandHandler.java
package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;

/**
 * 命令处理器接口，所有具体命令都要实现此接口
 */
public interface CommandHandler {
    /**
     * 执行命令
     * @param game 游戏实例
     * @param command 命令对象
     * @return 是否需要结束游戏
     */
    boolean execute(Game game, Command command);
}


