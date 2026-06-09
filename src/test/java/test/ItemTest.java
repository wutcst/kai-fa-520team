package test;

import cn.edu.whut.sept.zuul.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Item类的单元测试
 */
public class ItemTest {

    private Item item;

    @BeforeEach
    public void setUp() {
        item = new Item("测试物品", "这是一个测试物品", 1.5);
    }

    @Test
    public void testItemCreation() {
        assertNotNull(item, "Item对象应该被成功创建");
    }

    @Test
    public void testGetName() {
        assertEquals("测试物品", item.getName(), "物品名称应该正确");
    }

    @Test
    public void testGetDescription() {
        assertEquals("这是一个测试物品", item.getDescription(), "物品描述应该正确");
    }

    @Test
    public void testGetWeight() {
        assertEquals(1.5, item.getWeight(), 0.001, "物品重量应该正确");
    }

    @Test
    public void testToString() {
        String expected = "这是一个测试物品（重量：1.5kg）";
        assertEquals(expected, item.toString(), "toString方法应该返回正确的格式");
    }

    @Test
    public void testItemWithZeroWeight() {
        Item lightItem = new Item("羽毛", "一根很轻的羽毛", 0.0);
        assertEquals(0.0, lightItem.getWeight(), 0.001, "物品重量可以为0");
    }

    @Test
    public void testItemWithNegativeWeight() {
        Item negativeItem = new Item("特殊物品", "重量为负的物品", -1.0);
        assertEquals(-1.0, negativeItem.getWeight(), 0.001, "物品重量可以为负数");
    }
}