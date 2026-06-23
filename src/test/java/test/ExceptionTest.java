package test;

import cn.edu.whut.sept.zuul.Command.*;
import cn.edu.whut.sept.zuul.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 异常情况测试
 */
public class ExceptionTest {

    @Test
    public void testNullRoomDescription() {
        // Room构造函数接受null描述
        Room room = new Room(null);
        assertNull(room.getShortDescription(), "房间描述为null时getShortDescription应该返回null");

        // getLongDescription可能抛出NullPointerException，取决于实现
        // 这里我们测试它不抛出异常
        assertDoesNotThrow(() -> room.getLongDescription());
    }

    @Test
    public void testNullExitDirection() {
        Room room = new Room("测试房间");
        Room otherRoom = new Room("其他房间");

        // setExit应该能处理null方向吗？这取决于设计
        // 假设不能为null，我们测试它不抛出异常
        assertDoesNotThrow(() -> room.setExit(null, otherRoom));

        // 由于HashMap允许null key，getExit(null)应返回刚才设置的房间
        assertEquals(otherRoom, room.getExit(null), "null方向应该返回设置的房间");
    }

    @Test
    public void testNullExitRoom() {
        Room room = new Room("测试房间");

        // setExit应该能处理null房间吗？
        assertDoesNotThrow(() -> room.setExit("north", null));

        // 然后尝试获取这个出口
        assertNull(room.getExit("north"), "null房间的出口应该返回null");
    }

    @Test
    public void testPlayerWithNullName() {
        // Player构造函数应该能处理null名称
        Player player = new Player(null, new Room("测试房间"));
        assertNull(player.getName(), "玩家名称应该为null");
    }

    @Test
    public void testPlayerWithNullRoom() {
        // Player构造函数应该能处理null房间
        Player player = new Player("测试玩家", null);
        assertNull(player.getCurrentRoom(), "当前房间应该为null");

        // 设置null房间
        player.setCurrentRoom(null);
        assertNull(player.getCurrentRoom(), "设置null房间后当前房间应该为null");
    }

    @Test
    public void testTakeNullItem() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // takeItem传入null时会触发NullPointerException（item.getWeight()）
        assertThrows(NullPointerException.class, () -> player.takeItem(null));
    }

    @Test
    public void testDropNullItemName() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // dropItem应该能处理null物品名
        assertNull(player.dropItem(null), "null物品名应该返回null");
    }

    @Test
    public void testDropEmptyItemName() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // dropItem应该能处理空字符串物品名
        assertNull(player.dropItem(""), "空字符串物品名应该返回null");
    }

    @Test
    public void testHasItemWithNull() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        assertFalse(player.hasItem(null), "null物品名应该返回false");
    }

    @Test
    public void testItemWithNullName() {
        // Item构造函数应该能处理null名称
        Item item = new Item(null, "测试描述", 1.0);
        assertNull(item.getName(), "物品名称应该为null");

        // toString方法应该能处理null名称
        assertDoesNotThrow(() -> item.toString());
    }

    @Test
    public void testItemWithNullDescription() {
        // Item构造函数应该能处理null描述
        Item item = new Item("测试物品", null, 1.0);
        assertNull(item.getDescription(), "物品描述应该为null");

        // toString方法应该能处理null描述
        assertDoesNotThrow(() -> item.toString());
    }

    @Test
    public void testCommandHandlerWithNullGame() {
        GoCommand goCommand = new GoCommand();
        Command command = new Command("go", "north");

        // execute传入null game时会触发NullPointerException
        assertThrows(NullPointerException.class, () -> goCommand.execute(null, command));
    }

    @Test
    public void testCommandHandlerWithNullCommand() {
        GoCommand goCommand = new GoCommand();
        Game game = new Game();

        // execute传入null command时会触发NullPointerException
        assertThrows(NullPointerException.class, () -> goCommand.execute(game, null));
    }
}