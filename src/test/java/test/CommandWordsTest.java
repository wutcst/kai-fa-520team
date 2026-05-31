package test;

import cn.edu.whut.sept.zuul.Command.CommandWords;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CommandWords类的单元测试
 */
public class CommandWordsTest {

    private CommandWords commandWords;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        commandWords = new CommandWords();
    }

    @Test
    public void testIsCommandValid() {
        assertTrue(commandWords.isCommand("go"), "'go'应该是有效命令");
        assertTrue(commandWords.isCommand("help"), "'help'应该是有效命令");
        assertTrue(commandWords.isCommand("quit"), "'quit'应该是有效命令");
        assertTrue(commandWords.isCommand("look"), "'look'应该是有效命令");
        assertTrue(commandWords.isCommand("back"), "'back'应该是有效命令");
        assertTrue(commandWords.isCommand("take"), "'take'应该是有效命令");
        assertTrue(commandWords.isCommand("drop"), "'drop'应该是有效命令");
        assertTrue(commandWords.isCommand("items"), "'items'应该是有效命令");
        assertTrue(commandWords.isCommand("eat"), "'eat'应该是有效命令");
    }

    @Test
    public void testIsCommandInvalid() {
        assertFalse(commandWords.isCommand("invalid"), "'invalid'应该是无效命令");
        assertFalse(commandWords.isCommand(""), "空字符串应该是无效命令");
        assertFalse(commandWords.isCommand(null), "null应该是无效命令");
        assertFalse(commandWords.isCommand("GO"), "大小写敏感，'GO'应该是无效命令");
        assertFalse(commandWords.isCommand("go "), "带空格的'go '应该是无效命令");
    }

    @Test
    public void testIsCommandCaseSensitive() {
        // 注意：根据Parser的实现，命令被转换为小写，所以这里应该不区分大小写
        // 但CommandWords本身是区分大小写的，需要与Parser配合
        assertTrue(commandWords.isCommand("go"), "小写'go'应该有效");
        assertFalse(commandWords.isCommand("GO"), "大写'GO'应该无效（除非Parser做了转换）");
    }

    @Test
    public void testShowAll() {
        // showAll主要是输出到控制台，我们测试它不抛出异常
        assertDoesNotThrow(() -> commandWords.showAll(), "显示所有命令不应该抛出异常");
    }
}
