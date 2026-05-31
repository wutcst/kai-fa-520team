package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Room;

import java.util.Stack;

/**
 * 返回命令处理器。
 * 处理"back"命令，返回上一个房间。
 */
public class BackCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        Room previousRoom = game.goBack();
        if (previousRoom == null) {
            System.out.println("没有可以返回的房间！");
            return false;
        }

        System.out.println("返回到上一个房间...");
        System.out.println(previousRoom.getLongDescription());
        return false;
    }
}