package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Player;
import cn.edu.whut.sept.zuul.Room;

/**
 * 查看命令处理器。
 * 处理"look"命令，查看当前房间的详细信息。
 */
public class LookCommand implements CommandHandler {
    @Override
    public boolean execute(Game game, Command command) {
        Room currentRoom = game.getCurrentRoom();

        System.out.println("=== 房间详情 ===");
        System.out.println(currentRoom.getLongDescription());

        // 简化负重信息显示
        Player player = game.getPlayer();
        double availableWeight = player.getMaxWeight() - player.getCurrentWeight();
        System.out.println("你的可用负重: " + String.format("%.1f", availableWeight) + "kg");

        return false;
    }
}