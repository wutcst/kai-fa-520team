# 单元测试问题汇总

## 🔴 严重问题

1. **System.setIn() 未恢复，污染测试环境**
ParserTest 中几乎每个测试都调用了 `System.setIn(in)` 重定向标准输入，但从未恢复原始的 `System.in`。BoundaryTest.tes

```java
// ParserTest.java – 每个方法都是这样
String input = "help\n";
InputStream in = new ByteArrayInputStream(input.getBytes());
System.setIn(in); // ← 改了，但从不恢复！

  后果：后续测试（测试执行顺序不确定）可能会读到已经被消费或已关闭的流，导致间歇性失败。正确做法是用 @BeforeEach / @AfterEach 保存并恢复原始流。

  2. QuitCommand 测试会阻塞（hang）

  QuitCommand.execute() 在非 GUI 模式下会调用 game.getParser().getReader().nextLine() 等待用户输入。CommandHandlerTest.testQuitCommand()
  只测了"有第二参数"（直接返回 false）和 assertDoesNotThrow——后者实际上会卡住，因为它试图读取控制台输入。

  // QuitCommand.java:25 — 等待 stdin
  String confirmation = game.getParser().getReader().nextLine().toLowerCase();

  3. BackCommand 测试绕过了正常的移动流程

  CommandHandlerTest.testBackCommand() 中直接调用 game.setCurrentRoom(newRoom) 来设置房间，但这跳过了 BackCommand 内部调用的 game.goBack() 
  机制。测试虽然碰巧能通过（因为 setCurrentRoom 会推入历史栈），但它测的不是真正的 back 命令行为——它没有通过 GoCommand 移动，历史栈的构建方式与实际游戏不同。      

  4. ExceptionTest 过于宽松——null 输入"不抛异常"不代表行为正确

  多个异常测试只是断言"不抛异常"，但对 null 输入的实际行为不做断言：

  // ExceptionTest.java:74
  assertDoesNotThrow(() -> player.takeItem(null));  // 接受任何行为，包括静默失败

  // ExceptionTest.java:128
  assertDoesNotThrow(() -> goCommand.execute(null, command));  // null game → NPE 是合理的

  player.takeItem(null) 实际实现中会抛
  NullPointerException（item.getWeight()），但测试却说"不抛异常"——这个测试会失败，或者实现做了防御但测试没有验证正确行为。同理，goCommand.execute(null, command)   
  应该抛 NPE，assertDoesNotThrow 会失败。

  ---
  🟡 中等问题

  5. GameTest.testCreateRooms 硬编码了房间布局

  String description = initialRoom.getShortDescription();
  assertTrue(description.contains("outside") || description.contains("entrance"),
          "初始房间应该是outside或entrance");

  实际房间描述是 "大学主入口外"——既不包含 "outside" 也不包含 "entrance"。这个断言会失败。

  6. ItemTest 接受负重量为合法行为

  Item negativeItem = new Item("特殊物品", "重量为负的物品", -1.0);
  assertEquals(-1.0, negativeItem.getWeight(), 0.001, "物品重量可以为负数");

  负重量在游戏逻辑中是不合理的（可以通过反复拾取/丢弃负重量物品来无限增加负重），测试应该验证构造函数拒绝负重量，而不是断言它被接受。

  7. CommandTest.testCommandWithEmptyStrings 行为有争议

  Command command = new Command("", "");
  assertFalse(command.isUnknown(), "空字符串不应该被认为是未知命令");
  assertTrue(command.hasSecondWord(), "空字符串也应该被认为是有第二参数");

  空字符串命令词 "" 不应该被视为有效命令，但测试断言它不是未知命令。CommandWords.isCommand("") 返回 false，所以 Parser 会返回 new Command(null,
  "")——这意味着空命令实际上是未知命令。

  8. CommandWords 新增的命令（save/load/saves/delete）未被测试覆盖

  CommandWordsTest 只验证了 9 个命令，但 CommandWords 构造函数中注册了 13 个命令。新增的 save、load、saves、delete 没有被测试。

  ---
  🟢 覆盖率/设计问题

  ┌───────────────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐   
  │               问题                │                                                          说明                                                          │   
  ├───────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤   
  │ 缺少 Save/Load/Delete 命令的测试  │ SaveCommand、LoadCommand、ListSavesCommand、DeleteSaveCommand 完全没有测试                                             │   
  ├───────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤   
  │ Parser 中文别名未被测试           │ Parser 支持大量中文命令别名（"走"、"拾取"、"返回"等），但没有一个测试验证这些别名                                      │   
  ├───────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤   
  │ Parser 方向/物品别名未被测试      │ DIRECTION_ALIASES 和 ITEM_ALIASES 的中文映射完全未覆盖                                                                 │   
  ├───────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤   
  │ 测试在 test 包而非                │ 所有测试放在默认的 test 包中，无法测试 package-private 的方法                                                          │   
  │ cn.edu.whut.sept.zuul 包          │                                                                                                                        │   
  ├───────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤   
  │ testEatCookieMultipleTimes        │ BoundaryTest 假设吃 3 块饼干增加 15kg，但实际实现每块只增加 5kg（maxWeight += 5.0），所以 3 块增加 15kg                │   
  │ 测试假设不对                      │ 是对的——但测试直接操作 player 而不通过 EatCommand，没有测试命令层逻辑                                                  │   
  └───────────────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘   

  ---
  总结

  ┌───────────┬──────┬─────────────────────────────────────────────────────────────────────────────┐
  │ 严重程度  │ 数量 │                                  关键问题                                   │
  ├───────────┼──────┼─────────────────────────────────────────────────────────────────────────────┤
  │ 🔴 严重   │ 4    │ System.setIn 泄漏、QuitCommand 阻塞、null 处理测试错误、GameTest 硬编码失败 │
  ├───────────┼──────┼─────────────────────────────────────────────────────────────────────────────┤
  │ 🟡 中等   │ 4    │ 负重量被接受、空字符串行为、新增命令未覆盖                                  │
  ├───────────┼──────┼─────────────────────────────────────────────────────────────────────────────┤
  │ 🟢 覆盖率 │ 4    │ 中文别名、存档命令、测试包结构                                              │
  └───────────┴──────┴─────────────────────────────────────────────────────────────────────────────┘

  最紧急需要修复的是 System.setIn 未恢复和**ExceptionTest 中对 null 输入的错误断言**，这些会导致测试在不同执行顺序下出现随机失败。
