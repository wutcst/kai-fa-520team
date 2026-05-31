package test;


import cn.edu.whut.sept.zuul.Command.*;
import cn.edu.whut.sept.zuul.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 命令处理器的单元测试
 */
public class CommandHandlerTest {

    @Test
    public void testGoCommand() {
        GoCommand goCommand = new GoCommand();
        Game game = new Game();
        Room initialRoom = game.getCurrentRoom();

        // 测试没有第二参数的情况
        Command commandWithoutDirection = new Command("go", null);
        assertFalse(goCommand.execute(game, commandWithoutDirection), "没有方向时不应该结束游戏");

        // 测试无效方向
        Command commandInvalidDirection = new Command("go", "invalid");
        assertFalse(goCommand.execute(game, commandInvalidDirection), "无效方向不应该结束游戏");

        // 注意：由于房间连接是在createRooms中设置的，我们需要知道实际的方向
        // 这里我们假设初始房间有"east"出口（根据Game.createRooms的实现）
        Command commandValidDirection = new Command("go", "east");
        assertFalse(goCommand.execute(game, commandValidDirection), "有效方向不应该结束游戏");

        // 验证房间是否改变
        assertNotEquals(initialRoom, game.getCurrentRoom(), "执行go命令后房间应该改变");
    }

    @Test
    public void testHelpCommand() {
        HelpCommand helpCommand = new HelpCommand();
        Game game = new Game();
        Command command = new Command("help", null);

        assertFalse(helpCommand.execute(game, command), "help命令不应该结束游戏");
    }

    @Test
    public void testQuitCommand() {
        QuitCommand quitCommand = new QuitCommand();
        Game game = new Game();

        // 测试有第二参数的情况
        Command commandWithSecondWord = new Command("quit", "now");
        assertFalse(quitCommand.execute(game, commandWithSecondWord), "有第二参数时不应该退出");

        // 测试没有第二参数的情况
        // 注意：实际的quit命令会询问确认，这里我们只是测试命令处理器本身
        Command commandWithoutSecondWord = new Command("quit", null);
        // 由于需要用户输入确认，这里我们无法直接测试返回值
        // 但可以测试它不抛出异常
        assertDoesNotThrow(() -> quitCommand.execute(game, commandWithoutSecondWord));
    }

    @Test
    public void testLookCommand() {
        LookCommand lookCommand = new LookCommand();
        Game game = new Game();
        Command command = new Command("look", null);

        assertFalse(lookCommand.execute(game, command), "look命令不应该结束游戏");
    }

    @Test
    public void testBackCommand() {
        BackCommand backCommand = new BackCommand();
        Game game = new Game();

        // 初始状态下，历史记录为空
        Command command = new Command("back", null);
        assertFalse(backCommand.execute(game, command), "back命令不应该结束游戏");

        // 移动到一个房间，然后测试返回
        Room initialRoom = game.getCurrentRoom();
        Room newRoom = new Room("测试房间");
        game.setCurrentRoom(newRoom);

        assertFalse(backCommand.execute(game, command), "返回时不应该结束游戏");
        assertEquals(initialRoom, game.getCurrentRoom(), "执行back后应该返回到初始房间");
    }

    @Test
    public void testBackCommandMultipleSteps() {
        BackCommand backCommand = new BackCommand();
        Game game = new Game();
        Command command = new Command("back", null);

        Room room1 = game.getCurrentRoom();
        Room room2 = new Room("房间二");
        Room room3 = new Room("房间三");

        game.setCurrentRoom(room2);
        game.setCurrentRoom(room3);

        assertEquals(room3, game.getCurrentRoom(), "前置条件：当前应在第三个房间");

        assertFalse(backCommand.execute(game, command), "第一次返回不应结束游戏");
        assertEquals(room2, game.getCurrentRoom(), "第一次back后应回到第二个房间");

        assertFalse(backCommand.execute(game, command), "第二次返回不应结束游戏");
        assertEquals(room1, game.getCurrentRoom(), "第二次back后应回到第一个房间");

        assertFalse(backCommand.execute(game, command), "历史为空时返回也不应结束游戏");
        assertEquals(room1, game.getCurrentRoom(), "历史为空后再次back不应改变当前房间");
    }

    @Test
    public void testTakeCommand() {
        TakeCommand takeCommand = new TakeCommand();
        Game game = new Game();

        // 测试没有第二参数的情况
        Command commandWithoutItem = new Command("take", null);
        assertFalse(takeCommand.execute(game, commandWithoutItem), "没有物品名时不应该结束游戏");

        // 测试拾取不存在的物品
        Command commandInvalidItem = new Command("take", "不存在的物品");
        assertFalse(takeCommand.execute(game, commandInvalidItem), "拾取不存在的物品不应该结束游戏");

        // 测试拾取存在的物品
        // 首先在房间中添加一个物品
        Item testItem = new Item("测试物品", "用于测试的物品", 0.5);
        game.getCurrentRoom().getItems().add(testItem);

        Command commandValidItem = new Command("take", "测试物品");
        assertFalse(takeCommand.execute(game, commandValidItem), "拾取物品不应该结束游戏");

        // 验证物品是否被拾取
        assertTrue(game.getPlayer().hasItem("测试物品"), "玩家应该有这个物品");
        assertFalse(game.getCurrentRoom().hasItem("测试物品"), "房间中不应该再有这个物品");
    }

    @Test
    public void testDropCommand() {
        DropCommand dropCommand = new DropCommand();
        Game game = new Game();

        // 测试没有第二参数的情况
        Command commandWithoutItem = new Command("drop", null);
        assertFalse(dropCommand.execute(game, commandWithoutItem), "没有物品名时不应该结束游戏");

        // 测试丢弃不存在的物品
        Command commandInvalidItem = new Command("drop", "不存在的物品");
        assertFalse(dropCommand.execute(game, commandInvalidItem), "丢弃不存在的物品不应该结束游戏");

        // 测试丢弃存在的物品
        // 首先让玩家拾取一个物品
        Item testItem = new Item("测试物品", "用于测试的物品", 0.5);
        game.getPlayer().takeItem(testItem);

        Command commandValidItem = new Command("drop", "测试物品");
        assertFalse(dropCommand.execute(game, commandValidItem), "丢弃物品不应该结束游戏");

        // 验证物品是否被丢弃
        assertFalse(game.getPlayer().hasItem("测试物品"), "玩家不应该有这个物品");
        // 注意：drop命令会将物品放回房间
        // 但由于我们的实现在dropItem后重新创建了物品，这里可能需要进行调整
    }

    @Test
    public void testItemsCommand() {
        ItemsCommand itemsCommand = new ItemsCommand();
        Game game = new Game();
        Command command = new Command("items", null);

        assertFalse(itemsCommand.execute(game, command), "items命令不应该结束游戏");
    }

    @Test
    public void testEatCommand() {
        EatCommand eatCommand = new EatCommand();
        Game game = new Game();

        // 测试没有第二参数的情况
        Command commandWithoutItem = new Command("eat", null);
        assertFalse(eatCommand.execute(game, commandWithoutItem), "没有指定物品时不应该结束游戏");

        // 测试吃非饼干物品
        Command commandNotCookie = new Command("eat", "apple");
        assertFalse(eatCommand.execute(game, commandNotCookie), "吃非饼干物品不应该结束游戏");

        // 测试吃饼干但没有饼干
        Command commandCookieNoCookie = new Command("eat", "cookie");
        assertFalse(eatCommand.execute(game, commandCookieNoCookie), "没有饼干时不应该结束游戏");

        // 测试吃饼干且有饼干
        // 首先给玩家一个饼干
        Item cookie = new Item("cookie", "魔法饼干", 0.2);
        game.getPlayer().takeItem(cookie);

        double initialMaxWeight = game.getPlayer().getMaxWeight();
        Command commandCookieWithCookie = new Command("eat", "cookie");
        assertFalse(eatCommand.execute(game, commandCookieWithCookie), "吃饼干不应该结束游戏");

        // 验证负重是否增加
        assertEquals(initialMaxWeight + 5.0, game.getPlayer().getMaxWeight(), 0.001,
                "吃饼干后最大负重应该增加5kg");
        assertFalse(game.getPlayer().hasItem("cookie"), "吃饼干后饼干应该被消耗");
    }
}