package client;

//import component.Chat_Right;UIChatFrame
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.setDefaultLookAndFeelDecorated;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.miginfocom.swing.MigLayout;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class ChatFrame extends javax.swing.JFrame {

    private ChatFrame() {
        initComponents();
        Toolkit toolkit = getToolkit();
        Dimension size = toolkit.getScreenSize();
        setLocation(size.width / 2 - getWidth() / 2, size.height / 2 - getHeight() / 2);
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    JComboBox onlineUsers = new component.Combobox();
    private JLabel lbReceiver = new JLabel(" ");
    private String username;
    private DataInputStream dis;
    private DataOutputStream dos;
    private HashMap<String, JTextPane> chatWindows = new HashMap<String, JTextPane>();

    Thread receiver;

    private void autoScroll() {
        chatPanel.getVerticalScrollBar().setValue(chatPanel.getVerticalScrollBar().getMaximum());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Insert a emoji into chat pane.
     */
    private void newEmoji(String username, String emoji, Boolean yourMessage) {

        StyledDocument doc;
        if (username.equals(this.username)) {
            doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
        } else {
            doc = chatWindows.get(username).getStyledDocument();
        }

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.red);
        } else {
            StyleConstants.setForeground(userStyle, Color.BLUE);
        }
        // In ra màn hình tên người gửi
        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
        } catch (BadLocationException e) {
        }

        Style iconStyle = doc.getStyle("Icon style");
        if (iconStyle == null) {
            iconStyle = doc.addStyle("Icon style", null);
        }

        StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));

        // In ra màn hình Emoji
        try {
            doc.insertString(doc.getLength(), "invisible text", iconStyle);
        } catch (BadLocationException e) {
        }

        // Xuống dòng
        try {
            doc.insertString(doc.getLength(), "\n", userStyle);
        } catch (BadLocationException e) {
        }

        autoScroll();
    }

    /**
     * Insert a file into chat pane.
     */
    private void newFile(String username, String filename, byte[] file, Boolean yourMessage) {

        StyledDocument doc;
        String window = null;
        if (username.equals(this.username)) {
            window = lbReceiver.getText();
        } else {
            window = username;
        }
        doc = chatWindows.get(window).getStyledDocument();

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.red);
        } else {
            StyleConstants.setForeground(userStyle, Color.BLUE);
        }

        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
        } catch (BadLocationException e) {
        }

        Style linkStyle = doc.getStyle("Link style");
        if (linkStyle == null) {
            linkStyle = doc.addStyle("Link style", null);
            StyleConstants.setForeground(linkStyle, Color.BLUE);
            StyleConstants.setUnderline(linkStyle, true);
            StyleConstants.setBold(linkStyle, true);
            linkStyle.addAttribute("link", new HyberlinkListener(filename, file));
        }

        if (chatWindows.get(window).getMouseListeners() != null) {
            // Tạo MouseListener cho các đường dẫn tải về file
            chatWindows.get(window).addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    Element ele = doc.getCharacterElement(chatWindow.viewToModel(e.getPoint()));
                    AttributeSet as = ele.getAttributes();
                    HyberlinkListener listener = (HyberlinkListener) as.getAttribute("link");
                    if (listener != null) {
                        listener.execute();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

            });
        }

        // In ra đường dẫn tải file
        try {
            doc.insertString(doc.getLength(), "<" + filename + ">", linkStyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        // Xuống dòng
        try {
            doc.insertString(doc.getLength(), "\n", userStyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        autoScroll();
    }

    /**
     * Insert a new message into chat pane.
     */
    private void newMessage(String username, String message, Boolean yourMessage) {

        StyledDocument doc;
        if (username.equals(this.username)) {
            doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
        } else {
            doc = chatWindows.get(username).getStyledDocument();
        }

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.red);
        } else {
            StyleConstants.setForeground(userStyle, Color.BLUE);
        }

        // In ra tên người gửi
        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
        } catch (BadLocationException e) {
        }

        Style messageStyle = doc.getStyle("Message style");
        if (messageStyle == null) {
            messageStyle = doc.addStyle("Message style", null);
            StyleConstants.setForeground(messageStyle, Color.BLACK);
            StyleConstants.setBold(messageStyle, false);
        }

        // In ra nội dung tin nhắn
        try {
            doc.insertString(doc.getLength(), message + "\n", messageStyle);
        } catch (BadLocationException e) {
        }

        autoScroll();
    }

    /**
     * Create the frame.
     */
    public ChatFrame(String username, DataInputStream dis, DataOutputStream dos) {
        setTitle("App Chat 2DK");
        this.username = username;
        this.dis = dis;
        this.dos = dos;
        receiver = new Thread(new Receiver(dis));
        receiver.start();

        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 586, 500);
        contentPane = new JPanel();
//        contentPane.setBorder(new EmptyBorder(1/2, 1/2, 1/2, 1/2));
        contentPane.setBackground(new Color(250, 250, 250));
        setContentPane(contentPane);

        JPanel header = new JPanel();
        header.setBackground(new Color(204, 255, 255));

        txtMessage = new JTextField();
        txtMessage.setEnabled(false);
        txtMessage.setColumns(10);

        btnSend = new JButton("");
        btnSend.setBorderPainted(false);
        btnSend.setContentAreaFilled(false);
        btnSend.setEnabled(false);
        btnSend.setIcon(new ImageIcon("src\\data\\icon\\component\\reply.png"));

        chatPanel = new JScrollPane();
        chatPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtMessage.setPreferredSize(new Dimension(600, 600));
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(250, 250, 250));

        btnFile = new JButton("");
        btnFile.setBackground(new java.awt.Color(250, 250, 250));
        btnFile.setForeground(new java.awt.Color(0, 0, 0));
        btnFile.setBorderPainted(false);
        btnFile.setContentAreaFilled(false);
        btnFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Hiển thị hộp thoại cho người dùng chọn file để gửi
                JFileChooser fileChooser = new JFileChooser();
                int rVal = fileChooser.showOpenDialog(contentPane.getParent());
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    byte[] selectedFile = new byte[(int) fileChooser.getSelectedFile().length()];
                    BufferedInputStream bis;
                    try {
                        bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));
                        // Đọc file vào biến selectedFile
                        bis.read(selectedFile, 0, selectedFile.length);

                        dos.writeUTF("File");
                        dos.writeUTF(lbReceiver.getText());
                        dos.writeUTF(fileChooser.getSelectedFile().getName());
                        dos.writeUTF(String.valueOf(selectedFile.length));

                        int size = selectedFile.length;
                        int bufferSize = 2048;
                        int offset = 0;

                        // Lần lượt gửi cho server từng buffer cho đến khi hết file
                        while (size > 0) {
                            dos.write(selectedFile, offset, Math.min(size, bufferSize));
                            offset += Math.min(size, bufferSize);
                            size -= bufferSize;
                        }

                        dos.flush();

                        bis.close();

                        // In ra màn hình file
                        newFile(username, fileChooser.getSelectedFile().getName(), selectedFile, true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        btnFile.setEnabled(false);
        btnFile.setIcon(new ImageIcon("src\\data\\icon\\component\\attach.png"));
        
        JPanel emojis = new JPanel();
        emojis.setBackground(new Color(250, 250, 250));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addComponent(header, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(emojis, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(txtMessage, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnFile, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(header, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(emojis, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnFile, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)))
        );
// <editor-fold defaultstate="collapsed" desc="JPanel of icon ">

        JLabel smileIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\smile.png"));
        smileIcon.addMouseListener(new IconListener(smileIcon.getIcon().toString()));
        emojis.add(smileIcon);

        JLabel bigSmileIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\big-smile.png"));
        bigSmileIcon.addMouseListener(new IconListener(bigSmileIcon.getIcon().toString()));
        emojis.add(bigSmileIcon);

        JLabel happyIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\happy.png"));
        happyIcon.addMouseListener(new IconListener(happyIcon.getIcon().toString()));
        emojis.add(happyIcon);

        JLabel loveIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\love.png"));
        loveIcon.addMouseListener(new IconListener(loveIcon.getIcon().toString()));
        emojis.add(loveIcon);

        JLabel sadIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\sad.png"));
        sadIcon.addMouseListener(new IconListener(sadIcon.getIcon().toString()));
        emojis.add(sadIcon);

        JLabel madIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\mad.png"));
        madIcon.addMouseListener(new IconListener(madIcon.getIcon().toString()));
        emojis.add(madIcon);

        JLabel suspiciousIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\suspicious.png"));
        suspiciousIcon.addMouseListener(new IconListener(suspiciousIcon.getIcon().toString()));
        emojis.add(suspiciousIcon);

        JLabel angryIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\angry.png"));
        angryIcon.addMouseListener(new IconListener(angryIcon.getIcon().toString()));
        emojis.add(angryIcon);

        JLabel confusedIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\confused.png"));
        confusedIcon.addMouseListener(new IconListener(confusedIcon.getIcon().toString()));
        emojis.add(confusedIcon);

        JLabel unhappyIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\unhappy.png"));
        unhappyIcon.addMouseListener(new IconListener(unhappyIcon.getIcon().toString()));
        emojis.add(unhappyIcon);

        JLabel appleIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\apple.png"));
        appleIcon.addMouseListener(new IconListener(appleIcon.getIcon().toString()));
        emojis.add(appleIcon);


        JLabel cakeIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\cake1.png"));
        cakeIcon.addMouseListener(new IconListener(cakeIcon.getIcon().toString()));
        emojis.add(cakeIcon);

        JLabel vietnamIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\vietnam.png"));
        vietnamIcon.addMouseListener(new IconListener(vietnamIcon.getIcon().toString()));
        emojis.add(vietnamIcon);

        JLabel usIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\us1.png"));
        usIcon.addMouseListener(new IconListener(usIcon.getIcon().toString()));
        emojis.add(usIcon);

        JLabel ukIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\uk1.png"));
        ukIcon.addMouseListener(new IconListener(ukIcon.getIcon().toString()));
        emojis.add(ukIcon);

        JLabel canadaIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\canada.png"));
        canadaIcon.addMouseListener(new IconListener(canadaIcon.getIcon().toString()));
        emojis.add(canadaIcon);


        JLabel spainIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\spainIcon.png"));
        spainIcon.addMouseListener(new IconListener(spainIcon.getIcon().toString()));
        emojis.add(spainIcon);

        JLabel egyptIcon = new JLabel(new ImageIcon("src\\data\\icon\\emoji\\egyptIcon.png"));
        egyptIcon.addMouseListener(new IconListener(egyptIcon.getIcon().toString()));
        emojis.add(egyptIcon);


        JLabel australiaIcon = new JLabel(new ImageIcon("src\\src\\data\\icon\\emoji\\australia.png"));
        australiaIcon.addMouseListener(new IconListener(australiaIcon.getIcon().toString()));
        emojis.add(australiaIcon);

        JLabel userImage = new JLabel(new ImageIcon("src\\data\\icon\\component\\user-icon.png"));
//</editor-fold>
        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 250, 250));
        // onlineUsers.setBackground(new java.awt.Color(153, 153, 153));
        JLabel lblNewLabel_1 = new JLabel("LIST FRIENDS");
        lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 11));
        GroupLayout gl_leftPanel = new GroupLayout(leftPanel);
        gl_leftPanel.setHorizontalGroup(
                gl_leftPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addGap(25)
                                .addComponent(userImage, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addGap(25))
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addGap(28)
                                .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(29))
                        .addGroup(Alignment.TRAILING, gl_leftPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(onlineUsers, 0, 60, Short.MAX_VALUE)
                                .addContainerGap())
        );
        onlineUsers.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lbReceiver.setText((String) onlineUsers.getSelectedItem());
                    if (chatWindow != chatWindows.get(lbReceiver.getText())) {
                        txtMessage.setText("");
                        chatWindow = chatWindows.get(lbReceiver.getText());
                        chatPanel.setViewportView(chatWindow);
                        chatPanel.validate();
                    }

                    if (lbReceiver.getText().isEmpty()) {
                        btnSend.setEnabled(false);
                        btnFile.setEnabled(false);
                        txtMessage.setEnabled(false);
                    } else {
                        btnSend.setEnabled(true);
                        btnFile.setEnabled(true);
                        txtMessage.setEnabled(true);
                    }
                }

            }
        });

        gl_leftPanel.setVerticalGroup(
                gl_leftPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addGap(5)
                                .addComponent(userImage)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(panel, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                .addGap(41)
                                .addComponent(lblNewLabel_1)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(onlineUsers, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(150, Short.MAX_VALUE))
        );

        JLabel lbUsername = new JLabel(this.username);
        lbUsername.setFont(new Font("Arial", Font.BOLD, 15));
        panel.add(lbUsername);
        leftPanel.setLayout(gl_leftPanel);

        JLabel headerContent = new JLabel("Message");
        headerContent.setForeground(new java.awt.Color(0, 0, 0));
        headerContent.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(headerContent);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setBackground(new Color(250, 250, 250));
        chatPanel.setColumnHeaderView(usernamePanel);

        lbReceiver.setFont(new Font("Arial", Font.BOLD, 16));
        usernamePanel.add(lbReceiver);

        chatWindows.put(" ", new JTextPane());
        chatWindow = chatWindows.get(" ");
        chatWindow.setFont(new Font("Arial", Font.PLAIN, 14));
        chatWindow.setEditable(false);

        chatPanel.setViewportView(chatWindow);
        contentPane.setLayout(gl_contentPane);

        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtMessage.getText().isEmpty() || lbReceiver.getText().isEmpty()) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }
        });

        // Set action perform to send button.
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    dos.writeUTF("Text");
                    dos.writeUTF(lbReceiver.getText());
                    dos.writeUTF(txtMessage.getText());
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    newMessage("ERROR", "Network error!", true);
                }

                // In ra tin nhắn lên màn hình chat với người nhận
                newMessage(username, txtMessage.getText(), true);
                txtMessage.setText("");
            }
        });

        this.getRootPane().setDefaultButton(btnSend);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                try {
                    dos.writeUTF("Log out");
                    dos.flush();

                    try {
                        receiver.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    if (dos != null) {
                        dos.close();
                    }
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ChatFrame frame = new ChatFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Luồng nhận tin nhắn từ server của mỗi client
     */
    class Receiver implements Runnable {

        private DataInputStream dis;

        public Receiver(DataInputStream dis) {
            this.dis = dis;
        }

        @Override
        public void run() {
            try {

                while (true) {
                    // Chờ tin nhắn từ server
                    String method = dis.readUTF();
                    TypeMessage t = new TypeMessage(lbReceiver, chatWindows, username);
                    if (method.equals("Text")) {
                        // Nhận một tin nhắn văn bản
                        String sender = dis.readUTF();
                        String message = dis.readUTF();

                        // In tin nhắn lên màn hình chat với người gửi
                        newMessage(sender, message, false);
                    } else if (method.equals("Emoji")) {
                        // Nhận một tin nhắn Emoji
                        String sender = dis.readUTF();
                        String emoji = dis.readUTF();

                        // In tin nhắn lên màn hình chat với người gửi
                        newEmoji(sender, emoji, false);
                    } else if (method.equals("File")) {
                        // Nhận một file
                        String sender = dis.readUTF();
                        String filename = dis.readUTF();
                        int size = Integer.parseInt(dis.readUTF());
                        int bufferSize = 2048;
                        byte[] buffer = new byte[bufferSize];
                        ByteArrayOutputStream file = new ByteArrayOutputStream();

                        while (size > 0) {
                            dis.read(buffer, 0, Math.min(bufferSize, size));
                            file.write(buffer, 0, Math.min(bufferSize, size));
                            size -= bufferSize;
                        }

                        // In ra màn hình file đó
                        newFile(sender, filename, file.toByteArray(), false);

                    } else if (method.equals("Online users")) {
                        // Nhận yêu cầu cập nhật danh sách người dùng trực tuyến
                        String[] users = dis.readUTF().split(",");
                        onlineUsers.removeAllItems();

                        String chatting = lbReceiver.getText();

                        boolean isChattingOnline = false;

                        for (String user : users) {
                            if (user.equals(username) == false) {
                                // Cập nhật danh sách các người dùng trực tuyến vào ComboBox onlineUsers (trừ bản thân)
                                onlineUsers.addItem(user);
                                if (chatWindows.get(user) == null) {
                                    JTextPane temp = new JTextPane();
                                    temp.setFont(new Font("Arial", Font.PLAIN, 14));
                                    temp.setEditable(false);
                                    chatWindows.put(user, temp);
                                }
                            }
                            if (chatting.equals(user)) {
                                isChattingOnline = true;
                            }
                        }

                        if (isChattingOnline == false) {
                            // Nếu người đang chat không online thì chuyển hướng về màn hình mặc định và thông báo cho người dùng
                            onlineUsers.setSelectedItem(" ");
                            JOptionPane.showMessageDialog(null, chatting + " is offline!\n");
                        } else {
                            onlineUsers.setSelectedItem(chatting);
                        }

                        onlineUsers.validate();
                    } else if (method.equals("Safe to leave")) {
                        // Thông báo có thể thoát
                        break;
                    }

                }

            } catch (IOException ex) {
                System.err.println(ex);
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * MouseListener cho các đường dẫn tải file.
     */
    class HyberlinkListener extends AbstractAction {

        String filename;
        byte[] file;

        public HyberlinkListener(String filename, byte[] file) {
            this.filename = filename;
            this.file = Arrays.copyOf(file, file.length);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            execute();
        }

        public void execute() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(filename));
            int rVal = fileChooser.showSaveDialog(contentPane.getParent());
            if (rVal == JFileChooser.APPROVE_OPTION) {

                // Mở file đã chọn sau đó lưu thông tin xuống file đó
                File saveFile = fileChooser.getSelectedFile();
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(saveFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Hiển thị JOptionPane cho người dùng có muốn mở file vừa tải về không
                int nextAction = JOptionPane.showConfirmDialog(null, "Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?", "Successful", JOptionPane.YES_NO_OPTION);
                if (nextAction == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(saveFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bos != null) {
                    try {
                        bos.write(this.file);
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * MouseAdapter cho các Emoji.
     */
    class IconListener extends MouseAdapter {

        String emoji;

        public IconListener(String emoji) {
            this.emoji = emoji;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (txtMessage.isEnabled() == true) {

                try {
                    dos.writeUTF("Emoji");
                    dos.writeUTF(lbReceiver.getText());
                    dos.writeUTF(this.emoji);
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    newMessage("ERROR", "Network error!", true);
                }

                // In Emoji lên màn hình chat với người nhận
                newEmoji(username, this.emoji, true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        title = new javax.swing.JPanel();
        chatPanel = new javax.swing.JScrollPane();
        txtMessage = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatWindow = new javax.swing.JTextPane();
        contentPane = new javax.swing.JPanel();
        usernamePanel = new javax.swing.JPanel();
        panel = new javax.swing.JPanel();
        lbUsername = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        background.setBackground(new java.awt.Color(255, 255, 255));
        background.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        title.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout titleLayout = new javax.swing.GroupLayout(title);
        title.setLayout(titleLayout);
        titleLayout.setHorizontalGroup(
            titleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        titleLayout.setVerticalGroup(
            titleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        txtMessage.setColumns(10);

        btnFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/data/icon/component/attach.png"))); // NOI18N
        btnFile.setBorderPainted(false);
        btnFile.setContentAreaFilled(false);
        btnFile.setEnabled(false);
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        btnSend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/data/icon/component/reply.png"))); // NOI18N
        btnSend.setEnabled(false);

        jScrollPane1.setViewportView(chatWindow);

        javax.swing.GroupLayout contentPaneLayout = new javax.swing.GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout usernamePanelLayout = new javax.swing.GroupLayout(usernamePanel);
        usernamePanel.setLayout(usernamePanelLayout);
        usernamePanelLayout.setHorizontalGroup(
            usernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 167, Short.MAX_VALUE)
        );
        usernamePanelLayout.setVerticalGroup(
            usernamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        panel.setBackground(new java.awt.Color(98, 155, 243));

        lbUsername.setText(" ");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(lbUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lbUsername)
                .addContainerGap(233, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(contentPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(63, 63, 63)
                .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgroundLayout.createSequentialGroup()
                        .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                        .addComponent(btnFile)
                        .addGap(18, 18, 18)
                        .addComponent(btnSend)
                        .addGap(21, 21, 21))
                    .addComponent(jScrollPane1)
                    .addGroup(backgroundLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(backgroundLayout.createSequentialGroup()
                                .addComponent(usernamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(chatPanel)))))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(backgroundLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(contentPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(backgroundLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(usernamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFile)
                    .addComponent(btnSend))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel background;
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnSend;
    private javax.swing.JScrollPane chatPanel;
    private javax.swing.JTextPane chatWindow;
    private javax.swing.JPanel contentPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbUsername;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel title;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JPanel usernamePanel;
    // End of variables declaration//GEN-END:variables
}
