import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class LibraryManagement extends JFrame {

    private JTextField txtId, txtTitle, txtAuthor, txtSearch, txtIssuedTo;
    private DefaultTableModel model;
    private JTable table;
    private JButton btnAdd, btnDelete, btnIssue, btnReturn;
    private final String FILE_NAME = "books.txt";
    private final String ISSUED_FILE = "issued.txt";

    public LibraryManagement(String role) {
        setTitle("Library Management System - Role: " + role.toUpperCase());
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background image
        JLabel bgLabel = createBackgroundLabel("images/library_bg.jpg", 1000, 600);
        setContentPane(bgLabel);
        setLayout(new GridBagLayout());

        // Central dark overlay panel (slightly transparent)
        JPanel overlay = new JPanel(new BorderLayout(15,15));
        overlay.setPreferredSize(new Dimension(900,500));
        overlay.setBackground(new Color(30,30,30,150)); // alpha 150 to see background
        overlay.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        add(overlay);

        // Title
        JLabel lblTitle = new JLabel("Library Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        overlay.add(lblTitle, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setOpaque(false);
        overlay.add(mainPanel, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Book Details",0,0,new Font("Tahoma",Font.BOLD,16),Color.WHITE));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5); gbc.fill=GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(12);
        txtTitle = new JTextField(12);
        txtAuthor = new JTextField(12);
        txtIssuedTo = new JTextField(12);

        JLabel lblId = new JLabel("Book ID:"); lblId.setForeground(Color.WHITE);
        JLabel lblTitleLabel = new JLabel("Title:"); lblTitleLabel.setForeground(Color.WHITE);
        JLabel lblAuthor = new JLabel("Author:"); lblAuthor.setForeground(Color.WHITE);
        JLabel lblIssuedTo = new JLabel("Issued To:"); lblIssuedTo.setForeground(Color.WHITE);

        gbc.gridx=0; gbc.gridy=0; inputPanel.add(lblId, gbc);
        gbc.gridx=1; inputPanel.add(txtId, gbc);
        gbc.gridx=0; gbc.gridy=1; inputPanel.add(lblTitleLabel, gbc);
        gbc.gridx=1; inputPanel.add(txtTitle, gbc);
        gbc.gridx=0; gbc.gridy=2; inputPanel.add(lblAuthor, gbc);
        gbc.gridx=1; inputPanel.add(txtAuthor, gbc);
        gbc.gridx=0; gbc.gridy=3; inputPanel.add(lblIssuedTo, gbc);
        gbc.gridx=1; inputPanel.add(txtIssuedTo, gbc);

        // Buttons
        btnAdd = createButton("Add","images/add_icon.png");
        btnDelete = createButton("Delete","images/delete_icon.png");
        btnIssue = createButton("Issue","images/issue_icon.png");
        btnReturn = createButton("Return","images/return_icon.png");

        gbc.gridx=0; gbc.gridy=4; inputPanel.add(btnAdd, gbc);
        gbc.gridx=1; inputPanel.add(btnDelete, gbc);
        gbc.gridx=0; gbc.gridy=5; inputPanel.add(btnIssue, gbc);
        gbc.gridx=1; inputPanel.add(btnReturn, gbc);

        mainPanel.add(inputPanel, BorderLayout.WEST);

        // Table
        String[] columns = {"Book ID","Title","Author"};
        model = new DefaultTableModel(columns,0);
        table = new JTable(model);

        // Light table styling
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for(int i=0;i<table.getColumnCount();i++)
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        table.setRowHeight(30);
        table.setFont(new Font("Tahoma",Font.PLAIN,14));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(70,130,180));
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Tahoma",Font.BOLD,14));
        table.getTableHeader().setBackground(new Color(200,200,200));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Books Table",0,0,new Font("Tahoma",Font.BOLD,16),Color.BLACK));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(20);
        JButton btnSearch = createButton("Search","images/search_icon.png");
        JButton btnViewAll = new JButton("View All");
        JButton btnViewIssued = new JButton("View Issued Books");
        searchPanel.add(new JLabel("Search by Title:"){{
            setForeground(Color.WHITE);
        }});
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnViewAll);
        searchPanel.add(btnViewIssued);
        overlay.add(searchPanel, BorderLayout.SOUTH);

        loadBooks();

        // Button actions
        btnAdd.addActionListener(e -> addBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnIssue.addActionListener(e -> issueBook());
        btnReturn.addActionListener(e -> returnBook());
        btnSearch.addActionListener(e -> searchBook());
        btnViewAll.addActionListener(e -> loadBooks());
        btnViewIssued.addActionListener(e -> loadIssuedBooks());

        if(role.equalsIgnoreCase("student")){
            btnAdd.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private JLabel createBackgroundLabel(String path,int width,int height){
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width,height,Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(img));
    }

    private JButton createButton(String text,String iconPath){
        ImageIcon icon = new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(24,24,Image.SCALE_SMOOTH));
        JButton btn = new JButton(text,icon);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(70,130,180));
        btn.setForeground(Color.WHITE);
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ btn.setBackground(new Color(100,149,237)); }
            public void mouseExited(MouseEvent e){ btn.setBackground(new Color(70,130,180)); }
        });
        return btn;
    }

    class CustomTableCellRenderer extends DefaultTableCellRenderer{
        @Override
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,
                                                       boolean hasFocus,int row,int column){
            super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            setHorizontalAlignment(JLabel.CENTER);
            if(isSelected){
                setBackground(new Color(70,130,180));
                setForeground(Color.WHITE);
            } else {
                setBackground(row%2==0?Color.WHITE:new Color(240,240,240));
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    // ----------------- File handling methods -----------------
    private void loadBooks() {
        model.setRowCount(0);
        try(BufferedReader br=new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line=br.readLine())!=null){
                String[] data=line.split(",");
                model.addRow(new String[]{data[0],data[1],data[2]});
            }
        } catch(IOException ignored){}
    }

    private void addBook(){
        String id=txtId.getText().trim();
        String title=txtTitle.getText().trim();
        String author=txtAuthor.getText().trim();
        if(id.isEmpty()||title.isEmpty()||author.isEmpty()){
            JOptionPane.showMessageDialog(this,"All fields required!");
            return;
        }
        try(FileWriter fw=new FileWriter(FILE_NAME,true)){
            fw.write(id+","+title+","+author+"\n");
            JOptionPane.showMessageDialog(this,"Book added successfully!");
            clearFields();
            loadBooks();
        }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Error saving book!"); }
    }

    private void deleteBook(){
        int selectedRow=table.getSelectedRow();
        if(selectedRow==-1){
            JOptionPane.showMessageDialog(this,"Select a book to delete!");
            return;
        }
        String bookId=model.getValueAt(selectedRow,0).toString();
        updateFileExcludeBook(FILE_NAME,bookId);
        JOptionPane.showMessageDialog(this,"Book deleted successfully!");
        loadBooks();
    }

    private void issueBook(){
        int selectedRow=table.getSelectedRow();
        String issuedTo=txtIssuedTo.getText().trim();
        if(selectedRow==-1 || issuedTo.isEmpty()){
            JOptionPane.showMessageDialog(this,"Select book and enter name to issue!");
            return;
        }
        String id=model.getValueAt(selectedRow,0).toString();
        String title=model.getValueAt(selectedRow,1).toString();
        String author=model.getValueAt(selectedRow,2).toString();
        try(FileWriter fw=new FileWriter(ISSUED_FILE,true)){
            fw.write(id+","+title+","+author+","+issuedTo+"\n");
            JOptionPane.showMessageDialog(this,"Book issued successfully!");
            clearFields();
            loadBooks();
        }catch(IOException ex){ JOptionPane.showMessageDialog(this,"Error issuing book!"); }
    }

    private void returnBook(){
        int selectedRow=table.getSelectedRow();
        if(selectedRow==-1){
            JOptionPane.showMessageDialog(this,"Select issued book to return!");
            return;
        }
        String bookId=model.getValueAt(selectedRow,0).toString();
        updateFileExcludeBook(ISSUED_FILE,bookId);
        JOptionPane.showMessageDialog(this,"Book returned successfully!");
        loadBooks();
        loadIssuedBooks();
    }

    private void searchBook(){
        String keyword=txtSearch.getText().trim().toLowerCase();
        if(keyword.isEmpty()) return;
        model.setRowCount(0);
        try(BufferedReader br=new BufferedReader(new FileReader(FILE_NAME))){
            String line;
            while((line=br.readLine())!=null){
                String[] data=line.split(",");
                if(data[1].toLowerCase().contains(keyword)){
                    model.addRow(new String[]{data[0],data[1],data[2]});
                }
            }
        } catch(IOException ignored){}
    }

    private void loadIssuedBooks(){
        model.setRowCount(0);
        try(BufferedReader br=new BufferedReader(new FileReader(ISSUED_FILE))){
            String line;
            while((line=br.readLine())!=null){
                String[] data=line.split(",");
                model.addRow(new String[]{data[0],data[1],data[2]});
            }
        } catch(IOException ignored){}
    }

    private void updateFileExcludeBook(String fileName,String bookId){
        try{
            File inputFile=new File(fileName);
            File tempFile=new File("temp.txt");
            BufferedReader br=new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw=new BufferedWriter(new FileWriter(tempFile));
            String line;
            while((line=br.readLine())!=null){
                if(!line.startsWith(bookId+",")){
                    bw.write(line);
                    bw.newLine();
                }
            }
            br.close(); bw.close();
            inputFile.delete();
            tempFile.renameTo(inputFile);
        }catch(IOException ignored){}
    }

    private void clearFields(){
        txtId.setText(""); txtTitle.setText(""); txtAuthor.setText(""); txtIssuedTo.setText("");
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
