package cn.edu.whut.sept.zuul.test;

import cn.edu.whut.sept.zuul.Command.*;
import cn.edu.whut.sept.zuul.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

/**
 * 边界条件测试
 */
public class BoundaryTest {

    @Test
    public void testPlayerWeightPrecision() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // 测试浮点数精度
        Item item1 = new Item("物品1", "第一个物品", 0.1);
        Item item2 = new Item("物品2", "第二个物品", 0.2);
        Item item3 = new Item("物品3", "第三个物品", 0.3);

        player.takeItem(item1);
        player.takeItem(item2);
        player.takeItem(item3);

        assertEquals(0.6, player.getCurrentWeight(), 0.000001,
                "浮点数累加应该有足够的精度");
    }

    @Test
    public void testPlayerWeightAtLimit() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // 刚好达到负重上限
        Item itemAtLimit = new Item("重物", "刚好达到上限的物品", 10.0);
        assertTrue(player.takeItem(itemAtLimit), "刚好达到负重上限应该可以拾取");
        assertEquals(10.0, player.getCurrentWeight(), 0.001, "当前负重应该等于上限");

        // 尝试拾取超重0.001的物品
        Item itemOverLimit = new Item("超重物", "略微超重的物品", 0.001);
        assertFalse(player.takeItem(itemOverLimit), "略微超重也不应该拾取");
    }

    @Test
    public void testRoomWithManyExits() {
        Room room = new Room("多出口房间");

        // 添加多个出口
        for (int i = 0; i < 10; i++) {
            room.setExit("direction" + i, new Room("房间" + i));
        }

        // 验证所有出口
        for (int i = 0; i < 10; i++) {
            assertNotNull(room.getExit("direction" + i), "出口" + i + "应该存在");
        }

        // 验证不存在的出口
        assertNull(room.getExit("invalid"), "不存在的出口应该返回null");
    }

    @Test
    public void testRoomWithManyItems() {
        Room room = new Room("多物品房间");

        // 添加多个物品
        for (int i = 0; i < 20; i++) {
            room.getItems().add(new Item("物品" + i, "第" + i + "个物品", i * 0.1));
        }

        assertEquals(20, room.getItems().size(), "房间应该有20个物品");

        // 移除中间的物品
        Item removed = room.removeItem("物品10");
        assertNotNull(removed, "应该成功移除物品");
        assertEquals(19, room.getItems().size(), "移除后应该有19个物品");

        // 验证剩下的物品
        assertTrue(room.hasItem("物品5"), "物品5应该还在");
        assertFalse(room.hasItem("物品10"), "物品10应该已被移除");
    }

    @Test
    public void testPlayerInventoryCapacity() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // 添加大量轻物品
        for (int i = 0; i < 100; i++) {
            Item lightItem = new Item("轻物品" + i, "很轻的物品", 0.01);
            player.takeItem(lightItem);
        }

        // 玩家应该可以携带很多轻物品，直到达到负重上限
        // 10.0 / 0.01 = 1000，但这里我们只添加了100个，所以不会超重
        assertTrue(player.getInventory().size() <= 100, "背包应该可以容纳很多物品");

        // 验证总重量
        assertEquals(1.0, player.getCurrentWeight(), 0.01, "总重量应该正确计算");
    }

    @Test
    public void testCommandWithLongInput() {
        // 测试非常长的输入
        StringBuilder longInput = new StringBuilder();
        longInput.append("take ");
        for (int i = 0; i < 100; i++) {
            longInput.append("verylongitemname");
        }
        longInput.append("\n");

        InputStream in = new ByteArrayInputStream(longInput.toString().getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        // 命令应该被成功解析，但第二参数可能被截断
        assertNotNull(command, "长输入的命令不应该为null");
        assertEquals("take", command.getCommandWord(), "命令词应该是'take'");
        assertTrue(command.hasSecondWord(), "应该有第二参数");
    }

    @Test
    public void testEatCookieMultipleTimes() {
        Player player = new Player("测试玩家", new Room("测试房间"));

        // 多次吃饼干（如果有多块饼干）
        Item cookie1 = new Item("cookie", "魔法饼干1", 0.2);
        Item cookie2 = new Item("cookie", "魔法饼干2", 0.2);
        Item cookie3 = new Item("cookie", "魔法饼干3", 0.2);

        player.takeItem(cookie1);
        player.takeItem(cookie2);
        player.takeItem(cookie3);

        double initialMaxWeight = player.getMaxWeight();

        // 吃第一块饼干
        player.eatCookie();
        assertEquals(initialMaxWeight + 5.0, player.getMaxWeight(), 0.001,
                "吃第一块饼干后负重增加");

        // 吃第二块饼干
        player.eatCookie();
        assertEquals(initialMaxWeight + 10.0, player.getMaxWeight(), 0.001,
                "吃第二块饼干后负重再次增加");

        // 吃第三块饼干
        player.eatCookie();
        assertEquals(initialMaxWeight + 15.0, player.getMaxWeight(), 0.001,
                "吃第三块饼干后负重再次增加");

        // 验证饼干被消耗
        assertEquals(0, player.getInventory().size(), "所有饼干应该被消耗");
    }
}