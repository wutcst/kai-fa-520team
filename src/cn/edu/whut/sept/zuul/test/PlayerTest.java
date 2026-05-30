package cn.edu.whut.sept.zuul.test;

import cn.edu.whut.sept.zuul.Item;
import cn.edu.whut.sept.zuul.Room;
import cn.edu.whut.sept.zuul.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Player类的单元测试
 */
public class PlayerTest {

    private Player player;
    private Room startRoom;
    private Item lightItem;
    private Item heavyItem;

    @BeforeEach
    public void setUp() {
        startRoom = new Room("起始房间");
        player = new Player("测试玩家", startRoom);
        lightItem = new Item("羽毛", "一根羽毛", 0.1);
        heavyItem = new Item("石头", "一块大石头", 15.0);
    }

    @Test
    public void testPlayerCreation() {
        assertNotNull(player, "Player对象应该被成功创建");
        assertEquals("测试玩家", player.getName(), "玩家名称应该正确");
        assertEquals(startRoom, player.getCurrentRoom(), "玩家应该在起始房间");
    }

    @Test
    public void testTakeItemWithinWeightLimit() {
        assertTrue(player.takeItem(lightItem), "拾取轻物品应该成功");
        assertEquals(1, player.getInventory().size(), "背包应该有一个物品");
        assertEquals(0.1, player.getCurrentWeight(), 0.001, "当前负重应该正确");
    }

    @Test
    public void testTakeItemExceedsWeightLimit() {
        assertFalse(player.takeItem(heavyItem), "拾取超重物品应该失败");
        assertEquals(0, player.getInventory().size(), "背包应该为空");
        assertEquals(0.0, player.getCurrentWeight(), 0.001, "当前负重应该为0");
    }

    @Test
    public void testTakeMultipleItems() {
        Item item1 = new Item("物品1", "第一个物品", 2.0);
        Item item2 = new Item("物品2", "第二个物品", 3.0);
        Item item3 = new Item("物品3", "第三个物品", 6.0); // 这个会超重

        assertTrue(player.takeItem(item1), "拾取第一个物品应该成功");
        assertTrue(player.takeItem(item2), "拾取第二个物品应该成功");
        assertFalse(player.takeItem(item3), "拾取第三个物品应该失败（超重）");

        assertEquals(2, player.getInventory().size(), "背包应该有两个物品");
        assertEquals(5.0, player.getCurrentWeight(), 0.001, "当前负重应该正确");
    }

    @Test
    public void testDropItem() {
        player.takeItem(lightItem);
        assertTrue(player.dropItem("羽毛"), "丢弃存在的物品应该成功");
        assertEquals(0, player.getInventory().size(), "背包应该为空");
        assertEquals(0.0, player.getCurrentWeight(), 0.001, "当前负重应该为0");
    }

    @Test
    public void testDropNonExistentItem() {
        assertFalse(player.dropItem("不存在的物品"), "丢弃不存在的物品应该失败");
    }

    @Test
    public void testShowInventoryEmpty() {
        // 这个方法主要是输出到控制台，我们可以测试它不抛出异常
        assertDoesNotThrow(() -> player.showInventory(), "显示空背包不应该抛出异常");
    }

    @Test
    public void testShowInventoryWithItems() {
        player.takeItem(lightItem);
        assertDoesNotThrow(() -> player.showInventory(), "显示有物品的背包不应该抛出异常");
    }

    @Test
    public void testEatCookieWithoutCookie() {
        // 玩家没有饼干时尝试吃饼干
        player.eatCookie(); // 应该输出提示信息
        assertEquals(10.0, player.getMaxWeight(), 0.001, "没有饼干时最大负重不应该改变");
    }

    @Test
    public void testEatCookieWithCookie() {
        Item cookie = new Item("cookie", "魔法饼干", 0.2);
        player.takeItem(cookie);

        player.eatCookie();
        assertEquals(15.0, player.getMaxWeight(), 0.001, "吃饼干后最大负重应该增加5kg");
        assertEquals(0, player.getInventory().size(), "吃饼干后饼干应该被消耗");
        assertEquals(0.0, player.getCurrentWeight(), 0.001, "当前负重应该减少饼干的重量");
    }

    @Test
    public void testSetCurrentRoom() {
        Room newRoom = new Room("新房间");
        player.setCurrentRoom(newRoom);
        assertEquals(newRoom, player.getCurrentRoom(), "当前房间应该被更新");
    }

    @Test
    public void testHasItem() {
        player.takeItem(lightItem);
        assertTrue(player.hasItem("羽毛"), "玩家应该有这个物品");
        assertFalse(player.hasItem("石头"), "玩家不应该有这个物品");
        assertFalse(player.hasItem(""), "检查空字符串应该返回false");
        assertFalse(player.hasItem(null), "检查null应该返回false");
    }

    @Test
    public void testGetMaxWeight() {
        assertEquals(10.0, player.getMaxWeight(), 0.001, "初始最大负重应该为10kg");
    }

    @Test
    public void testGetCurrentWeight() {
        assertEquals(0.0, player.getCurrentWeight(), 0.001, "初始当前负重应该为0");
        player.takeItem(lightItem);
        assertEquals(0.1, player.getCurrentWeight(), 0.001, "拾取物品后当前负重应该更新");
    }

    @Test
    public void testGetInventory() {
        assertNotNull(player.getInventory(), "背包列表不应该为null");
        assertTrue(player.getInventory().isEmpty(), "初始背包应该为空");

        player.takeItem(lightItem);
        assertEquals(1, player.getInventory().size(), "背包应该有一个物品");
        assertEquals(lightItem, player.getInventory().get(0), "背包中的物品应该正确");
    }
}