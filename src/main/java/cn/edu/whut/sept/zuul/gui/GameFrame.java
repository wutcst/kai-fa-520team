package cn.edu.whut.sept.zuul.gui;

import cn.edu.whut.sept.zuul.Game;
import cn.edu.whut.sept.zuul.Item;
import cn.edu.whut.sept.zuul.Player;
import cn.edu.whut.sept.zuul.Room;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Queue;

/**
 * Swing 图形界面：房间卡片、物品贴图、背包弹窗、命令按钮、黑屏切换动画。
 */
public class GameFrame extends JFrame {
    private static final int FRAME_WIDTH = 1366;
    private static final int FRAME_HEIGHT = 900;
    private static final int ITEM_ICON_SIZE = 74;
    private static final int BAG_ICON_SIZE = 42;

    private final Game game;
    private final CardLayout roomCardLayout = new CardLayout();
    private final JPanel roomCardPanel = new JPanel(roomCardLayout);
    private final BackgroundCanvasPanel backgroundCanvas = new BackgroundCanvasPanel();
    private final Map<Room, RoomScenePanel> roomSceneMap = new HashMap<>();
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private final Map<String, JButton> commandButtons = new LinkedHashMap<>();
    private final Map<String, Map<String, double[]>> fixedRoomAnchors = new HashMap<>();

    private final JLabel playerNameLabel = new JLabel();
    private final JLabel roomLabel = new JLabel();
    private final JLabel weightLabel = new JLabel();
    private final JLabel capacityLabel = new JLabel();
    private final JLabel bagIconLabel = new JLabel();
    private final JPanel exitsPanel = new JPanel();
    private final JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    private final JTextField commandField = new JTextField();
    private final JButton sendButton = new JButton("执行指令");
    private final MessageBoxPanel messageBoxPanel = new MessageBoxPanel();
    private final FadeGlassPane fadeGlassPane = new FadeGlassPane();

    private InventoryDialog inventoryDialog;
    private boolean switchingRoom;

    public GameFrame(Game game) {
        this.game = game;
        this.game.setGuiMode(true);
        initFixedAnchors();

        setTitle("祖尔世界 - Swing 图形冒险游戏");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setContentPane(backgroundCanvas);
        backgroundCanvas.setLayout(new BorderLayout());
        roomCardPanel.setOpaque(false);

        buildTopHud();
        buildRoomScenes();
        buildRightExits();
        buildBottomControls();

        add(roomCardPanel, BorderLayout.CENTER);
        add(exitsPanel, BorderLayout.EAST);

        setGlassPane(fadeGlassPane);
        fadeGlassPane.setVisible(false);

        // 在显示游戏界面前，先弹出取名对话框（若玩家选择取消或留空则使用默认名）
        promptPlayerName();

        refreshAllViews();
        showCurrentRoom(false);
        appendWelcomeText();
    }

    private void promptPlayerName() {
        // 在构造函数中调用，因 main 已经在 EDT 中创建 frame，可以直接使用 JOptionPane
        Player player = game.getPlayer();
        String current = player.getName() == null ? "冒险者" : player.getName();
        String input = (String) JOptionPane.showInputDialog(
                this,
                "欢迎！请输入你的名字：",
                "角色命名",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                current
        );
        if (input != null) {
            input = input.trim();
            if (!input.isEmpty()) {
                player.setName(input);
            }
        }
    }

    private void buildTopHud() {
        JPanel hud = new JPanel(new BorderLayout(12, 0));
        hud.setBorder(new EmptyBorder(10, 12, 10, 12));
        hud.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        bagIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bagIconLabel.setToolTipText("打开背包");
        bagIconLabel.setPreferredSize(new Dimension(BAG_ICON_SIZE + 8, BAG_ICON_SIZE + 8));
        bagIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bagIconLabel.setVerticalAlignment(SwingConstants.CENTER);
        bagIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openInventoryDialog();
            }
        });
        refreshBagIcon();

        left.add(bagIconLabel);
        left.add(Box.createHorizontalStrut(14));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new GridLayout(4, 1, 0, 2));
        // HUD 文字改回黑色并放大，同时使用半透明白底以提高可读性
        Color hudBlack = Color.BLACK;
        playerNameLabel.setForeground(hudBlack);
        roomLabel.setForeground(hudBlack);
        weightLabel.setForeground(hudBlack);
        capacityLabel.setForeground(hudBlack);
        for (JLabel label : new JLabel[]{playerNameLabel, roomLabel, weightLabel, capacityLabel}) {
            label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
            label.setOpaque(true);
            label.setBackground(new Color(255, 255, 255, 140));
            label.setBorder(new EmptyBorder(4, 6, 4, 6));
        }
        infoPanel.add(playerNameLabel);
        infoPanel.add(roomLabel);
        infoPanel.add(weightLabel);
        infoPanel.add(capacityLabel);
        left.add(infoPanel);

        hud.add(left, BorderLayout.WEST);
        add(hud, BorderLayout.NORTH);
    }

    private void buildRoomScenes() {
        for (Room room : game.getRoomsMap().values()) {
            RoomScenePanel panel = new RoomScenePanel(room);
            roomSceneMap.put(room, panel);
            roomCardPanel.add(panel, cardName(room));
        }
    }

    private void buildRightExits() {
        exitsPanel.setPreferredSize(new Dimension(200, 0));
        exitsPanel.setBorder(new EmptyBorder(16, 10, 16, 10));
        exitsPanel.setOpaque(false);
        exitsPanel.setLayout(new BoxLayout(exitsPanel, BoxLayout.Y_AXIS));
    }

    private void buildBottomControls() {
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(10, 12, 12, 12));
        bottom.setOpaque(false);

        messageBoxPanel.setPreferredSize(new Dimension(0, 170));
        messageBoxPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        bottom.add(messageBoxPanel);
        bottom.add(Box.createVerticalStrut(8));

        commandPanel.setOpaque(false);
        commandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buildCommandButtons();
        bottom.add(commandPanel);
        bottom.add(Box.createVerticalStrut(8));

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        commandField.setFont(commandField.getFont().deriveFont(Font.PLAIN, 16f));
        commandField.setToolTipText("支持中英文命令，例如：前进 东 / go east / 拾取 钥匙 / take key");
        commandField.addActionListener(e -> executeInputCommand());
        sendButton.addActionListener(e -> executeInputCommand());
        // 美化发送按钮
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD, 14f));
        sendButton.setBackground(new Color(66, 133, 244));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setOpaque(true);
        inputPanel.add(commandField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        bottom.add(inputPanel);

        add(bottom, BorderLayout.SOUTH);
    }

    private void buildCommandButtons() {
        commandPanel.removeAll();
        commandButtons.clear();

        addCommandButton("go", "移动", e -> promptAndExecuteGo());
        addCommandButton("look", "查看", e -> executeCommandLine("look"));
        addCommandButton("take", "拾取", e -> promptAndExecuteTake());
        addCommandButton("drop", "丢弃", e -> openInventoryDialog());
        addCommandButton("items", "背包", e -> executeCommandLine("items"));
        addCommandButton("back", "返回", e -> executeCommandLine("back"));
        addCommandButton("eat", "吃饼干", e -> executeCommandLine("eat cookie"));
        addCommandButton("save", "保存", e -> promptAndExecuteSave());
        addCommandButton("load", "载入", e -> promptAndExecuteLoad());
        addCommandButton("saves", "存档列表", e -> executeCommandLine("saves"));
        addCommandButton("delete", "删除存档", e -> promptAndExecuteDelete());
        addCommandButton("help", "帮助", e -> executeCommandLine("help"));
        addCommandButton("quit", "退出", e -> promptAndExecuteQuit());
    }

    private void addCommandButton(String key, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.addActionListener(listener);
        commandButtons.put(key, button);
        // 美化命令按钮：主色调、白字、加粗字体和内边距
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setBackground(new Color(66, 133, 244));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(6, 12, 6, 12));
        commandPanel.add(button);
    }

    private void appendWelcomeText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 欢迎来到祖尔世界！ ===\n");
        sb.append("祖尔世界是一个全新的、令人兴奋的冒险游戏。\n");
        sb.append("输入框支持直接输入命令，右侧可以快速切换方向。\n");
        sb.append("提示：可点击房间中的物品拾取，点击背包图标查看/丢弃物品。\n\n");
        sb.append(game.getCurrentRoom().getLongDescription()).append("\n");
        messageBoxPanel.enqueueText(sb.toString(), null);
    }

    private void executeInputCommand() {
        String input = commandField.getText();
        commandField.setText("");
        executeCommandLine(input);
    }

    private void executeCommandLine(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        Game.ExecutionResult result = game.executeCommandLine(input);
        handleExecutionResult(result);
    }

    private void handleExecutionResult(Game.ExecutionResult result) {
        refreshAllViews();

        if (result.isRoomChanged()) {
            switchRoom(result.getCurrentRoom());
        }

        String output = result.getOutput();
        Runnable afterMessage = null;
        if (result.isFinished()) {
            afterMessage = this::dispose;
        }
        messageBoxPanel.enqueueText(output, afterMessage);
    }

    private void switchRoom(Room room) {
        if (room == null || switchingRoom) {
            return;
        }
        switchingRoom = true;
        fadeGlassPane.play(() -> {
            roomCardLayout.show(roomCardPanel, cardName(room));
            refreshScene(room);
            backgroundCanvas.repaint();
            switchingRoom = false;
        });
    }

    private void showCurrentRoom(boolean refreshScene) {
        Room room = game.getCurrentRoom();
        roomCardLayout.show(roomCardPanel, cardName(room));
        if (refreshScene) {
            refreshScene(room);
        }
    }

    private void refreshAllViews() {
        refreshHud();
        refreshExits();
        refreshCommandButtonState();
        for (RoomScenePanel panel : roomSceneMap.values()) {
            panel.refreshItems();
        }
        if (inventoryDialog != null && inventoryDialog.isShowing()) {
            inventoryDialog.refreshInventory();
        }
        backgroundCanvas.repaint();
    }

    private void refreshScene(Room room) {
        RoomScenePanel panel = roomSceneMap.get(room);
        if (panel != null) {
            panel.refreshItems();
        }
    }

    private void refreshHud() {
        Player player = game.getPlayer();
        Room room = game.getCurrentRoom();
        playerNameLabel.setText("玩家：" + player.getName());
        roomLabel.setText("当前位置：" + room.getShortDescription());
        weightLabel.setText("负重：" + String.format("%.1f", player.getCurrentWeight()) + " / " + String.format("%.1f", player.getMaxWeight()));
        capacityLabel.setText("可用负重：" + String.format("%.1f", player.getMaxWeight() - player.getCurrentWeight()) + "kg");
        refreshBagIcon();
    }

    private void refreshBagIcon() {
        ImageIcon icon = createScaledIcon("bag.png", BAG_ICON_SIZE, BAG_ICON_SIZE);
        bagIconLabel.setIcon(icon);
        bagIconLabel.setText(null);
    }

    private void refreshExits() {
        exitsPanel.removeAll();
        exitsPanel.add(createPanelTitle("可前往方向"));
        exitsPanel.add(Box.createVerticalStrut(8));

        Room room = game.getCurrentRoom();
        String[] directions = {"north", "south", "east", "west", "up", "down"};
        boolean hasExit = false;
        for (String direction : directions) {
            if (room.getExit(direction) != null) {
                hasExit = true;
                JButton button = new JButton(directionToChinese(direction));
                button.setFocusable(false);
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.addActionListener(e -> executeCommandLine("go " + direction));
                // 美化出口按钮：与底部命令按钮保持一致的蓝色主题
                button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
                button.setBackground(new Color(66, 133, 244));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setOpaque(true);
                button.setBorder(new EmptyBorder(6, 12, 6, 12));
                exitsPanel.add(button);
                exitsPanel.add(Box.createVerticalStrut(6));
            }
        }

        if (!hasExit) {
            JLabel label = new JLabel("没有可用出口");
            label.setForeground(Color.WHITE);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            exitsPanel.add(label);
        }

        exitsPanel.add(Box.createVerticalGlue());
        exitsPanel.revalidate();
        exitsPanel.repaint();
    }

    private void refreshCommandButtonState() {
        Player player = game.getPlayer();
        Room room = game.getCurrentRoom();
        setButtonEnabled("go", hasAnyExit(room));
        setButtonEnabled("take", !room.getItems().isEmpty());
        setButtonEnabled("drop", !player.getInventory().isEmpty());
        setButtonEnabled("eat", player.hasItem("cookie"));
        setButtonEnabled("back", !game.getRoomHistory().isEmpty());
        for (String key : new String[]{"look", "items", "save", "load", "saves", "delete", "help", "quit"}) {
            setButtonEnabled(key, true);
        }
    }

    private boolean hasAnyExit(Room room) {
        for (String direction : new String[]{"north", "south", "east", "west", "up", "down"}) {
            if (room.getExit(direction) != null) {
                return true;
            }
        }
        return false;
    }

    private void setButtonEnabled(String key, boolean enabled) {
        JButton button = commandButtons.get(key);
        if (button != null) {
            button.setEnabled(enabled);
        }
    }

    private void promptAndExecuteGo() {
        Room room = game.getCurrentRoom();
        List<String> directions = new ArrayList<>();
        List<String> directionLabels = new ArrayList<>();
        for (String direction : new String[]{"north", "south", "east", "west", "up", "down"}) {
            if (room.getExit(direction) != null) {
                directions.add(direction);
                directionLabels.add(directionToChinese(direction));
            }
        }
        if (directions.isEmpty()) {
            messageBoxPanel.enqueueText("当前房间没有可移动方向。\n", null);
            return;
        }
        Object selected = JOptionPane.showInputDialog(
                this,
                "请选择前往方向：",
                "移动",
                JOptionPane.QUESTION_MESSAGE,
                null,
                directionLabels.toArray(),
                directionLabels.get(0)
        );
        if (selected != null) {
            int index = directionLabels.indexOf(selected.toString());
            if (index >= 0) {
                executeCommandLine("go " + directions.get(index));
            }
        }
    }

    private void promptAndExecuteTake() {
        Room room = game.getCurrentRoom();
        if (room.getItems().isEmpty()) {
            messageBoxPanel.enqueueText("当前房间没有可以拾取的物品。\n", null);
            return;
        }
        Item[] items = room.getItems().toArray(new Item[0]);
        Object selected = JOptionPane.showInputDialog(this, "请选择要拾取的物品：", "拾取物品", JOptionPane.QUESTION_MESSAGE, null, items, items[0]);
        if (selected instanceof Item) {
            executeCommandLine("take " + ((Item) selected).getName());
        }
    }

    private void promptAndExecuteSave() {
        String input = JOptionPane.showInputDialog(this, "输入要覆盖的存档ID，留空则新建存档：", "保存游戏", JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;
        }
        input = input.trim();
        if (input.isEmpty()) {
            executeCommandLine("save");
        } else {
            executeCommandLine("save " + input);
        }
    }

    private void promptAndExecuteLoad() {
        String input = JOptionPane.showInputDialog(this, "输入要载入的存档ID，留空则载入最近存档：", "载入游戏", JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return;
        }
        input = input.trim();
        if (input.isEmpty()) {
            executeCommandLine("load");
        } else {
            executeCommandLine("load " + input);
        }
    }

    private void promptAndExecuteDelete() {
        String input = JOptionPane.showInputDialog(this, "输入要删除的存档ID：", "删除存档", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        executeCommandLine("delete " + input.trim());
    }

    private void promptAndExecuteQuit() {
        int choice = JOptionPane.showConfirmDialog(this, "确定要退出游戏吗？", "退出游戏", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            executeCommandLine("quit");
        }
    }

    private void openInventoryDialog() {
        if (inventoryDialog == null) {
            inventoryDialog = new InventoryDialog(this);
        }
        inventoryDialog.refreshInventory();
        inventoryDialog.setLocationRelativeTo(this);
        inventoryDialog.setVisible(true);
    }

    private void refreshSceneForCurrentRoom() {
        refreshScene(game.getCurrentRoom());
    }

    private String cardName(Room room) {
        return room.getShortDescription();
    }

    private JLabel createPanelTitle(String text) {
        JLabel label = new JLabel(text);
        // 更醒目的标题样式（黑色、稍大）
        label.setForeground(Color.BLACK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private BufferedImage loadImage(String fileName) {
        if (fileName == null) {
            return null;
        }
        BufferedImage cached = imageCache.get(fileName);
        if (cached != null) {
            return cached;
        }

        File file = new File("picture", fileName);
        if (!file.exists()) {
            imageCache.put(fileName, null);
            return null;
        }

        try {
            BufferedImage image = ImageIO.read(file);
            imageCache.put(fileName, image);
            return image;
        } catch (IOException e) {
            imageCache.put(fileName, null);
            return null;
        }
    }

    private ImageIcon createScaledIcon(String fileName, int width, int height) {
        BufferedImage image = loadImage(fileName);
        if (image == null) {
            image = createPlaceholderImage(fileName, width, height);
        }
        Image scaled = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private BufferedImage createPlaceholderImage(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(40, 40, 40, 220));
        g.fillRoundRect(0, 0, width, height, 16, 16);
        g.setColor(new Color(220, 220, 220));
        g.drawRoundRect(1, 1, width - 2, height - 2, 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, width / 7)));
        FontMetrics metrics = g.getFontMetrics();
        String label = text.replace(".png", "");
        int stringWidth = metrics.stringWidth(label);
        int x = Math.max(4, (width - stringWidth) / 2);
        int y = (height + metrics.getAscent()) / 2 - 4;
        g.drawString(label, x, y);
        g.dispose();
        return image;
    }

    private String roomBackgroundFile(Room room) {
        String desc = room.getShortDescription().toLowerCase();
        // 支持中英文房间描述匹配
        if (desc.contains("outside") || desc.contains("entrance") || desc.contains("主入口") || desc.contains("入口") || desc.contains("外")) {
            return "room_outside.png";
        }
        if (desc.contains("pub") || desc.contains("酒吧")) {
            return "room_pub.png";
        }
        if (desc.contains("lab") || desc.contains("实验")) {
            return "room_lab.png";
        }
        if (desc.contains("office") || desc.contains("教务") || desc.contains("办公室")) {
            return "room_office.png";
        }
        if (desc.contains("theater") || desc.contains("讲堂") || desc.contains("礼堂")) {
            return "room_theater.png";
        }
        return "room_outside.png";
    }

    private String itemImageFile(Item item) {
        String name = item.getName() == null ? "" : item.getName().toLowerCase();
        switch (name) {
            case "key":
                return "item_key.png";
            case "map":
                return "item_map.png";
            case "notebook":
                return "item_notebook.png";
            case "pen":
                return "item_pen.png";
            case "beer":
                return "item_beer.png";
            case "coin":
                return "item_coin.png";
            case "book":
                return "item_book.png";
            case "laptop":
            case "labtop":
                return "item_labtop.png";
            case "coffee":
                return "item_coffee.png";
            case "paper":
                return "item_paper.png";
            case "cookie":
                return "item_cookie.png";
            default:
                return "item_" + name + ".png";
        }
    }

    private String directionToChinese(String direction) {
        if ("north".equals(direction)) return "北";
        if ("south".equals(direction)) return "南";
        if ("east".equals(direction)) return "东";
        if ("west".equals(direction)) return "西";
        if ("up".equals(direction)) return "上";
        if ("down".equals(direction)) return "下";
        return direction;
    }

    private String normalizeRoomKey(Room room) {
        String desc = room.getShortDescription().toLowerCase();
        if (desc.contains("outside") || desc.contains("entrance") || desc.contains("主入口") || desc.contains("入口") || desc.contains("外"))
            return "outside";
        if (desc.contains("pub") || desc.contains("酒吧")) return "pub";
        if (desc.contains("lab") || desc.contains("实验")) return "lab";
        if (desc.contains("office") || desc.contains("教务") || desc.contains("办公室")) return "office";
        if (desc.contains("theater") || desc.contains("讲堂") || desc.contains("礼堂")) return "theater";
        return "outside";
    }

    private void initFixedAnchors() {
        fixedRoomAnchors.clear();
        fixedRoomAnchors.put("outside", anchorsOf(
                "key", 0.24, 0.61,
                "map", 0.64, 0.56,
                "cookie", 0.44, 0.47
        ));
        fixedRoomAnchors.put("theater", anchorsOf(
                "notebook", 0.45, 0.62,
                "pen", 0.69, 0.52,
                "cookie", 0.34, 0.46
        ));
        fixedRoomAnchors.put("pub", anchorsOf(
                "beer", 0.40, 0.63,
                "coin", 0.66, 0.56,
                "cookie", 0.53, 0.46
        ));
        fixedRoomAnchors.put("lab", anchorsOf(
                "book", 0.31, 0.60,
                "laptop", 0.63, 0.52,
                "cookie", 0.47, 0.46
        ));
        fixedRoomAnchors.put("office", anchorsOf(
                "coffee", 0.36, 0.58,
                "paper", 0.68, 0.50,
                "cookie", 0.52, 0.43
        ));
    }

    private Map<String, double[]> anchorsOf(Object... kv) {
        Map<String, double[]> result = new HashMap<>();
        for (int i = 0; i + 2 < kv.length; i += 3) {
            String key = String.valueOf(kv[i]);
            double x = ((Number) kv[i + 1]).doubleValue();
            double y = ((Number) kv[i + 2]).doubleValue();
            result.put(key, new double[]{x, y});
        }
        return result;
    }

    private double[] fixedAnchorFor(Room room, Item item) {
        String roomKey = normalizeRoomKey(room);
        String itemKey = item.getName() == null ? "" : item.getName().toLowerCase();
        Map<String, double[]> roomAnchors = fixedRoomAnchors.getOrDefault(roomKey, Collections.emptyMap());
        double[] fixed = roomAnchors.get(itemKey);
        if (fixed != null) {
            return fixed;
        }

        // 未配置坐标时，按名称哈希给一个稳定的回退位置，不会因其它物品消失而漂移。
        double[][] fallback = {
                {0.20, 0.56}, {0.35, 0.46}, {0.52, 0.60}, {0.68, 0.47}, {0.81, 0.62},
                {0.26, 0.74}, {0.44, 0.72}, {0.62, 0.71}, {0.79, 0.75}
        };
        int index = Math.abs(itemKey.hashCode()) % fallback.length;
        return fallback[index];
    }

    private String itemTooltip(Item item) {
        return "<html>描述：" + item.getDescription()
                + "<br/>重量：" + String.format("%.1f", item.getWeight()) + "kg</html>";
    }

    private String itemDisplayName(Item item) {
        String key = item.getName() == null ? "" : item.getName().toLowerCase();
        switch (key) {
            case "key":
                return "钥匙";
            case "map":
                return "地图";
            case "notebook":
                return "笔记本";
            case "pen":
                return "钢笔";
            case "beer":
                return "啤酒";
            case "coin":
                return "金币";
            case "book":
                return "书";
            case "laptop":
                return "笔记本电脑";
            case "labtop":
                return "笔记本电脑";
            case "coffee":
                return "咖啡";
            case "paper":
                return "文件";
            case "cookie":
                return "魔法饼干";
            default:
                return item.getDescription() != null ? item.getDescription() : item.getName();
        }
    }

    /**
     * 全窗口背景画布：将当前房间背景铺满整个界面。
     */
    private class BackgroundCanvasPanel extends JPanel {
        BackgroundCanvasPanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Room room = game.getCurrentRoom();
            if (room == null) {
                return;
            }
            BufferedImage background = loadImage(roomBackgroundFile(room));
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            } else {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(35, 40, 55));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        }
    }

    /**
     * 房间场景：背景图 + 物品贴图。
     */
    private class RoomScenePanel extends JPanel {
        private final Room room;
        private final List<ItemSprite> itemSprites = new ArrayList<>();

        RoomScenePanel(Room room) {
            this.room = room;
            setLayout(null);
            setOpaque(false);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    layoutItemLabels();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            // 前景层只承载可交互的物品图标，不再重复绘制房间背景。
            super.paintComponent(g);
        }

        void refreshItems() {
            removeAll();
            itemSprites.clear();

            List<Item> items = room.getItems();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                JLabel label = createItemLabel(item);
                label.setSize(100, 104);
                itemSprites.add(new ItemSprite(item, label));
                add(label);
            }

            layoutItemLabels();
            revalidate();
            repaint();
        }

        private void layoutItemLabels() {
            for (ItemSprite sprite : itemSprites) {
                JLabel label = sprite.label;
                double[] p = fixedAnchorFor(room, sprite.item);
                int x = (int) (p[0] * Math.max(1, getWidth()));
                int y = (int) (p[1] * Math.max(1, getHeight()));
                label.setBounds(x, y, 100, 104);
            }
        }

        private JLabel createItemLabel(Item item) {
            JLabel label = new JLabel(itemDisplayName(item), createScaledIcon(itemImageFile(item), ITEM_ICON_SIZE, ITEM_ICON_SIZE), SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            // 物品名称显示风格与玩家信息一致：黑色、放大、半透明白底
            label.setForeground(Color.BLACK);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
            label.setToolTipText(itemTooltip(item));
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.setOpaque(true);
            label.setBackground(new Color(255, 255, 255, 140));
            label.setBorder(new EmptyBorder(4, 6, 4, 6));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    executeCommandLine("take " + item.getName());
                }
            });
            return label;
        }

        private class ItemSprite {
            private final Item item;
            private final JLabel label;

            private ItemSprite(Item item, JLabel label) {
                this.item = item;
                this.label = label;
            }
        }
    }

    /**
     * 黑屏动画覆盖层。
     */
    private class FadeGlassPane extends JComponent {
        private float alpha = 0f;
        private Timer timer;
        private Runnable midpointAction;
        private boolean fadingIn = true;

        FadeGlassPane() {
            setOpaque(false);
        }

        void play(Runnable midpointAction) {
            if (timer != null && timer.isRunning()) {
                return;
            }
            this.midpointAction = midpointAction;
            alpha = 0f;
            fadingIn = true;
            setVisible(true);
            timer = new Timer(15, e -> tick());
            timer.start();
        }

        private void tick() {
            if (fadingIn) {
                alpha += 0.08f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadingIn = false;
                    if (midpointAction != null) {
                        midpointAction.run();
                    }
                }
            } else {
                alpha -= 0.08f;
                if (alpha <= 0f) {
                    alpha = 0f;
                    timer.stop();
                    setVisible(false);
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0f, 0f, 0f, Math.max(0f, Math.min(1f, alpha))));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    /**
     * 半透明输出框，带逐字显示动画。
     */
    private class MessageBoxPanel extends JPanel {
        private final JTextArea textArea = new JTextArea();
        private final Queue<MessageTask> queue = new ArrayDeque<>();
        private final StringBuilder currentLine = new StringBuilder();
        private final Timer timer;
        private MessageTask currentTask;
        private int index;

        MessageBoxPanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(8, 8, 8, 8));

            textArea.setEditable(false);
            textArea.setOpaque(false);
            textArea.setForeground(Color.WHITE);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBorder(new EmptyBorder(8, 10, 8, 10));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            add(scrollPane, BorderLayout.CENTER);

            timer = new Timer(18, e -> tick());
        }

        void enqueueText(String text, Runnable callback) {
            if (text == null) {
                if (callback != null) {
                    SwingUtilities.invokeLater(callback);
                }
                return;
            }
            String normalized = text.replace("\r\n", "\n");
            if (normalized.trim().isEmpty()) {
                if (callback != null) {
                    SwingUtilities.invokeLater(callback);
                }
                return;
            }
            queue.add(new MessageTask(normalized, callback));
            if (!timer.isRunning() && currentTask == null) {
                startNextTask();
            }
        }

        private void startNextTask() {
            currentTask = queue.poll();
            index = 0;
            currentLine.setLength(0);
            if (currentTask == null) {
                timer.stop();
                return;
            }
            if (textArea.getDocument().getLength() > 0) {
                textArea.append("\n");
            }
            timer.start();
        }

        private void tick() {
            if (currentTask == null) {
                timer.stop();
                return;
            }

            if (index < currentTask.text.length()) {
                char c = currentTask.text.charAt(index++);
                currentLine.append(c);
                textArea.append(String.valueOf(c));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            } else {
                timer.stop();
                Runnable callback = currentTask.callback;
                currentTask = null;
                if (callback != null) {
                    callback.run();
                }
                startNextTask();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 170));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }

        private class MessageTask {
            private final String text;
            private final Runnable callback;

            private MessageTask(String text, Runnable callback) {
                this.text = text;
                this.callback = callback;
            }
        }
    }

    /**
     * 背包窗口。
     */
    private class InventoryDialog extends JDialog {
        private final JPanel gridPanel = new JPanel();

        InventoryDialog(JFrame owner) {
            super(owner, "背包", false);
            setSize(620, 460);
            setResizable(false);
            setLayout(new BorderLayout());

            JPanel content = new JPanel(new BorderLayout());
            content.setBorder(new EmptyBorder(18, 18, 18, 18));
            content.setBackground(new Color(245, 245, 245));
            add(content, BorderLayout.CENTER);

            gridPanel.setOpaque(false);
            content.add(gridPanel, BorderLayout.CENTER);
        }

        void refreshInventory() {
            gridPanel.removeAll();
            List<Item> items = game.getPlayer().getInventory();
            int columns = 4;
            int rows = Math.max(2, (int) Math.ceil(Math.max(1, items.size()) / (double) columns));
            gridPanel.setLayout(new GridLayout(rows, columns, 10, 10));

            for (Item item : items) {
                gridPanel.add(createInventoryItemButton(item));
            }

            int emptyCells = rows * columns - items.size();
            for (int i = 0; i < emptyCells; i++) {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                empty.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
                gridPanel.add(empty);
            }

            gridPanel.revalidate();
            gridPanel.repaint();
        }

        private JComponent createInventoryItemButton(Item item) {
            JLabel label = new JLabel(itemDisplayName(item), createScaledIcon(itemImageFile(item), 64, 64), SwingConstants.CENTER);
            label.setVerticalTextPosition(SwingConstants.BOTTOM);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
            label.setForeground(new Color(30, 30, 30));
            label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
            label.setToolTipText(itemTooltip(item));
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // 背包物品卡片美化：白底、圆角边框与内边距
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                    new EmptyBorder(8, 8, 8, 8)
            ));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int choice = JOptionPane.showConfirmDialog(InventoryDialog.this,
                            "你确定要丢弃" + item.getName() + "吗？",
                            "确认丢弃",
                            JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        executeCommandLine("drop " + item.getName());
                        refreshInventory();
                        refreshAllViews();
                    }
                }
            });
            return label;
        }
    }
}
