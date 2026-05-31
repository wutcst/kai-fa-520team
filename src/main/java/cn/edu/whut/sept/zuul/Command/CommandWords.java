package cn.edu.whut.sept.zuul.Command;

import java.util.HashSet;
import java.util.Set;

/**
 * 该类维护游戏中所有有效命令的列表。
 * 用于验证用户输入的命令是否有效，并提供显示所有命令的功能。
 *
 * @version 2.0 (添加新命令)
 */
public class CommandWords {
    /**
     * 有效命令集合。
     * 使用HashSet提高查找效率。
     */
    private Set<String> validCommands;

    /**
     * 创建命令字典对象。
     * 初始化所有有效命令。
     */
    public CommandWords() {
        validCommands = new HashSet<>();

        // 原有命令
        validCommands.add("go");   // 移动到指定方向的房间
        validCommands.add("quit"); // 退出游戏
        validCommands.add("help"); // 显示帮助信息

        // 新增命令
        validCommands.add("look");  // 查看房间详情
        validCommands.add("back");  // 返回上一个房间
        validCommands.add("take");  // 拾取物品
        validCommands.add("drop");  // 丢弃物品
        validCommands.add("items"); // 查看背包
        validCommands.add("eat");   // 吃魔法饼干
        // 存档/载入命令
        validCommands.add("save");  // 保存游戏状态到数据库
        validCommands.add("load");  // 载入最近一次存档（或 load <id>）
        validCommands.add("saves"); // 列出所有存档
        validCommands.add("delete"); // 删除指定 id 的存档
    }

    /**
     * 检查给定的字符串是否为有效命令。
     * @param aString 要检查的字符串
     * @return 如果字符串是有效命令则返回true，否则返回false
     */
    public boolean isCommand(String aString) {
        return validCommands.contains(aString);
    }

    /**
     * 显示所有有效命令。
     * 将有效命令集合中的所有命令以空格分隔的形式打印到控制台。
     */
    public void showAll() {
        System.out.println("可用命令:");
        int count = 0;
        for (String command : validCommands) {
            System.out.print(command + "  ");
            count++;
            if (count % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }
}