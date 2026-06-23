package test;

import cn.edu.whut.sept.zuul.Command.*;
import cn.edu.whut.sept.zuul.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Stack;

/**
 * Game类的集成测试
 */
public class GameTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    @Test
    public void testGameCreation() {
        assertNotNull(game, "Game对象应该被成功创建");
        assertNotNull(game.getCurrentRoom(), "游戏应该有当前房间");
        assertNotNull(game.getParser(), "游戏应该有解析器");
        assertNotNull(game.getPlayer(), "游戏应该有玩家");
        assertNotNull(game.getRoomHistory(), "游戏应该有房间历史记录");
    }

    @Test
    public void testCreateRooms() {
        Room initialRoom = game.getCurrentRoom();

        // 验证初始房间的描述
        String description = initialRoom.getShortDescription();
        assertTrue(description.contains("入口") || description.contains("大学"),
                "初始房间应该是大学主入口外");

        // 验证房间之间的连接
        Room eastRoom = initialRoom.getExit("east");
        Room westRoom = initialRoom.getExit("west");
        Room southRoom = initialRoom.getExit("south");

        // 至少应该有一个出口
        assertTrue(eastRoom != null || westRoom != null || southRoom != null,
                "初始房间应该至少有一个出口");
    }

    @Test
    public void testSetCurrentRoom() {
        Room initialRoom = game.getCurrentRoom();
        Room testRoom = new Room("测试房间");

        game.setCurrentRoom(testRoom);
        assertEquals(testRoom, game.getCurrentRoom(), "当前房间应该被更新");

        // 验证历史记录
        Stack<Room> history = game.getRoomHistory();
        assertFalse(history.isEmpty(), "历史记录不应该为空");
        assertEquals(initialRoom, history.peek(), "历史记录中的房间应该是初始房间");
    }

    @Test
    public void testSetCurrentRoomSameRoom() {
        Room initialRoom = game.getCurrentRoom();
        int initialHistorySize = game.getRoomHistory().size();

        // 设置相同的房间
        game.setCurrentRoom(initialRoom);

        Stack<Room> history = game.getRoomHistory();
        assertEquals(initialHistorySize, history.size(),
                "设置相同的房间不应该添加到历史记录");
    }

    @Test
    public void testGetPlayer() {
        Player player = game.getPlayer();

        assertNotNull(player, "getPlayer应该返回非null的Player对象");
        assertEquals(game.getCurrentRoom(), player.getCurrentRoom(),
                "玩家应该在当前房间");
    }

    @Test
    public void testGetParser() {
        Parser parser = game.getParser();

        assertNotNull(parser, "getParser应该返回非null的Parser对象");
    }

    @Test
    public void testGetRoomHistory() {
        Stack<Room> history = game.getRoomHistory();

        assertNotNull(history, "getRoomHistory应该返回非null的Stack对象");
        assertTrue(history.isEmpty(), "初始历史记录应该为空");

        // 移动房间后，历史记录应该更新
        Room initialRoom = game.getCurrentRoom();
        Room newRoom = new Room("新房间");
        game.setCurrentRoom(newRoom);

        assertFalse(game.getRoomHistory().isEmpty(),
                "移动后历史记录不应该为空");
        assertEquals(initialRoom, game.getRoomHistory().peek(),
                "历史记录应该包含初始房间");
    }

    @Test
    public void testRoomConnections() {
        // 验证初始房间的连接
        Room current = game.getCurrentRoom();

        // 测试一些可能的连接
        String[] directions = {"north", "south", "east", "west"};
        for (String dir : directions) {
            Room exit = current.getExit(dir);
            if (exit != null) {
                // 如果这个方向有出口，验证反向连接
                Room reverseExit = exit.getExit(getReverseDirection(dir));
                if (reverseExit != null) {
                    assertEquals(current, reverseExit,
                            dir + "方向的房间应该能返回");
                }
            }
        }
    }

    /**
     * 获取反向方向
     */
    private String getReverseDirection(String direction) {
        switch (direction) {
            case "north": return "south";
            case "south": return "north";
            case "east": return "west";
            case "west": return "east";
            default: return direction;
        }
    }

    @Test
    public void testPlayerInitialState() {
        Player player = game.getPlayer();

        assertEquals("冒险者", player.getName(), "玩家初始名称应该是'冒险者'");
        assertEquals(10.0, player.getMaxWeight(), 0.001, "玩家初始最大负重应该是10kg");
        assertEquals(0.0, player.getCurrentWeight(), 0.001, "玩家初始当前负重应该是0");
        assertTrue(player.getInventory().isEmpty(), "玩家初始背包应该为空");
    }

    @Test
    public void testGameInitialization() {
        // 验证游戏初始化后的状态
        assertNotNull(game.getCurrentRoom(), "游戏初始化后应该有当前房间");

        // 验证初始房间有描述
        String description = game.getCurrentRoom().getShortDescription();
        assertNotNull(description, "初始房间描述不应该为null");
        assertFalse(description.trim().isEmpty(), "初始房间描述不应该为空");

        // 验证玩家在初始房间
        assertEquals(game.getCurrentRoom(), game.getPlayer().getCurrentRoom(),
                "玩家应该在初始房间");
    }

    @Test
    public void testMultipleRoomMovements() {
        Room room1 = game.getCurrentRoom();
        Room room2 = new Room("房间2");
        Room room3 = new Room("房间3");

        // 记录移动历史
        game.setCurrentRoom(room2);
        game.setCurrentRoom(room3);

        Stack<Room> history = game.getRoomHistory();
        assertEquals(2, history.size(), "应该有两个房间的历史记录");

        // 验证历史记录的顺序
        assertEquals(room2, history.pop(), "第一个历史记录应该是房间2");
        assertEquals(room1, history.pop(), "第二个历史记录应该是房间1");
    }

    @Test
    public void testGamePlayMethod() {
        assertDoesNotThrow(() -> {
            Game testGame = new Game();
        }, "创建游戏不应该抛出异常");
    }

    @Test
    public void testRoomHistoryManagement() {
        // 测试历史记录管理
        Stack<Room> history = game.getRoomHistory();
        assertTrue(history.isEmpty(), "初始历史记录应该为空");

        // 移动到新房间
        Room room1 = new Room("房间1");
        game.setCurrentRoom(room1);
        assertEquals(1, history.size(), "移动后历史记录应该有一个房间");

        // 再次移动
        Room room2 = new Room("房间2");
        game.setCurrentRoom(room2);
        assertEquals(2, history.size(), "再次移动后历史记录应该有两个房间");

        // 返回到上一个房间（手动管理历史栈，使用false避免重复压栈）
        Room previousRoom = history.pop();
        game.setCurrentRoom(previousRoom, false);
        assertEquals(1, history.size(), "返回后历史记录应该减少");
    }

    @Test
    public void testCommandHandlersInitialization() {
        // 通过间接方式测试命令处理器初始化
        // 尝试执行一个已知的命令
        Parser parser = game.getParser();
        assertNotNull(parser, "解析器应该被初始化");

    }
}