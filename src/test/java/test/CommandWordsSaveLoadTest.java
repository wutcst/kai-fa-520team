package test;

import cn.edu.whut.sept.zuul.Command.CommandWords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 CommandWords 中的存档相关命令是否已注册：save/load/saves/delete
 */
public class CommandWordsSaveLoadTest {

    private CommandWords commandWords;

    @BeforeEach
    public void setUp() {
        commandWords = new CommandWords();
    }

    @Test
    public void testPersistenceCommandsPresent() {
        assertTrue(commandWords.isCommand("save"), "'save' 应该被注册为命令");
        assertTrue(commandWords.isCommand("load"), "'load' 应该被注册为命令");
        assertTrue(commandWords.isCommand("saves"), "'saves' 应该被注册为命令");
        assertTrue(commandWords.isCommand("delete"), "'delete' 应该被注册为命令");
    }

    @Test
    public void testPersistenceCommandDescriptionsExist() {
        assertNotNull(commandWords.getDescription("save"), "'save' 应有描述");
        assertNotNull(commandWords.getDescription("load"), "'load' 应有描述");
        assertNotNull(commandWords.getDescription("saves"), "'saves' 应有描述");
        assertNotNull(commandWords.getDescription("delete"), "'delete' 应有描述");
    }
}
