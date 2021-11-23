

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MyFrame extends JFrame implements ActionListener
{
    private FileCommands controller;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenu data;
    private JMenu viev;
    private JMenu help;
    private JMenuItem append;
    private JMenuItem print;
    private JMenuItem find;
    private JMenuItem delete;
    private JMenuItem file_name;
    private JMenuItem exit;
    private JMenuItem font;
    private JMenuItem about;
    private JList<String> list;
    private JLabel status;
    private Font fontStyle = new Font("Courier", Font.BOLD, 16);

    public MyFrame(FileCommands controller) throws HeadlessException
    {
        super("Lab 11");

        this.controller = controller;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (JOptionPane.showConfirmDialog(null, "Do you want to close application?", "Close",
                                                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                {
                    setVisible(false);
                    dispose();
                }
            }
        });
        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException ignored)
        {
        }
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        menuBar = new JMenuBar();

        file = new JMenu("File");
        file_name = new JMenuItem("File name");
        exit = new JMenuItem("Exit");
        file.add(file_name);
        file_name.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        file.addSeparator();
        file.add(exit);
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));

        data = new JMenu("Data");
        append = new JMenuItem("Append");
        print = new JMenuItem("Print");
        delete = new JMenuItem("Delete");
        find = new JMenuItem("Find");


        data.add(append);
        data.addSeparator();
        data.add(print);
        data.addSeparator();
        data.add(find);
        data.addSeparator();
        data.add(delete);


        viev = new JMenu("Viev");
        font = new JMenuItem("Font");

        viev.add(font);
        viev.addSeparator();


        help = new JMenu("Help");
        about = new JMenuItem("About");
        help.add(about);


        menuBar.add(file);
        menuBar.add(data);
        menuBar.add(viev);
        menuBar.add(help);

        setJMenuBar(menuBar);

        file_name.addActionListener(this);
        exit.addActionListener(this);
        append.addActionListener(this);
        print.addActionListener(this);
        delete.addActionListener(this);
        find.addActionListener(this);
        about.addActionListener(this);
        font.addActionListener(this);

        list = new JList<>();
        list.setFont(fontStyle);
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane);

        status = new JLabel(FileCommands.getFile());
        status.setHorizontalAlignment(JLabel.LEFT);

        Border border = BorderFactory.createEtchedBorder(Color.white, new Color(100, 100, 100));

        status.setBorder(border);

        status.setOpaque(true);
        add(status);

        viev.add(ShowComponent.createPlafMenu(this));

        pack();
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == file_name)
        {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(1, 2));
            panel.add(new JLabel("File name"));
            String text = FileCommands.getFilename();
            JTextField name = new JTextField(text);
            panel.add(name);
            if (JOptionPane.showConfirmDialog(this, panel,
                                              "File name", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                try
                {
                    controller.open(name.getText());
                } catch (IllegalArgumentException e)
                {
                    showError(e);
                }
            }
        }
        else if (event.getSource() == exit)
        {
            if (JOptionPane.showConfirmDialog(null, "Do you want to close application?", "Close",
                                              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            {
                setVisible(false);
                dispose();
            }
        }
        else if (event.getSource() == font)
        {
            FontChooser fc = new FontChooser(this);
            fc.setLocationRelativeTo(null);
            fc.setVisible(true);
            fontStyle = fc.getSelectedFont();
            list.setFont(fontStyle);
        }
        else if (event.getSource() == delete)
        {
            JPanel inputPanel = new JPanel();
            JComboBox<String> index = new JComboBox<>(new String[]{"By house number", "By apartment", "By full name", "By date"});
            index.setEnabled(true);
            index.setSelectedIndex(0);
            inputPanel.add(new JLabel("Index"));
            inputPanel.add(index);
            JTextField key = new JTextField("21", 4);
            inputPanel.add(new JLabel("Key"));
            inputPanel.add(key);
            String[] hints = {"21", "501", "Рудьман Сергей Владимирович", "2001.09.05"};
            index.addItemListener(e -> key.setText(hints[index.getSelectedIndex()]));
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                                              "Enter position", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                try
                {
                    controller.delete(index.getSelectedIndex(), key.getText());
                } catch (ClassNotFoundException | IOException | KeyNotUniqueException | IllegalArgumentException e)
                {
                    showError(e);
                }
            }
        }
        else if (event.getSource() == append)
        {
            JTextField houseNumber = new JTextField("", 10);
            JTextField apartment = new JTextField("", 10);
            JTextField address = new JTextField("", 20);
            JTextField fullName = new JTextField("", 7);
            JTextField date = new JTextField("", 10);
            JTextField price = new JTextField("", 5);
            JTextField fine = new JTextField("", 5);
            JTextField delay = new JTextField("", 5);

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridLayout(8, 2));
            inputPanel.add(new JLabel("House number"));
            inputPanel.add(houseNumber);
            inputPanel.add(new JLabel("Apartment"));
            inputPanel.add(apartment);
            inputPanel.add(new JLabel("Address"));
            inputPanel.add(address);
            inputPanel.add(new JLabel("Full name"));
            inputPanel.add(fullName);
            inputPanel.add(new JLabel("Date of bill"));
            inputPanel.add(date);
            inputPanel.add(new JLabel("Price"));
            inputPanel.add(price);
            inputPanel.add(new JLabel("Fine"));
            inputPanel.add(fine);
            inputPanel.add(new JLabel("Delay"));
            inputPanel.add(delay);

            if (JOptionPane.showConfirmDialog(this, inputPanel,
                                              "Append bill", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                try
                {
                    Bill bill = new Bill(houseNumber.getText(), apartment.getText(), date.getText(), address.getText(), fullName.getText(), price.getText(), fine.getText(), delay.getText());
                    controller.appendFile(bill);

                } catch (IOException | ClassNotFoundException | KeyNotUniqueException | DateTimeParseException | IllegalArgumentException e)
                {
                    showError(e);
                }
            }
        }
        else if (event.getSource() == about)
        {


            JPanel Panel = new JPanel();
            Font font = new Font("Verdana", Font.BOLD, 18);
            Panel.setFont(font);
            Panel.setLayout(new GridLayout(4, 1));


            JLabel text = new JLabel("2 курс     |   Рудьман Сергей   |   ФПМИ    |   КБ");
            text.setHorizontalAlignment(JLabel.CENTER);
            text.setFont(font);
            Panel.add(text);
            Panel.add(new JLabel(""));
            JLabel info1 = new JLabel("Счёт за комунальные услуги имеет атрибуты: номер дома, номер квартиры, адрес, ФИО, дата платежа, сумма платежа,");
            JLabel info2 = new JLabel("процент пени, на сколько дней просрочен платеж. Индексировать по: номеру дома, квартиры, ФИО, дате");
            Font infoFont = new Font("Verdana", Font.BOLD, 15);
            info1.setFont(infoFont);
            info2.setFont(infoFont);
            Panel.add(info1);
            Panel.add(info2);

            JOptionPane.showConfirmDialog(this, Panel,
                                          "Developer information", JOptionPane.DEFAULT_OPTION);

        }
        else if (event.getSource() == print)
        {

            JRadioButton sorted = new JRadioButton("Sorted");
            JRadioButton revSorted = new JRadioButton("Reverse sorted");
            ButtonGroup group = new ButtonGroup();

            group.add(sorted);
            group.add(revSorted);


            JPanel inputPanel = new JPanel();

            inputPanel.add(sorted);
            inputPanel.add(revSorted);

            JComboBox<String> index = new JComboBox<>(new String[]{"By house number", "By apartment", "By full name", "By date"});
            index.setEnabled(false);
            index.setSelectedIndex(0);

            sorted.addActionListener(e -> index.setEnabled(true));
            revSorted.addActionListener(e -> index.setEnabled(true));
            inputPanel.add(new JLabel(" Index "));
            inputPanel.add(index);
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                                              "Print bills", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                try
                {
                    if (sorted.isSelected() || revSorted.isSelected())
                    {
                        controller.printSorted(revSorted.isSelected(), index.getSelectedIndex());
                    }
                    else
                    {
                        controller.printFile();
                    }
                } catch (IOException | ClassNotFoundException | IllegalArgumentException e)
                {
                    showError(e);
                }
            }
        }
        else if (event.getSource() == find)
        {
            JRadioButton byKey = new JRadioButton("By key");
            JRadioButton larger = new JRadioButton("Large");
            JRadioButton less = new JRadioButton("Less");
            JPanel inputPanel = new JPanel();
            inputPanel.add(byKey);
            inputPanel.add(larger);
            inputPanel.add(less);
            ButtonGroup group = new ButtonGroup();
            group.add(byKey);
            group.add(larger);
            group.add(less);
            byKey.setSelected(true);
            JComboBox<String> index = new JComboBox<>(new String[]{"By house number", "By apartment", "By full name", "By date"});
            index.setEnabled(true);
            index.setSelectedIndex(0);
            inputPanel.add(new JLabel("Index"));
            inputPanel.add(index);
            JTextField key = new JTextField("21", 15);
            inputPanel.add(new JLabel("Key"));
            inputPanel.add(key);
            String[] hints = {"21", "501", "Рудьман Сергей Владимирович", "2001.09.05"};
            index.addItemListener(e -> key.setText(hints[index.getSelectedIndex()]));
            if (JOptionPane.showConfirmDialog(this, inputPanel,
                                              "Find bills", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                try
                {
                    if (byKey.isSelected())
                    {
                        controller.printByKey(index.getSelectedIndex(), key.getText());
                    }
                    else
                    {
                        controller.printByKey(larger.isSelected(), index.getSelectedIndex(), key.getText());
                    }
                } catch (IOException | ClassNotFoundException | IllegalArgumentException e)
                {
                    showError(e);
                }
            }
        }
    }

    void showData(List<String> data)
    {
        list.setListData(data.toArray(new String[0]));
    }

    void showError(Exception e)
    {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    void update(String string)
    {
        status.setText(string);
    }
}

