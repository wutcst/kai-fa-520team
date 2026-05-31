package test;

import cn.edu.whut.sept.zuul.Command.Command;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Command类的单元测试
 */
public class CommandTest {

    @Test
    public void testCommandCreation() {
        Command command = new Command("go", "north");
        assertNotNull(command, "Command对象应该被成功创建");
    }

    @Test
    public void testGetCommandWord() {
        Command command = new Command("help", null);
        assertEquals("help", command.getCommandWord(), "命令词应该正确");
    }

    @Test
    public void testGetSecondWord() {
        Command command = new Command("take", "key");
        assertEquals("key", command.getSecondWord(), "第二参数应该正确");
    }

    @Test
    public void testGetSecondWordNull() {
        Command command = new Command("look", null);
        assertNull(command.getSecondWord(), "第二参数为null时应该返回null");
    }

    @Test
    public void testIsUnknown() {
        Command knownCommand = new Command("go", "north");
        Command unknownCommand = new Command(null, "something");

        assertFalse(knownCommand.isUnknown(), "已知命令不应该被认为是未知的");
        assertTrue(unknownCommand.isUnknown(), "命令词为null时应该是未知命令");
    }

    @Test
    public void testHasSecondWord() {
        Command withSecondWord = new Command("take", "book");
        Command withoutSecondWord = new Command("look", null);

        assertTrue(withSecondWord.hasSecondWord(), "有第二参数时应该返回true");
        assertFalse(withoutSecondWord.hasSecondWord(), "没有第二参数时应该返回false");
    }

    @Test
    public void testCommandWithNullFirstWord() {
        Command command = new Command(null, "parameter");
        assertTrue(command.isUnknown(), "第一参数为null时应该是未知命令");
        assertEquals("parameter", command.getSecondWord(), "第二参数应该正确");
    }

    @Test
    public void testCommandWithEmptyStrings() {
        Command command = new Command("", "");
        assertFalse(command.isUnknown(), "空字符串不应该被认为是未知命令");
        assertEquals("", command.getCommandWord(), "命令词应该为空字符串");
        assertEquals("", command.getSecondWord(), "第二参数应该为空字符串");
        assertTrue(command.hasSecondWord(), "空字符串也应该被认为是有第二参数");
    }
}