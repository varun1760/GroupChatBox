import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;

public class ClientFive extends JFrame implements Runnable{
    JPanel panel;
    JLabel label;
    JButton send;
    JTextField text;
    JTextArea chatArea;
    boolean typing;

    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;

    // constructor
    ClientFive(){

        // building a chat window
        setSize(365,515);   // size of chat window
        setLocation(500,200);   // location of chat window in screen
        setLayout(null);    // null so inside element won't expand
//        setUndecorated(true);   // removing title bar which contains closing button (optional)

        // building a panel section
        panel = new JPanel();   // creating a panel
        panel.setLayout(null);  // setting to null layout
        panel.setBounds(0,0,350,60);    // panel size with location
        panel.setBackground(new Color(90000000));   // setting unknown color to panel
//        panel.setBackground(new Color(7,94,84));  // color similar to whatsapp
        add(panel);

        // building back icon into panel section without using method
        addMyIcon("icons/back.png",30,30);  // calling method
        label.setBounds(5,5,30,30);   // to set size with location
        panel.add(label); // adding into panel

        // adding mouse event on back icon
        label.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.exit(0);
                    }
                });
/*  NOTE:
    1. addMouseListener method of Component class which takes interface MouseListener as argument
    2. MouseListener extends interface EventListener
    3. MouseAdopter is abstract class so we don't have to implement all it's abstract method
       but one we need which is mouseClicked
    4. MouseAdopter implements MouseListener that is why we can use it here as argument
    */

        // building display picture using method addMyIcon
        addMyIcon("icons/batman.png",60,60);
        label.setBounds(40,0,60,60);
        panel.add(label);

        // building display Name label
        JLabel label_name = new JLabel("BATMAN");
        label_name.setFont(new Font("SAN_SERIF", Font.BOLD,20));
        label_name.setForeground(Color.white);
        label_name.setBounds(110,10,120,20);
        panel.add(label_name);

        // building active status label
        JLabel status = new JLabel("Active Now");
        status.setFont(new Font("SAN_SERIF", Font.PLAIN,14));
        status.setForeground(Color.white);
        status.setBounds(110,35,90,20);
        panel.add(status);
        Timer timer = new Timer(1, event -> {
            if (!typing){
                status.setText("Active Now");
            }
        });
        timer.setInitialDelay(2000);

        // building video call label using method addMyIcon
        addMyIcon("icons/video.png",30,30);
        label.setBounds(250,15,30,30);
        panel.add(label);

        // building calling label using method addMyIcon
        addMyIcon("icons/phone.png",30,30);
        label.setBounds(290,15,30,30);
        panel.add(label);

        // building option label using method addMyIcon
        addMyIcon("icons/3icon.png",10,30);
        label.setBounds(330,15,15,30);
        panel.add(label);

// TODO : Scrolling ChatArea needed
        // building Chat Area
        chatArea = new JTextArea();
        chatArea.setBounds(5,65,340,350);
        chatArea.setBackground(Color.WHITE);
        chatArea.setFont(new Font("SAN_SERIF",Font.PLAIN,18));
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        add(chatArea);

        // building another panel at bottom
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0,420,350,60);
        panel.setBackground(new Color(90000000));
        add(panel);

// TODO : Scrolling TextField needed
        // building text field to type message
        text = new JTextField();
        text.setBounds(15,10,260,40);
        text.setFont(new Font("SAN_SERIF",Font.PLAIN,14));
        text.setForeground(Color.BLUE);
        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                status.setText("typing...");
                timer.stop();
                typing = true;
            }
            @Override
            public void keyReleased(KeyEvent e) {
                typing = false;
                if (!timer.isRunning()){
                    timer.start();
                }
            }
        });
        panel.add(text);

        // adding send button
        send = new JButton("Send");
        send.setBounds(280,15,65,30);
        send.setBackground(new Color(200,120,255));
        send.setForeground(new Color(7,95,75));
        send.addActionListener(e -> {
            String message = "WAYNE: \n"+text.getText();
            backUpFile(message);
            try {
                bufferedWriter.write(message+"\n");
                bufferedWriter.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            text.setText("");
        });
        panel.add(send);

        try {
            Socket clientConnection = new Socket("localhost",6666);
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(clientConnection.getOutputStream())
            );
            bufferedReader = new BufferedReader(
                    new InputStreamReader(clientConnection.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addMyIcon(String image_address,  int width, int height){
        ImageIcon name = new ImageIcon(
                getClass().getResource(image_address));
        Image resize_name = name.getImage().getScaledInstance(width,height,Image.SCALE_DEFAULT);
        ImageIcon new_name = new ImageIcon(resize_name);
        label = new JLabel(new_name);
    }

    private void backUpFile(String message) {
        try {
            FileWriter fileWriter = new FileWriter("backup.txt",true);
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(fileWriter));
            printWriter.println(message);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            String msg;
            while ((msg = bufferedReader.readLine())!=null){
                chatArea.append(msg + "\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientFive clientfive = new ClientFive();
        clientfive.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientfive.setVisible(true);

        Thread thread = new Thread(clientfive);
        thread.start();
    }
}
