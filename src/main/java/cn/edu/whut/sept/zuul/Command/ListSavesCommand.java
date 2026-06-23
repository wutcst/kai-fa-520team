package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.DBUtil;

import java.util.List;
import java.util.Map;

public class ListSavesCommand implements CommandHandler {
    @Override
    public boolean execute(cn.edu.whut.sept.zuul.Game game, Command command) {
        try {
            List<Map<String, String>> list = DBUtil.listSaves();
            if (list.isEmpty()) {
                System.out.println("没有存档。");
                return false;
            }
            System.out.println("可用存档（按时间倒序）：");
            System.out.printf("%-4s %-20s %-25s %s\n", "id", "name", "saved_at", "current_room");
            for (Map<String, String> m : list) {
                System.out.printf("%-4s %-20s %-25s %s\n", m.get("id"), m.get("name"), m.get("saved_at"), m.get("current_room"));
            }
        } catch (Exception e) {
            System.out.println("查询存档失败: " + e.getMessage());
        }
        return false;
    }
}
