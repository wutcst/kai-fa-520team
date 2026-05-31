package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.Command.Command;
import cn.edu.whut.sept.zuul.Command.CommandWords;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 命令解析器。
 * 负责从用户输入中解析命令，将用户输入的文本转换为Command对象。
 */
public class Parser {
    private static final Map<String, String> COMMAND_ALIASES = new HashMap<>();
    private static final Map<String, String> DIRECTION_ALIASES = new HashMap<>();
    private static final Map<String, String> ITEM_ALIASES = new HashMap<>();

    static {
        // 命令别名（中文 -> 英文内部命令）
        COMMAND_ALIASES.put("go", "go");
        COMMAND_ALIASES.put("走", "go");
        COMMAND_ALIASES.put("前进", "go");
        COMMAND_ALIASES.put("移动", "go");
        COMMAND_ALIASES.put("去", "go");

        COMMAND_ALIASES.put("help", "help");
        COMMAND_ALIASES.put("帮助", "help");

        COMMAND_ALIASES.put("quit", "quit");
        COMMAND_ALIASES.put("退出", "quit");
        COMMAND_ALIASES.put("离开", "quit");

        COMMAND_ALIASES.put("look", "look");
        COMMAND_ALIASES.put("查看", "look");
        COMMAND_ALIASES.put("观察", "look");

        COMMAND_ALIASES.put("back", "back");
        COMMAND_ALIASES.put("返回", "back");
        COMMAND_ALIASES.put("后退", "back");

        COMMAND_ALIASES.put("take", "take");
        COMMAND_ALIASES.put("拿", "take");
        COMMAND_ALIASES.put("拿起", "take");
        COMMAND_ALIASES.put("拾取", "take");
        COMMAND_ALIASES.put("获取", "take");

        COMMAND_ALIASES.put("drop", "drop");
        COMMAND_ALIASES.put("丢弃", "drop");
        COMMAND_ALIASES.put("放下", "drop");

        COMMAND_ALIASES.put("items", "items");
        COMMAND_ALIASES.put("背包", "items");
        COMMAND_ALIASES.put("物品", "items");

        COMMAND_ALIASES.put("eat", "eat");
        COMMAND_ALIASES.put("吃", "eat");

        COMMAND_ALIASES.put("save", "save");
        COMMAND_ALIASES.put("保存", "save");

        COMMAND_ALIASES.put("load", "load");
        COMMAND_ALIASES.put("载入", "load");
        COMMAND_ALIASES.put("读取", "load");
        COMMAND_ALIASES.put("加载", "load");

        COMMAND_ALIASES.put("saves", "saves");
        COMMAND_ALIASES.put("存档", "saves");
        COMMAND_ALIASES.put("存档列表", "saves");
        COMMAND_ALIASES.put("列表", "saves");

        COMMAND_ALIASES.put("delete", "delete");
        COMMAND_ALIASES.put("删除", "delete");

        // 方向别名
        DIRECTION_ALIASES.put("north", "north");
        DIRECTION_ALIASES.put("south", "south");
        DIRECTION_ALIASES.put("east", "east");
        DIRECTION_ALIASES.put("west", "west");
        DIRECTION_ALIASES.put("up", "up");
        DIRECTION_ALIASES.put("down", "down");
        DIRECTION_ALIASES.put("北", "north");
        DIRECTION_ALIASES.put("南", "south");
        DIRECTION_ALIASES.put("东", "east");
        DIRECTION_ALIASES.put("西", "west");
        DIRECTION_ALIASES.put("上", "up");
        DIRECTION_ALIASES.put("下", "down");

        // 物品别名
        ITEM_ALIASES.put("key", "key");
        ITEM_ALIASES.put("map", "map");
        ITEM_ALIASES.put("notebook", "notebook");
        ITEM_ALIASES.put("pen", "pen");
        ITEM_ALIASES.put("beer", "beer");
        ITEM_ALIASES.put("coin", "coin");
        ITEM_ALIASES.put("book", "book");
        ITEM_ALIASES.put("laptop", "laptop");
        ITEM_ALIASES.put("labtop", "laptop");
        ITEM_ALIASES.put("coffee", "coffee");
        ITEM_ALIASES.put("paper", "paper");
        ITEM_ALIASES.put("cookie", "cookie");
        ITEM_ALIASES.put("钥匙", "key");
        ITEM_ALIASES.put("地图", "map");
        ITEM_ALIASES.put("笔记本", "notebook");
        ITEM_ALIASES.put("钢笔", "pen");
        ITEM_ALIASES.put("啤酒", "beer");
        ITEM_ALIASES.put("金币", "coin");
        ITEM_ALIASES.put("书", "book");
        ITEM_ALIASES.put("笔记本电脑", "laptop");
        ITEM_ALIASES.put("电脑", "laptop");
        ITEM_ALIASES.put("咖啡", "coffee");
        ITEM_ALIASES.put("文件", "paper");
        ITEM_ALIASES.put("纸", "paper");
        ITEM_ALIASES.put("饼干", "cookie");
        ITEM_ALIASES.put("魔法饼干", "cookie");
    }

    private CommandWords commands;
    private Scanner reader;

    /**
     * 创建解析器对象。
     * 初始化命令字典和用户输入读取器。
     */
    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    /**
     * 获取用户输入的命令。
     * @return 解析后的Command对象
     */
    public Command getCommand() {
        System.out.print("> ");
        return parseCommandLine(reader.nextLine());
    }

    /**
     * 将一行文本解析为命令对象，供 GUI 或其他非控制台入口调用。
     * @param inputLine 用户输入文本
     * @return 解析后的Command对象
     */
    public Command parseCommandLine(String inputLine) {
        String word1 = null;
        String word2 = null;

        if (inputLine != null) {
            Scanner tokenizer = new Scanner(inputLine);
            if (tokenizer.hasNext()) {
                word1 = normalizeCommand(tokenizer.next().toLowerCase());
                if (tokenizer.hasNext()) {
                    StringBuilder secondBuilder = new StringBuilder(tokenizer.next().toLowerCase());
                    while (tokenizer.hasNext()) {
                        secondBuilder.append(" ").append(tokenizer.next().toLowerCase());
                    }
                    word2 = normalizeSecondWord(word1, secondBuilder.toString());
                }
            }
            tokenizer.close();
        }

        if (commands.isCommand(word1)) {
            return new Command(word1, word2);
        }
        return new Command(null, word2);
    }

    private String normalizeCommand(String commandWord) {
        if (commandWord == null) {
            return null;
        }
        String mapped = COMMAND_ALIASES.get(commandWord);
        return mapped != null ? mapped : commandWord;
    }

    private String normalizeSecondWord(String commandWord, String secondWord) {
        if (secondWord == null) {
            return null;
        }
        String cleaned = secondWord.trim();
        if (cleaned.isEmpty()) {
            return null;
        }

        if ("go".equals(commandWord)) {
            String direction = DIRECTION_ALIASES.get(cleaned);
            return direction != null ? direction : cleaned;
        }

        if ("take".equals(commandWord) || "drop".equals(commandWord) || "eat".equals(commandWord)) {
            String item = ITEM_ALIASES.get(cleaned);
            return item != null ? item : cleaned;
        }

        return cleaned;
    }

    /**
     * 显示所有可用的命令。
     */
    public void showCommands() {
        commands.showAll();
    }

    /**
     * 获取输入读取器。
     * @return Scanner对象
     */
    public Scanner getReader() {
        return reader;
    }
}