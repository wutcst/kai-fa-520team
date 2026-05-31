package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.Command.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
    private boolean guiMode;

    public Game() {
        createRooms();
        parser = new Parser();
        roomHistory = new Stack<>();
        player = new Player("冒险者", currentRoom);
        initializeCommandHandlers();
    }

    /**
     * 设置是否运行在 GUI 模式。
     * GUI 模式下，某些命令（例如 quit）不会读取控制台输入。
     */
    public void setGuiMode(boolean guiMode) {
        this.guiMode = guiMode;
    }

    /**
     * 判断当前是否运行在 GUI 模式。
     */
    public boolean isGuiMode() {
        return guiMode;
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
        outside = new Room("大学主入口外");
        theater = new Room("讲堂内");
        pub = new Room("校园酒吧");
        lab = new Room("计算机实验室");
        office = new Room("教务办公室");

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
        System.out.println("输入 '帮助' 或 'help' 获取帮助信息（支持中英文指令）。");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
        System.out.println();
        System.out.println("提示：可使用 '查看' 或 'look' 查看房间详情；使用 '拾取 <物品>' 或 'take <item>' 拾取物品。");
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

    /**
     * 代表一次命令执行的结果，供 GUI 使用。
     */
    public static class ExecutionResult {
        private final boolean finished;
        private final String output;
        private final Room previousRoom;
        private final Room currentRoom;

        public ExecutionResult(boolean finished, String output, Room previousRoom, Room currentRoom) {
            this.finished = finished;
            this.output = output;
            this.previousRoom = previousRoom;
            this.currentRoom = currentRoom;
        }

        public boolean isFinished() {
            return finished;
        }

        public String getOutput() {
            return output;
        }

        public Room getPreviousRoom() {
            return previousRoom;
        }

        public Room getCurrentRoom() {
            return currentRoom;
        }

        public boolean isRoomChanged() {
            return previousRoom != currentRoom;
        }
    }

    /**
     * 执行一行命令文本，并捕获命令输出。
     * @param inputLine 用户输入
     * @return 命令执行结果
     */
    public ExecutionResult executeCommandLine(String inputLine) {
        Command command = parser.parseCommandLine(inputLine);
        return executeCommand(command);
    }

    /**
     * 执行一个命令对象，并捕获输出。
     * @param command 命令对象
     * @return 命令执行结果
     */
    public ExecutionResult executeCommand(Command command) {
        Room previousRoom = currentRoom;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream capture;
        try {
            capture = new PrintStream(buffer, true, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 is not supported", e);
        }

        boolean finished;
        try {
            System.setOut(capture);
            finished = processCommand(command);
        } finally {
            capture.flush();
            System.setOut(originalOut);
        }

        String output = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        return new ExecutionResult(finished, output, previousRoom, currentRoom);
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
        setCurrentRoom(room, true);
    }

    /**
     * 设置当前房间，可选择是否将当前房间记录到历史记录中。
     * 当从历史返回时应传入 recordHistory=false，避免在历史中再次推入当前房间从而导致在两个房间间来回切换。
     * @param room 要设置的新房间
     * @param recordHistory 是否记录当前房间到历史
     */
    public void setCurrentRoom(Room room, boolean recordHistory) {
        if (recordHistory && currentRoom != null && !room.equals(currentRoom)) {
            roomHistory.push(currentRoom);
        }
        currentRoom = room;
        player.setCurrentRoom(room);
    }

    /**
     * 清空房间历史记录。
     * 一般在载入存档后调用，避免旧会话的返回路径干扰当前会话。
     */
    public void clearRoomHistory() {
        roomHistory.clear();
    }

    /**
     * 返回到上一个房间，并保持历史栈按层逐步回退。
     * @return 返回的房间；如果没有历史则返回 null
     */
    public Room goBack() {
        if (roomHistory.isEmpty()) {
            return null;
        }
        Room previousRoom = roomHistory.pop();
        setCurrentRoom(previousRoom, false);
        return previousRoom;
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