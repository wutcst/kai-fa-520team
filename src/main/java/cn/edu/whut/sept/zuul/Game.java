package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.Command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

/**
 * "World-of-Zuul"应用程序的主类。
 * 创建所有房间，并将它们连接成迷宫；创建解析器接收用户输入。
 *
 * @version 3.0 (完整功能版)
 */
public class Game {
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> roomHistory;
    private Map<String, CommandHandler> commandHandlers;
    private Player player;
    private Map<String, Room> roomsMap; // 房间描述 -> 房间对象 的映射，便于存档/载入

    public Game() {
        createRooms();
        parser = new Parser();
        roomHistory = new Stack<>();
        player = new Player("冒险者", currentRoom);
        initializeCommandHandlers();
    }

    private void initializeCommandHandlers() {
        commandHandlers = new HashMap<>();
        commandHandlers.put("go", new GoCommand());
        commandHandlers.put("help", new HelpCommand());
        commandHandlers.put("quit", new QuitCommand());
        commandHandlers.put("look", new LookCommand());
        commandHandlers.put("back", new BackCommand());
        commandHandlers.put("take", new TakeCommand());
        commandHandlers.put("drop", new DropCommand());
        commandHandlers.put("items", new ItemsCommand());
        commandHandlers.put("eat", new EatCommand());
        commandHandlers.put("save", new SaveCommand());
        commandHandlers.put("load", new LoadCommand());
        commandHandlers.put("saves", new ListSavesCommand());
        commandHandlers.put("delete", new DeleteSaveCommand());
    }

    private void createRooms() {
        Room outside, theater, pub, lab, office;

        // 创建房间
        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");

        // 为房间添加物品
        outside.getItems().add(new Item("key", "一把生锈的钥匙", 0.1));
        outside.getItems().add(new Item("map", "一张破旧的地图", 0.2));

        theater.getItems().add(new Item("notebook", "一本笔记本", 0.5));
        theater.getItems().add(new Item("pen", "一支钢笔", 0.05));

        pub.getItems().add(new Item("beer", "一杯啤酒", 0.5));
        pub.getItems().add(new Item("coin", "一枚金币", 0.1));

        lab.getItems().add(new Item("book", "计算机科学教材", 1.5));
        lab.getItems().add(new Item("laptop", "笔记本电脑", 2.0));

        office.getItems().add(new Item("coffee", "一杯咖啡", 0.3));
        office.getItems().add(new Item("paper", "一份重要文件", 0.2));

        // 随机将魔法饼干放入某个房间
        Room[] rooms = {outside, theater, pub, lab, office};
        Random random = new Random();
        int randomIndex = random.nextInt(rooms.length);
        rooms[randomIndex].getItems().add(new Item("cookie", "魔法饼干（增加负重能力）", 0.2));

        // 建立房间描述到房间对象的映射，便于存档/载入时查找
        roomsMap = new HashMap<>();
        for (Room r : rooms) {
            roomsMap.put(r.getShortDescription(), r);
        }

        // 初始化房间出口
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theater.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);

        currentRoom = outside;  // 游戏从outside开始
    }

    /**
     * 游戏主控循环，直到用户输入退出命令后结束整个程序。
     */
    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("感谢游玩祖尔世界！再见！");
    }

    /**
     * 向用户输出欢迎信息。
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("=== 欢迎来到祖尔世界！ ===");
        System.out.println("祖尔世界是一个全新的、令人兴奋的冒险游戏。");
        System.out.println("输入 'help' 获取帮助信息。");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
        System.out.println();
        System.out.println("提示：试试 'look' 命令查看房间详情，'take' 命令拾取物品！");
    }

    /**
     * 执行用户输入的游戏指令。
     * @param command 待处理的游戏指令
     * @return 如果执行的是游戏结束指令，则返回true，否则返回false
     */
    private boolean processCommand(Command command) {
        if (command.isUnknown()) {
            System.out.println("我不知道你说的什么意思...");
            System.out.println("输入 'help' 获取可用命令列表");
            return false;
        }

        String commandWord = command.getCommandWord();
        CommandHandler handler = commandHandlers.get(commandWord);

        if (handler != null) {
            return handler.execute(this, command);
        }

        System.out.println("命令未实现: " + commandWord);
        return false;
    }

    // ============ Getter 方法 ============

    /**
     * 获取当前房间。
     * @return 当前房间对象
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * 根据房间描述查找已创建的房间对象（用于载入存档时恢复位置）
     */
    public Room findRoomByDescription(String desc) {
        if (roomsMap == null) return null;
        return roomsMap.get(desc);
    }

    /**
     * 获取房间映射（描述->房间），用于存档/载入
     */
    public Map<String, Room> getRoomsMap() {
        return roomsMap;
    }

    /**
     * 设置当前房间。
     * @param room 要设置的新房间
     */
    public void setCurrentRoom(Room room) {
        if (currentRoom != null && !room.equals(currentRoom)) {
            roomHistory.push(currentRoom);
        }
        currentRoom = room;
        player.setCurrentRoom(room);
    }

    /**
     * 获取解析器。
     * @return 解析器对象
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * 获取房间历史记录。
     * @return 房间历史堆栈
     */
    public Stack<Room> getRoomHistory() {
        return roomHistory;
    }

    /**
     * 获取玩家对象。
     * @return 玩家对象
     */
    public Player getPlayer() {
        return player;
    }
}