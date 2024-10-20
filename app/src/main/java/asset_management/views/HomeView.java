package asset_management.views;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import asset_management.Database;
import java.sql.ResultSet;

public class HomeView extends JFrame {
    // Frame variable
    final int WIDTH = 1280;
    final int HEIGHT = 720;
    int count = 0;
    JFrame frame;
    JLabel query;

    public HomeView() {
        JTextField textField = new JTextField(Integer.toString(count));
        textField.setBounds(0, 0, 150, 40);
        query = new JLabel();
        query.setBounds(540, 320, 300, 300);
        // Creates button and sizes it
        JButton button = new JButton("Connect to database");
        button.setBounds(540, 270, 100, 40);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out.println("Increasing by one..");
                count += 1;
                System.out.println(count);
                textField.setText(Integer.toString(count));
                Database db = new Database();
                
                ResultSet result = db.queryAll("testing");
                // TODO: Implement ResultSet --> String conversion. Connects successfully. 
                query.setText("Text set.");
            }
        });



        // Adds components to frame
        add(query);
        add(button);
        add(textField);        
        // Adjust frame properties
        setTitle("Inventory Manager");
        setSize(WIDTH, HEIGHT);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
