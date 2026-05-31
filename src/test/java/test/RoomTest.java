package test;

import cn.edu.whut.sept.zuul.Item;
import cn.edu.whut.sept.zuul.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Room类的单元测试
 */
public class RoomTest {

    private Room room;
    private Room northRoom;
    private Room southRoom;

    @BeforeEach
    public void setUp() {
        room = new Room("测试房间");
        northRoom = new Room("北房间");
        southRoom = new Room("南房间");
    }

    @Test
    public void testRoomCreation() {
        assertNotNull(room, "Room对象应该被成功创建");
        assertEquals("测试房间", room.getShortDescription(), "房间描述应该正确");
    }

    @Test
    public void testSetExit() {
        room.setExit("north", northRoom);
        room.setExit("south", southRoom);

        assertEquals(northRoom, room.getExit("north"), "北出口应该指向正确的房间");
        assertEquals(southRoom, room.getExit("south"), "南出口应该指向正确的房间");
        assertNull(room.getExit("east"), "东出口不存在应该返回null");
    }

    @Test
    public void testGetShortDescription() {
        assertEquals("测试房间", room.getShortDescription(), "简短描述应该正确");
    }

    @Test
    public void testGetLongDescriptionWithoutExits() {
        String longDesc = room.getLongDescription();
        assertTrue(longDesc.contains("You are 测试房间"), "长描述应该包含房间描述");
        assertTrue(longDesc.contains("Exits:"), "长描述应该包含Exits标签");
    }

    @Test
    public void testGetLongDescriptionWithExits() {
        room.setExit("north", northRoom);
        String longDesc = room.getLongDescription();
        assertTrue(longDesc.contains("Exits: north"), "长描述应该包含出口方向");
    }

    @Test
    public void testAddItem() {
        Item item = new Item("钥匙", "一把钥匙", 0.1);
        room.getItems().add(item);

        List<Item> items = room.getItems();
        assertEquals(1, items.size(), "房间应该有一个物品");
        assertEquals(item, items.get(0), "房间内的物品应该正确");
    }

    @Test
    public void testRemoveItem() {
        Item item1 = new Item("钥匙", "一把钥匙", 0.1);
        Item item2 = new Item("地图", "一张地图", 0.2);
        room.getItems().add(item1);
        room.getItems().add(item2);

        Item removed = room.removeItem("钥匙");
        assertNotNull(removed, "应该成功移除物品");
        assertEquals("钥匙", removed.getName(), "移除的物品名称应该正确");
        assertEquals(1, room.getItems().size(), "移除后应该还剩一个物品");
    }

    @Test
    public void testRemoveNonExistentItem() {
        Item removed = room.removeItem("不存在的物品");
        assertNull(removed, "移除不存在的物品应该返回null");
    }

    @Test
    public void testHasItem() {
        Item item = new Item("钥匙", "一把钥匙", 0.1);
        room.getItems().add(item);

        assertTrue(room.hasItem("钥匙"), "房间应该有这个物品");
        assertFalse(room.hasItem("地图"), "房间不应该有这个物品");
    }

    @Test
    public void testGetExitString() {
        room.setExit("north", northRoom);
        room.setExit("south", southRoom);

        // 注意：getExitString是私有方法，这里通过getLongDescription间接测试
        String longDesc = room.getLongDescription();
        assertTrue(longDesc.contains("north"), "应该包含north方向");
        assertTrue(longDesc.contains("south"), "应该包含south方向");
    }
}