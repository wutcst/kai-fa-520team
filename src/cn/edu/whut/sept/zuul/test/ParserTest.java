package cn.edu.whut.sept.zuul.test;

import cn.edu.whut.sept.zuul.Command.*;
import cn.edu.whut.sept.zuul.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Parser类的单元测试
 * 注意：需要模拟用户输入
 */
public class ParserTest {

    @Test
    public void testParserCreation() {
        Parser parser = new Parser();
        assertNotNull(parser, "Parser对象应该被成功创建");
    }

    @Test
    public void testGetCommandWithSingleWord() {
        // 模拟输入"help"
        String input = "help\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertNotNull(command, "命令不应该为null");
        assertEquals("help", command.getCommandWord(), "命令词应该是'help'");
        assertNull(command.getSecondWord(), "第二参数应该是null");
        assertFalse(command.isUnknown(), "应该是已知命令");
    }

    @Test
    public void testGetCommandWithTwoWords() {
        // 模拟输入"go east"
        String input = "go east\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertEquals("go", command.getCommandWord(), "命令词应该是'go'");
        assertEquals("east", command.getSecondWord(), "第二参数应该是'east'");
        assertTrue(command.hasSecondWord(), "应该有第二参数");
    }

    @Test
    public void testGetCommandWithMultipleWords() {
        // 模拟输入"take magic cookie"
        String input = "take magic cookie\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertEquals("take", command.getCommandWord(), "命令词应该是'take'");
        assertEquals("magic cookie", command.getSecondWord(), "第二参数应该是'magic cookie'");
    }

    @Test
    public void testGetCommandWithEmptyInput() {
        // 模拟空输入（直接回车）
        String input = "\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertTrue(command.isUnknown(), "空输入应该是未知命令");
        assertNull(command.getCommandWord(), "命令词应该是null");
    }

    @Test
    public void testGetCommandWithWhitespace() {
        // 模拟输入"  go  north  "（前后有空格）
        String input = "  go  north  \n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertEquals("go", command.getCommandWord(), "命令词应该是'go'");
        assertEquals("north", command.getSecondWord(), "第二参数应该是'north'");
    }

    @Test
    public void testGetCommandLowerCaseConversion() {
        // 模拟输入"GO NORTH"（大写）
        String input = "GO NORTH\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertEquals("go", command.getCommandWord(), "命令词应该被转换为小写'go'");
        assertEquals("north", command.getSecondWord(), "第二参数应该被转换为小写'north'");
    }

    @Test
    public void testGetCommandInvalidCommand() {
        // 模拟输入"invalid command"
        String input = "invalid command\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Parser parser = new Parser();
        Command command = parser.getCommand();

        assertTrue(command.isUnknown(), "无效命令应该是未知命令");
        assertNull(command.getCommandWord(), "无效命令的命令词应该是null");
        assertEquals("command", command.getSecondWord(), "第二参数应该保留");
    }

    @Test
    public void testShowCommands() {
        Parser parser = new Parser();

        // showCommands主要是输出到控制台，我们测试它不抛出异常
        assertDoesNotThrow(() -> parser.showCommands(), "显示命令不应该抛出异常");
    }

    @Test
    public void testGetReader() {
        Parser parser = new Parser();

        assertNotNull(parser.getReader(), "getReader应该返回非null的Scanner对象");
    }
}