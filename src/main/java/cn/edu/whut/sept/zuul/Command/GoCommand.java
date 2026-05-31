package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Room;

/**
 * 移动命令处理器。
 * 处理"go"命令，让玩家移动到指定方向的房间。
 */
public class GoCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            System.out.println("请指定方向，如: go east, go west, go north, go south");
            return false;
        }

        String direction = command.getSecondWord();
        Room nextRoom = game.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("这个方向没有门！");
            System.out.println("可用的方向: " + getAvailableExits(game));
        } else {
            game.setCurrentRoom(nextRoom);
            System.out.println(nextRoom.getLongDescription());
        }
        return false;
    }

    /**
     * 获取当前房间的可用出口。
     * @param game 游戏实例
     * @return 可用出口字符串
     */
    private String getAvailableExits(Game game) {
        StringBuilder exits = new StringBuilder();
        String[] directions = {"north", "south", "east", "west", "up", "down"};

        for (String dir : directions) {
            if (game.getCurrentRoom().getExit(dir) != null) {
                if (exits.length() > 0) exits.append(", ");
                exits.append(dir);
            }
        }

        return exits.length() > 0 ? exits.toString() : "没有可用出口";
    }
}