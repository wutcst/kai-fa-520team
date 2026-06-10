package cn.edu.whut.sept.zuul.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 该类维护游戏中所有有效命令的列表。
 * 用于验证用户输入的命令是否有效，并提供显示所有命令的功能。
 *
 * @version 3.0 (添加命令描述)
 */
public class CommandWords {
    /**
     * 命令及其描述的映射。
     * 用于显示帮助信息时提供每个命令的说明。
     */
    private Map<String, String> commands;

    /**
     * 创建命令字典对象。
     * 初始化所有有效命令及其描述。
     */
    public CommandWords() {
        commands = new HashMap<>();

        // 移动命令
        commands.put("go", "移动到指定方向的房间");
        commands.put("back", "返回上一个房间");

        // 查看命令
        commands.put("look", "查看当前房间的详细信息");
        commands.put("items", "查看背包中的物品");

        // 物品操作命令
        commands.put("take", "拾取房间中的物品");
        commands.put("drop", "丢弃背包中的物品");
        commands.put("eat", "食用物品（如魔法饼干可增加负重）");

        // 存档命令
        commands.put("save", "保存当前游戏状态到数据库");
        commands.put("load", "载入存档（输入 load 或 load <存档ID>）");
        commands.put("saves", "列出所有已保存的游戏存档");
        commands.put("delete", "删除指定ID的存档");

        // 系统命令
        commands.put("help", "显示帮助信息");
        commands.put("quit", "退出游戏");
    }

    /**
     * 检查给定的字符串是否为有效命令。
     *
     * @param aString 要检查的字符串
     * @return 如果字符串是有效命令则返回true，否则返回false
     */
    public boolean isCommand(String aString) {
        return commands.containsKey(aString);
    }

    /**
     * 获取所有有效命令。
     *
     * @return 命令及其描述的映射
     */
    public Set<String> getCommands() {
        return commands.keySet();
    }

    /**
     * 获取命令的描述。
     *
     * @param command 命令名称
     * @return 命令描述，如果命令不存在则返回null
     */
    public String getDescription(String command) {
        return commands.get(command);
    }

    /**
     * 显示所有有效命令及其描述。
     * 按类别分组显示，便于玩家理解。
     */
    public void showAll() {
        System.out.println("=== 可用命令列表 ===");
        System.out.println();

        // 移动命令
        System.out.println("【移动命令】");
        printCommand("go <方向>", "移动到指定方向的房间");
        printCommand("back", "返回上一个房间");
        System.out.println();

        // 查看命令
        System.out.println("【查看命令】");
        printCommand("look", "查看当前房间的详细信息");
        printCommand("items", "查看背包中的物品");
        System.out.println();

        // 物品操作命令
        System.out.println("【物品操作】");
        printCommand("take <物品名>", "拾取房间中的物品");
        printCommand("drop <物品名>", "丢弃背包中的物品");
        printCommand("eat <物品名>", "食用物品（如魔法饼干可增加负重）");
        System.out.println();

        // 存档命令
        System.out.println("【存档管理】");
        printCommand("save", "保存当前游戏状态到数据库");
        printCommand("load", "载入存档（输入 load 或 load <存档ID>）");
        printCommand("saves", "列出所有已保存的游戏存档");
        printCommand("delete <ID>", "删除指定ID的存档");
        System.out.println();

        // 系统命令
        System.out.println("【系统命令】");
        printCommand("help", "显示帮助信息");
        printCommand("quit", "退出游戏");
    }

    /**
     * 打印单个命令及其描述，格式化对齐。
     *
     * @param command     命令名称（可带参数）
     * @param description 命令描述
     */
    private void printCommand(String command, String description) {
        System.out.printf("  %-20s - %s%n", command, description);
    }
}