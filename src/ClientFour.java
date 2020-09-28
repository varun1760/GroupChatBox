import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClientFour extends JFrame implements Runnable{  // runnable to make multiple threads
                                                            // as we want receive message from multiple users
    JPanel panel;
    JLabel label;
    JButton send;
    JTextField text;
    JPanel chatArea;
    boolean typing;
    Box vertical = Box.createVerticalBox();

    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;

    // constructor
    ClientFour(){

        // building a chat window
        setSize(365, 515);   // size of chat window
        setLocation(100, 150);   // location of chat window in screen
        setLayout(null);    // null so inside element won't expand
//        setUndecorated(true);   // removing title bar which contains closing button (optional)

        // building a panel section
        panel = new JPanel();   // creating a panel
        panel.setLayout(null);  // setting to null layout
        panel.setBounds(0, 0, 350, 60);    // panel size with location
        panel.setBackground(new Color(90000000));   // setting unknown color to panel
//        panel.setBackground(new Color(7,94,84));  // color similar to whatsapp
        add(panel);

        // building back icon into panel section without using method
        addMyIcon("icons/back.png", 30, 30);  // calling method
        label.setBounds(5, 5, 30, 30);   // to set size with location
        panel.add(label); // adding into panel

/*  NOTE:
    1. addMouseListener method of Component class which takes interface MouseListener as argument
    2. MouseListener extends interface EventListener
    3. MouseAdopter is abstract class so we don't have to implement all it's abstract method
       but one we need which is mouseClicked
    4. MouseAdopter implements MouseListener that is why we can use it here as argument
    */

        // adding mouse event on back icon
        label.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.exit(0); // closes chat window
                    }
                });

        // building display picture using method addMyIcon
        addMyIcon("icons/avengers.png",60,60);
        label.setBounds(40, 0, 60, 60);
        panel.add(label);

        // building display Name label
        JLabel label_name = new JLabel("AVENGERS");
        label_name.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        label_name.setForeground(Color.white);
        label_name.setBounds(110, 10, 120, 20);
        panel.add(label_name);

        // building Name label
        JLabel name = new JLabel("C.AMERICA");
        name.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
        name.setForeground(Color.white);
        name.setBounds(110, 35, 120, 20);
        panel.add(name);
        // setting not typing status
        Timer timer = new Timer(1, event -> {
            if (!typing){
                name.setText("C.AMERICA");
            }
        });
        timer.setInitialDelay(500);    // delay (1/2) sec

        // building video call label using method addMyIcon
        addMyIcon("icons/video.png", 30, 30);
        label.setBounds(250, 15, 30, 30);
        panel.add(label);

        // building calling label using method addMyIcon
        addMyIcon("icons/phone.png", 30, 30);
        label.setBounds(290, 15, 30, 30);
        panel.add(label);

        // building option label using method addMyIcon
        addMyIcon("icons/3icon.png", 10, 30);
        label.setBounds(330, 15, 15, 30);
        panel.add(label);

        // building Chat Area
        chatArea = new JPanel();
        chatArea.setBounds(5, 65, 340, 350);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(5, 65, 340, 350);

        // auto scroll to bottom
        // Credit Source code: https://tips4java.wordpress.com/2013/03/03/smart-scrolling/
        new SmartScroller(scrollPane, SmartScroller.VERTICAL, SmartScroller.END);   // calling SmartScroller class
        add(scrollPane);

        // building another panel at bottom
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 420, 350, 60);
        panel.setBackground(new Color(90000000));
        add(panel);

// TODO : Scrolling TextFiled needed
        // building text field to type message
        text = new JTextField();
        text.setBounds(15, 10, 260, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
        text.setForeground(Color.BLUE);
        text.addKeyListener(new KeyAdapter() {  // adding event when any key pressed or released in textField
            // setting typing status
            @Override
            public void keyPressed(KeyEvent e) {    // when key get pressed
                name.setText("C.AMERICA typing...");
                timer.stop();
                typing = true;
            }
            @Override
            public void keyReleased(KeyEvent e) {   // when key released or not pressed
                typing = false;
                if (!timer.isRunning()){
                    timer.start();
                }
            }
        });
        panel.add(text);    // adding textField into panel

        // adding send button
        send = new JButton("Send");
        send.setBounds(280, 15, 65, 30);
        send.setBackground(new Color(200, 120, 255));
        send.setForeground(new Color(7, 95, 75));
        // performing action after clicking send button
        send.addActionListener(e -> {
            String message = "ROGERS: " + text.getText();  // getting message that user typed as string.

            backUpFile(message);    // calling backupFile method to create record file

            JPanel p2 = formatLabel(message);   // adding panel with label containing message
            chatArea.setLayout(new BorderLayout()); // chat area border setup
            JPanel right = new JPanel(new BorderLayout());  // another panel to hold border type
            right.add(p2, BorderLayout.LINE_END);    // adding message panel at right side into this panel
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(10));  // add padding(space) between panels
            chatArea.add(vertical, BorderLayout.PAGE_START);    // adding chatArea at start of border
            chatArea.revalidate();  //  repaints

// NOTE: printing next line in code below will actually a second message which let first message
//       add into panel and when next message will arrive it will printed into next line
            try {
                bufferedWriter.write(message + "\n"); // writing message from string
                                                        // which which will be sent to server using
                // OutputStreamWriter for reference check block commented => // Connecting user to port
                bufferedWriter.flush(); // flushing characters to intended stream to reach destination
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            text.setText("");   // setting written message to empty

        });
        panel.add(send);    // adding button into panel

        // Connecting user to port
        try {
            Socket clientConnection = new Socket("localhost", 6666);
            // send message to port. our case server which is local host or 127.0.0.1 with port 6666
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(clientConnection.getOutputStream())  // getting sent message into socket
            );

            // receiving streamed message from server port using BufferedReader
            bufferedReader = new BufferedReader(
                    new InputStreamReader(clientConnection.getInputStream())    // getting received message into socket
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // implementing abstract method run()
    @Override
    public void run() {
        // printing message into other client's chatArea
        try{
            String msg; // declaring string which can store message
            while ((msg = bufferedReader.readLine()) != null){    // read until message gets empty,
                                                                // using BufferedReader
                JPanel p2 = formatLabel(msg);   // adding panel with label containing message
                JPanel left = new JPanel(new BorderLayout());   // another panel to hold border type
                left.add(p2, BorderLayout.LINE_START);   // adding message panel to left at border starts
                vertical.add(left);
                vertical.add(Box.createVerticalStrut(10));
                chatArea.add(vertical, BorderLayout.PAGE_START);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addMyIcon(String image_address,  int width, int height){
        ImageIcon name = new ImageIcon(
                getClass().getResource(image_address));
        Image resize_name = name.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        ImageIcon new_name = new ImageIcon(resize_name);
        label = new JLabel(new_name);
    }

    private void backUpFile(String message) {
        try {
            FileWriter fileWriter = new FileWriter("backup.txt", true);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(fileWriter));
            printWriter.println(message);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static JPanel formatLabel(String message) {
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));

        JLabel label1 = new JLabel("<html><p style = \"width : 150px\">" + message + "</p></html>");
        label1.setBackground(new Color(200, 120, 255));
        label1.setForeground(new Color(7, 95, 75));
        label1.setFont(new Font("SAN_SERIF", Font.PLAIN, 18));
        label1.setOpaque(true);
        label1.setBorder(new EmptyBorder(15, 15, 15, 70));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        JLabel label2 = new JLabel();
        label2.setText(simpleDateFormat.format(calendar.getTime()));

        p3.add(label1);
        p3.add(label2);
        return p3;
    }

    public static void main(String[] args) {
        ClientFour clientFour = new ClientFour();
        clientFour.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFour.setVisible(true);

        // calling thread class explicitly
        Thread thread = new Thread(clientFour);  // passing constructor object to create thread(s)
        thread.start(); // start() method will call run() method
    }
}
