import java.awt.event.*;
import java.awt.FlowLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Font;

class GUI {
  
  // GUI
  public GUI() {
    final int X_SCALE = 700;
    final int Y_SCALE = 550;
    JButton btn0;
    JButton btn1;
    JButton btn2;
    JButton btn3;
    JTextField input;
    JTextArea result;
    JScrollPane scroll; // scroll attached to result
    JTextArea instructions;
    JFrame window;
    Container contentPane;

    Paragraph prompt1 = new Paragraph("""
            Surround elements with square brackets [], replace the yield sign or arrow with a "right facing caret" >, surround subscripts with underscores _, and surround unknowns with colons ::. Place unknowns outside of the square brackets (to the left of them) (label each unknown with a different letter of the alphabet :a:)

            example:

            :a:[H]_2_ + :b:[O]_2_ > :c:[H]_2_[O]

            a = 2

            b = 1

            c = 2

            """);
    prompt1.paragraphLineFormat(80);
    String output = " ".repeat(45) + "Press enter to calculate";

    // Frame elements
    // create window
    window = new JFrame("Balancing Equations Calculator");
    // add input box
    input = new JTextField("", 12);
    // buttons
    btn0 = new JButton("[Element]");
    btn1 = new JButton("_Subscript_");
    btn2 = new JButton(":Unknown:");
    btn3 = new JButton(">Yield");
    // print result
    result = new JTextArea();
    scroll = new JScrollPane(result);
    // display info
    instructions = new JTextArea();

    // input attributes
    input.setSize(200,20);
    input.setFont(new Font("Arial", Font.PLAIN, 21));

    // button attributes
    btn0.setSize(20,20);
    btn0.setFont(new Font("Arial", Font.BOLD, 15));
    btn1.setSize(20,20);
    btn1.setFont(new Font("Arial", Font.BOLD, 15));
    btn2.setSize(20,20);
    btn2.setFont(new Font("Arial", Font.BOLD, 15));
    btn3.setSize(20,20);
    btn3.setFont(new Font("Arial", Font.BOLD, 15));

    // result attributes
    result.setPreferredSize(new Dimension(7 * X_SCALE / 8, 320));
    result.setRows(18);
    result.setFont(new Font("Arial", Font.BOLD, 15));
    result.setBackground(java.awt.Color.getHSBColor(0.5861f,0.034f,0.83f));
    result.setLineWrap(true);
    result.setWrapStyleWord(true);
    result.setEditable(false);
    result.append(prompt1.toString());

    // instruction attributes
    instructions.setSize(3 * X_SCALE / 4, 2 * Y_SCALE / 10);
    instructions.setFont(new Font("Arial",Font.BOLD,14));
    instructions.setLineWrap(true);
    instructions.setOpaque(false);
    instructions.setEditable(false);
    instructions.append(output);

    btn0.addActionListener(arg -> {
      String temp = input.getText();

      input.setText(temp + "[]");
      input.requestFocusInWindow();
      input.setCaretPosition(temp.length() + 1);
    });

    btn1.addActionListener(arg -> {
        String temp = input.getText();

        input.setText(temp + "__");
        input.requestFocusInWindow();
        input.setCaretPosition(temp.length() + 1);
    });

    btn2.addActionListener(arg -> {
        String temp = input.getText();

        input.setText(temp + "::");
        input.requestFocusInWindow();
        input.setCaretPosition(temp.length() + 1);
    });

    btn3.addActionListener(arg -> {
        String temp = input.getText();

        input.setText(temp + " > ");
        input.requestFocusInWindow();
    });

    // Action Event:
    input.addActionListener(new ActionListener(){
      private int lineCount = 18;

      public void actionPerformed(ActionEvent arg){
        //add userInput into the txtChat
        String uText = input.getText();
        result.append("\n" + uText + "\n\n" + Main.solveChemEquation(uText));
        //set the input field to be empty
        input.setText("");
        while (result.getLineCount() > lineCount) {
          lineCount = result.getLineCount() + 10;
          result.setPreferredSize(new Dimension(7 * X_SCALE / 8, 165 * lineCount / 9));
          window.revalidate();
        }
      }
    });

    // exit on termination
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // window size
    window.setSize(X_SCALE, Y_SCALE); // 2000, 2000
    window.setResizable(false);
    // opens centered
    window.setLocationRelativeTo(null);
    // sets window template
    contentPane = window.getContentPane();
    contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));

    // add Items To Frame
    contentPane.add(instructions);
    contentPane.add(input);
    contentPane.add(btn0);
    contentPane.add(btn1);
    contentPane.add(btn2);
    contentPane.add(btn3);
    contentPane.add(scroll);

    // visible
    window.setVisible(true);

    /*

    // GUI examples

    // Action Event:
    input.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent arg){

            //add userInput into the txtChat
            String uText = input.getText();
            result.append(Main.solveChemEquation(uText) + "-");
            //auto scroll down
            result.setCaretPosition(result.getDocument().getLength());
            //set the input field to be empty
            input.setText("");
        }
    });

    // dropdown
    String[] types = {"Test1", "Test2"};
    JComboBox<String> type = new JComboBox<String>(types);
    type.setSelectedIndex(0);

    // layout stuff
    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
    panel.setLayout(new GridLayout(0, 1));
    */
  }
}