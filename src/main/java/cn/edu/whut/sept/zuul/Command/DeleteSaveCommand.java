package cn.edu.whut.sept.zuul.Command;

import cn.edu.whut.sept.zuul.DBUtil;

public class DeleteSaveCommand implements CommandHandler {
    @Override
    public boolean execute(cn.edu.whut.sept.zuul.Game game, Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("使用: delete <id> 来删除指定存档");
            return false;
        }
        String idStr = command.getSecondWord();
        try {
            int id = Integer.parseInt(idStr);
            boolean ok = DBUtil.deleteById(id);
            if (ok) {
                System.out.println("已删除存档 id=" + id);
            } else {
                System.out.println("未找到 id=" + id + " 的存档。");
            }
        } catch (NumberFormatException nfe) {
            System.out.println("id 必须为数字。");
        } catch (Exception e) {
            System.out.println("删除存档失败: " + e.getMessage());
        }
        return false;
    }
}
